/*
 * Copyright (c) 2018, CiBO Technologies, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package torch.evilplot.plot

import torch.evilplot.geometry.{Drawable, Placeable, SeqPlaceable, EmptyDrawable, Extent}
import torch.evilplot.numeric.{Bounds, Point}
import torch.evilplot.plot.renderers.{BarRenderer, ContinuousBinRenderer, PlotRenderer}
import torch.evilplot.colors.Color
import torch.evilplot.plot.aesthetics.Theme
import scala.math.Fractional.Implicits.infixFractionalOps
import scala.math.Integral.Implicits.infixIntegralOps
import scala.math.Numeric.Implicits.infixNumericOps

object Histogram {

  //TODO for v0.7.x deprecate -1 in favor of Option[Int] where None is automatic sizing
  private val automaticBinCount: Int = -1
  val defaultBinCount: Int = automaticBinCount

  /** auto select number of bins Note:Rice rule: 1k => 20bins (less decimating than Sturges) */
  def defaultBinCount(nSamples: Int): Int = {
    val bins = math.ceil(2 * math.pow(nSamples, 1d / 3)).toInt
    math.min(math.max(10, bins), 1000)
  }

  /** Create binCount bins from the given data and xbounds.
    * @param values the raw data
    * @param xbounds the bounds over which to bin
    * @param binCount the number of bins to create
    * @return a sequence of points, where the x coordinates represent the left
    *         edge of the bins and the y coordinates represent their heights
    */
  def createBins(values: Seq[Double], xbounds: Bounds, binCount: Int): Seq[Point] =
    createBins(values, xbounds, binCount, normalize = false, cumulative = false)

  /** Create binCount bins from the given data and xbounds, normalizing the heights
    * such that their sum is 1 */
  def normalize(values: Seq[Double], xbounds: Bounds, binCount: Int): Seq[Point] =
    createBins(values, xbounds, binCount, normalize = true, cumulative = false)

  /** Create binCount bins from the given data and xbounds, cumulatively
    * such that each bin includes the data in all previous bins */
  def cumulative(values: Seq[Double], xbounds: Bounds, binCount: Int): Seq[Point] =
    createBins(values, xbounds, binCount, normalize = false, cumulative = true)

  /** Create binCount bins from the given data and xbounds, computing the bin
    * heights such that they represent the average probability density over each
    * bin interval */
  def density(values: Seq[Double], xbounds: Bounds, binCount: Int): Seq[Point] = {
    val binWidth = xbounds.range / binCount
    createBins(values, xbounds, binCount, normalize = true, cumulative = false)
      .map { case Point(x, y) => Point(x, y / binWidth) }
  }

  /** Create binCount bins from the given data and xbounds, cumulatively
    * such that each bin includes the data in all previous bins, and normalized
    * so that bins approximate a CDF */
  def cumulativeDensity(values: Seq[Double], xbounds: Bounds, binCount: Int): Seq[Point] =
    createBins(values, xbounds, binCount, normalize = true, cumulative = true)

  // Create binCount bins from the given data and xbounds.
  private def createBins(
    values: Seq[Double],
    xbounds: Bounds,
    binCount: Int,
    normalize: Boolean,
    cumulative: Boolean): Seq[Point] = {
    val binWidth = xbounds.range / binCount

    val grouped = values.groupBy { value =>
      math.min(((value - xbounds.min) / binWidth).toInt, binCount - 1)
    }
    val pts = (0 until binCount).map { i =>
      val x = i * binWidth + xbounds.min
      grouped.get(i) match {
        case Some(vs) =>
          val y = if (normalize) vs.size.toDouble / values.size else vs.size.toDouble
          Point(x, y)
        case _ => Point(x, 0)
      }
    }
    if (cumulative) {
      pts.scanLeft(Point(0, 0)) { case (Point(_, t), Point(x, y)) => Point(x, y + t) }.drop(1)
    } else {
      pts
    }
  }

  @deprecated(
    "Use HistogramBinRenderer instead to prevent double binning and separation of data and view bounds",
    "v0.6.1")
  case class HistogramRenderer(
    data: Seq[Double],
    barRenderer: BarRenderer,
    binCount: Int,
    spacing: Double,
    boundBuffer: Double,
    binningFunction: (Seq[Double], Bounds, Int) => Seq[Point])
      extends PlotRenderer {

    def render(plot: Plot, plotExtent: Extent)(implicit theme: Theme): Drawable = {
      val histRenderer = Histogram(
        data,
        bins = binCount,
        barRenderer = Some(barRenderer),
        spacing = Some(spacing),
        boundBuffer = Some(boundBuffer),
        binningFunction = binningFunction).renderer
      histRenderer.render(plot, plotExtent)
    }

    override val legendContext: LegendContext =
      barRenderer.legendContext.getOrElse(LegendContext.empty)

  }

  /** this render assumes the binning of the data has already been applied; i.e in cases where the plot ranges need to be pre-calculated
    *  @param binPoints each point x:left edge y:count of total*/
  case class HistogramBinRenderer(
    binPoints: Seq[Point],
    binWidth: Double,
    barRenderer: BarRenderer,
    spacing: Double)
      extends PlotRenderer {
    def render(plot: Plot, plotExtent: Extent)(implicit theme: Theme): Drawable =
      if (binPoints.isEmpty) EmptyDrawable()
      else {
        val ctx = PlotCtx(plot, plotExtent, spacing)

        //--constrain to plot view bounds
        val bars = for (p <- binPoints;
                        xb <- Bounds(p.x, p.x + binWidth) intersect plot.xbounds;
                        yb <- Bounds(0, p.y) intersect plot.ybounds) yield {
          val bar = BoundedBar(xb, yb, ctx)
          barRenderer.render(plot, bar.extent, Bar(bar.ymax)).translate(x = bar.xmin, y = bar.ymax)
        }
        bars.group
      }

    override val legendContext: LegendContext =
      barRenderer.legendContext.getOrElse(LegendContext.empty)
  }

  private case class PlotCtx(plot: Plot, extent: Extent, spacing: Double) {
    lazy val tx = plot.xtransform(plot, extent)
    lazy val ty = plot.ytransform(plot, extent)
  }
  private case class BoundedBar(xbin: Bounds, ybin: Bounds, ctx: PlotCtx) {
    //transform data space to pixel space
    lazy val xmin = ctx.tx(xbin.min) + ctx.spacing / 2.0
    lazy val xmax = ctx.tx(xbin.max) - ctx.spacing / 2.0
    lazy val width = math.max(0d, xmax - xmin)
    lazy val ymax = ctx.ty(ybin.max)
    lazy val ymin = ctx.ty(0)
    lazy val height = math.abs(ymax - ymin)
    lazy val extent = Extent(width, height)
  }

  /** Create a histogram.
    * @param values The data.
    * @param bins The number of bins to divide the data into.
    * @param barRenderer The renderer to render bars for each bin.
    * @param spacing The spacing between bars.
    * @param boundBuffer Extra padding to place at the top of the plot.
    * @param binningFunction A function taking the raw data, the x bounds, and a bin count
    *                        that returns a sequence of points with x points representing left
    *                        bin boundaries and y points representing bin heights
    * @param xbounds optionally use an explicit xbounds instead of an automatic bounds
    * @param color color to use for the default barRenderer and override the implicit theme bar color
    * @param color series name to use if using the default barRenderer
    * @return A histogram plot.
    */
  def apply(
    values: Seq[Double],
    bins: Int = automaticBinCount, //TODO in next major version (v0.7.x) make this an option instead of -1
    barRenderer: Option[BarRenderer] = None,
    spacing: Option[Double] = None,
    boundBuffer: Option[Double] = None,
    binningFunction: (Seq[Double], Bounds, Int) => Seq[Point] = createBins,
    xbounds: Option[Bounds] = None,
    ybounds: Option[Bounds] = None,
    color: Option[Color] = None,
    name: Option[String] = None)(implicit theme: Theme): Plot = {
    val binCount = if (bins != automaticBinCount) bins else defaultBinCount(values.size)

    require(binCount > 0, "must have at least one bin")

    //--merge the multiple sources of configuration options with priority on the arguments over default and themes
    val theColor = color getOrElse theme.colors.bar
    val theBarRenderer = barRenderer getOrElse BarRenderer.named(
      color = Some(theColor),
      name = name)
    val theSpacing = spacing getOrElse theme.elements.barSpacing
    val theBufRatio = boundBuffer getOrElse theme.elements.boundBuffer
    val theXbounds = xbounds getOrElse Bounds.of(values)

    //--auto plot bounds from data.
    //  Note: the whole histogram shouldn't have to be re-calculated at render time if it is already computed here for the ybounds
    val binPoints = binningFunction(values, theXbounds, binCount)
    val theYbounds = Bounds.of(binPoints.map { _.y } :+ 0d).padMax(theBufRatio)
    val binWidth = theXbounds.range / binCount

    val renderer = HistogramBinRenderer(binPoints, binWidth, theBarRenderer, theSpacing)
    Plot(theXbounds, theYbounds, renderer)
  }

  case class ContinuousBinPlotRenderer(
    bins: Seq[ContinuousBin],
    binRenderer: ContinuousBinRenderer,
    spacing: Double,
    boundBuffer: Double)
      extends PlotRenderer {

    def render(plot: Plot, plotExtent: Extent)(implicit theme: Theme): Drawable =
      if (bins.isEmpty) EmptyDrawable()
      else {

        val ctx = PlotCtx(plot, plotExtent, spacing)

        val bars = for (bin <- bins;
                        xbin <- bin.x intersect plot.xbounds;
                        ybin <- Bounds(0, bin.y) intersect plot.ybounds) yield {
          val bar = BoundedBar(xbin, ybin, ctx)
          binRenderer.render(plot, bar.extent, bin).translate(x = bar.xmin, y = bar.ymax)
        }
        bars.group
      }

    override val legendContext: LegendContext =
      binRenderer.legendContext.getOrElse(LegendContext.empty)

  }

  def fromBins(
    bins: Seq[ContinuousBin],
    binRenderer: Option[ContinuousBinRenderer] = None,
    spacing: Option[Double] = None,
    boundBuffer: Option[Double] = None)(implicit theme: Theme): Plot = {
    require(bins.nonEmpty, "must have at least one bin")

    //view bounds restricting presented(rendered) data
    val bufRatio = boundBuffer getOrElse theme.elements.boundBuffer
    val xBounds = Bounds.union(bins.map(_.x)) //no padding on x
    val yBounds = Bounds.of(bins.map(_.y) :+ 0d).padMax(bufRatio) //pad top

    Plot(
      xbounds = xBounds,
      ybounds = yBounds,
      renderer = ContinuousBinPlotRenderer(
        bins,
        binRenderer.getOrElse(ContinuousBinRenderer.default()),
        spacing.getOrElse(theme.elements.barSpacing),
        boundBuffer.getOrElse(theme.elements.boundBuffer)
      )
    )
  }
}

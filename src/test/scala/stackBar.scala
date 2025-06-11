import torch.evilplot.colors.Color
import torch.evilplot.colors.HTMLNamedColors.{green, red}
import torch.evilplot.geometry.Extent
import torch.evilplot.plot.*
import torch.evilplot.numeric.{Bounds, Point}
import torch.evilplot.plot.*
import torch.evilplot.plot.aesthetics.DefaultTheme.*
import torch.evilplot.plot.renderers.{BarRenderer, PathRenderer}

import scala.util.Random

object stackBar extends App  {
  val data = Seq[Seq[Double]](
    Seq(1, 2, 3),
    Seq(4, 5, 6),
    Seq(3, 4, 1),
    Seq(2, 3, 4)
  )
  BarChart
    .clustered(
      data,
      labels = Seq("one", "two", "three")
    )
    .title("Clustered Bar Chart Demo")
    .xAxis(Seq("a", "b", "c", "d"))
    .yAxis()
    .frame()
    .bottomLegend()
    .render()

}

object stackBar2 extends App  {

  val data = Seq[Seq[Double]](
    Seq(1, 2, 3),
    Seq(4, 5, 6),
    Seq(3, 4, 1),
    Seq(2, 3, 4)
  )
  BarChart
    .stacked(
      data,
      labels = Seq("one", "two", "three")
    )
    .title("Stacked Bar Chart Demo")
    .xAxis(Seq("a", "b", "c", "d"))
    .yAxis()
    .frame()
    .bottomLegend()
    .render()
}

object stackBar3 extends App{
  val data = Seq[Seq[Seq[Double]]](
    Seq(Seq(1, 2, 3), Seq(4, 5, 6)),
    Seq(Seq(3, 4, 1), Seq(2, 3, 4))
  )
  BarChart
    .clusteredStacked(
      data,
      labels = Seq("one", "two", "three")
    ).title("Clustered Stacked Bar Chart Demo")
    .standard(Seq("Category 1", "Category 2"))
    .xLabel("Category")
    .yLabel("Level")
    .rightLegend()
    .render()
}


object boxbar extends App{

  import scala.util.Random

  val data = Seq.fill(10)(Seq.fill(Random.nextInt(30))(Random.nextDouble()))
  BoxPlot(data)
    .standard(xLabels = (1 to 10).map(_.toString))
    .render()
}

//object scatterplot extends App{
//
//  import torch.evilplot.numeric._
//  import torch.evilplot.plot._
//  import torch.evilplot.plot.renderers.PointRenderer
//
//  val points = Seq.fill(150) {
//    Point(Random.nextDouble(), Random.nextDouble())
//  } :+ Point(0.0, 0.0)
//  val years = Seq.fill(150)(Random.nextDouble()) :+ 1.0
//  ScatterPlot(
//    points,
//    pointRenderer = Some(PointRenderer.depthColor(years, None, None))
//  )
//    .standard()
//    .xLabel("x")
//    .yLabel("y")
//    .trend(1, 0)
//    .rightLegend()
//    .render()
//}


object densityPlot extends App{


  def gaussianKernel(u: Double): Double = {
    1 / math.sqrt(2 * math.Pi) * math.exp(-0.5d * u * u)
  }

  def densityEstimate(data: Seq[Double], bandwidth: Double)(
    x: Double
  ): Double = {
    val totalProbDensity = data.map { x_i =>
      gaussianKernel((x - x_i) / bandwidth)
    }.sum
    totalProbDensity / (data.length * bandwidth)
  }

  val data = Seq.fill(150)(Random.nextDouble() * 30)
  val colors = Color.getGradientSeq(3)
  val bandwidths = Seq(5d, 2d, 0.5d)
  Overlay(
    colors.zip(bandwidths).map { case (c, b) =>
      FunctionPlot(
        densityEstimate(data, b),
        Some(Bounds(0, 30)),
        Some(500),
        Some(PathRenderer.default(color = Some(c)))
      )
    }: _*
  )
    .standard()
    .xbounds(0, 30)
    .render()
}


object  pairPlot extends App{

  val labels = Vector("a", "b", "c", "d")
  val data = for (i <- 1 to 4) yield {
    (labels(i - 1), Seq.fill(10) {
      Random.nextDouble() * 10
    })
  }
  val plots = for ((xlabel, xdata) <- data) yield {
    for ((ylabel, ydata) <- data) yield {
      val points = (xdata, ydata).zipped.map { (a, b) => Point(a, b) }
      if (ylabel == xlabel) {
        Histogram(xdata, bins = 4)
      } else {
        ScatterPlot(points)
      }
    }
  }
  Facets(plots)
    .standard()
    .title("Pairs Plot with Histograms")
    .topLabels(data.map {
      _._1
    })
    .rightLabels(data.map {
      _._1
    })
    .render()
}


object histogramPlot extends App{
  val plotAreaSize: Extent = Extent(1000, 600)
  val data = Seq.fill(150)(Random.nextDouble() * 22)
  val data2 = Seq.fill(150)((Random.nextDouble() * 28) + 12)
  Overlay(
    Histogram(data,
      barRenderer = Some(BarRenderer.default(Some(red.copy(opacity = 0.5))))),
    Histogram(data2,
      barRenderer = Some(BarRenderer.default(Some(green.copy(opacity = 0.5)))))
  )
    .standard()
    .render(plotAreaSize)
}

//object  contourPlot extends App{
//  val data: Seq[Double] = Seq.fill(100) {
//    Point(Random.nextDouble() * 20, Random.nextDouble() * 20
//  }
//  ContourPlot(data)
//    .standard()
//    .xbounds(0, 20)
//    .ybounds(0, 20)
//    .render()
//}
//import  io.circe.generic.extras.decoding
import torch.evilplot.colors.{ContinuousColoring, HTMLNamedColors}
import torch.evilplot.geometry.{Align, Disc, Drawable, Extent, LineStyle, LinearGradient, Placeable, Rect, Rotate, SeqPlaceable, Style, Text, Wedge}
import torch.evilplot.numeric.{Bounds, Datum2d, Point, Point3d}
import torch.evilplot.plot.aesthetics.DefaultTheme.{DefaultFonts, DefaultTheme}
import torch.evilplot.plot.components.{Legend, Marker, Position}
import torch.evilplot.plot.{Bar, BarChart, BinnedPlot, BoxPlot, CartesianPlot, ContourPlot, Facets, FunctionPlot, Heatmap, Histogram, LegendContext, LegendStyle, LinePlot, MixedBoundsOverlay, Overlay, PieChart, Plot, PlotContext, RichPlot, ScatterPlot}
import torch.evilplot.plot.aesthetics.ClassicTheme.classicTheme
import torch.evilplot.plot.aesthetics.Theme
import torch.evilplot.plot.renderers.PointRenderer
//import torch.evilplot.plot.aesthetics.DefaultTheme.defaultTheme
import scala.util.Random
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@main
def main(): Unit =
  //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
  // to see how IntelliJ IDEA suggests fixing it.
  (1 to 5).map(println)
  import torch.evilplot.colors.HTMLNamedColors.{green, red}
  import torch.evilplot.geometry.Extent
  import torch.evilplot.plot.renderers.BarRenderer
  import scala.util.Random

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

//  val plotAreaSize: Extent = Extent(1000, 600)
  Random.setSeed(666L) //evil seed
  import torch.evilplot.numeric.Point
  import scala.util.Random

  val datas = Seq.fill(100) (
    Point(Random.nextDouble() * 20, Random.nextDouble() * 20
    ))
  ContourPlot(datas)
    .standard()
    .xbounds(0, 20)
    .ybounds(0, 20)
    .render()

  val points = Seq.fill(150)(
    Point3d(Random.nextDouble(), Random.nextDouble(), Random.nextDouble())) :+ Point3d(
    0.0,
    0.0,
    Random.nextDouble())
  println(points.mkString(","))
  println(s"ponit size ${points.length}")
  ScatterPlot(
    points,
    pointRenderer = Some(PointRenderer.depthColor[Point3d[Double]](
      x => x.z,
      points.map(_.z).min,
      points.map(_.z).max,
      Some(ContinuousColoring
        .gradient3(HTMLNamedColors.green, HTMLNamedColors.yellow, HTMLNamedColors.red)),
      None
    ))
  ).standard()
    .xLabel("x")
    .yLabel("y")
    .trend(1, 0)
    .rightLegend()
    .render(plotAreaSize) //.draw ("scatterplot.png")
  for (i <- 1 to 5) do
    //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
    // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
    println(s"i = $i")


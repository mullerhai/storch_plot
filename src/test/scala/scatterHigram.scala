//import torch.evilplot.colors.RGB
//import torch.evilplot.geometry.Extent
//import torch.evilplot.geometry.LineStyle.DashDot
//import torch.evilplot.numeric.Point
//import torch.evilplot.plot.*
//import torch.evilplot.plot.renderers.PointRenderer
//
//import scala.util.Random
//import torch.evilplot.plot.aesthetics.DefaultTheme._
//object scatterHigram extends App {
//
//  val allYears = (2007 to 2013).map(_.toDouble).toVector
//  val data = Seq.fill(150)(Point(Random.nextDouble(), Random.nextDouble()))
//  val years = Seq.fill(150)(allYears(Random.nextInt(allYears.length)))
//
//  val xhist = Histogram(data.map(_.x), bins = 50)
//  val yhist = Histogram(data.map(_.y), bins = 40)
//  ScatterPlot(
//    data = data,
//    pointRenderer = Some(PointRenderer.colorByCategory(years))
//  ).topPlot(xhist)
//    .rightPlot(yhist)
//    .standard()
//    .title("Measured vs Actual")
//    .xLabel("measured")
//    .yLabel("actual")
//    .trend(1, 0, color = RGB(45, 45, 45), lineStyle = LineStyle.DashDot)
//    .overlayLegend(x = 0.95, y = 0.8)
//    .render(Extent(600, 400))
//}

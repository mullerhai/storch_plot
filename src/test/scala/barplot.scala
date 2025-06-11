import torch.evilplot.colors.RGB
import torch.evilplot.geometry.{Align, Drawable, Extent, Placeable, Rect, Text}
import torch.evilplot.plot.*
import torch.evilplot.plot.aesthetics.DefaultTheme.{DefaultFonts, DefaultTheme}
import torch.evilplot.plot.aesthetics.Theme
import torch.evilplot.plot.renderers.BarRenderer
object barplot extends App {

  implicit val theme: Theme = DefaultTheme.copy(
    fonts = DefaultFonts
      .copy(tickLabelSize = 14, legendLabelSize = 14, fontFace = "'Lato', sans-serif")
  )

  val percentChange = Seq[Double](-10, 5, 12, 68, -22)
  val labels = Seq("one", "two", "three", "four", "five")

  val labeledByColor = new BarRenderer {
    val positive = RGB(241, 121, 6)
    val negative = RGB(226, 56, 140)

    def render(plot: Plot, extent: Extent, category: Bar): Drawable = {
      val rect = Rect(extent)
      val value = category.values.head
      val color = if (value >= 0) positive else negative
      Align.center(rect filled color, Text(s"$value%", size = 20)
        .filled(theme.colors.label)
      ).head//.group
    }
  }

  BarChart.custom(percentChange.map(Bar.apply), spacing = Some(20),
      barRenderer = Some(labeledByColor)
    )
    .standard(xLabels = labels)
    .hline(0)
    .render()
}

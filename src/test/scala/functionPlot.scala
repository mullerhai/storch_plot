import torch.evilplot.colors.HTMLNamedColors
import torch.evilplot.colors.HTMLNamedColors.*
import torch.evilplot.numeric.Bounds
import torch.evilplot.plot.*
import torch.evilplot.plot.aesthetics.DefaultTheme._
object functionPlot extends App {
  Overlay(
    FunctionPlot.series(x => x * x, "y = x^2",
      HTMLNamedColors.dodgerBlue, xbounds = Some(Bounds(-1, 1))),
    FunctionPlot.series(x => math.pow(x, 3), "y = x^3",
      HTMLNamedColors.crimson, xbounds = Some(Bounds(-1, 1))),
    FunctionPlot.series(x => math.pow(x, 4), "y = x^4",
      HTMLNamedColors.green, xbounds = Some(Bounds(-1, 1)))
  ).title("A bunch of polynomials.")
    .overlayLegend()
    .standard()
    .render()
}


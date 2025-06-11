import torch.evilplot.plot._
import torch.evilplot.plot.aesthetics.DefaultTheme._
object Main extends App {
  def mains(args: Array[String]): Unit = {

    val data = Seq("one" -> 1.5, "two" -> 3.5, "three" -> 2.0)
    PieChart(data).rightLegend().render()
  }

}


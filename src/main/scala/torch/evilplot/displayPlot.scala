package torch.evilplot

import java.awt.{Graphics, Graphics2D}

import torch.evilplot.plot.Plot
import javax.swing.{JFileChooser, JFrame, JPanel}
import java.awt.event.{ActionEvent, ActionListener, ComponentAdapter, ComponentEvent}
import java.io.File
import java.util.prefs.Preferences
import torch.evilplot.geometry.{Drawable, Extent}
import torch.evilplot.geometry.Placeable
import torch.evilplot.plot.aesthetics.Theme
import javax.swing.filechooser.FileNameExtensionFilter

/** Helper utilities for displaying EvilPlot plots in their own window. The window offers
 * dynamic resizing for plots and the ability to save the plot as a PNG.
 *
 * This utility is published in the `evilplot-repl` artifact, released alongside the
 * regular `evilplot-jvm` artifact. It will need to be added to your build to use it.
 */
object displayPlot {

  private class DrawablePanel extends JPanel {
    var drawable: Option[Drawable] = None // scalastyle:ignore

    def setDrawable(drawnPlot: Drawable): Unit = {
      drawable = Some(drawnPlot)
    }

    override def paintComponent(g: Graphics): Unit = {
      super.paintComponent(g)
      val g2 = g.asInstanceOf[Graphics2D]
      drawable.foreach { d =>
        g2.drawImage(d.asBufferedImage, -30, 0, this)
      }
    }
  }

  private class DrawableFrame(displayable: Either[Plot, Drawable])(implicit theme: Theme)
    extends JFrame {

    import javax.swing.JMenuBar
    import javax.swing.JMenuItem

    val panel: DrawablePanel = new DrawablePanel()
    init()

    private def createMenuBar()(implicit theme: Theme): Unit = {
      val menubar = new JMenuBar
      val save = new JMenuItem("Save")
      val actionListener = new ActionListener {
        def actionPerformed(e: ActionEvent) = {
          val selectFile = new JFileChooser()
          selectFile.setCurrentDirectory(loadLastSaveDir()) //scalastyle:ignore
          selectFile.setFileFilter(new FileNameExtensionFilter("png", "png"))
          val savedFile: Int = selectFile.showSaveDialog(panel)
          if (savedFile == JFileChooser.APPROVE_OPTION) {
            val extensionPattern = "(.*\\.png)".r
            val file: File = selectFile.getSelectedFile.toString match {
              case extensionPattern(s) => new File(s)
              case s                   => new File(s + ".png")
            }
            savePlot(file)
          }
        }
      }
      save.addActionListener(actionListener)
      menubar.add(save)
      setJMenuBar(menubar)
    }

    private def lastSaveDirPref = "lastSaveDir"

    private def updateLastSaveDir(file: File): Unit = {
      prefs.put(lastSaveDirPref, file.getParent)
    }

    private def loadLastSaveDir(): File = {
      Option(prefs.get(lastSaveDirPref, null))
        .map(new File(_))
        .getOrElse(null)
    }

    private def prefs: Preferences = {
      Preferences.userNodeForPackage(getClass).node("displayPlot")
    }

    private def init()(implicit theme: Theme): Unit = {
      setTitle("Plot")
      displayable match {
        case Right(d) =>
          setSize(d.extent.width.toInt, d.extent.height.toInt + 20)
          panel.setDrawable(d.scaled(0.25, 0.25))
        case Left(p) =>
          setSize(400, 420)
          panel.setDrawable(p.render(Extent(400, 400)).scaled(0.25, 0.25))
      }

      add(panel)
      createMenuBar()
      addComponentListener(new ComponentAdapter {
        override def componentResized(e: ComponentEvent): Unit = {
          resizePlot(getWidth, getHeight)
        }
      })
      setVisible(true)
    }

    def getPlotExtent: Extent = {
      Extent(this.getWidth, this.getHeight - 20)
    }

    def resizePlot(width: Int, height: Int)(implicit theme: Theme): Unit = {
      displayable match {
        case Left(p) => panel.setDrawable(p.render(getPlotExtent).scaled(0.25, 0.25))
        case _       =>
      }
    }

    def savePlot(result: File)(implicit theme: Theme): Unit = {
      displayable match {
        case Right(d) => d.write(result)
        case Left(p)  => p.render(getPlotExtent).scaled(0.25, 0.25).write(result)
      }

      updateLastSaveDir(result)
    }

  }

  /** Display a plot in a JFrame. Passing in a plot makes the window resizable.
   * @param plot The plot to render.
   * @param theme The plot's theme. */
  def apply(plot: Plot)(implicit theme: Theme): Unit = {
    JFrame.setDefaultLookAndFeelDecorated(true)
    new DrawableFrame(Left(plot))
  }

  /** Display any Drawable in a JFrame. Resizing the window does not affect the size of
   * the rendered image.
   * @param drawnPlot the Drawable to show in the window.
   */
  def apply(drawnPlot: Drawable)(implicit theme: Theme): Unit = {
    JFrame.setDefaultLookAndFeelDecorated(true)
    new DrawableFrame(Right(drawnPlot))
  }
}
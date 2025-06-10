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

package torch.evilplot

import plot.components._

//package plot:
//  extension (plot: Plot) {
//    // 这里通过混入特质来添加功能，Scala 3 里扩展方法和特质混合使用
//    def withAxesImplicits = new Axes.AxesImplicits {
//      protected val plot: Plot = plot
//    }
//    def withPlotLineImplicits = new PlotLineImplicits {
//      protected val plot: Plot = plot
//    }
//    def withAnnotationImplicits = new AnnotationImplicits {
//      protected val plot: Plot = plot
//    }
//    def withBackgroundImplicits = new BackgroundImplicits {
//      protected val plot: Plot = plot
//    }
//    def withBorderPlotImplicits = new BorderPlotImplicits {
//      protected val plot: Plot = plot
//    }
//    def withFacetLabelImplicits = new FacetLabelImplicits {
//      protected val plot: Plot = plot
//    }
//    def withLabelImplicits = new LabelImplicits {
//      protected val plot: Plot = plot
//    }
//    def withLegendImplicits = new LegendImplicits {
//      protected val plot: Plot = plot
//    }
//    def withPadImplicits = new PadImplicits {
//      protected val plot: Plot = plot
//    }
//    def withPlotDefaultsImplicits = new PlotDefaultsImplicits {
//      protected val plot: Plot = plot
//    }
//    def withOverlayImplicits = new OverlayImplicits {
//      protected val plot: Plot = plot
//    }
//  }
package object plot {

  implicit class RichPlot(
    protected val plot: Plot
  ) extends Axes.AxesImplicits
      with PlotLineImplicits
      with AnnotationImplicits
      with BackgroundImplicits
      with BorderPlotImplicits
      with FacetLabelImplicits
      with LabelImplicits
      with LegendImplicits
      with PadImplicits
      with PlotDefaultsImplicits
      with OverlayImplicits
}

package tadp
import tadp.parsers
import scalafx.scene.paint.Color
import tadp.internal.TADPDrawingAdapter
import tadp.parsers.{Grupo, dibujarColor, dibujarFigura, parserGrupo, parserRectangulo, parserTriangulo}

object TADPDrawingApp extends App {

    val triangulo = parserTriangulo("triangulo[250 @ 150, 150 @ 300, 350 @ 300]").get.elementoParseado

    val grupo = parserGrupo("grupo(triangulo[200 @ 50, 101 @ 335, 299 @ 335],circulo[200 @ 350, 100])").get.elementoParseado.asInstanceOf[Grupo]

  //TADPDrawingAdapter.forScreen({adapter => dibujarColor(new tadp.parsers.Color(255,0,0),dibujarFigura(grupo,adapter))})
  TADPDrawingAdapter.forScreen({adapter => dibujarFigura(grupo,dibujarColor(new tadp.parsers.Color(255,255,0),adapter))})

  //TADPDrawingAdapter.forScreen({adapter => dibujarFigura(triangulo,adapter)})

}
// Ejemplo de uso del drawing adapter:
//  TADPDrawingAdapter
//    .forScreen { adapter =>
//      adapter
//        .beginColor(Color.rgb(100, 100, 100))
//        .rectangle((0, 0), (400, 400))
//        .end()
//    }
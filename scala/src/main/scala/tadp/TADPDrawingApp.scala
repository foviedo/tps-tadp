package tadp
import tadp.parsers
import scalafx.scene.paint.Color
import tadp.internal.TADPDrawingAdapter
import tadp.parsers.{Grupo, parserEscala, parserFigura, parserGrupo, parserRectangulo, parserTraslacion, parserTriangulo}
import tadp.dibujar.dibujarFigura


object TADPDrawingApp extends App {

    val triangulo = parserTriangulo("triangulo[250 @ 150, 150 @ 300, 350 @ 300]").get.elementoParseado

    val grupo = parserGrupo("grupo(triangulo[200 @ 50, 101 @ 335, 299 @ 335],circulo[200 @ 350, 100])").get.elementoParseado.asInstanceOf[Grupo]

  val string = """traslacion[200.0, 50.0](
                 |	triangulo[0 @ 100, 200 @ 300, 150 @ 500]
                 |)""".stripMargin
  val figuraTrasladada = parserTraslacion(string).get.elementoParseado
  val figuraSinTrasladar = parserTriangulo("triangulo[0 @ 100, 200 @ 300, 150 @ 500]").get.elementoParseado
val unString = "escala[1.45, 1.45](\n grupo(\n   color[0.0, 0.0, 0.0](\n \trectangulo[0 @ 0, 400 @ 400]\n   ),\n   color[200.0, 70.0, 0.0](\n \trectangulo[0 @ 0, 180 @ 150]\n   ),\n   color[250.0, 250.0, 250.0](\n \tgrupo(\n   \trectangulo[186 @ 0, 400 @ 150],\n   \trectangulo[186 @ 159, 400 @ 240],\n   \trectangulo[0 @ 159, 180 @ 240],\n   \trectangulo[45 @ 248, 180 @ 400],\n   \trectangulo[310 @ 248, 400 @ 400],\n   \trectangulo[186 @ 385, 305 @ 400]\n\t)\n   ),\n   color[30.0, 50.0, 130.0](\n   \trectangulo[186 @ 248, 305 @ 380]\n   ),\n   color[250.0, 230.0, 0.0](\n   \trectangulo[0 @ 248, 40 @ 400]\n   )\n )\n)"

  val stringParseado = parserFigura(unString).get.elementoParseado
  //TADPDrawingAdapter.forScreen({adapter => dibujarColor(new tadp.parsers.Color(255,0,0),dibujarFigura(grupo,adapter))})

  //TADPDrawingAdapter.forScreen({adapter => dibujarFigura(triangulo,adapter)})

  TADPDrawingAdapter.forScreen({adapter => dibujarFigura(stringParseado,adapter)})

  //TADPDrawingAdapter.forScreen({adapter => dibujarFigura(figuraTrasladada,adapter)})

  // TADPDrawingAdapter.forScreen({adapter => dibujarFigura(grupo,dibujarColor(new tadp.parsers.Color(255,255,0),adapter))})

  //TADPDrawingAdapter.forScreen({adapter => adapter.circle((30,2),5)})

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
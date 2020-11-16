package tadp
import tadp.parsers
import scalafx.scene.paint.Color
import tadp.internal.TADPDrawingAdapter
import tadp.parsers.{Grupo, dibujarFigura, dibujarFigura2, dibujarGrupo, dibujarGrupo2, dibujarRectangulo, parserCirculo, parserGrupo, parserRectangulo, parserTriangulo}

object TADPDrawingApp extends App {

//  TADPDrawingAdapter.
//    forScreen{ adapter=>
//        adapter
//        .beginColor(Color.Aqua)
//        .rectangle((0,0),(100,100))
//        .end()
//        .beginColor(Color.Beige)
//        .triangle((0,100),(50,200),(100,100))
//        .end()
//    }

//    triangulo[250 @ 150, 150 @ 300, 350 @ 300],
  //  triangulo[150 @ 300, 50 @ 450, 250 @ 450],
    //triangulo[350 @ 300, 250 @ 450, 450 @ 450]
    val triangulo = parserTriangulo("triangulo[250 @ 150, 150 @ 300, 350 @ 300]").get.elementoParseado
    val triangulo1 = dibujarFigura(parserTriangulo("triangulo[250 @ 150, 150 @ 300, 350 @ 300]").get.elementoParseado)
    val triangulo2 = dibujarFigura(parserTriangulo("triangulo[150 @ 300, 50 @ 450, 250 @ 450]").get.elementoParseado)
    val triangulo3 = dibujarFigura(parserTriangulo("triangulo[350 @ 300, 250 @ 450, 450 @ 450]").get.elementoParseado)
 //   val funcion: String => Grupo = unString => parserGrupo(unString).get.elementoParseado.asInstanceOf[Grupo]
   val grupo = parserGrupo("grupo(triangulo[200 @ 50, 101 @ 335, 299 @ 335],circulo[200 @ 350, 100])").get.elementoParseado.asInstanceOf[Grupo]

  val grupoDibujable = dibujarGrupo2(grupo)

  val figuraDibujada = dibujarFigura2(triangulo,TADPDrawingAdapter)

    //TADPDrawingAdapter.forScreen(triangulo1.compose(triangulo2).compose(triangulo3))

      TADPDrawingAdapter.forScreen(grupoDibujable)
  //  TADPDrawingAdapter.forScreen(grupensio)


//  object dibujarRectangulo {
//    def apply(verticeSuperior: punto2D,verticeInferior: punto2D): TADPDrawingAdapter => TADPDrawingAdapter = {
//      adapter => adapter.rectangle((verticeInferior.x,verticeSuperior.y),(verticeInferior.x,verticeInferior.y))
//    }
//  }

TADPDrawingAdapter.forScreen
  {
    adapter => adapter.circle((2,3),5)
  }
//  TADPDrawingAdapter.forScreen { adapter =>
//    adapter.beginScale(1, 1)
//      .beginColor(Color.rgb(0, 0, 0))
//        .rectangle((0, 0), (400, 400))
//      .end()
//
//      .beginColor(Color.rgb(200, 70, 0))
//        .rectangle((0, 0), (180, 150))
//      .end()
//
//      .beginColor(Color.rgb(250, 250, 250))
//        .rectangle((186, 0), (400, 150))
//        .rectangle((186, 159), (400, 240))
//        .rectangle((0, 159), (180, 240))
//        .rectangle((45, 248), (180, 400))
//        .rectangle((310, 248), (400, 400))
//        .rectangle((186, 385), (305, 400))
//      .end()
//
//      .beginColor(Color.rgb(30, 50, 130))
//        .rectangle((186, 248), (305, 380))
//      .end()
//
//      .beginColor(Color.rgb(250, 230, 0))
//        .rectangle((0, 248), (40, 400))
//      .end()
//
//    .end()
//  }
}





// Ejemplo de uso del drawing adapter:
//  TADPDrawingAdapter
//    .forScreen { adapter =>
//      adapter
//        .beginColor(Color.rgb(100, 100, 100))
//        .rectangle((0, 0), (400, 400))
//        .end()
//    }

package tadp
import tadp.parsers
import scalafx.scene.paint.Color
import tadp.internal.TADPDrawingAdapter
import tadp.parsers.{parserRectangulo, parserTriangulo}


object TADPDrawingApp extends App {
  val trianguloParseado = parserTriangulo("triangulo[0 @ 100, 100 @ 100, 50 @ 200]")
  val triangulo = trianguloParseado.get.elementoParseado

  val rectanguloParseado = parserRectangulo("rectangulo[0 @ 0, 100 @ 100]")
  val rectangulo = rectanguloParseado.get.elementoParseado

//  TADPDrawingAdapter.
//    forScreen{ adapter=>
//      adapter
//        .beginColor(Color.rgb(100,100,100))
//        .rectangle(rectangulo.verticeInferior,rectangulo.verticeSuperior)
//        .triangle(triangulo.verticePrimero,triangulo.verticeSegundo,triangulo.verticeTercero)
//
//    }

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

  TADPDrawingAdapter.forScreen { adapter =>
    adapter.beginScale(1, 1)
      .beginColor(Color.rgb(0, 0, 0))
        .rectangle((0, 0), (400, 400))
      .end()

      .beginColor(Color.rgb(200, 70, 0))
        .rectangle((0, 0), (180, 150))
      .end()

      .beginColor(Color.rgb(250, 250, 250))
        .rectangle((186, 0), (400, 150))
        .rectangle((186, 159), (400, 240))
        .rectangle((0, 159), (180, 240))
        .rectangle((45, 248), (180, 400))
        .rectangle((310, 248), (400, 400))
        .rectangle((186, 385), (305, 400))
      .end()

      .beginColor(Color.rgb(30, 50, 130))
        .rectangle((186, 248), (305, 380))
      .end()

      .beginColor(Color.rgb(250, 230, 0))
        .rectangle((0, 248), (40, 400))
      .end()

    .end()
  }
}





// Ejemplo de uso del drawing adapter:
//  TADPDrawingAdapter
//    .forScreen { adapter =>
//      adapter
//        .beginColor(Color.rgb(100, 100, 100))
//        .rectangle((0, 0), (400, 400))
//        .end()
//    }

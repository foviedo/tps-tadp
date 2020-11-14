package tadp
import tadp.parsers
import scalafx.scene.paint.Color
import tadp.internal.TADPDrawingAdapter
import tadp.parsers.{dibujarFigura, dibujarRectangulo, parserCirculo, parserRectangulo, parserTriangulo}

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

    val rectanguloParseado = parserRectangulo("rectangulo[200 @ 100, 0 @ 0]")
    val rectangulo = rectanguloParseado.get.elementoParseado

    val trianguloParseado = parserTriangulo("triangulo[0 @ 100, 200 @ 300, 150 @ 500]")
    val triangulo = trianguloParseado.get.elementoParseado

    val circuloParseado = parserCirculo("circulo[100 @ 100, 50]")
    val circulo = circuloParseado.get.elementoParseado

    dibujarFigura (rectangulo)
    dibujarFigura (triangulo)
    dibujarFigura (circulo)


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

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


}



// Ejemplo de uso del drawing adapter:
//  TADPDrawingAdapter
//    .forScreen { adapter =>
//      adapter
//        .beginColor(Color.rgb(100, 100, 100))
//        .rectangle((0, 0), (400, 400))
//        .end()
//    }

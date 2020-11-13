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

  TADPDrawingAdapter.
    forScreen{ adapter=>
        adapter
        .beginColor(Color.Aqua)
        .rectangle((0,0),(100,100))
        .end()
        .beginColor(Color.Beige)
        .triangle((0,100),(50,200),(100,100))
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

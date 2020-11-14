package tadp.parsers

import org.scalatest.TryValues.convertTryToSuccessOrFailure
import org.scalatest.flatspec._
import org.scalatest.matchers._
import tadp.internal.TADPDrawingAdapter

import scala.util.Success


class ProjectSpec extends AnyFlatSpec with should.Matchers {


  it should "deberia retornar un Success b de banana" in {
    AnyChar("banana") shouldBe Success(ResultadoParser('b', "anana"))
  }

  it should "deberia retornar un failure de string vacio" in {
    AnyChar("").failure.exception shouldBe a[StringVacioException]
  }


  it should "deberia retornar un success con h de hola" in {
    char('h')("hola") shouldBe Success(ResultadoParser('h', "ola"))
  }

  it should "deberia darme un char exception porque le pase otra letra" in {
    char('z')("hola").failure.exception shouldBe a[CharException]
  }

  it should "deberia retornar un failure de string vacio, string" in {
    char('z')("").failure.exception shouldBe a[StringVacioException]

  }

  it should "deberia retornar un 7 re piola" in {
    IsDigit("7") shouldBe Success(ResultadoParser('7', ""))
  }
  it should "deberia sobrarme un 1" in {
    IsDigit("11") shouldBe Success(ResultadoParser('1',"1"))
  }
  it should "deberia romper si te tiro frula" in {
    IsDigit("bro momento").failure.exception shouldBe a[SatisfiesException]
  }


  it should "deberia romper porque no arranca con mundo" in {
    string("mundo")("hola mundo").failure.exception shouldBe a[StringException]
  }

  it should "deberia retornar hola porque empieza con hola" in {
    string("hola")("hola mundo") shouldBe Success(ResultadoParser("hola", " mundo"))

  }


  it should "deberia funcionar con números positivos" in {
    integer("27") shouldBe Success(ResultadoParser(27, ""))
  }
  it should "deberia funcionar con números negativos" in {
    integer("-27") shouldBe Success(ResultadoParser(-27, ""))
  }

  it should "deberia darme lo no parseado" in {
    integer("27foo") shouldBe Success(ResultadoParser(27, "foo"))
  }

  it should "deberia romper si le tiro frula" in {
    integer("bro momento").failure.exception shouldBe a [ConcatException]
  }

  it should "deberia funcionar con números positivos el double" in {
    double("27.5") shouldBe Success(ResultadoParser(27.5, ""))
  }
  it should "deberia funcionar con números negativos el double" in {
    double("-27.5") shouldBe Success(ResultadoParser(-27.5, ""))
  }

  it should "deberia romper si le mando un entero" in {
    double("5").failure.exception shouldBe a[ConcatException]
  }

  it should "deberia devloverme lo que no parseo en double" in {
    double("27.5foo") shouldBe Success(ResultadoParser(27.5, "foo"))
  }

  it should "deberia romper si le tiro frula el double" in {
    double("bro momento").failure.exception shouldBe a[ConcatException]
  }

  it should "deberia andar test <|> para primer parser" in {
    (char('a') <|> char('b'))("arbol") shouldBe Success(ResultadoParser('a', "rbol"))
  }

  it should "deberia andar test de <|> para segundo parser" in {
    (char('a') <|> char('b'))("bort") shouldBe Success(ResultadoParser('b', "ort"))
  }

  it should "deberia romper test de <|>" in {
    (char('a') <|> char('b'))("kahoot").failure.exception shouldBe a[CharException]
  }


  it should "el <> deberia andar" in {
    (string("hola") <> string("mundo"))("holamundo") shouldBe Success(ResultadoParser(("hola", "mundo"), ""))
  }

  it should "deberia retornar ConcatError porque el segundo tira un error" in {
    (string("hola") <> string("mundo"))("holachau").failure.exception shouldBe a[ConcatException]
  }

  it should "deberia retornar ConcatError porque el primero tira un error" in {
    (string("aaaa") <> string("mundo"))("holamundo").failure.exception shouldBe a[ConcatException]
  }

  it should "deberia retornar el string mundo el ~>" in {
    (string("hola") ~> string("mundo"))("holamundo") shouldBe Success(ResultadoParser("mundo", ""))
  }

  it should "deberia retornar ConcatException" in {
    (string("hola") ~> string("mundo"))("testosterona").failure.exception shouldBe a[ConcatException]
  }

  it should "test * que anda con anychar" in {
    AnyChar.*.apply("abcd") shouldBe Success(ResultadoParser(List('a','b','c','d'),""))
  }

  it should "test * que anda con char" in {
    char('a').*.apply("aacd") shouldBe Success(ResultadoParser(List('a','a'),"cd"))
  }
  it should "* deberia no parsear nada" in {
    char('a').*.apply("") shouldBe Success(ResultadoParser(List(),""))
  }

  it should "* " in {
    char('a').*.apply("bokita el + grande papa") shouldBe Success(ResultadoParser(List(),"bokita el + grande papa"))
  }



  it should "test + que anda con anychar" in {
    AnyChar.+.apply("abcd") shouldBe Success(ResultadoParser(List('a','b','c','d'),""))
  }
  it should "test + que anda con char" in {
    char('a').+.apply("aacd") shouldBe Success(ResultadoParser(List('a','a'),"cd"))
  }

  it should "+ deberia estallar porque no llega a parsear" in {
    char('a').+.apply("").failure.exception shouldBe a [SatisfiesException]
  }

  it should "test de map con resultado vacio" in {
    integer.map(2.*)("27") shouldBe Success(ResultadoParser(54,""))
  }

  it should "test de map con sobra" in {
    integer.map(2.*)("27foo") shouldBe Success(ResultadoParser(54,"foo"))
  }

  it should "satisfies anda" in {
    AnyChar.satisfies('a'.==)("anana") shouldBe Success(ResultadoParser('a',"nana"))
  }

  it should "satisfies no cumple condicion" in {
    AnyChar.satisfies('a'.==)("nana").failure.exception shouldBe a [SatisfiesException]
  }

  it should "limpiarString elimina \\n \\t \\s" in {
    limpiadorDeString("       h\tola -    c h\nau()@[]-~,,,  ") shouldBe "hola-chau()@[]-~,,,"
  }

  it should "satisfies no anda el parser original" in {
    AnyChar.satisfies('a'.==)("").failure.exception shouldBe a [StringVacioException]
  }

  it should "sepby que funciona" in {
    integer.sepBy(char('-'))("4356-1234-2") shouldBe Success(ResultadoParser(List(4356,1234,2),""))

  }
  it should "sepby con string" in {
    integer.sepBy(string(" @ ")) ("0 @ 100") shouldBe Success(ResultadoParser(List(0,100),""))
  }

  it should "deberia generar un rectangulo" in {
    parserRectangulo("rectangulo[0 @ 100, 200 @ 300]") shouldBe Success(ResultadoParser(Rectangulo(punto2D(0,100),punto2D(200,300)),""))

  }

  it should "deberia estallar por pasar un rectangulo con mas de dos coordenada" in {
    parserRectangulo("rectangulo[0 @ 100, 200 @ 300, 300 @ 200]").failure.exception shouldBe a [ConcatException]

  }

  it should "deberia estallar por pasar un rectangulo con menos de dos coordenada" in {
    parserRectangulo("rectangulo[0 @ 100]").failure.exception shouldBe a [ConcatException]

  }

  it should "deberia generar un triangulo" in {
    parserTriangulo("triangulo[0 @ 100, 200 @ 300, 150 @ 500]") shouldBe Success(ResultadoParser(Triangulo(punto2D(0,100), punto2D(200,300), punto2D(150,500)),""))


  }

  it should "deberia generar un circulo" in {
    parserCirculo("circulo[100 @ 100, 50]") shouldBe Success(ResultadoParser(Circulo(punto2D(100,100),50),""))
  }

  it should "Parser figura deberia parsear cualquier figura" in {
    parserRectangulo("rectangulo[0 @ 100, 200 @ 300]") shouldBe Success(ResultadoParser(Rectangulo(punto2D(0,100),punto2D(200,300)),""))
    parserTriangulo("triangulo[0 @ 100, 200 @ 300, 150 @ 500]") shouldBe Success(ResultadoParser(Triangulo(punto2D(0,100), punto2D(200,300), punto2D(150,500)),""))
    parserCirculo("circulo[100 @ 100, 50]") shouldBe Success(ResultadoParser(Circulo(punto2D(100,100),50),""))
  }

  it should "Parser grupo deberia parsear un grupo" in {
    val unString = "grupo(\n      triangulo[200 @ 50, 101 @ 335, 299 @ 335],circulo[200 @ 350, 100])"
    parserGrupo (unString) shouldBe Success(ResultadoParser(Grupo(List(
      Triangulo(punto2D(200,50), punto2D(101,335), punto2D(299,335)),
      Circulo(punto2D(200,350),100)))
      ,""))
  }

  it should "Parser grupo deberia parsear grupos anidados" in {
    val unString = " grupo(grupo( triangulo[250 @ 150, 150 @ 300, 350 @ 300],  triangulo[150 @ 300, 50 @ 450, 250 @ 450], triangulo[350 @ 300, 250 @ 450, 450 @ 450]  ),grupo(      rectangulo[460 @ 90, 470 @ 100], rectangulo[430 @ 210, 500 @ 220], rectangulo[430 @ 210, 440 @ 230], rectangulo[490 @ 210, 500 @ 230], rectangulo[450 @ 100, 480 @ 260] )) "

    parserGrupo (unString) shouldBe Success(ResultadoParser(Grupo(List(
      Grupo(List(
        Triangulo(punto2D(250, 150), punto2D(150, 300), punto2D(350, 300)),
        Triangulo(punto2D(150, 300), punto2D(50, 450), punto2D(250, 450)),
        Triangulo(punto2D(350, 300), punto2D(250, 450), punto2D(450, 450))
      )),
      Grupo(List(
        Rectangulo(punto2D(460, 90), punto2D(470, 100)),
        Rectangulo(punto2D(430, 210), punto2D(500, 220)),
        Rectangulo(punto2D(430, 210), punto2D(440, 230)),
        Rectangulo(punto2D(490, 210), punto2D(500, 230)),
        Rectangulo(punto2D(450, 100), punto2D(480, 260))
      ))
    ))
      ,""))

  }

  it should "Parser grupo deberia parsear grupos anidados1" in {
    val unString = " grupo(grupo( triangulo[250 @ 150, 150 @ 300, 350 @ 300] ),grupo(      rectangulo[460 @ 90, 470 @ 100] )) "

    parserGrupo (unString) shouldBe Success(ResultadoParser(Grupo(List(
      Grupo(List(
        Triangulo(punto2D(250, 150), punto2D(150, 300), punto2D(350, 300))
      )),
      Grupo(List(
        Rectangulo(punto2D(460, 90), punto2D(470, 100))
      ))
    ))
      ,""))

  }

  it should "parser de color" in {
    val string = "color[60.0, 150.0, 200.0](\n    grupo(\n   \t triangulo[200 @ 50, 101 @ 335, 299 @ 335],\n   \t circulo[200 @ 350, 100]\n    )\n)"
    parserColor(string) shouldBe Success(ResultadoParser(FiguraTransformada(Grupo(List(Triangulo(punto2D(200,50),punto2D(101,335),punto2D(299,335)),Circulo(punto2D(200,350),100))),Color(60,150,200)),""))
  }

  it should "figura simple de doble color" in {
    val string = "color[60.0, 150.0, 200.0](color[1.0,1.0,1.0](triangulo[0 @ 100, 200 @ 300, 150 @ 500]))"
    simplificador(parserColor(string).get.elementoParseado) shouldBe FiguraTransformada(Triangulo(punto2D(0, 100), punto2D(200, 300), punto2D(150, 500)), Color(1, 1, 1))
  }
    it should "parser de escala" in {
    val string = """escala[2.5, 1.0](
                   |	rectangulo[0 @ 100, 200 @ 300]
                   |)""".stripMargin
    parserEscala(string) shouldBe Success(ResultadoParser(FiguraTransformada(Rectangulo(punto2D(0, 100), punto2D(200, 300)), Escala(2.5, 1.0)), ""))
  }

  it should "parser de rotacion" in {
    val string = """rotacion[45.0](
                   |	rectangulo[300 @ 0, 500 @ 200]
                   |)""".stripMargin
    parserRotacion(string) shouldBe Success(ResultadoParser(FiguraTransformada(Rectangulo(punto2D(300, 0), punto2D(500, 200)), Rotacion(45)), ""))
  }

  it should "parser de traslación" in {
    val string = """traslacion[200.0, 50.0](
                   |	triangulo[0 @ 100, 200 @ 300, 150 @ 500]
                   |)""".stripMargin
    parserTraslacion(string) shouldBe Success(ResultadoParser(FiguraTransformada(Triangulo(punto2D(0, 100), punto2D(200, 300), punto2D(150, 500)), Traslacion(200, 50)), ""))
  }

  it should "dasda" in{
    val circuloParseado = parserCirculo("circulo[200 @ 350, 100]")
    val circulo = circuloParseado.get.elementoParseado

    val trianguloParseado = parserTriangulo("triangulo[150 @ 300, 50 @ 450, 250 @ 450]")
    val triangulo = trianguloParseado.get.elementoParseado

    val rectanguloParseado = parserRectangulo("rectangulo[0 @ 100, 200 @ 300]")
    val rectangulo = rectanguloParseado.get.elementoParseado


    val figurita = dibujarFigura(circulo)
    val otraFigurita = dibujarFigura(triangulo)
    val terceraFigurita = dibujarFigura(rectangulo)

 //este test es solo para instanciar objetos y ver cómo tipan.
  }
//  ███████╗██╗███╗   ███╗██████╗ ██╗     ██╗███████╗██╗ ██████╗ █████╗ ██████╗  ██████╗ ██████╗
//  ██╔════╝██║████╗ ████║██╔══██╗██║     ██║██╔════╝██║██╔════╝██╔══██╗██╔══██╗██╔═══██╗██╔══██╗
//  ███████╗██║██╔████╔██║██████╔╝██║     ██║█████╗  ██║██║     ███████║██║  ██║██║   ██║██████╔╝
//  ╚════██║██║██║╚██╔╝██║██╔═══╝ ██║     ██║██╔══╝  ██║██║     ██╔══██║██║  ██║██║   ██║██╔══██╗
//  ███████║██║██║ ╚═╝ ██║██║     ███████╗██║██║     ██║╚██████╗██║  ██║██████╔╝╚██████╔╝██║  ██║
//  ╚══════╝╚═╝╚═╝     ╚═╝╚═╝     ╚══════╝╚═╝╚═╝     ╚═╝ ╚═════╝╚═╝  ╚═╝╚═════╝  ╚═════╝ ╚═╝  ╚═╝

  it should "figura simple de doble color" in {
    val string = "color[60.0, 150.0, 200.0](color[1.0,1.0,1.0](triangulo[0 @ 100, 200 @ 300, 150 @ 500]))"
    simplificador(parserColor(string).get.elementoParseado) shouldBe FiguraTransformada(Triangulo(punto2D(0, 100), punto2D(200, 300), punto2D(150, 500)), Color(1, 1, 1))
  }

  it should "simplificacion 1" in {
    /*  - Si tenemos una transformación de color aplicada a otra transformacion de color, debería quedar la de adentro.
        - Si tenemos una transformación aplicada a todos los hijos de un grupo, eso debería convertirse en una
        transformación aplicada al grupo.
     */
    val malo = """grupo(
                   |	color[200, 200, 200](rectangulo[100 @ 100, 200 @ 200]),
                   |	color[200, 200, 200](circulo[100 @ 300, 150])
                   |)""".stripMargin

    val bueno = """color[200, 200, 200](
                  |   grupo(
                  |	rectangulo[100 @ 100, 200 @ 200],
                  |	circulo[100 @ 300, 150]
                  |   )
                  |)""".stripMargin

    simplificador(parserGrupo(malo).get.elementoParseado) shouldBe simplificador(parserColor(bueno).get.elementoParseado)
  }

//  it should "deberia retornar el string hola el <~" in {
//    (string("hola") <~ string("mundo"))("holamundo") shouldBe Success(ResultadoParser("hola", ""))
//  }
//
//  it should "<~ deberia retornar ConcatException" in {
//    (string("hola") <~ string("mundo"))("testosterona").failure.exception shouldBe a[ConcatException]
//  }

}



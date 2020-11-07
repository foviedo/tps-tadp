package tadp.parsers

import org.scalatest.TryValues.convertTryToSuccessOrFailure
import org.scalatest.flatspec._
import org.scalatest.matchers._
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
    parserRectangulo("rectangulo[0 @ 100, 200 @ 300]") shouldBe Success(ResultadoParser(Rectangulo((0,100),(200,300)),""))
  //  parserRectangulo("rectangulo[0 @ 100, 200 @ 300]").get.elementoParseado.verticeSuperior._1 shouldBe 0
   // parserRectangulo("rectangulo[0 @ 100, 200 @ 300]").get.elementoParseado.verticeSuperior._2 shouldBe 100
   // parserRectangulo("rectangulo[0 @ 100, 200 @ 300]").get.elementoParseado.verticeInferior._1 shouldBe 200
   // parserRectangulo("rectangulo[0 @ 100, 200 @ 300]").get.elementoParseado.verticeInferior._2 shouldBe 300
  }

  it should "deberia generar un triangulo" in {
    parserTriangulo("triangulo[0 @ 100, 200 @ 300, 150 @ 500]") shouldBe Success(ResultadoParser(Triangulo((0,100),(200,300),(150,500)),""))
  }

//  it should "deberia retornar el string hola el <~" in {
//    (string("hola") <~ string("mundo"))("holamundo") shouldBe Success(ResultadoParser("hola", ""))
//  }
//
//  it should "<~ deberia retornar ConcatException" in {
//    (string("hola") <~ string("mundo"))("testosterona").failure.exception shouldBe a[ConcatException]
//  }

}



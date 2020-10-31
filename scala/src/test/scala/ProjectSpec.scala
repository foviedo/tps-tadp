package tadp.parsers

import org.scalatest.TryValues.convertTryToSuccessOrFailure
//import org.scalatest.{FreeSpec, Matchers}
import org.scalatest.flatspec._
import org.scalatest.matchers._
import scala.util.{Failure, Success, Try}


class ProjectSpec extends AnyFlatSpec with should.Matchers {

  //shouldBe Success(ResultadoParser('h',"ola")) esto es la posta!

  it should "deberia retornar un Success b de banana" in {
    AnyChar.aplicar("banana") shouldBe Success(ResultadoParser('b', "anana"))
  }

  it should "deberia retornar un failure de string vacio" in {
    AnyChar.aplicar("").failure.exception shouldBe a[StringVacioException]
  }


  it should "deberia retornar un success con h de hola" in {
    char('h').aplicar("hola") shouldBe Success(ResultadoParser('h', "ola"))
  }

  it should "deberia darme un char exception porque le pase otra letra" in {
    char('z').aplicar("hola").failure.exception shouldBe a[CharException]
  }

  it should "deberia retornar un failure de string vacio, string" in {
    char('z').aplicar("").failure.exception shouldBe a[StringVacioException]

  }

  it should "deberia retornar un 7 re piola" in {
    IsDigit.aplicar("7") shouldBe Success(ResultadoParser('7', ""))
  }
  it should "deberia romper con un numero mas grande" in {
    IsDigit.aplicar("11").failure.exception shouldBe a[IsDigitException]
  }
  it should "deberia romper si te tiro frula" in {
    IsDigit.aplicar("bro momento").failure.exception shouldBe a[java.lang.NumberFormatException]
  }


  it should "deberia romper porque no arranca con mundo" in {
    string("mundo").aplicar("hola mundo").failure.exception shouldBe a[StringException]
  }

  it should "deberia retornar hola porque empieza con hola" in {
    string("hola").aplicar("hola mundo") shouldBe Success(ResultadoParser("hola", " mundo"))

  }


  it should "deberia funcionar con números positivos" in {
    integer.aplicar("27") shouldBe Success(ResultadoParser(27, ""))
  }
  it should "deberia funcionar con números negativos" in {
    integer.aplicar("-27") shouldBe Success(ResultadoParser(-27, ""))
  }

  it should "deberia darme lo no parseado" in {
    integer.aplicar("27foo") shouldBe Success(ResultadoParser(27, "foo"))
  }

  it should "deberia romper si le tiro frula" in {
    integer.aplicar("bro momento").failure.exception shouldBe a [java.lang.NumberFormatException]
  }

  it should "deberia funcionar con números positivos el double" in {
    double.aplicar("27.5") shouldBe Success(ResultadoParser(27.5, ""))
  }
  it should "deberia funcionar con números negativos el double" in {
    double.aplicar("-27.5") shouldBe Success(ResultadoParser(-27.5, ""))
  }

  it should "deberia andar aunque le mande un entero" in {
    double.aplicar("5") shouldBe Success(ResultadoParser(5.0, ""))
  }

  it should "deberia devloverme lo que no parseo en double" in {
    double.aplicar("27.5foo") shouldBe Success(ResultadoParser(27.5, "foo"))
  }

  it should "deberia romper si le tiro frula el double" in {
    double.aplicar("bro momento").failure.exception shouldBe a[java.lang.NumberFormatException]
  }

  it should "deberia andar test <|> para primer parser" in {
    (char('a') <|> char('b')).aplicar("arbol") shouldBe Success(ResultadoParser('a', "rbol"))
  }

  it should "deberia andar test de <|> para segundo parser" in {
    (char('a') <|> char('b')).aplicar("bort") shouldBe Success(ResultadoParser('b', "ort"))
  }

  it should "deberia romper test de <|>" in {
    (char('a') <|> char('b')).aplicar("kahoot").failure.exception shouldBe a[CharException]
  }


  it should "el <> deberia andar" in {
    (string("hola") <> string("mundo")).aplicar("holamundo") shouldBe Success(ResultadoParser(("hola", "mundo"), ""))
  }
  it should "deberia retornar ConcatError porque el segundo tira un error" in {
    (string("hola") <> string("mundo")).aplicar("holachau").failure.exception shouldBe a[ConcatException]
  }

  it should "deberia retornar ConcatError porque el primero tira un error" in {
    (string("aaaa") <> string("mundo")).aplicar("holamundo").failure.exception shouldBe a[ConcatException]
  }

  it should "deberia retornar el string mundo el ~>" in {
    (string("hola") ~> string("mundo")).aplicar("holamundo") shouldBe Success(ResultadoParser("mundo", ""))
  }

  it should "deberia retornar RightMostException" in {
    (string("hola") ~> string("mundo")).aplicar("testosterona").failure.exception shouldBe a[RightMostException]
  }

  it should "test * que anda con anychar" in {
    AnyChar.*.aplicar("abcd") shouldBe Success(ResultadoParser((List('a','b','c','d')),""))
  }

  it should "test * que anda con char" in {
    char('a').*.aplicar("aacd") shouldBe Success(ResultadoParser((List('a','a')),"cd"))
  }
  it should "* deberia no parsear nada" in {
    char('a').*.aplicar("") shouldBe Success(ResultadoParser((List()),""))
  }

  it should "test + que anda con anychar" in {
    AnyChar.+.aplicar("abcd") shouldBe Success(ResultadoParser((List('a','b','c','d')),""))
  }
  it should "test + que anda con char" in {
    char('a').+.aplicar("aacd") shouldBe Success(ResultadoParser((List('a','a')),"cd"))
  }

  it should "+ deberia estallar porque no llega a parsear" in {
    char('a').+.aplicar("").failure.exception shouldBe a [ClausuraPositivaException]
  }

  it should "test de map con resultado vacio" in {
    integer.map(2.*).aplicar("27") shouldBe Success(ResultadoParser(54,""))
  }

  it should "test de map con sobra" in {
    integer.map(2.*).aplicar("27foo") shouldBe Success(ResultadoParser(54,"foo"))
  }

  it should "satisfies anda" in {
    AnyChar.satisfies('a'.==).aplicar("anana") shouldBe Success(ResultadoParser('a',"nana"))
  }

  it should "satisfies no cumple condicion" in {
    AnyChar.satisfies('a'.==).aplicar("nana").failure.exception shouldBe a [SatisfiesException]
  }

  it should "satisfies no anda el parser original" in {
    AnyChar.satisfies('a'.==).aplicar("").failure.exception shouldBe a [StringVacioException]
  }


}



package tadp
import org.scalatest.TryValues.convertTryToSuccessOrFailure
//import org.scalatest.{FreeSpec, Matchers}
import org.scalatest.flatspec._
import org.scalatest.matchers._
import scala.util.{Failure, Success, Try}


class ProjectSpec extends AnyFlatSpec with should.Matchers{


    it should "deberia retornar un Success b de banana" in {
      AnyChar.aplicar("banana").success.value == 'b'
    }
    it should "deberia retornar un failure de string vacio" in {
      AnyChar.aplicar("").failure.exception shouldBe a[StringVacioException]

    }


    it should "deberia retornar un success con h de hola" in {
      char('h').aplicar("hola") shouldBe Success(ResultadoParser('h'))
    }
    it should "deberia darme un char exception porque le pase otra letra" in {
      char('z').aplicar("hola").failure.exception shouldBe a [CharException]
    }
    it should "deberia retornar un failure de string vacio, string" in {
      char('z').aplicar("").failure.exception shouldBe a [StringVacioException]

    }

    it should "deberia retornar un 7 re piola" in {
      IsDigit.aplicar("7") shouldBe Success(ResultadoParser('7'))
    }
    it should "deberia romper con un numero mas grande" in {
      IsDigit.aplicar("11").failure.exception shouldBe a [IsDigitException]
    }
    it should "deberia romper si te tiro frula" in {
      IsDigit.aplicar("bro momento").failure.exception shouldBe a [java.lang.NumberFormatException]
    }


    it should "deberia romper porque no arranca con mundo" in {
      string("mundo").aplicar("hola mundo").failure.exception shouldBe a [StringException]
    }

    it should "deberia retornar hola porque empieza con hola" in {
      string("hola").aplicar("hola mundo") shouldBe Success(ResultadoParser("hola"))

    }


    it should "deberia funcionar con números positivos" in {
      integer.aplicar("27") shouldBe Success(ResultadoParser(27))
    }
    it should  "deberia funcionar con números negativos" in {
      integer.aplicar("-27") shouldBe Success(ResultadoParser(-27))
    }
    it should "deberia romper si le tiro frula" in {
      integer.aplicar("bro momento").failure.exception shouldBe a [java.lang.NumberFormatException]
    }

  it should "deberia funcionar con números positivos el double" in {
    double.aplicar("27.5") shouldBe Success(ResultadoParser(27.5))
  }
  it should  "deberia funcionar con números negativos el double" in {
    double.aplicar("-27.5") shouldBe Success(ResultadoParser(-27.5))
  }

  it should "deberia andar aunque le mande un entero" in {
    double.aplicar("5") shouldBe Success(ResultadoParser(5.0))
  }

  it should "deberia romper si le tiro frula el double" in {
    double.aplicar("bro momento").failure.exception shouldBe a [java.lang.NumberFormatException]
  }



}



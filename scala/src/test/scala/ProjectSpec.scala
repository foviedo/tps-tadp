import org.scalatest.TryValues.convertTryToSuccessOrFailure
import org.scalatest.{FreeSpec, Matchers}

import scala.util.{Failure, Success, Try}


class ProjectSpec extends FreeSpec with Matchers {

  "Este proyecto" - {

    "cuando está correctamente configurado" - {
      "debería resolver las dependencias y pasar este test" in {
        Prueba.materia shouldBe "tadp"
      }
    }
  }

  "anychar" - {
    "deberia retornar un Success b de banana" in {
      AnyChar.aplicar("banana").success.value == 'b'
    }
    "deberia retornar un failure de string vacio" in {
      AnyChar.aplicar("").failure.exception shouldBe a[StringVacioException]

    }
  }

  "char" - {
    "deberia retornar un success con h de hola" in {
      char('h').aplicar("hola") shouldBe Success(ResultadoParser('h'))
    }
    "deberia darme un char exception porque le pase otra letra" in {
      char('z').aplicar("hola").failure.exception shouldBe a [CharException]
    }
    "deberia retornar un failure de string vacio" in {
      char('z').aplicar("").failure.exception shouldBe a [StringVacioException]

    }
  }
  "isDigit" - {
    "deberia retornar un 7 re piola" in {
      IsDigit.aplicar("7") shouldBe Success(ResultadoParser('7'))
    }
    "deberia romper con un numero mas grande" in {
      IsDigit.aplicar("11").failure.exception shouldBe a [IsDigitException]
    }
    "deberia romper si te tiro frula" in {
      IsDigit.aplicar("bro momento").failure.exception shouldBe a [java.lang.NumberFormatException]
    }

  }
  "string" - {
    "deberia romper porque no arranca con mundo" in {
      string("mundo").aplicar("hola mundo").failure.exception shouldBe a [StringException]
    }

    "deberia retornar hola porque empieza con hola" in {
      string("hola").aplicar("hola mundo") shouldBe Success(ResultadoParser("hola"))

    }
  }

  "integer" - {
    "deberia funcionar con números positivos" in {
      integer.aplicar("27") shouldBe Success(ResultadoParser(27))
    }
    "deberia funcionar con números negativos" in {
      integer.aplicar("-27") shouldBe Success(ResultadoParser(-27))
    }
    "deberia romper si le tiro frula" in {
      integer.aplicar("bro momento").failure.exception shouldBe a [java.lang.NumberFormatException]
    }
  }


}



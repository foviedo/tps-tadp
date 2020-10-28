package tadp.internal
import scala.util.{Failure, Success, Try}

case object Parser {
  def anyChar(unString: String): Try[ResultadoParser] = {
      if(unString.isEmpty) throw new StringVacioException
      Try(ResultadoParser(unString.head))
    }

  def char(un_string: String, first:Char): Try[ResultadoParser] =  {
    if(un_string.head != first) throw new CharException
    Try(ResultadoParser(first))
  }

}

class StringVacioException extends Exception
class CharException extends Exception
case class ResultadoParser(elementoParseado: Char)

//def dividir(dividendo: Float, divisor: Float): Try[Float] = {
//  Try {
//  if(divisor == 0) throw new RuntimeException("Division por cero")
//  dividendo / divisor
//}
//}
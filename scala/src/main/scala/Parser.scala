package tadp
import scala.util.{Failure, Success, Try}

abstract class Parser[T] {
  def aplicar(entrada:String): Try[ResultadoParser[T]]

}

case object AnyChar extends Parser[Char] {
  def aplicar(unString: String): Try[ResultadoParser[Char]] = unString.toList match {
    case List() => Failure (new StringVacioException)
    case head :: tail => Success (new ResultadoParser(head))
  }
}

case object IsDigit extends Parser[Char] {
  def aplicar(digitoEnString:String): Try[ResultadoParser[Char]] =
    Try {
      if (digitoEnString.toInt <= 9 && digitoEnString.toInt >= 0)
        new ResultadoParser[Char](digitoEnString.charAt(0))
      else
        throw new IsDigitException
    }
}

case class char(inicio: Char) extends Parser[Char]{
  def aplicar(unString: String): Try[ResultadoParser[Char]] = AnyChar.aplicar(unString) match {
    case Failure(_) =>  Failure(new StringVacioException)
    case Success(ResultadoParser(unChar)) if unChar!= inicio => Failure(new CharException)
    case Success(ResultadoParser(unChar)) if unChar == inicio => Success(new ResultadoParser(inicio))
  }
}

case class string(inicio: String) extends Parser[String]{
  def aplicar(stringOriginal: String): Try[ResultadoParser[String]] =
    if(stringOriginal.startsWith(inicio)) new Success(ResultadoParser[String](inicio)) else Failure(new StringException)
}

case object integer extends Parser[Int]{
  def aplicar(entero:String): Try[ResultadoParser[Int]]=
    Try {
  ResultadoParser(entero.toInt)
  }
}

case object double extends Parser[Double]{
  def aplicar(unDouble:String): Try[ResultadoParser[Double]]=
    Try {
      ResultadoParser(unDouble.toDouble)
    }
}


class StringVacioException extends Exception
class CharException extends Exception
class IsDigitException extends Exception
class StringException extends Exception

case class ResultadoParser[+T](elementoParseado: T)

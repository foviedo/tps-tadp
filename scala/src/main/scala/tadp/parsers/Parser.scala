package tadp.parsers
import scala.util.{Failure, Success, Try}

abstract class Parser[T] {
  def aplicar(entrada:String): Try[ResultadoParser[T]]

  def <|> (otroParser:Parser[T]):Parser[T]={
    (new <|>).combinar(this,otroParser)
  }
  def <>[K] (otroParser:Parser[K]):Parser[(T,K)]= {
    (new <>).combinar(this,otroParser)
  }

  //def <> (otroParser:Parser[T]):Parser[(T,K)] = new Parser(T,K)
}

case object AnyChar extends Parser[Char] {
  def aplicar(unString: String): Try[ResultadoParser[Char]] = unString.toList match {
    case List() => Failure (new StringVacioException)
    case head :: tail => Success (new ResultadoParser(head,tail.mkString("")))
  }
}

case object IsDigit extends Parser[Char] {
  def aplicar(digitoEnString:String): Try[ResultadoParser[Char]] =
    Try {
      if (digitoEnString.toInt <= 9 && digitoEnString.toInt >= 0)
        new ResultadoParser[Char](digitoEnString.charAt(0),"")
      else
        throw new IsDigitException
    }
}

case class char(inicio: Char) extends Parser[Char]{
  def aplicar(unString: String): Try[ResultadoParser[Char]] = AnyChar.aplicar(unString) match {
    case Failure(_) =>  Failure(new StringVacioException)
    case Success(ResultadoParser(unChar,resto)) if unChar!= inicio => Failure(new CharException)
    case Success(ResultadoParser(unChar,resto)) if unChar == inicio => Success(new ResultadoParser(inicio,resto))
  }
}

case class string(inicio: String) extends Parser[String]{
  def aplicar(stringOriginal: String): Try[ResultadoParser[String]] =
    if(stringOriginal.startsWith(inicio)) new Success(ResultadoParser[String](inicio,stringOriginal.slice(inicio.length(),stringOriginal.length()))) else Failure(new StringException)
}

case object integer extends Parser[Int]{
  def aplicar(entero:String): Try[ResultadoParser[Int]]=
    Try {
  ResultadoParser(entero.toInt,"")
  }
}

case object double extends Parser[Double]{
  def aplicar(unDouble:String): Try[ResultadoParser[Double]]=
    Try {
      ResultadoParser(unDouble.toDouble,"")
    }
}



class <|>[T] {
  def combinar(unParser:Parser[T],otroParser:Parser[T]): Parser[T] ={
    new Parser[T]{
      def aplicar(input:String):Try[ResultadoParser[T]]={
        unParser.aplicar(input) match {
          case Success(ResultadoParser(head,tail)) => Success(ResultadoParser(head,tail))
          case Failure(_) => otroParser.aplicar(input)
        }
      }
    }
  }

}



class <>[T,S]{
  def combinar(unParser:Parser[T],otroParser:Parser[S]): Parser[(T,S)] ={
    new Parser[(T,S)] {
      override def aplicar(entrada: String): Try[ResultadoParser[(T,S)]] = {
          val resultadoPrimerParser = unParser.aplicar(entrada)
          if(resultadoPrimerParser.isFailure){
            return Failure(new ConcatException)
          }
          otroParser.aplicar(unParser.aplicar(entrada).get.loQueSobra.toString) match {
            case Success(ResultadoParser(resultadoSegundoParser,loQueSobra)) =>
              Success(ResultadoParser((resultadoPrimerParser.get.elementoParseado,resultadoSegundoParser),loQueSobra))
            case Failure(_) => Failure(new ConcatException)
          }
      }
    }
  }
}


case class ResultadoParser[T](elementoParseado: T, loQueSobra: String)

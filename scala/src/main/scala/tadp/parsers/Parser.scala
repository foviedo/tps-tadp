package tadp.parsers
import scala.util.{Failure, Success, Try}

abstract class Parser[T] {
  def apply(entrada:String): Try[ResultadoParser[T]]

  def <|> (otroParser:Parser[T]):Parser[T]={
    (new <|>).combinar(this,otroParser)
  }
  def <>[K] (otroParser:Parser[K]):Parser[(T,K)]= {
    (new <>).combinar(this,otroParser)
  }

  def ~>[K] (otroParser:Parser[K]):Parser[K] = {
    (new ~>).combinar(this,otroParser)
  }

  def <~[K] (otroParser:Parser[K]):Parser[K] = {
    (new ~>).combinar(this,otroParser)
  }

  def * ():Parser[List[T]] = {
    val yo = this
    new Parser[List[T]] {
      override def apply(entrada: String): Try[ResultadoParser[List[T]]] = {
        Try {
          armameLaLista(yo,entrada)
        }

      }
      def armameLaLista[K](parser:Parser[T],entrada:String): ResultadoParser[List[T]] ={
        val resultado = parser.apply(entrada) match {
          case Success(ResultadoParser(parseado,sobranteAParsear)) =>
            val ResultadoParser(parseadoNuevo,sobranteNuevo) = armameLaLista(parser, sobranteAParsear)
            (parseado:: parseadoNuevo,sobranteNuevo)
          case Failure(_) => (List(),entrada)
        }
        return ResultadoParser(resultado._1,resultado._2)
      }
    }
  }

  def +(): Parser[List[T]] = {
    val yo= this
    new Parser[List[T]] {
      override def apply(entrada: String): Try[ResultadoParser[List[T]]] = {
        return yo.*().satisfies(this.noEstaVacio).apply(entrada)


      }
      def noEstaVacio(entrada: List[T]): Boolean ={
        !(entrada.isEmpty)
      }
    }

  }


  def map[S](funcion: T=>S):Parser[S] ={
    val yo= this
    new Parser[S] {
      override def apply(entrada: String): Try[ResultadoParser[S]] = {
        yo.apply(entrada) match {
          case Success(ResultadoParser(elementoParseado,loQueSobra)) => Success(ResultadoParser(funcion(elementoParseado),loQueSobra))
          case Failure(fail) => Failure(fail)
        }
      /*  Try{
          val aplicado = yo.aplicar(entrada).get
          ResultadoParser(funcion(aplicado.elementoParseado),aplicado.loQueSobra)
        }*/
      }
    }
  }

  def satisfies(funcion: T=>Boolean):Parser[T] ={
    val yo=this
    new Parser[T] {
      override def apply(entrada: String): Try[ResultadoParser[T]] = {
        yo.apply(entrada) match {
          case Success(ResultadoParser(elementoParseado,loQueSobra)) if funcion(elementoParseado) => Success(ResultadoParser(elementoParseado,loQueSobra))
          case Success(_) => Failure(new SatisfiesException)
          case Failure(fallo) => Failure(fallo)
        }
      }
    }
  }

  def opt():Parser[Option[T]] = {
    val yo=this
    new Parser[Option[T]] {
      override def apply(entrada: String): Try[ResultadoParser[Option[T]]] = {
        yo.apply(entrada) match {
          case Success(ResultadoParser(parseado,loQueSobra)) => Success(ResultadoParser(Some(parseado),loQueSobra))
          case Failure(exception) => Success(ResultadoParser(None,entrada))
        }
      }
    }
  }


}


case object AnyChar extends Parser[Char] {
  def apply(unString: String): Try[ResultadoParser[Char]] = unString.toList match {
    case List() => Failure (new StringVacioException)
    case head :: tail => Success (new ResultadoParser(head,tail.mkString("")))
  }
}

case object IsDigit extends Parser[Char] {
  def apply(entrada:String): Try[ResultadoParser[Char]] =
      AnyChar.satisfies(x => x.isDigit)(entrada)

}

case class char(inicio: Char) extends Parser[Char]{
  def apply(unString: String): Try[ResultadoParser[Char]] = AnyChar.apply(unString) match {
    case Failure(_) =>  Failure(new StringVacioException)
    case Success(ResultadoParser(unChar,resto)) if unChar!= inicio => Failure(new CharException)
    case Success(ResultadoParser(unChar,resto)) if unChar == inicio => Success(new ResultadoParser(inicio,resto))
  }
}

case class string(inicio: String) extends Parser[String]{
  def apply(stringOriginal: String): Try[ResultadoParser[String]] =
    if(stringOriginal.startsWith(inicio)) new Success(ResultadoParser[String](inicio,stringOriginal.slice(inicio.length(),stringOriginal.length()))) else Failure(new StringException)
}

case class parserSigno(elSigno: String) extends Parser[String]{
  def apply(inicio: String): Try[ResultadoParser[String]] = {
    string(elSigno)(inicio) match {
      case Success(elem) => Success(elem)
      case Failure(_) => string("")(inicio)
    }
  }
}

case object parserNumero extends Parser[String]{
  def apply(numero: String): Try[ResultadoParser[String]] = {
    IsDigit.+().map(lista => lista.mkString("")).apply(numero)

  }
}

case object integer extends Parser[Int]{
  def apply(entero:String): Try[ResultadoParser[Int]]={
       (parserSigno("-") <> parserNumero).map(tupla => (tupla._1 + tupla._2).toInt).apply(entero)

  }


}

case object double extends Parser[Double]{
  def apply(unDouble:String): Try[ResultadoParser[Double]]= {
    (((parserSigno("-") <> parserNumero) <> parserSigno(".")) <> parserNumero).map(tuplas =>
      (tuplas._1._1._1+tuplas._1._1._2 + tuplas._1._2 + tuplas._2).toDouble)(unDouble)
  }

}



class <|>[T] {
  def combinar(unParser:Parser[T],otroParser:Parser[T]): Parser[T] ={
    new Parser[T]{
      def apply(input:String):Try[ResultadoParser[T]]={
        unParser.apply(input) match {
          case Success(ResultadoParser(head,tail)) => Success(ResultadoParser(head,tail))
          case Failure(_) => otroParser.apply(input)
        }
      }
    }
  }

}



class <>[T,S]{
  def combinar(unParser:Parser[T],otroParser:Parser[S]): Parser[(T,S)] ={
    new Parser[(T,S)] {
      override def apply(entrada: String): Try[ResultadoParser[(T,S)]] = {
        unParser.apply(entrada) match {
          case Success(ResultadoParser(resultado1,loQueSobra)) => otroParser.apply(loQueSobra) match {
            case Success(ResultadoParser(resultado2,loQueSobra)) =>
              Success(ResultadoParser((resultado1,resultado2),loQueSobra))
            case Failure(_) => Failure(new ConcatException)
          }
          case Failure(_) => Failure(new ConcatException)
        }
      }
    }
  }
}

class ~>[T,S]{
  def combinar(unParser:Parser[T],otroParser:Parser[S]):Parser[S] ={
    new Parser[S] {
      override def apply(entrada:String): Try[ResultadoParser[S]] = {
        (unParser <> otroParser).map(elem => elem._2) (entrada)

      }
    }
  }
}

class <~[T,S]{
  def combinar(unParser:Parser[T],otroParser:Parser[S]):Parser[T] = {
    new Parser[T] {
      override def apply(entrada: String): Try[ResultadoParser[T]] = {
        (unParser <> otroParser).map(elem => elem._1) (entrada)
      }
    }
  }
}


/*
class sepBy[T,S]{
  def combinar(parserDeContenido:Parser[T],parserSeparador:Parser[S]): Parser[T] ={
    new Parser[T] {
      override def aplicar(entrada:String): Try[ResultadoParser[T]] = {

      }

      }
  }
}
*/


case class ResultadoParser[T](elementoParseado: T, loQueSobra: String)
//TODO abusar de left most y right most para las figuras
//TODO hacer que los parser puedan usar for comprehension (ya implementamos map), tenemos que convertir Parser en una mónada
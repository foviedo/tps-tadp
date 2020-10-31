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

  def ~>[K] (otroParser:Parser[K]):Parser[K] = {
    (new ~>).combinar(this,otroParser)
  }

  def * ():Parser[List[T]] = {
    val yo = this
    new Parser[List[T]] {
      override def aplicar(entrada: String): Try[ResultadoParser[List[T]]] = { //TODO cambiar aplicar por apply y voy a poder hacer parser("unaEntrada")
        Try {
          val listaParseados = armameLaLista(yo,entrada)
            ResultadoParser(listaParseados._1,listaParseados._2)
        }

      }
      def armameLaLista[K](parser:Parser[T],entrada:String): (List[T],String) ={ //TODO armame la lista deberia retornar un resultado parser
        parser.aplicar(entrada) match {
          case Success(ResultadoParser(parseado,sobranteAParsear)) =>
            val (parseadoNuevo,sobranteNuevo) = armameLaLista(parser, sobranteAParsear)
            (parseado:: parseadoNuevo,sobranteNuevo)
          case Failure(_) => (List(),entrada)
        }
      }
    }
  }

  def +(): Parser[List[T]] = {
    val yo= this
    new Parser[List[T]] {
      override def aplicar(entrada: String): Try[ResultadoParser[List[T]]] = {
        val clausuraDeKleene = yo.*().aplicar(entrada)
        if(clausuraDeKleene.get.elementoParseado.isEmpty){
          return Failure(new ClausuraPositivaException) //TODO: ventaja de hacerlo con pattern matching?
        } else {
          return clausuraDeKleene //TODO: Estoy fijandome si se cumple una condicion, rompo o devuelvo el otro. Refactor
        }

      }
    }

  }


  def map[S](funcion: T=>S):Parser[S] ={
    val yo= this
    new Parser[S] {
      override def aplicar(entrada: String): Try[ResultadoParser[S]] = {
        yo.aplicar(entrada) match {
          case Success(ResultadoParser(elementoParseado,loQueSobra)) => Success(ResultadoParser(funcion(elementoParseado),loQueSobra))
          case Failure(_) => Failure(new MapException) //TODO: mejor devolver el error original
            //TODO 2: Juan dice que no van a revisar esto
            //TODO 3: tampoco seamos ratas
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
      override def aplicar(entrada: String): Try[ResultadoParser[T]] = {
        yo.aplicar(entrada) match {
          case Success(ResultadoParser(elementoParseado,loQueSobra)) if funcion(elementoParseado) => Success(ResultadoParser(elementoParseado,loQueSobra))
          case Success(_) => Failure(new SatisfiesException)
          case Failure(fallo) => Failure(fallo)
        }
      }
    }
  }


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
        new ResultadoParser[Char](digitoEnString.charAt(0),"") //TODO: corregir, podemos usar anychar y satisfies
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
  def aplicar(entero:String): Try[ResultadoParser[Int]]={
    Try {
      var enteroPosta: String = ""
      if (entero.startsWith("-")) {
        enteroPosta = ("-" + buscarDigitosValidos(entero.substring(1)).mkString(""))
      } else {
        enteroPosta = buscarDigitosValidos(entero).mkString("")
      }
       ResultadoParser(enteroPosta.toInt, entero.substring(enteroPosta.length))
    } //TODO fijate si se puede usar takewhile, también puedo usar clausura de kleene y parser optional
  }

  def buscarDigitosValidos(entero:String):List[Char]={
    entero.toList match {
      case Nil => Nil
      case head::tail if head.isDigit => head::buscarDigitosValidos(tail.mkString(""))
      case _::_ => Nil
    }
  }

}

case object double extends Parser[Double]{
  def aplicar(unDouble:String): Try[ResultadoParser[Double]]=
    Try {
      var enteroPosta: String = ""
      if (unDouble.startsWith("-")) {
        enteroPosta = ("-" + buscarDigitosValidos(unDouble.substring(1)).mkString(""))
      } else {
        enteroPosta = buscarDigitosValidos(unDouble).mkString("")
      }
      ResultadoParser(enteroPosta.toDouble, unDouble.substring(enteroPosta.length))
    }

 def buscarDigitosValidos(unDouble:String):List[Char] ={
   unDouble.toList match {
     case Nil => Nil
     case head::tail if head.isDigit => head::buscarDigitosValidos(tail.mkString(""))
     case head::tail if head == '.' => head::integer.buscarDigitosValidos(tail.mkString(""))
     case _::_ => Nil //TODO: refactorizar más
   }
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
          if(resultadoPrimerParser.isFailure){ //TODO: hacerlo todo funcional
            return Failure(new ConcatException)
          }
          otroParser.aplicar(unParser.aplicar(entrada).get.loQueSobra) match {
            case Success(ResultadoParser(resultadoSegundoParser,loQueSobra)) =>
              Success(ResultadoParser((resultadoPrimerParser.get.elementoParseado,resultadoSegundoParser),loQueSobra))
            case Failure(_) => Failure(new ConcatException)
          }
      }
    }
  }
}

class ~>[T,S]{
  def combinar(unParser:Parser[T],otroParser:Parser[S]):Parser[S] ={
    new Parser[S] {
      override def aplicar(entrada:String): Try[ResultadoParser[S]] = {
        (unParser <> otroParser).aplicar(entrada) match {
          case Success(ResultadoParser((_,res2),loQueSobra)) => Success(ResultadoParser(res2,loQueSobra))
          case Failure(_) => Failure(new RightMostException)
        }
      }//TODO se puede refactorizar un poquto mas? Si no lo refactorizamos desaprobamos
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
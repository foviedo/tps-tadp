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

  def <~[K] (otroParser:Parser[K]):Parser[T] = {
    (new <~).combinar(this,otroParser)
  }

  def sepByn[K] (otroParser:Parser[K],cantidad:Int):Parser[List[T]] = {
    (new sepByn).combinar(this,otroParser,cantidad)
  }

  def sepBy[K] (otroParser:Parser[K]):Parser[List[T]] = {
    (new sepBy).combinar(this,otroParser)
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
        ResultadoParser(resultado._1,resultado._2)
      }
    }
  }

  def +(): Parser[List[T]] = {
    val yo= this
    new Parser[List[T]] {
      override def apply(entrada: String): Try[ResultadoParser[List[T]]] = {
        yo.*().satisfies(this.noEstaVacio).apply(entrada)


      }
      def noEstaVacio(entrada: List[T]): Boolean ={
        entrada.nonEmpty
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
          case Failure(_) => Success(ResultadoParser(None,entrada))
        }
      }
    }
  }

  def limpiarString(unString:String):String = {
    unString.filter(_ > ' ')
  }

}


case object AnyChar extends Parser[Char] {
  def apply(unString: String): Try[ResultadoParser[Char]] = unString.toList match {
    case List() => Failure (new StringVacioException)
    case head :: tail => Success (ResultadoParser(head,tail.mkString("")))
  }
}

case object IsDigit extends Parser[Char] {
  def apply(entrada:String): Try[ResultadoParser[Char]] =
      AnyChar.satisfies(x => x.isDigit)(entrada)

}

case class char(inicio: Char) extends Parser[Char]{
  def apply(unString: String): Try[ResultadoParser[Char]] = AnyChar.apply(unString) match {
    case Failure(_) =>  Failure(new StringVacioException)
    case Success(ResultadoParser(unChar,_)) if unChar!= inicio => Failure(new CharException)
    case Success(ResultadoParser(unChar,resto)) if unChar == inicio => Success(ResultadoParser(inicio,resto))
  }
}

case class string(inicio: String) extends Parser[String]{
  def apply(stringOriginal: String): Try[ResultadoParser[String]] =
    if(stringOriginal.startsWith(inicio)) Success(ResultadoParser[String](inicio,stringOriginal.slice(inicio.length(),stringOriginal.length()))) else Failure(new StringException)
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
} // Success(ResultadoParser((algoDeTipoT,algoDeTipo),loQueSobra))

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



class sepBy[T,S]{
  def combinar(parserDeContenido:Parser[T],parserSeparador:Parser[S]): Parser[List[T]] ={
    new Parser[List[T]] {
      override def apply(entrada:String): Try[ResultadoParser[List[T]]] = {
        (parserDeContenido <> (parserSeparador ~> parserDeContenido).*()).map(tuplaConListaALista)(entrada)

      }
      def tuplaConListaALista (tupla:(T,List[T])) ={
        tupla._1 :: tupla._2
      }

      }
  }
}

class sepByn[T,S] {
  def combinar (parserDeContenido:Parser[T],parserSeparador:Parser[S], cantidadDeVeces: Int): Parser[List[T]] ={
    return new Parser[List[T]] {
      override def apply(entrada: String): Try[ResultadoParser[List[T]]] = {
        (new sepBy).combinar(parserDeContenido,parserSeparador).satisfies(x => x.length == cantidadDeVeces)(entrada)
      }
    }
  }
}

//TODO parsearPuntos


case object parserRectangulo extends Parser[Rectangulo] {
  def apply(unString:String): Try[ResultadoParser[Rectangulo]] ={
    Try{
      val rectanguloParseado = (string("rectangulo")  ~> char('[') ~> integer.sepBy(string(" @ ")).sepByn(string(", "),2) <~ char(']'))(unString).get
      ResultadoParser(new Rectangulo((rectanguloParseado.elementoParseado.apply(0).apply(0),rectanguloParseado.elementoParseado.apply(0).apply(1))
        ,(rectanguloParseado.elementoParseado.apply(1).apply(0),rectanguloParseado.elementoParseado.apply(1).apply(1))),rectanguloParseado.loQueSobra)
    }
  }
}


case object parserTriangulo extends Parser[Triangulo] {
    def apply(unString:String): Try[ResultadoParser[Triangulo]] ={
      Try{
        val trianguloParseado = ((((string("triangulo")  ~> char('['))) ~> (integer.sepBy(string(" @ "))).sepBy(string(", "))) <~ char(']'))(unString).get
        val trianguloConContenidoExtraido = trianguloParseado.elementoParseado
        ResultadoParser(new Triangulo((trianguloConContenidoExtraido.apply(0).apply(0),trianguloConContenidoExtraido.apply(0).apply(1))
          ,(trianguloConContenidoExtraido.apply(1).apply(0),trianguloConContenidoExtraido.apply(1).apply(1))
          ,(trianguloConContenidoExtraido.apply(2).apply(0),trianguloConContenidoExtraido.apply(2).apply(1))),trianguloParseado.loQueSobra)
      }
    } //TODO: Usar map
  //TODO: abstraer lo de las coordenadas, puedo terminar teniendo una lista de coordenadas, también puedo hacer pattern matching
} //TODO: hacer que el sepby se fije de la cantidad de elementos


case object parserCirculo extends Parser[Circulo] {
  def apply (unString:String):Try[ResultadoParser[Circulo]] ={
    Try {
      val circuloParseado = (((string("circulo")  ~> char('[')) ~> integer.sepBy(string(" @ ")).sepBy(string(", "))) <~ char(']'))(unString).get
      ResultadoParser(Circulo((circuloParseado.elementoParseado.apply(0).apply(0),circuloParseado.elementoParseado.apply(0).apply(1)),
        circuloParseado.elementoParseado.apply(1).apply(0)),circuloParseado.loQueSobra)
    }

  }
}

/*case object parserFigura extends Parser[] {

}*/
//TODO: solucionar el problema de los whitespace


//TODO: usar un trait que defina el supertipo o algo así
case class Triangulo(var verticePrimero:(Double,Double), var verticeSegundo:(Double,Double), var verticeTercero:(Double,Double))
case class Rectangulo(var verticeSuperior:(Double,Double),var verticeInferior:(Double,Double))
case class Circulo(var centro: (Double,Double),var radio : Double)




case class ResultadoParser[T](elementoParseado: T, loQueSobra: String)
//TODO hacer que los parser puedanusar for comprehension (ya implementamos map), tenemos que convertir Parser en una mónada

//TODO inspirarse en la clase del microprocesador para el tema de los dibujos, mas que nada para lo de simplificar
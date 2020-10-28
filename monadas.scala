Monada: mecanismo para secuenciar computaciones, lo que hago es tener una manera de armar 
una cadena de operaciones sobre algo que me produce un contenedor.
No es una clase, ni un trait, es mas un Patron o un Concepto que una interfaz que uno implementa
es una 'idea', imaginalo como una caja.

unit :: T -> Monad[T]
"guarda un valor en una caja" 

bind :: (T -> Monad[R]) -> Monad[T] - Monad[R]
"transformacion delcontenido de la caja": "dado una caja y una operacion que sabe hacer algo
con el contenido de una caja, me devuelve otra caja"
OBS: no hace falta abrir la 'caja' para modificarlo, bind hace todo

zero :: Monad[T]
"caja vacia"

plus :: Monad[T] -> Monad[T] -> Monad[T]
"combina dos cajas"

// -------------------- TIPOS MONADAS --------------//
Try: es parecido a python, contiene los siguiente
	apply[T](value: => T): Try[T]
	map...
	flatMap..
	filter..
	fold[R])(f: Throwable => R, g:T => R): R
	getOrElse(default : T) : T
	recover[R](handler: Throwable => R): Try[R]
	toOption(): Option[T] // convierte esta monada a Option


contiene un Succes[T](value: T)
 y un       Failure[T](err : Throwable)


//ejemplo
tenemos una class Curso>> 
					hayCupo(): Boolean
					inscribir(alumno: Alumno): Curso

tambien tenemos una class Docente>>
							autorizarIngreso(alumno: Alumno, curso: Curso) : Unit				

lo que queremos hacer es "anotar un alumno a un curso"
def anotar(alumno: Alumno, curso: Curso): Try[Curso] =  Try {
	if (!curso.hayCupo) throw new NoHayCupo(curso)
	curso.inscribir(alumno)
	}.recover {case _: NoAutorizado => // el recover es como el catch o el except en python
	curso.docente.autorizarIngreso(alumno,curso)
	curso.inscirbir(alumno)
}

// -------------------- abstraccion --------------//


for { // esto saca cosas de una caja
	materia <- todasLasMaterias if materia.electiva // saca la materia que sea electiva
	curso <- materia.cursos 						// sacame el curso
	alumno <- curso.inscriptos if alumno.regular	// sacame un alumno regular
} yield alumno.legajo 								// mete el legajo en una caja y devolvemelo



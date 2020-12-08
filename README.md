# TADP

TPS de la materia Técnicas Avanzadas de Programación

## TP Metaprogramación en Ruby: Contratos  
[Enunciado](https://docs.google.com/document/d/15iDg-uOyv6NXPygyl0vEQk7a6-47Qk7cBeyf1NRg4cU/edit?usp=sharing)  
Desarrollado principalmente con [@FranciscoDumont](https://github.com/FranciscoDumont)  
Framework con funciones varias:  
-Verificación de condiciones antes y después de funciones definindas  
-Verificación de tipos de parámetros  
-Verificación de mensajes que entienden los parámetros  
### Modo de uso:  
Al momento de crear una clase, si se desea que use el framework, se debe poner include Contrato  
Los invariant se escriben en cualquier lugar del cuerpo de la clase. Sintaxis: invariant{energia > 20} siendo energia una variable  
Los pre y post se declaran antes de una funcion. Sintaxis: pre {param > 0} donde param es un parametro de la funcion, post{|resultado| resultado>0} donde resultado es el retorno de la función  
El typed (checkeo de tipos) se declara en el mismo lugar. Sintaxis:  typed({p1: Integer, p2: String},Integer) donde p1 y p2 son los nombres de los parámetros y el segundo miembro es el tipo del retorno  
El duck (checkeo de declaración de funciones) se declara al igual que los anteriores. Sintaxis: duck([:metodo_banana, :cambioElOrden], [:to_s]). Donde cada lista pertenece a un parámetro. Nota: tienen que estar en el mismo orden que los parámetros

## TP Paradigma Objetos-Funcional en Scala: Parser Combinators
[Enunciado](https://docs.google.com/document/d/1SLaPtu8qFq_dPhkcOCtkBiOXmQFBtbJXIF7ziH1NwEM/edit?usp=sharing)  
Desarrollado con [@FranciscoDumont](https://github.com/FranciscoDumont) y [@brianUtn98](https://github.com/brianUtn98)  
Adaptador para el uso del framework de descripción de imágenes de la cátedra de TADP  
### Modo de uso: 
Se debe crear un objeto o clase que extienda de App para que sea ejecutable  
Se usa la función dibujarEnPantalla(unString) donde unString es un string que cumple las condiciones descriptas en el enunciado, puede ser figuras sueltas, grupos, transformaciones o todas juntas!  
Importante: en los transformadores, los números que las describen, deben ser doubles (en caso de poner enteros, se tendría que agregar el .0)  


require_relative 'spec_helper'

describe 'Prueba' do

  it 'Invariants' do
    class Atleta
      attr_accessor :nombre, :edad, :velocidad
      include Contrato

      def initialize(nombre, velocidad, edad)
        @nombre = nombre
        @velocidad = velocidad
        @edad = edad
      end

      #Para ser un atleta y poder competir necesitamos que sean mayores de edad
      invariant {edad >= 18}
      #Para ser un atleta capo tiene que correr a mas de 25km/h pero no puede ir a mas de 100km/h sino asumimos que esta consumiendo sustancias ilegales o que la esta cheeteando
      invariant {velocidad > 25 && velocidad < 100}

      def metodo(edad)
        @edad = edad
      end
    end

    expect(Atleta.new('Fran', 50, 20).metodo(21)).to eq 21
    expect{Atleta.new('Maxi', 120, 20)}.to raise_error(RuntimeError)
    expect{Atleta.new('Facu', 50, 17)}.to raise_error(RuntimeError)
    expect{Atleta.new('Guido', 15, 20)}.to raise_error(RuntimeError)

  end

end

describe 'rompiendo las copias' do
  it 'test actualizacion de variables después de ejecutar el método principal' do
    class Contador
      attr_accessor :valor
      include Contrato
      def initialize
        @valor = 0
      end

      post { valor > 0 }
      def incrementar
        @valor += 1
      end

      post {variableParametro > 0}
      def checkeo_de_variable_parametro(variableParametro)
        return variableParametro
      end

    end
    un_contador=Contador.new
    un_contador.incrementar
    expect(un_contador.valor).to eq 1
    expect(un_contador.checkeo_de_variable_parametro(35)).to eq 35 #lo importante de estos dos test es que no rompan
  end
end


describe 'the last dance' do
  class UnaClase
    attr_accessor :energia, :potencia
    include Contrato

    def initialize
      self.energia = 420
      self.potencia = 666
    end

    invariant{energia <= 420}

    post{energia>0}
    def fumarseUnCigarro
      self.energia -= 10
    end

    pre {param > 0}
    def unMeto(param)
      param
    end

    post{|resultado| resultado>0}
    def verParam(param)
      param
    end

    def tomarCerveza(gramos)
      self.energia += gramos
    end

    pre {energia < 420}
    def metodoDePruebaPrioridad(energia)
      energia
    end

    pre {energia > 10}
    post {|potenciaNueva| potenciaNueva < potencia}
    def correr(cuadras)
      self.potencia - 4
    end

    def metodoBanana
      return "banana"
    end

    pre{metodoBanana == "manzana"}
    def unMetodoQuePidioJuan(metodoBanana)
      return metodoBanana
    end

  end

  it 'si se manda una instancia a ver param con un parametro mayor a uno todo sale bien' do
    expect(UnaClase.new.verParam(10)).to eq 10
  end
  it 'se cumplen tanto pre como post' do
    expect(UnaClase.new.correr(5)).to eq 662
  end
  it 'si se manda una instancia a tomar cerveza con una cantidad de gramos mayor a 420, el invariant deberia romper' do
    expect{UnaClase.new.tomarCerveza(500)}.to raise_error(RuntimeError)
  end
  it 'si se manda una instancia a tomar cerveza con una cantidad de gramos de 0, todo sale bien porque sigue cumpliendose el invariant' do
    expect(UnaClase.new.tomarCerveza(0)).to eq(420)
  end

  it 'Si uso unMeto, el pre va a tener que usar los mismos parámetros que el método' do
    foo = UnaClase.new
    expect(foo.unMeto(5)).to eq 5
    expect(foo.unMeto(6)).to eq 6
  end

  it 'si un post no recibe parámetros, no debería romper de todos modos' do
    UnaClase.new.fumarseUnCigarro
  end
  it 'se priorizan los parametros a la hora de validar contratos en el caso de que tanto parametros como atributos tengan el mismo nombre' do
    expect{
      luken = UnaClase.new
      luken.fumarseUnCigarro
      luken.metodoDePruebaPrioridad(450)}.to raise_error(RuntimeError)
  end
  it 'no debería pisar los métodos que se llaman igual que los parametros' do
    clase = UnaClase.new
    clase.unMetodoQuePidioJuan("manzana")
    expect(clase.metodoBanana).to eq "banana"
  end

end


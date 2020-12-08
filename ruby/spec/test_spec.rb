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
    expect{Atleta.new('Maxi', 120, 20)}.to raise_error(InvariantError)
    expect{Atleta.new('Facu', 50, 17)}.to raise_error(InvariantError)
    expect{Atleta.new('Guido', 15, 20)}.to raise_error(InvariantError)

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

      post {variable_parametro > 0}
      def checkeo_de_variable_parametro(variable_parametro)
        return variable_parametro
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
    def fumarse_un_cigarro
      self.energia -= 10
    end

    pre {param > 0}
    def un_meto(param)
      param
    end

    post{|resultado| resultado>0}
    def ver_param(param)
      param
    end

    def tomar_cerveza(gramos)
      self.energia += gramos
    end

    pre {energia < 420}
    def metodo_de_prueba_prioridad(energia)
      energia
    end

    pre {energia > 10}
    post {|potenciaNueva| potenciaNueva < potencia}
    def correr()
      self.potencia - 4
    end

    def metodo_banana
      return "banana"
    end

    pre{metodo_banana == "manzana"}
    def un_metodo_que_pidio_juan(metodo_banana)
      return metodo_banana
    end

    typed({parametro: Integer}, Integer)
    def metodoParaProbarElTyped(parametro)
      parametro + 5
    end

    typed({},String)
    def metodoQueRompeElTyped(parametro)
      return parametro + 5
    end

    typed({parametro: String},Integer)
    def metodoQueRompeElTypedPorParametros(parametro)
      return parametro + 5
    end

    typed({p1: Integer, p2: String},Integer)
    def cambioElOrden(p2,p1)
      unString = p2 + "asd"
      p1 + 5
    end

    duck([:metodo_banana, :cambioElOrden], [:to_s])
    def paraElDuck(unaUnaClase,unString)
      unaUnaClase.metodo_banana
      unString
    end


    duck([:cambioElOrden,:metodo_banana,:metodo_que_definitivamente_no_existe],[:to_s])
    def paraElDuckQueNoAnda(unaUnaClase,unString)
      unaUnaClase.metodo_banana
      unString + "equis de"
    end

    duck([:to_s],[:xd])
    def paraElDuckUnArg(string)
      string + "asd"
    end

  end


  it 'si se manda una instancia a ver param con un parametro mayor a uno todo sale bien' do
    expect(UnaClase.new.ver_param(10)).to eq 10
  end
  it 'se cumplen tanto pre como post' do
    expect(UnaClase.new.correr).to eq 662
  end
  it 'si se manda una instancia a tomar cerveza con una cantidad de gramos mayor a 420, el invariant deberia romper' do
    expect{UnaClase.new.tomar_cerveza(500)}.to raise_error(InvariantError)
  end
  it 'si se manda una instancia a tomar cerveza con una cantidad de gramos de 0, todo sale bien porque sigue cumpliendose el invariant' do
    expect(UnaClase.new.tomar_cerveza(0)).to eq(420)
  end

  it 'Si uso unMeto, el pre va a tener que usar los mismos parámetros que el método' do
    foo = UnaClase.new
    expect(foo.un_meto(5)).to eq 5
    expect(foo.un_meto(6)).to eq 6
  end

  it 'si un post no recibe parámetros, no debería romper de todos modos' do
    UnaClase.new.fumarse_un_cigarro
  end
  it 'se priorizan los parametros a la hora de validar contratos en el caso de que tanto parametros como atributos tengan el mismo nombre' do
    expect{
      luken = UnaClase.new
      luken.fumarse_un_cigarro
      luken.metodo_de_prueba_prioridad(450)}.to raise_error(PreError)
  end

  it 'no debería pisar los métodos que se llaman igual que los parametros' do
    clase = UnaClase.new
    clase.un_metodo_que_pidio_juan("manzana")
    expect(clase.metodo_banana).to eq "banana"
  end

  it 'typed que anda' do
    clase = UnaClase.new
    clase.metodoParaProbarElTyped(4)
  end

  it 'typed que tiene mal el retorno' do
    clase = UnaClase.new
    expect{clase.metodoQueRompeElTyped(4)}.to raise_error(PostError)
  end

  it 'typed que rompe por el tipo del parametro' do
    clase = UnaClase.new
    expect{clase.metodoQueRompeElTypedPorParametros(4)}.to raise_error(PreError)
  end

  it 'cambiando el orden de los parametros el typed anda' do
    clase = UnaClase.new
    clase.cambioElOrden("banana",5)
  end


  it 'duck type que funciona' do
    #  p [[1,2],[2,3]].each_with_index.map{|listaMensajes,indice| listaMensajes.all?{|mensaje| true}}

    clase = UnaClase.new
    clase.paraElDuck(clase,"bro")
  end


  it 'duck type que NO funciona' do
    clase = UnaClase.new
    expect{clase.paraElDuckQueNoAnda(clase,"bro")}.to raise_error(PreError)
  end

  it 'duck type que no funciona pero esta vez por la cantidad de argumentos' do
    clase = UnaClase.new
    expect{clase.paraElDuckUnArg("banana")}.to raise_error(PreError)
  end
end


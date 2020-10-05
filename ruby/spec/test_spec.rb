require_relative 'spec_helper'

describe Prueba do
  let(:prueba) { Prueba.new }

  describe '#materia' do
    it 'debería pasar este test' do
      expect(prueba.materia).to be :tadp
    end
  end

  describe 'Prueba' do
    xit 'before_and_after_each_call' do
      nueva_clase = Class.new do
        before_and_after_each_call(proc {puts 'Hago algo antes'}, proc {puts 'Hago algo despues'})

        def decir_chau
          puts 'Chau'
        end
        nueva_clase.new.decir_chau

        puts ''
        before_and_after_each_call(proc {puts 'Otro algo antes'}, proc {puts 'Otro algo despues'})
        def decir_chau2
          puts 'Chau 2'
        end
        nueva_clase.new.decir_chau
      end

      expect(2).to be 2
    end

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

    it 'pre y post' do
      class Prueba
        pre {diez == 10}
        def banana
          @diez += 1
          p 'banana'
        end

        def manzana
          p 'manzana'
        end

        post { 1 == 1 }
        def naranja
          puts 'naranja'
        end
      end

      puts "\n\nEstoy en el ultimo test :)"
      puts "\n==BANANA=="
      objeto = Prueba.new
      objeto.banana
      puts 'LLEGUE HASTA ACA BIEN'
      expect{objeto.banana}.to raise_error(RuntimeError)

      puts "\n==MANZANA=="
      Prueba.new.manzana

      puts "\n==NARANJA=="
      Prueba.new.naranja

      puts "\n==Decir Hola=="
      Prueba.new.decir_hola
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

      def fumarseUnCigarro
        self.energia -= 1
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

      pre {energia < 20}
      def metodoDePruebaPrioridad(energia)

      end

      pre {energia > 10}
      def correr(cuadras)
        self.potencia - 4
      end
    end

    it 'si se manda una instancia a ver param con un parametro mayor a uno todo sale bien' do
      expect(UnaClase.new.verParam(10)).to eq 10
    end
    it 'si se manda una instancia a tomar falopa con una cantidad de gramos mayor a 420, el invariant deberia romper' do
      expect{UnaClase.new.tomarCerveza(500)}.to raise_error(RuntimeError)
    end
    it 'si se manda una instancia a tomar falopa con una cantidad de gramos de 0, todo sale bien porque sigue cumpliendose el invariant' do
      expect(UnaClase.new.tomarCerveza(0)).to eq(420)
    end
    it 'se priorizan los parametros a la hora de validar contratos en el caso de que tanto parametros como atributos tengan el mismo nombre' do
      expect{luken = UnaClase.new
      luken.fumarseUnCigarro
      luken.metodoDePruebaPrioridad(50)}.to raise_error(RuntimeError)
    end
    it 'se cumplen tanto pre como post' do
      expect(UnaClase.new.correr(5)).to eq 662
    end
    xit 'bro' do
      expect(UnaClase.new.unMeto(5)).to eq 5
    end
  end
end
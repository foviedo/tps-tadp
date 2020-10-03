require 'rspec'

require_relative '../lib/contrato'


describe 'pruebaFinal' do

    unaClase = Class.new do
      extend Contract

      attr_accessor :energia,:potencia

      def initialize
        self.energia=420
        self.potencia = 666
      end

      invariant{energia<=420}

      def fumarseUnFaso
        self.energia-=1
      end

      pre {param > 0}
      def unMeto(param)
        param
      end

      post {|res| res >1}
      def verParam(param)
        param
      end

      def tomarFalopa(gramos)
          self.energia+=gramos
      end

      pre {energia < 20}
      def metodoDePruebaPrioridad(energia)

      end

      pre {energia > 10}
      post {|nuevaPotencia| potencia > nuevaPotencia}
      def correr(cuadras)
        self.potencia - 4
      end


    end

    it 'Lanza PostError porque retorna 0 y debe ser mayor a 1 el resultado, que es igual al parametro que recibe' do
      expect{klass.new.verParam(0)}.to raise_error(PostError)
    end
    it 'si se manda una instancia a ver param con un parametro mayor a uno todo sale bien' do
      expect(klass.new.verParam(10)).to eq 10
    end
    it 'si se manda una instancia a tomar falopa con una cantidad de gramos mayor a 0, el invariant deberia romper' do
      expect{klass.new.tomarFalopa(100)}.to raise_error(InvariantError)
    end
    it 'si se manda una instancia a tomar falopa con una cantidad de gramos de 0, todo sale bien porque sigue cumpliendose el invariant' do
      expect(klass.new.tomarFalopa(0)).to eq(20)
    end
    it 'se priorizan los parametros a la hora de validar contratos en el caso de que tanto parametros como atributos tengan el mismo nombre' do
      expect{luken=klass.new
      luken.fumarseUnFaso
      luken.metodoDePruebaPrioridad(50)}.to raise_error(PreError)
    end
    it 'se cumplen tanto pre como post' do
      expect(klass.new.correr(5)).to eq 6
    end
end
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
      class Prueba
        before_and_after_each_call(proc {puts 'Hago algo antes'}, proc {puts 'Hago algo despues'})

        def decir_chau
          puts 'Chau'
        end
        Prueba.new.decir_chau

        puts ''
        before_and_after_each_call(proc {puts 'Otro algo antes'}, proc {puts 'Otro algo despues'})
        def decir_chau2
          puts 'Chau 2'
        end
        Prueba.new.decir_chau
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

      expect(Atleta.new("Fran", 50, 20).metodo(21)).to eq 21
      expect{Atleta.new("Maxi", 120, 20)}.to raise_error(RuntimeError)
      expect{Atleta.new("Facu", 50, 17)}.to raise_error(RuntimeError)
      expect{Atleta.new("Guido", 15, 20)}.to raise_error(RuntimeError)

    end

    it 'pre y post' do
      class Prueba
        pre {diez == 10}
        def banana
          @diez += 1
          p "banana"
        end

        def tetas
          p "tetas"
        end

        post { 1 == 1 }
        def poronga
          puts "hola"
        end
      end

      puts "\n\nEstoy en el ultimo test :)"
      puts "\n==BANANA=="
      objeto = Prueba.new
      objeto.banana
      puts "LLEGUE HASTA ACA BIEN"
      expect{objeto.banana}.to raise_error(RuntimeError)

      puts "\n==TETAS=="
      Prueba.new.tetas

      puts "\n==PORONGA=="
      Prueba.new.poronga

      puts "\n==Decir Hola=="
      Prueba.new.decir_hola
    end

  end
end
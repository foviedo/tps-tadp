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
      sleep 1
      class Prueba
        invariant {diez == 10}
        def metodo_invariants(s)
          s
        end
      end

      Prueba.new.metodo_invariants('bien')
      expect(Prueba.new.metodo_invariants('bien')).to eq 'bien'
      # expect(Prueba.new(9)).to raise_error(RuntimeError)
      Prueba.new(10)
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
require_relative 'spec_helper'

describe Prueba do
  let(:prueba) { Prueba.new }

  describe '#materia' do
    it 'debería pasar este test' do
      expect(prueba.materia).to be :tadp
    end
  end

  describe 'Prueba' do
    it 'before_and_after_each_call' do
      class Prueba
        before_and_after_each_call(proc {puts 'Hago algo antes'}, proc {puts 'Hago algo despues'})
        # puts "Estoy en la prueba: \n\t#{Prueba.before}"

        def decir_chau
          puts "Chau"
        end

      end

      instancia = Prueba.new
      instancia.decir_chau
      expect(2).to be 2
    end

    it 'Invariants' do
      sleep 1
      class Prueba
        invariant {diez == 10}
      end
      prueba.checkear_invariants
      expect(2).to be 2
    end
  end
end


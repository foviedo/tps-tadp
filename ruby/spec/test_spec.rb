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
        include Contrato

        before_and_after_each_call(proc {puts 'Hago algo antes'}, proc {puts 'Hago algo despues'})

        def decir_chau
          puts 'Chau'
        end
        Prueba.new.decir_chau

        puts ''
        before_and_after_each_call(proc {puts 'Otro algo antes'}, proc {puts 'Otro algo despues'})
        def decir_chau
          puts 'Chau 2'
        end
        Prueba.new.decir_chau
      end

      expect(2).to be 2
    end

    xit 'Invariants' do
      sleep 1
      class Prueba
        invariant {diez == 10}
      end

      expect(2).to be 2
    end
  end
end
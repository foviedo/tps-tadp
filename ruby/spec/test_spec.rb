require_relative 'spec_helper'

describe Prueba do
  let(:prueba) { Prueba.new }

  describe '#materia' do
    it 'debería pasar este test' do
      expect(prueba.materia).to be :tadp
    end
  end

  describe 'Prueba' do
    it 'asda' do
      prueba.before_and_after_each_call(proc {puts 'Hago algo antes'}, proc {puts 'Hago algo despues'})
      prueba.decir_hola
      expect(2).to be 2
    end
  end
end


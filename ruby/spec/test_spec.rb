require_relative 'spec_helper'

describe Prueba do
  let(:prueba) { Prueba.new }

  describe '#materia' do
    it 'debería pasar este test' do
      prueba.before_and_after_each_call(proc {puts 'Before'}, proc {puts 'After'})
      expect(prueba.materia).to be :tadp
    end
  end
end

describe 'Prueba' do
  it 'asda' do
    expect(2).to be 2
  end
end
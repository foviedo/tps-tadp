require_relative 'contrato'

class Prueba
  include Contrato
  attr_accessor :diez, :hola, :verdadero

  def initialize
    @diez = 10
    @hola = 'hola'
    @verdadero = true
  end

  def materia
    :tadp
  end

  def decir_hola
    puts 'Hola'
  end
end

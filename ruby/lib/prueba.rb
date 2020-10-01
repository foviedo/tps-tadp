require_relative 'contrato'

class Prueba
  attr_accessor :diez, :hola, :verdadero
  include Contrato

  def initialize(numero = 0)
    @diez = 10
    @hola = 'hola'
    @verdadero = true
    @numero = numero
  end

  def materia
    :tadp
  end

  def decir_hola
    puts 'Hola'
  end
end

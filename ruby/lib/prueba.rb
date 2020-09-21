require_relative 'contrato'

class Prueba
  include Contrato

  def materia
    :tadp
  end

  def decir_hola
    puts 'Hola'
  end
end

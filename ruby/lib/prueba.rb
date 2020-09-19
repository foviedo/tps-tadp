require_relative 'contrato'

class Prueba
  include Contrato

  def materia
    :tadp
  end

  def hola
    puts "chau"
  end
end

module Contrato
  attr_accessor :before, :after

  def self.method_added(method_name)
    alias_method :metodo_viejo, method_name
    puts "Estoy entrando en #{method_name}"
    define_method(method_name) do
      before.call
      metodo_viejo.call
      after.call
    end
    puts "Estoy saliendo de #{method_name}"
  end

  def before_and_after_each_call(before, after)
    @before = before
    @after = after
  end

end


class Prueba
  include Contrato

  def materia
    puts "hola"
    :tadp
  end

  def hola
    puts "chau"
  end
end

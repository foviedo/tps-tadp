module Contrato
  attr_accessor :before, :after

  # self.included permite a los metodos ejecutar en el contexto del que incluye
  # https://stackoverflow.com/questions/4699355/ruby-is-it-possible-to-define-a-class-method-in-a-module
  def self.included(base)
    base.extend(ClassMethods)
  end

  # Aca adentro self = la clase que incluye Contrato (self == Prueba)
  module ClassMethods
    # method_added se va a ejecutar cada vez que se define un metodo en la clase
    def method_added(method_name)
      puts "Estoy entrando en #{method_name}"
      metodo_viejo = instance_method(method_name)
      # redefinir el metodo viejo:
      #   pre.call
      #   resultado = metodo_viejo.bindear.call
      #   after.call
      #   return resultado
      puts "Estoy saliendo de #{method_name}\n"
    end
  end

  def before_and_after_each_call(before, after)
    @before = before
    @after = after
  end
end

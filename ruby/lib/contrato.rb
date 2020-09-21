module Contrato
  # self.included permite a los metodos ejecutar en el contexto del que incluye
  # https://stackoverflow.com/questions/4699355/ruby-is-it-possible-to-define-a-class-method-in-a-module
  # https://stackoverflow.com/questions/46112695/undefined-self-module-method-called-in-method-added
  def self.included(base)
    attr_accessor :before, :after
    base.extend(ClassMethods)
    base.extend(Contrato)
  end

  # Aca adentro self = la clase que incluye Contrato (self == Prueba)
  module ClassMethods
    # method_added se va a ejecutar cada vez que se define un metodo en la clase
    def method_added(method_name)
      # https://stackoverflow.com/questions/53487250/stack-level-too-deep-with-method-added-ruby
      return if @_adding_a_method

      metodo_viejo = instance_method(method_name)

      @_adding_a_method = true
      define_method(method_name) do
        before.call if @before
        resultado = metodo_viejo.bind(self).call
        after.call if @after
        resultado
      end
      @_adding_a_method = false
    end
  end


  def before_and_after_each_call(before, after)
    @before = before
    @after = after
  end
end

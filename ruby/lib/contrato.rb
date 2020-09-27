module Contrato
  # self.included permite a los metodos ejecutar en el contexto del que incluye
  # https://stackoverflow.com/questions/4699355/ruby-is-it-possible-to-define-a-class-method-in-a-module
  # https://stackoverflow.com/questions/46112695/undefined-self-module-method-called-in-method-added
  def self.included(base)
    base.extend(ClassMethods)
    # base.extend(Contrato)
  end


  # Aca adentro self = la clase que incluye Contrato (self == Prueba)
  module ClassMethods
    @before = nil
    @after = nil
    class << self
      attr_accessor :before, :after
    end

    # method_added se va a ejecutar cada vez que se define un metodo en la clase
    def method_added(method_name)
      # https://stackoverflow.com/questions/53487250/stack-level-too-deep-with-method-added-ruby
      return if @_adding_a_method

      metodo_viejo = instance_method(method_name)

      @_adding_a_method = true
      proc_before = before
      proc_after = after
      define_method(method_name) do
        proc_before.call
        resultado = metodo_viejo.bind(self).call
        proc_after.call
        resultado
      end
      @_adding_a_method = false
    end

    def before_and_after_each_call(before = proc {}, after = proc {})
      @before = before
      @after = after
    end

    # Agrega un nuevo invariant al vector
    def invariant(&block)
      @invariants ||= [] # Loco no te voy a mentir, no se por que sin esto no funciona
      @invariants << block
    end

    # Con esto inicializo el vector de invariants
    def invariants
      @invariants ||= []
    end

    def before
      @before ||= proc {}
    end

    def after
      @after ||= proc {}
    end
  end
end

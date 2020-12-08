require 'sourcify'

module Contrato
  # self.included permite a los metodos ejecutar en el contexto del que incluye
  # https://stackoverflow.com/questions/4699355/ruby-is-it-possible-to-define-a-class-method-in-a-module
  # https://stackoverflow.com/questions/46112695/undefined-self-module-method-called-in-method-added
  def self.included(base)
    base.extend(ClassMethods)
  end

  # Aca adentro self = la clase que incluye Contrato (self == Prueba)
  module ClassMethods
    # method_added se va a ejecutar cada vez que se define un metodo en la clase
    def method_added(method_name)
      # https://stackoverflow.com/questions/53487250/stack-level-too-deep-with-method-added-ruby
      return if @_adding_a_method
      
      metodo_viejo = instance_method(method_name)
      nombre_argumentos = metodo_viejo.parameters.map { |arg| arg[1].to_s }

      @_adding_a_method = true
      proc_before = before
      proc_after = after

      define_method(method_name) do |*argumentos|
        mi_objeto_copia = self.clone
        nombre_argumentos.each_with_index do |item, index|
          mi_objeto_copia.define_singleton_method(item) do
            argumentos[index]
          end
        end
        raise PreError, "Error con un pre en #{self}:#{method_name}}" unless mi_objeto_copia.instance_exec(*argumentos,&proc_before)

        resultado = metodo_viejo.bind(self).call(*argumentos)

        #prco_invariants.each ...
        # Checkeo cada invariant
        self.class.invariants.each do |invariant|
          raise InvariantError, "Error con un invariant en #{self}:#{method_name}}" unless instance_eval(&invariant)
        end
        mi_objeto_copia = self.clone
        nombre_argumentos.each_with_index do |item, index|
          mi_objeto_copia.define_singleton_method(item) do
            argumentos[index]
          end
        end
        raise PostError, "Error con un post en #{self}:#{method_name}}" unless mi_objeto_copia.instance_exec(resultado, &proc_after)

        resultado
      end

      # Reseteo los before y after
      proc_true = proc { true }
      before_and_after_each_call(proc_true, proc_true)
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

    def pre(&block)
      @before = block
    end

    def post(&block)
      @after = block
    end

    def typed(mapa_tipos,tipo_retorno)
      pre {(mapa_tipos.all?{|elemento| instance_eval(elemento[0].to_s).is_a? elemento[1]})}
      post {|retorno| (retorno.is_a? tipo_retorno)}
    end

    def duck (*listas_de_funciones)
      pre { |*argumentos| listas_de_funciones.each_with_index.map{|listaMensajes,indice| listaMensajes.all?{|mensaje| argumentos[indice].respond_to?(mensaje)}}.all? and argumentos.length == listas_de_funciones.length }
    end


    # Con esto inicializo el vector de invariants
    def invariants
      @invariants ||= [proc { true }]
    end

    def before
      @before ||= proc { true }
    end

    def after
      @after ||= proc { true }
    end
  end
end

class PreError < RuntimeError
end
class PostError < RuntimeError
end
class InvariantError < RuntimeError
end
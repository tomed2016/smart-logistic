# language: es
Característica: Gestión de clientes
  Como operador comercial de la empresa de distribución de bidones
  Quiero crear, consultar y mantener actualizada la información de mis clientes
  Para administrar correctamente su cartera comercial y sus direcciones de entrega

  Escenario: Crear un cliente persona natural con datos válidos
    Cuando creo un cliente persona natural con nombre "Cliente QA Persona"
    Entonces la respuesta tiene el estado HTTP 201
    Cuando consulto el cliente creado
    Entonces el cliente consultado tiene nombre "Cliente QA Persona"
    Y el cliente consultado tiene estado "ACTIVO"
    Y el cliente consultado tiene condición de pago "CONTADO"
    Y el cliente consultado tiene prioridad comercial "ESTANDAR"

  Escenario: Consultar un cliente existente por su identificador
    Dado que he creado un cliente persona natural con nombre "Cliente QA Consulta"
    Cuando consulto el cliente creado
    Entonces la respuesta tiene el estado HTTP 200
    Y el cliente consultado tiene nombre "Cliente QA Consulta"

  Escenario: Agregar una dirección con una comuna válida del catálogo geográfico
    Dado que he creado un cliente persona natural con nombre "Cliente QA Direccion"
    Cuando agrego al cliente creado una dirección en la comuna "13119"
    Entonces la respuesta tiene el estado HTTP 201
    Cuando consulto el cliente creado
    Entonces el cliente consultado tiene exactamente 1 dirección
    Y la primera dirección del cliente consultado tiene comuna "Maipú"

  Escenario: Rechazar una dirección cuya comuna no existe en el catálogo geográfico
    Dado que he creado un cliente persona natural con nombre "Cliente QA Comuna Invalida"
    Cuando agrego al cliente creado una dirección en la comuna "999999"
    Entonces la respuesta tiene el estado HTTP 400

  Escenario: Actualizar el nombre comercial de un cliente
    Dado que he creado un cliente persona natural con nombre "Cliente QA Original"
    Cuando actualizo el nombre del cliente creado a "Cliente QA Actualizado"
    Entonces la respuesta tiene el estado HTTP 200
    Cuando consulto el cliente creado
    Entonces el cliente consultado tiene nombre "Cliente QA Actualizado"

  Escenario: Desactivar un cliente preservando su historial
    Dado que he creado un cliente persona natural con nombre "Cliente QA Desactivar"
    Cuando desactivo el cliente creado
    Entonces la respuesta tiene el estado HTTP 204
    Cuando consulto el cliente creado
    Entonces el cliente consultado tiene estado "INACTIVO"
    Y el cliente consultado tiene nombre "Cliente QA Desactivar"

  Escenario: Rechazar la creación de un cliente con un RUT ya registrado
    Dado que he creado un cliente persona natural con nombre "Cliente QA Original Rut"
    Cuando intento crear otro cliente con el mismo RUT y nombre "Cliente QA Rut Duplicado"
    Entonces la respuesta tiene el estado HTTP 409

  Escenario: Consultar un cliente que no existe
    Cuando consulto el cliente con identificador "00000000-0000-0000-0000-000000000000"
    Entonces la respuesta tiene el estado HTTP 404

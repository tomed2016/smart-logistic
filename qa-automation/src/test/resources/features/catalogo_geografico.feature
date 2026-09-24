# language: es
Característica: Consulta del catálogo geográfico chileno
  Como sistema de gestión logística
  Quiero consultar regiones, provincias, comunas y días hábiles
  Para validar direcciones de clientes y planificar entregas del siguiente día hábil

  Escenario: Consultar una región existente
    Cuando consulto la región con código 13
    Entonces la respuesta tiene el estado HTTP 200
    Y la región consultada tiene nombre "Región Metropolitana de Santiago"

  Escenario: Listar las comunas de la Región Metropolitana
    Cuando listo las comunas de la región con código 13
    Entonces la respuesta tiene el estado HTTP 200
    Y el listado de comunas incluye la comuna "Maipú"

  Escenario: Consultar una comuna existente por su código
    Cuando consulto la comuna con código 13119
    Entonces la respuesta tiene el estado HTTP 200
    Y la comuna consultada tiene nombre "Maipú"
    Y la comuna consultada pertenece a la región con código 13

  Escenario: Consultar una comuna que no existe en el catálogo
    Cuando consulto la comuna con código 999999
    Entonces la respuesta tiene el estado HTTP 404

  Escenario: Verificar que un domingo no es día hábil
    Cuando verifico si la fecha "2024-06-02" es día hábil
    Entonces la respuesta tiene el estado HTTP 200
    Y la fecha consultada no es día hábil

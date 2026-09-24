# Project Context

## 1. Supuestos principales

1. La primera iteración debe crear una base productiva mínima, no la plataforma completa.
2. El despliegue inicial favorece un monolito modular con DDD y arquitectura hexagonal preparado para extraer servicios.
3. La planificación automática se implementará inicialmente con heurísticas simples y un puerto de optimización desacoplado.
4. La operación de negocio usa zona horaria `America/Santiago`, moneda `CLP` y calendario laboral chileno.
5. El RUT se trata como atributo de negocio y nunca como identificador técnico.

## 2. Alcance MVP

- Gestión básica de clientes.
- Gestión básica de productos e inventario.
- Registro y confirmación de pedidos.
- Recurrencia de pedidos como concepto de dominio.
- Planificación simple para el siguiente día hábil.
- Reprogramación con trazabilidad.
- Registro de entregas y pagos.
- Consulta de saldo por cliente.

## 3. Casos de uso prioritarios

1. Crear cliente.
2. Registrar vehículo.
3. Crear producto.
4. Registrar inventario.
5. Crear y confirmar pedido.
6. Crear regla de recurrencia.
7. Generar ocurrencia idempotente.
8. Generar planificación.
9. Reprogramar pedido.
10. Registrar entrega fallida y reintento.
11. Confirmar entrega.
12. Registrar pago y consultar saldo.

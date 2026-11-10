import java.time.LocalDate

enum class EstadoPedido(val descripcion: String) {
    EN_PREPARACION("En preparación"), LISTO("Listo"), EN_CAMINO("En camino")
}

data class LineaPedido(val producto: Producto, val cantidad: Int) {
    val subtotal: Int get() = producto.precio * cantidad
}

class Pedido(
    val cliente: Cliente,
    val lineas: List<LineaPedido>,
    val fecha: LocalDate,
    val notas: String? = null
) {
    var estado = EstadoPedido.EN_PREPARACION
        private set

    val subtotal: Int get() = lineas.sumOf { it.subtotal }

    // Descuento de los productos marcados con @Promocionable, detectados en tiempo de ejecución
    val descuentoPromocionables: Int
        get() = lineas.sumOf { linea ->
            linea.producto.promocion()?.let { linea.subtotal * it.porcentajeDescuento / 100 } ?: 0
        }

    // Descuento adicional si la fecha del pedido cae dentro del rango de promociones
    val descuentoPeriodo: Int
        get() = if (estaEnPeriodoPromocion(fecha)) (subtotal - descuentoPromocionables) * DESCUENTO_PERIODO / 100 else 0

    val total: Int get() = subtotal - descuentoPromocionables - descuentoPeriodo

    fun cambiarEstado(nuevoEstado: EstadoPedido) {
        estado = nuevoEstado
    }

    // Sobrecarga de + : combina dos pedidos del mismo cliente, suma los productos y recalcula el total.
    // El Map usa equals()/hashCode() de Producto, así el mismo producto queda en una sola línea.
    operator fun plus(otro: Pedido): Pedido {
        if (cliente != otro.cliente) {
            throw IllegalArgumentException("Solo se pueden combinar pedidos del mismo cliente.")
        }
        val cantidades = mutableMapOf<Producto, Int>()
        for ((producto, cantidad) in lineas + otro.lineas) {
            cantidades[producto] = (cantidades[producto] ?: 0) + cantidad
        }
        val lineasCombinadas = cantidades.map { (producto, cantidad) -> LineaPedido(producto, cantidad) }
        val notasCombinadas = when {
            notas != null && otro.notas != null -> "$notas / ${otro.notas}"
            else -> notas ?: otro.notas
        }
        val fechaCombinada = if (otro.fecha > fecha) otro.fecha else fecha
        return Pedido(cliente, lineasCombinadas, fechaCombinada, notasCombinadas)
    }

    // Declaraciones de desestructuración: val (cliente, productos, total) = pedido
    operator fun component1(): Cliente = cliente
    operator fun component2(): List<LineaPedido> = lineas
    operator fun component3(): Int = total

    fun detalle(): String {
        val texto = mutableListOf<String>()
        texto.add("  Cliente : ${cliente.nombre}")
        texto.add("  Fecha   : $fecha (periodo de promoción: ${if (estaEnPeriodoPromocion(fecha)) "Sí" else "No"})")
        for ((producto, cantidad) in lineas) {
            val promo = producto.promocion()?.let { "  [promo -${it.porcentajeDescuento}%]" } ?: ""
            texto.add("    - $cantidad x ${producto.nombre} ${producto.precio.aPesos()} = ${(producto.precio * cantidad).aPesos()}$promo")
        }
        texto.add("  Subtotal                    : ${subtotal.aPesos()}")
        texto.add("  Descuento promocionables    : -${descuentoPromocionables.aPesos()}")
        texto.add("  Descuento periodo (-$DESCUENTO_PERIODO%)    : -${descuentoPeriodo.aPesos()}")
        texto.add("  TOTAL                       : ${total.aPesos()}")
        texto.add("  Estado: ${estado.descripcion} | Notas: ${notas ?: "Sin notas"}")
        return texto.joinToString("\n")
    }
}

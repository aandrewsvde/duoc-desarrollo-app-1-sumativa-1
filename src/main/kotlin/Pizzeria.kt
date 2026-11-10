import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty

class Pizzeria(val nombre: String) {
    // Los Set usan equals() y hashCode() para no aceptar elementos repetidos
    val clientes = mutableSetOf<Cliente>()
    val productos = mutableSetOf<Producto>()
    val pedidos = mutableListOf<Pedido>()
    val inventario = mutableMapOf(
        "masa" to 300, "salsa de tomate" to 300, "queso" to 500,
        "pepperoni" to 200, "albahaca" to 100, "jamón" to 100
    )

    fun registrarCliente(cliente: Cliente): Boolean {
        val agregado = clientes.add(cliente)
        println(if (agregado) "Cliente registrado: ${cliente.nombre}" else "Cliente duplicado, no se registra: ${cliente.nombre} (${cliente.correo})")
        return agregado
    }

    fun registrarProducto(producto: Producto): Boolean {
        val agregado = productos.add(producto)
        println(if (agregado) "Producto registrado: ${producto.detallarProducto()}" else "Producto duplicado, no se registra: ${producto.nombre} (${producto.categoria.name.lowercase()})")
        return agregado
    }

    // Verifica el inventario y descuenta los ingredientes; si faltan, lanza una excepción
    fun registrarPedido(pedido: Pedido) {
        val requeridos = mutableMapOf<String, Int>()
        for ((producto, cantidad) in pedido.lineas) {
            for (ingrediente in producto.ingredientes) {
                requeridos[ingrediente] = (requeridos[ingrediente] ?: 0) + cantidad
            }
        }
        val faltantes = requeridos.filter { (ingrediente, cantidad) -> (inventario[ingrediente] ?: 0) < cantidad }
        when {
            faltantes.isNotEmpty() -> throw IllegalStateException(
                "Ingredientes insuficientes: ${faltantes.keys.joinToString()}. Reabastezca el inventario antes de aceptar el pedido."
            )
            else -> {
                for ((ingrediente, cantidad) in requeridos) {
                    inventario[ingrediente] = (inventario[ingrediente] ?: 0) - cantidad
                }
                pedidos.add(pedido)
            }
        }
    }

    // Identifica dinámicamente los productos con la anotación @Promocionable
    fun productosPromocionables(): List<Producto> = productos.filter { it.promocion() != null }
}

// Reflection: lista todas las propiedades y métodos de una clase en tiempo de ejecución
fun listarMiembros(clase: KClass<*>): String {
    val propiedades = clase.members.filterIsInstance<KProperty<*>>().map { it.name }.sorted()
    val metodos = clase.members.filterIsInstance<KFunction<*>>().map { it.name }.distinct().sorted()
    return "  Clase ${clase.simpleName}\n" +
        "    Propiedades: ${propiedades.joinToString()}\n" +
        "    Métodos    : ${metodos.joinToString()}"
}

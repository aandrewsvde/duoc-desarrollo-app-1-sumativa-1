import java.io.File
import java.time.LocalDate

fun titulo(texto: String) = println("\n===== $texto =====")

fun main() {
    val pizzeria = Pizzeria("Pizzería Duoc")
    println("Bienvenido al sistema de gestión de ${pizzeria.nombre}")

    // ---------- Catálogo de productos (Paso 5: se evitan duplicados con equals/hashCode) ----------
    titulo("Registro de productos")
    val margarita = PizzaMargarita()
    val pepperoni = PizzaPepperoni()
    val combo = ComboFamiliar()
    val bebida = Bebida("Bebida 1,5 L", 2490)
    val quesoExtra = Ingrediente("Queso", 1200)
    listOf(margarita, pepperoni, combo, bebida, quesoExtra).forEach { pizzeria.registrarProducto(it) }
    val pepperoniRepetida = Pizza("PEPPERONI", 9990, listOf("masa", "queso", "pepperoni"))
    pizzeria.registrarProducto(pepperoniRepetida)

    // ---------- Registro de cliente por consola (Paso 1: Regex) ----------
    titulo("Registro de cliente")
    val nombre = leerTexto("Nombre: ")
    val correo = leerCorreo()
    val telefono = leerTelefono()
    val cliente = Cliente(nombre, correo, telefono)
    pizzeria.registrarCliente(cliente)
    val clienteRepetido = Cliente(nombre.uppercase(), correo.lowercase(), "+56 (9) 8765-4321")
    pizzeria.registrarCliente(clienteRepetido)
    val otroCliente = Cliente("Ana Rojas", "ana.rojas@correo.cl", formatearTelefono("+56 9 1122 3344") ?: "Sin teléfono")
    pizzeria.registrarCliente(otroCliente)

    // ---------- Pedido por consola (Paso 1: Ranges 1..100) ----------
    titulo("Nuevo pedido")
    println("Pizza ${pepperoni.detallarProducto()} | Ingrediente adicional: ${quesoExtra.detallarProducto()}")
    val cantidadPizzas = leerCantidad("Cantidad de pizzas Pepperoni (1-100): ")
    val cantidadIngredientes = leerCantidad("Cantidad de ingredientes adicionales de queso (1-100): ")
    print("Notas del pedido (Enter para omitir): ")
    val notasIngresadas = readlnOrNull()?.trim() ?: ""
    val notas: String? = if (notasIngresadas.isEmpty()) null else notasIngresadas

    val hoy = LocalDate.now()
    val pedido1 = Pedido(cliente, listOf(LineaPedido(pepperoni, cantidadPizzas), LineaPedido(quesoExtra, cantidadIngredientes)), hoy, notas)
    val pedido2 = Pedido(cliente, listOf(LineaPedido(pepperoni, 1), LineaPedido(combo, 1), LineaPedido(bebida, 2)), hoy)

    // Pedidos del día en un arreglo; manejo de errores al registrarlos (inventario insuficiente)
    val pedidosDelDia = arrayOf(pedido1, pedido2)
    for (pedido in pedidosDelDia) {
        try {
            pizzeria.registrarPedido(pedido)
            println("Pedido registrado para ${pedido.cliente.nombre}. Total: ${pedido.total.aPesos()}")
        } catch (e: IllegalStateException) {
            println("No se pudo registrar el pedido: ${e.message}")
        }
    }

    // Ranges con fechas: verificación del periodo de promoción
    titulo("Periodo de promoción ${PERIODO_PROMOCION.start} a ${PERIODO_PROMOCION.endInclusive}")
    listOf(hoy, LocalDate.of(2026, 12, 15)).forEach { fecha ->
        println("Pedido con fecha $fecha: ${if (estaEnPeriodoPromocion(fecha)) "dentro del periodo, aplica -$DESCUENTO_PERIODO%" else "fuera del periodo, sin descuento"}")
    }

    // ---------- Paso 3: operador + ----------
    val pedidoCombinado = pedido1 + pedido2
    pedidoCombinado.cambiarEstado(EstadoPedido.LISTO)
    try {
        pedido1 + Pedido(otroCliente, listOf(LineaPedido(margarita, 1)), hoy)
    } catch (e: IllegalArgumentException) {
        println("\nError al combinar pedidos: ${e.message}")
    }

    // ---------- Paso 2: Reflection ----------
    titulo("Reflection: propiedades y métodos de las clases")
    listOf(Cliente::class, Producto::class, Pedido::class).forEach { println(listarMiembros(it)) }

    // ---------- Paso 4: Declaraciones de desestructuración ----------
    val (nombreCliente, correoCliente, telefonoCliente) = cliente
    val (clientePedido, productosPedido, totalPedido) = pedidoCombinado

    // ---------- Paso 6: Resumen ----------
    val resumen = mutableListOf<String>()
    resumen.add("==================================================")
    resumen.add("   RESUMEN ${pizzeria.nombre.uppercase()} - ${LocalDate.now()}")
    resumen.add("==================================================")
    resumen.add("1) PEDIDOS COMBINADOS (operador +)")
    resumen.add(" Pedido 1:")
    resumen.add(pedido1.detalle())
    resumen.add(" Pedido 2:")
    resumen.add(pedido2.detalle())
    resumen.add(" Pedido 1 + Pedido 2:")
    resumen.add(pedidoCombinado.detalle())
    resumen.add("")
    resumen.add("2) CLIENTE DESESTRUCTURADO  val (nombre, correo, telefono) = cliente")
    resumen.add("  Nombre  : $nombreCliente")
    resumen.add("  Correo  : $correoCliente")
    resumen.add("  Teléfono: $telefonoCliente")
    resumen.add(" Pedido desestructurado  val (cliente, productos, total) = pedidoCombinado")
    resumen.add("  Cliente  : ${clientePedido.nombre}")
    resumen.add("  Productos: ${productosPedido.joinToString { "${it.cantidad} x ${it.producto.nombre}" }}")
    resumen.add("  Total    : ${totalPedido.aPesos()}")
    resumen.add("")
    resumen.add("3) PRODUCTOS PROMOCIONABLES (@Promocionable)")
    for (producto in pizzeria.productosPromocionables()) {
        producto.promocion()?.let { resumen.add("  - ${producto.nombre}: ${it.descripcion} (-${it.porcentajeDescuento}%)") }
    }
    resumen.add("")
    resumen.add("4) VALIDACIÓN DE IGUALDAD DE OBJETOS")
    resumen.add("  Clientes:")
    resumen.add("    '${cliente.nombre}' == '${clienteRepetido.nombre}' (mismo nombre y correo): ${cliente == clienteRepetido}")
    resumen.add("    '${cliente.nombre}' === '${clienteRepetido.nombre}' (misma instancia)     : ${cliente === clienteRepetido}")
    resumen.add("    '${cliente.nombre}' == '${otroCliente.nombre}'                           : ${cliente == otroCliente}")
    resumen.add("    Clientes registrados sin duplicados: ${pizzeria.clientes.size} -> ${pizzeria.clientes.joinToString { it.nombre }}")
    resumen.add("  Productos:")
    resumen.add("    'Pepperoni' == 'PEPPERONI' (mismo nombre y categoría): ${pepperoni == pepperoniRepetida}")
    resumen.add("    'Pepperoni' == 'Margarita'                            : ${pepperoni == margarita}")
    resumen.add("    Productos registrados sin duplicados: ${pizzeria.productos.size} -> ${pizzeria.productos.joinToString { it.nombre }}")
    resumen.add("")
    resumen.add("5) VALIDACIONES (Regex y Ranges)")
    resumen.add("  Correo '$correoCliente' con formato válido: ${esCorreoValido(correoCliente)}")
    resumen.add("  Teléfono formateado: $telefonoCliente")
    resumen.add("  Cantidades en rango $RANGO_CANTIDAD: $cantidadPizzas pizzas, $cantidadIngredientes ingredientes adicionales")
    resumen.add("  Pedido del $hoy dentro del periodo de promoción: ${estaEnPeriodoPromocion(hoy)}")
    resumen.add("")
    resumen.add("Inventario restante: ${pizzeria.inventario.entries.joinToString { (ingrediente, cantidad) -> "$ingrediente=$cantidad" }}")
    resumen.add("==================================================")
    resumen.add("Gracias por su preferencia, ${cliente.nombre}.")

    val textoResumen = resumen.joinToString("\n")
    println()
    println(textoResumen)
    File("resumen_pedido.txt").writeText(textoResumen)
    println("Resumen guardado en el archivo resumen_pedido.txt")
}

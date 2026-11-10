enum class Categoria { PIZZA, BEBIDA, INGREDIENTE, COMBO }

open class Producto(
    val nombre: String,
    val categoria: Categoria,
    val precio: Int,
    val ingredientes: List<String> = emptyList()
) {
    fun detallarProducto(): String = "$nombre (${categoria.name.lowercase()}) ${precio.aPesos()}"

    // Lee dinámicamente la anotación @Promocionable de la clase real del producto (null si no la tiene)
    fun promocion(): Promocionable? =
        this::class.annotations.find { it is Promocionable } as? Promocionable

    // Sobrecarga de == : dos productos son el mismo si coinciden nombre y categoría
    override fun equals(other: Any?): Boolean {
        if (other is Producto) {
            return nombre.equals(other.nombre, ignoreCase = true) && categoria == other.categoria
        }
        return false
    }

    // hashCode coherente con equals: usa los mismos atributos clave
    override fun hashCode(): Int = 31 * nombre.lowercase().hashCode() + categoria.hashCode()

    override fun toString(): String = detallarProducto()
}

open class Pizza(nombre: String, precio: Int, ingredientes: List<String>) :
    Producto(nombre, Categoria.PIZZA, precio, ingredientes)

class PizzaMargarita : Pizza("Margarita", 8990, listOf("masa", "salsa de tomate", "queso", "albahaca"))

@Promocionable(descripcion = "Pizza de la semana", porcentajeDescuento = 15)
class PizzaPepperoni : Pizza("Pepperoni", 10990, listOf("masa", "salsa de tomate", "queso", "pepperoni"))

@Promocionable(descripcion = "Combo familiar: pizza + bebida 1,5 L", porcentajeDescuento = 20)
class ComboFamiliar : Producto("Combo Familiar", Categoria.COMBO, 15990, listOf("masa", "salsa de tomate", "queso", "jamón"))

class Bebida(nombre: String, precio: Int) : Producto(nombre, Categoria.BEBIDA, precio)

class Ingrediente(nombre: String, precio: Int) :
    Producto(nombre, Categoria.INGREDIENTE, precio, listOf(nombre.lowercase()))

// Función de extensión para mostrar montos en pesos: 10990 -> $10990
fun Int.aPesos(): String = "\$$this"

import java.time.LocalDate

// ---------- Regex ----------
val REGEX_CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
// Celular chileno: código de país 56, el 9 de móvil y 8 dígitos
val REGEX_TELEFONO = Regex("^(56)(9)(\\d{4})(\\d{4})$")

fun esCorreoValido(correo: String): Boolean = REGEX_CORREO.matches(correo)

// Deja solo los dígitos (acepta +56912345678, 56 9 1234 5678, etc.) y los formatea como +56 (9) XXXX-XXXX.
// Retorna null si no es un celular chileno válido.
fun formatearTelefono(telefono: String): String? {
    val digitos = telefono.replace(Regex("\\D"), "")
    return REGEX_TELEFONO.matchEntire(digitos)?.let { resultado ->
        val (pais, movil, bloque1, bloque2) = resultado.destructured
        "+$pais ($movil) $bloque1-$bloque2"
    }
}

// ---------- Ranges ----------
val RANGO_CANTIDAD = 1..100
val PERIODO_PROMOCION = LocalDate.of(2026, 11, 1)..LocalDate.of(2026, 11, 30)
const val DESCUENTO_PERIODO = 10

fun esCantidadValida(cantidad: Int): Boolean = cantidad in RANGO_CANTIDAD

fun estaEnPeriodoPromocion(fecha: LocalDate): Boolean = fecha in PERIODO_PROMOCION

// ---------- Lectura de datos por consola (se repite hasta que el dato sea válido) ----------
fun leerTexto(mensaje: String): String {
    while (true) {
        print(mensaje)
        val texto = readlnOrNull()?.trim() ?: ""
        if (texto.isNotEmpty()) return texto
        println("  Error: el campo no puede estar vacío.")
    }
}

fun leerCorreo(): String {
    while (true) {
        val correo = leerTexto("Correo electrónico: ")
        if (esCorreoValido(correo)) return correo
        println("  Error: '$correo' no tiene el formato nombre@dominio.com. Reingréselo.")
    }
}

fun leerTelefono(): String {
    while (true) {
        val telefono = leerTexto("Teléfono celular (ej: +56912345678): ")
        formatearTelefono(telefono)?.let { return it }
        println("  Error: '$telefono' no es un celular chileno válido (+569 seguido de 8 dígitos). Reingréselo.")
    }
}

fun leerCantidad(mensaje: String): Int {
    while (true) {
        val cantidad = leerTexto(mensaje).toIntOrNull()
        when {
            cantidad == null -> println("  Error: debe ingresar un número entero.")
            !esCantidadValida(cantidad) -> println("  Error: la cantidad debe estar entre ${RANGO_CANTIDAD.first} y ${RANGO_CANTIDAD.last}.")
            else -> return cantidad
        }
    }
}

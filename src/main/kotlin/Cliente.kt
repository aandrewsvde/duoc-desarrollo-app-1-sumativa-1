open class Usuario(val nombre: String)

class Cliente(nombre: String, val correo: String, val telefono: String) : Usuario(nombre) {

    // Declaraciones de desestructuración: val (nombre, correo, telefono) = cliente
    operator fun component1(): String = nombre
    operator fun component2(): String = correo
    operator fun component3(): String = telefono

    // Dos clientes son iguales si coinciden nombre y correo (sin importar mayúsculas)
    override fun equals(other: Any?): Boolean {
        if (other is Cliente) {
            return nombre.equals(other.nombre, ignoreCase = true) &&
                correo.equals(other.correo, ignoreCase = true)
        }
        return false
    }

    override fun hashCode(): Int = 31 * nombre.lowercase().hashCode() + correo.lowercase().hashCode()

    override fun toString(): String = "$nombre | $correo | $telefono"
}

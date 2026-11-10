// Anotación personalizada que marca las pizzas o combos que califican para promociones especiales.
// RUNTIME permite leerla con reflection mientras se procesan los pedidos.
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Promocionable(val descripcion: String, val porcentajeDescuento: Int)

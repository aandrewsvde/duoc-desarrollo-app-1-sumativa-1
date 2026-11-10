# Pizzería – Creando soluciones con Kotlin (Semana 3)

Sistema de gestión de una pizzería (clientes, productos y pedidos) que incorpora Regex, Ranges, anotaciones, reflection, sobrecarga de operadores, desestructuración e igualdad de objetos.

## Ejecución
1. Abrir la carpeta `PizzeriaKotlin` en IntelliJ IDEA (proyecto Gradle).
2. Ejecutar `src/main/kotlin/Main.kt`.
3. Ingresar por consola el nombre, el correo, el teléfono y las cantidades que se piden.

La dependencia `kotlin-reflect`, necesaria para reflection, ya está declarada en `build.gradle.kts`.

## Estructura
| Archivo | Contenido |
|---|---|
| `Validaciones.kt` | Paso 1: Regex de correo, formato de celular chileno `+56 (9) XXXX-XXXX`, rango de cantidades `1..100` y rango de fechas de promoción |
| `Promocionable.kt` | Paso 2: anotación personalizada `@Promocionable` |
| `Producto.kt` | Productos (pizza, bebida, ingrediente, combo), lectura de `@Promocionable` y `equals()`/`hashCode()` por nombre y categoría |
| `Cliente.kt` | Cliente con desestructuración `(nombre, correo, telefono)` y `equals()`/`hashCode()` por nombre y correo |
| `Pedido.kt` | Operador `+` para combinar pedidos, cálculo de descuentos y desestructuración `(cliente, productos, total)` |
| `Pizzeria.kt` | Registro sin duplicados (Set), inventario, productos promocionables y `listarMiembros()` con reflection |
| `Main.kt` | Paso 6: ejecución integrada y resumen en consola (también se guarda en `resumen_pedido.txt`) |

plugins {
    kotlin("jvm") version "2.4.21"
    application
}

group = "cl.duoc.pizzeria"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    // Necesaria para usar reflection (KClass.members, KProperty, KFunction, annotations)
    implementation(kotlin("reflect"))
}

application {
    mainClass.set("MainKt")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

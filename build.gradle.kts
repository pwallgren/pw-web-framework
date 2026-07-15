plugins {
    java
    application
}

group = "com.petwal"
version = "1.0"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

application {
    mainClass.set("com.petwal.pwweb.demo.Main")
}

dependencies {
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.slf4j:slf4j-api:2.0.18")
    implementation("ch.qos.logback:logback-classic:1.5.38")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.22.1")
}

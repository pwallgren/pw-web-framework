plugins {
    java
    application
    `maven-publish`
}

group = "com.petwal"
version = "1.0"

repositories {
    mavenCentral()
    maven {
        url = uri("http://192.168.0.22:8080/releases")
        isAllowInsecureProtocol = true
    }
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "reposilite"
            url = uri(
                if (version.toString().endsWith("-SNAPSHOT")) {
                    "http://pw-pi-1:8080/snapshots"
                } else {
                    "http://pw-pi-1:8080/releases"
                }
            )
            credentials {
                username = findProperty("reposiliteUser") as String? ?: System.getenv("REPOSILITE_USER")
                password = findProperty("reposilitePassword") as String? ?: System.getenv("REPOSILITE_PASSWORD")
            }
            isAllowInsecureProtocol = true
        }
    }
}

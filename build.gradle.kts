plugins {
    java
    application
    `maven-publish`
}

group = "com.petwal"
version = "1.0"

val reposiliteUrl: String? = findProperty("reposiliteUrl") as String? ?: System.getenv("REPOSILITE_URL")

repositories {
    mavenCentral()
    if (reposiliteUrl != null) {
        maven {
            url = uri("$reposiliteUrl/releases")
            isAllowInsecureProtocol = true
        }
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
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.22.0")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        if (reposiliteUrl != null) {
            maven {
                name = "reposilite"
                url = uri(
                    if (version.toString().endsWith("-SNAPSHOT")) {
                        "$reposiliteUrl/snapshots"
                    } else {
                        "$reposiliteUrl/releases"
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
}

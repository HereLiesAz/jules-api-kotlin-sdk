import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.20"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20"
    `maven-publish`
}

group = "com.hereliesaz.jules-api-sdk"
version = "1.0.1"

repositories {
    mavenCentral()
}

dependencies {
    // Ktor client
    implementation("io.ktor:ktor-client-core:2.3.6")
    implementation("io.ktor:ktor-client-cio:2.3.6")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.6")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.6")
    implementation("io.ktor:ktor-client-logging:2.3.6")

    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.9")

    // Testing
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("io.ktor:ktor-client-mock:2.3.6")
    testImplementation("ch.qos.logback:logback-classic:1.4.11")

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.hereliesaz.jules-api-sdk"
            artifactId = "kotlin-sdk"
            version = "1.0.1"

            from(components["java"])

            pom {
                name.set("Jules API Kotlin SDK")
                description.set("A Kotlin SDK for the Jules API.")
                url.set("https://github.com/hereliesaz/julesapisdk")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("hereliesaz")
                        name.set("Jules")
                        email.set("jules@example.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/hereliesaz/julesapisdk.git")
                    developerConnection.set("scm:git:ssh://github.com/hereliesaz/julesapisdk.git")
                    url.set("https://github.com/hereliesaz/julesapisdk/tree/main")
                }
            }
        }
    }
}

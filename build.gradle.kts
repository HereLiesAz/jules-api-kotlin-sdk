plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    `maven-publish`
}

android {
    namespace = "com.hereliesaz.julesapisdk"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    publishing {
        singleVariant("release")
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

group = "com.hereliesaz.julesapisdk"
version = "1.0.1"


dependencies {
    // Ktor client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)

    // Logging
    implementation(libs.slf4j.api)

    // Testing
    testImplementation(kotlin("test-junit5"))
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.mockk)
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.logback.classic)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = group.toString()
            artifactId = "kotlin-sdk"
            version = version.toString()

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
                        email.set("hereliesaz@gmail.com")
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

afterEvaluate {
    publishing.publications.getByName<MavenPublication>("release") {
        from(components["release"])
    }
}

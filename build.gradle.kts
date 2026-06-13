plugins {
    kotlin("multiplatform") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
    id("ivy-publish")
}

group = "kotlin.nmea"
version = "0.11.1-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
    }
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
                implementation("com.squareup.okio:okio:3.9.0")
                implementation("co.touchlab:stately-concurrent-collections:2.1.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
                runtimeOnly("org.junit.vintage:junit-vintage-engine:4.12.2")
            }
        }
    }
}

publishing {
    repositories {
        ivy {
            url = uri(System.getenv("OSMAND_BINARIES_IVY_ROOT") ?: "./")
        }
    }
    publications {
        create<IvyPublication>("ivyKmpNmeaJvm") {
            organisation = project.group.toString()
            module = "kmp-nmea"
            revision = project.version.toString()
            artifact(tasks.named("jvmJar")) {
                type = "jar"
            }
            artifact(tasks.named("jvmSourcesJar")) {
                type = "jar"
                classifier = "sources"
            }
        }
    }
}

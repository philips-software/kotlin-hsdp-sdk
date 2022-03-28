plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    kotlin("kapt") version "1.6.10"
    id("org.jetbrains.kotlinx.kover") version "0.4.4"
    id("org.jetbrains.dokka") version "1.6.10"
    id("org.sonarqube") version "3.3"
    jacoco
    id("com.github.hierynomus.license") version "0.16.1"
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
}

val okHttpVersion = "4.9.3"
val kotestVersion = "5.2.2"
val coroutineVersion = "1.6.0-native-mt"

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()

    extensions.configure(kotlinx.kover.api.KoverTaskExtension::class) {
        isEnabled = true
        excludes = listOf(
            "com\\.philips\\.hsdp\\.apis\\..*\\.domain\\.sdk\\..*",
            "com\\.philips\\.hsdp\\.apis\\..*\\.domain\\.hsdp\\..*",
        )
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutineVersion")
    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")
    implementation("io.jsonwebtoken:jjwt:0.9.1")

    implementation("com.thinkinglogic.builder:kotlin-builder-annotation:1.2.1")
    kapt("com.thinkinglogic.builder:kotlin-builder-processor:1.2.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutineVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-json:$kotestVersion")
    testImplementation("io.mockk:mockk:1.12.2")
    testImplementation("com.squareup.okhttp3:mockwebserver:$okHttpVersion")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

license {
    header = file("copyright-header")
    strictCheck = true
    includes(listOf("**/*.kt", "**/*.java"))
}

downloadLicenses {
    includeProjectDependencies = true
    dependencyConfiguration = "default"
}

tasks.check {
    dependsOn(tasks["license"])
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    dokkaSourceSets {
        named("main") {
            includes.from("Module.md")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("kotlin-hsdp-sdk") {
            from(components["kotlin"])
            pom {
                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                        distribution.set("repo")
                    }
                }
            }
        }
    }
}


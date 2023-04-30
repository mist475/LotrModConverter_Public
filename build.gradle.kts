plugins {
    java
    `maven-publish`
    application
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://jitpack.io")
    }
    mavenCentral()
}

dependencies {
    implementation("com.github.piegamesde:nbt:3.0.1")
    implementation("com.google.code.gson:gson:2.9.0")
}

group = "misterymob475"
version = "2.1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
publishing {
    publications {
        create<MavenPublication>("LotrModConverter") {
            from(components["java"])
        }
    }
}
tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
}

application {
    mainClass.set("misterymob475.Main")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
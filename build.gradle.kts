plugins {
    java
    `maven-publish`
    application
    idea
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation("com.github.piegamesde:nbt:3.0.1")
    implementation("com.google.code.gson:gson:2.9.0")
    annotationProcessor("com.github.bsideup.jabel:jabel-javac-plugin:0.4.2")
    annotationProcessor("net.java.dev.jna:jna-platform:5.13.0") // required due to https://github.com/bsideup/jabel/issues/174
    compileOnly("com.github.bsideup.jabel:jabel-javac-plugin:0.4.2")
}

group = "misterymob475"
version = "2.1.0-SNAPSHOT"

java {
    toolchain {
        //We compile with newer java, due to jabel we still compile to java 8 byte code
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

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    sourceCompatibility = "17"
    options.release.set(8)

    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(17))
    })
}

application {
    mainClass.set("misterymob475.Main")
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.withType<Jar> {
    manifest.attributes["Main-Class"] = "misterymob475.Main"

    from(
            configurations.runtimeClasspath
                    .get()
                    .map {
                        if (it.isDirectory) it
                        else zipTree(it)
                    }
    )
}
plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
    id("maven-publish")
}

group = "hi.korperka.dscore"
version = "1.0"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

//    implementation("com.minestom:minestom:6.5.1-BETA")
//    implementation("com.dfsek.terra:terra-api:6.5.1-BETA")
//    implementation("com.dfsek.terra:terra-platform-minestom:6.5.1-BETA")
//    implementation("com.pg85.otg:common:1.16.5-0.0.17")
    implementation("com.github.emortalmc:Rayfast:684e854a48")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.2")
    implementation("org.fusesource.jansi:jansi:2.2.0")
    implementation("org.slf4j:slf4j-nop:2.0.7")
    implementation("net.minestom:minestom-snapshots:2f5bb97908")
    implementation("de.articdive:jnoise-pipeline:4.1.0")
    implementation("com.typesafe:config:1.4.2")
    implementation("commons-io:commons-io:2.18.0")
    implementation("io.github.classgraph:classgraph:4.8.162")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21)) // Minestom has a minimum Java version of 21
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "hi.korperka.dscore"
            artifactId = "DSCore"
            version = "1.0"

            from(components["java"])
        }
    }
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "hi.korperka.dscore.DSCore" // Change this to your main class
        }
    }

    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("") // Prevent the -all suffix on the shadowjar file.
    }
}

tasks.test {
    useJUnitPlatform()
}
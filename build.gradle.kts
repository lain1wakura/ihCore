import org.gradle.api.artifacts.ConfigurationContainer
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.JavaExec

plugins {
    kotlin("jvm") version "2.0.21"
    id("fabric-loom") version "1.7.1"
    id("maven-publish")
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 17
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    //withSourcesJar()
}


loom {
    splitEnvironmentSourceSets()

    mods {
        // Переименуйте имя модуля, чтобы оно не конфликтовало с уже зарегистрированным
        register("ihcoreMod") {
            sourceSet("main")
            sourceSet("client")
        }
    }
}




repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
    maven {
        url = uri("https://maven.wispforest.io/releases/")
    }
    maven {
        name = "IzzelAliz Maven"
        url = uri("https://maven.izzel.io/releases/")
    }
    maven {
        name = "Fuzs Mod Resources"
        url = uri("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
    }
    maven { url = uri("https://files.minecraftforge.net/maven/") }
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    implementation("icyllis.modernui:ModernUI-Core:3.11.0")
    implementation("icyllis.modernui:ModernUI-Markdown:3.11.0")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("kotlin_loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.15.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.0")
    implementation("org.xerial:sqlite-jdbc:3.41.2.2")
    modImplementation("icyllis.modernui:ModernUI-Fabric:1.20.1-3.11.0.1")
    implementation("com.typesafe:config:1.4.1")
    //implementation("org.litote.kmongo:kmongo-async:4.10.0")
    include(implementation("org.mongodb:mongodb-driver-sync:5.2.1")!!)
    include(implementation("org.mongodb:mongodb-driver-core:5.2.1")!!)
    include(implementation("org.mongodb:bson:5.2.1")!!)
    include(implementation("org.mongodb:bson-record-codec:5.2.1")!!)
    include(implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")!!)
    include(implementation("com.fasterxml.jackson.core:jackson-core:2.17.1")!!)
    include(implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.1")!!)
    include(implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.1")!!)
    // KMongo Core (основная библиотека для работы с MongoDB)
    //include(implementation("org.litote.kmongo:kmongo:5.1.0")!!)
    include(implementation("org.litote.kmongo:kmongo-core:5.1.0")!!)
    include(implementation("org.litote.jackson:jackson-module-loader:0.4.0")!!)
    include(implementation("org.litote.kmongo:kmongo-data:5.1.0")!!)
    include(implementation("org.litote.kmongo:kmongo-id:5.1.0")!!)
    include(implementation("org.litote.kmongo:kmongo-id-jackson:5.1.0")!!)
    include(implementation("org.litote.kmongo:kmongo-jackson-mapping:5.1.0")!!)
    include(implementation("org.litote.kmongo:kmongo-property:5.1.0")!!)
    include(implementation("org.litote.kmongo:kmongo-shared:5.1.0")!!)
    include(implementation("de.undercouch:bson4jackson:2.15.1")!!)
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.property("minecraft_version"))
    inputs.property("loader_version", project.property("loader_version"))
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to project.property("minecraft_version"),
            "loader_version" to project.property("loader_version"),
            "kotlin_loader_version" to project.property("kotlin_loader_version")
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

tasks.jar {
    // Включение лицензионного файла в JAR
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName}" }
    }
}

// configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.property("archives_base_name") as String
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
    }
}

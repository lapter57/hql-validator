import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.github.johnrengelman.shadow") version "4.0.4"
}

group = "com.github.lapter57"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.hive:hive-exec:3.1.2"){
        exclude(group = "org.apache.logging.log4j")
    }
    implementation("com.github.rvesse:airline:2.8.2")
}

tasks {
    named<ShadowJar>("shadowJar") {
        isZip64 = true

        archiveBaseName.set("hql-validator")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "com.github.lapter57.ValidateCommand"))
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}
plugins {
    java
}

group = "programs.heroes_task"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(files("libs/heroes_task_lib-1.0-SNAPSHOT.jar"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
}

sourceSets {
    main {
        java {
            setSrcDirs(listOf("src/programs"))
        }
    }
}

// Полная задача сборки JAR
tasks.register<Jar>("buildJar") {
    archiveBaseName.set("heroes_task_impl")
    archiveVersion.set("1.0-SNAPSHOT")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // Добавляем свои классы
    from(sourceSets.main.get().output)

    // Включаем зависимости
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

    manifest {
        attributes["Implementation-Title"] = "Heroes Task Plugin"
        attributes["Implementation-Version"] = version
    }
}
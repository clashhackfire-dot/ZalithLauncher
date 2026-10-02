plugins {
    java
}

group = "org.lwjgl.glfw"

configurations.getByName("default").isCanBeResolved = true

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveBaseName.set("lwjgl-glfw-classes")
    destinationDirectory.set(file("../ZalithLauncher/src/main/assets/components/lwjgl3/"))
    // Auto update the version with a timestamp so the project jar gets updated by Pojav
    doLast {
        val versionFile = file("../ZalithLauncher/src/main/assets/components/lwjgl3/version")
        versionFile.writeText(System.currentTimeMillis().toString())
    }
    from({
        configurations.getByName("default").map {
            println(it.name)
            if (it.isDirectory) it else zipTree(it)
        }
    })
    exclude("net/java/openjdk/cacio/ctc/**")
    manifest {
        attributes("Manifest-Version" to "3.3.6")
        attributes("Automatic-Module-Name" to "org.lwjgl")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

configurations {
    create("minecraftLwjglModules") {
        isCanBeResolved = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Minecraft 26.2/26.3 ships LWJGL 3.4.1 optional Java bindings.
    // Keep the launcher's patched core/GLFW/OpenGL stack, but add the
    // modules that Minecraft loads during startup (especially Vulkan).
    listOf(
        "org.lwjgl:lwjgl-vulkan:3.4.1",
        "org.lwjgl:lwjgl-vma:3.4.1",
        "org.lwjgl:lwjgl-spvc:3.4.1",
        "org.lwjgl:lwjgl-shaderc:3.4.1",
        "org.lwjgl:lwjgl-spng:3.4.1"
    ).forEach { coordinate ->
        add("minecraftLwjglModules", coordinate) {
            isTransitive = false
        }
    }
}

tasks.jar {
    inputs.files(configurations["minecraftLwjglModules"])
    doLast {
        copy {
            from(configurations["minecraftLwjglModules"])
            into(archiveFile.get().asFile.parentFile)
        }
    }
}

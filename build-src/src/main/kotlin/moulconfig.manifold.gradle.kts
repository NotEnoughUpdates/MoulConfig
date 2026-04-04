plugins {
    id("systems.manifold.manifold-gradle-plugin")
}

manifold {
    manifoldVersion.set("2025.1.27")
}

dependencies {
    val manifoldSubSystems = listOf(
        "strings" to false,
        "preprocessor" to false,
    )
    manifoldSubSystems.forEach { (name, rt) ->
        if (rt)
            implementation("systems.manifold:manifold-$name-rt:${manifold.manifoldVersion.get()}")
        annotationProcessor("systems.manifold:manifold-$name:${manifold.manifoldVersion.get()}")
    }
}
val preProcessorArgs = extensions.create("preprocessorArgs", PreProcessorArgs::class)
val sourceDump = layout.buildDirectory.dir("manifoldSourceDump")
tasks.clean {
    doLast {
        sourceDump.get().asFile.deleteRecursively()
    }
}
preProcessorArgs.forDefaultCompilation {
    define("manifold.source.target", sourceDump.get().asFile.absolutePath)
}
tasks.compileJava {
    doFirst {
        val sourceDumpDir = sourceDump.get().asFile
        sourceDumpDir.deleteRecursively()
        sourceDumpDir.mkdirs()
    }
}
preProcessorArgs.preprocessedSources.set(tasks.compileJava.flatMap { sourceDump })



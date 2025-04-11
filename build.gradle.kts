
plugins {
	base
	id("moulconfig.base")
	alias(libs.plugins.mkdocs)
	id("moulconfig.dokka.base")
}

mkdocs {
	python {
		pip("mkdocs-zettelkasten:0.1.9")
	}
	strict = false
}

val compileAllDocs = tasks.register("compileAllDocs", Copy::class) {
	dependsOn(tasks.mkdocsBuild)
	dependsOn(tasks.dokkaHtmlMultiModule)
	destinationDir = layout.buildDirectory.dir("allDocs").get().asFile
	from(tasks.mkdocsBuild)
	from(tasks.dokkaHtmlMultiModule.get().outputDirectory) {
		into("javadocs")
	}
}

val docJar = tasks.register("docJar", Zip::class) {
	from(compileAllDocs)
	archiveClassifier.set("docs")
}

val docConfig = configurations.create("documentation")
artifacts.add(docConfig.name, docJar)
tasks.assemble { dependsOn(docJar) }

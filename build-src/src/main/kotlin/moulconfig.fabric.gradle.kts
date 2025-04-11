import xyz.wagyourtail.unimined.api.UniminedExtension
import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask

plugins {
	id("xyz.wagyourtail.unimined")
	id("org.jetbrains.dokka")
	id("moulconfig.kotlin")
	id("moulconfig.leaf")
}

the<UniminedExtension>().minecraft {
	version(property("moulconfig.minecraft") as String)
	mappings {
		intermediary()
		yarn(property("moulconfig.yarn") as String)
	}

	fabric {
		loader("0.16.9")
		val aF = project.file("src/main/resources/moulconfig.accesswidener")
		if (aF.exists())
			accessWidener(aF)
	}
	runs {
		config("client") {
			jvmArgs("-Dmoulconfig.testmod=true")
			jvmArgs("-Dmoulconfig.warn.crash=false")
//			env.putAll(parseEnvFile(file(".env")))
		}
	}
}


val remapJar by tasks.named("remapJar", RemapJarTask::class) {
	asJar {
		archiveClassifier.set("")
	}

	dependsOn(tasks.shadowJar)
	inputFile.set(tasks.shadowJar.flatMap { it.archiveFile })
}

tasks.named("jar", Jar::class) {
	archiveClassifier.set("small")
	dependsOn(tasks.processResources)
}


tasks.withType(Jar::class) {
	this.filesMatching(listOf("fabric.mod.json")) {
		filter {
			if (it.contains("FabricMain")) ""
			else it
		}
	}
	exclude("io/github/notenoughupdates/moulconfig/test/**")
}

configure<PublishingExtension> {
	publications {
		defaultMaven {
			artifact(remapJar) {
				classifier = ""
			}
		}
	}
}



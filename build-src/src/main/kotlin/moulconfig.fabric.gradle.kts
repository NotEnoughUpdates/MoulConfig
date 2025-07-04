import xyz.wagyourtail.unimined.api.UniminedExtension
import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask

plugins {
	id("xyz.wagyourtail.unimined")
	id("org.jetbrains.dokka")
	id("moulconfig.kotlin")
	id("moulconfig.leaf")
}

val fabricVersion = property("moulconfig.fabric") as String
val aF = project.file("src/main/resources/moulconfig.accesswidener")
val hasAW = aF.exists()
the<UniminedExtension>().minecraft {
	version(property("moulconfig.minecraft") as String)
	mappings {
		intermediary()
		yarn(property("moulconfig.yarn") as String)
	}

	fabric {
		loader("0.16.14")
		if (hasAW)
			accessWidener(aF)
	}
	mods {
		this.modImplementation {
			this.mixinRemap {
				this.enableBaseMixin()
			}
		}
	}
	runs {
		config("client") {
			jvmArgs("-Dmoulconfig.testmod=true")
			jvmArgs("-Dmoulconfig.warn.crash=false")
//			env.putAll(parseEnvFile(file(".env")))
		}
	}
}

val fabricDeps = extensions.create("fabricDeps", FabricUtils::class, fabricVersion)

fabricDeps.impl("fabric-resource-loader-v0")

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

tasks.processResources {
	from(project(":modern").file("templates/resources")) {
		filesMatching("fabric.mod.json") {
			filter {
				if (!it.contains("accessWidener") || hasAW)
					it
				else
					""
			}
		}
	}
}

val generateFilteredSource = tasks.register("generateFilteredSource", Copy::class) {
	from(project(":modern").file("templates/kotlin"))
	rootSpec.into(layout.buildDirectory.dir("sharedModernSource"))
}
sourceSets.main {
	kotlin {
		srcDir(files(generateFilteredSource))
	}
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



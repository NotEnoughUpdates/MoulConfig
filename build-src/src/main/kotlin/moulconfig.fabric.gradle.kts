import xyz.wagyourtail.unimined.api.UniminedExtension
import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask

plugins {
	id("xyz.wagyourtail.unimined")
	id("org.jetbrains.dokka")
	id("moulconfig.kotlin")
	id("moulconfig.leaf")
	id("moulconfig.manifold")
}

val fabricVersion = property("moulconfig.fabric") as String
val minecraftVersion = property("moulconfig.minecraft") as String
val aF = project.file("src/main/resources/moulconfig.accesswidener")
val hasAW = aF.exists()
the<UniminedExtension>().minecraft {
	version(minecraftVersion)
	mappings {
		intermediary()
		mojmap()
	}

	fabric {
		loader("0.18.4")
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
			parseEnvFile(rootProject.file(".env")).forEach { (name, value) ->
				environment(name, value)
			}
			parseEnvFile(file(".env")).forEach { (name, value) ->
				environment(name, value)
			}
		}
		config("server") {
			enabled = false
		}
	}
}

val numericMinecraftVersion = minecraftVersion.split("-").first().split(".")
	.map { it.toInt() }
	.let {
		if (it.size < 3)
			it + listOf(0)
		else if (it.size == 3)
			it
		else error("Unparsable minecraft version $minecraftVersion")
	}
	.reduce { a, b -> a * 100 + b }
println("Numeric version for $minecraftVersion is $numericMinecraftVersion")
the<PreProcessorArgs>().forDefaultCompilation {
	define("MC", numericMinecraftVersion)
	if (numericMinecraftVersion >= 12107)
		define("MC217", "true")
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
	from(project(":modern").file("templates/java"))
	rootSpec.into(layout.buildDirectory.dir("sharedModernSource"))
}
sourceSets.main {
	java {
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



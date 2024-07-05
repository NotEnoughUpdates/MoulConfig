import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask

plugins {
	java
	idea
	`maven-publish`
	alias(libs.plugins.unimined)
	alias(libs.plugins.kotlin.jvm)
//    alias(libs.plugins.kotlin.lombok)
	alias(libs.plugins.dokka)
	alias(libs.plugins.shadow)

}

unimined.minecraft {
	version(libs.versions.mc.modern.get())
	mappings {
		intermediary()
		yarn(libs.versions.yarn.build.get())
	}

	fabric {
		loader(libs.versions.fabric.loader.get())
		accessWidener(project.file("src/main/resources/moulconfig.accesswidener"))
	}
	runs {
		config("client") {
			jvmArgs.add("-Dmoulconfig.testmod=true")
			jvmArgs.add("-Dmoulconfig.warn.crash=false")
			env.putAll(parseEnvFile(file(".env")))
		}
	}
}


java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

val shadowInclude by configurations.creating
dependencies {
	implementation(project(":common"))
	"modImplementation"(fabricApi.fabricModule("fabric-command-api-v2", libs.versions.fabric.api.get()))
	"modImplementation"(fabricApi.fabricModule("fabric-resource-loader-v0", libs.versions.fabric.api.get()))
	shadowInclude(project(":common", configuration = "singleFile"))
	implementation(libs.libninepatch)
	shadowInclude(libs.libninepatch)
}

tasks.shadowJar {
	configurations = listOf(shadowInclude)
	archiveClassifier.set("dev")
}

val remapJar by tasks.named("remapJar", RemapJarTask::class) {
	archiveClassifier.set("")
	dependsOn(tasks.shadowJar)
	inputFile.set(tasks.shadowJar.flatMap { it.archiveFile })
}

tasks.processResources {
	from(project(":common").tasks.processResources)
}

tasks.jar {
	archiveClassifier.set("small")
	dependsOn(tasks.processResources)
}

val sourcesJar by tasks.creating(Jar::class) {
	from(sourceSets.main.get().allSource)
	from(project(":common").sourceSets.getByName("main").allSource)
	archiveClassifier.set("sources")
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

publishing {
	publications {
		create<MavenPublication>("maven") {
			artifact(remapJar) {
				classifier = ""
			}
			artifact(tasks.shadowJar) {
				classifier = "named"
			}
			artifact(tasks["sourcesJar"]) {
				classifier = "sources"
			}
		}
	}
}




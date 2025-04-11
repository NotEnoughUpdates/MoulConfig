import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask
import java.net.URL

plugins {
	kotlin("jvm") version (libs.versions.kotlin.get()) apply false
	alias(libs.plugins.dokka)
	alias(libs.plugins.mkdocs)
}

allprojects {
	afterEvaluate {
		tasks.withType(AbstractDokkaLeafTask::class).configureEach {
			dokkaSourceSets.configureEach {
				println("Configuring $this")
				sourceLink {
					localDirectory.set(project.file("src/"))
//					remoteUrl.set(URL("https://github.com/NotEnoughUpdates/MoulConfig/blob/${Version.hash}/${project.name}/src"))
					remoteLineSuffix.set("#L")
				}
			}
		}
	}
}
mkdocs {
	python {
		pip("mkdocs-zettelkasten:0.1.9")
	}
	strict = false
}

tasks.register("compileAllDocs", Copy::class) {
	dependsOn(tasks.mkdocsBuild)
	dependsOn(tasks.dokkaHtmlMultiModule)
	destinationDir = layout.buildDirectory.dir("allDocs").get().asFile
	from(tasks.mkdocsBuild)
	from(tasks.dokkaHtmlMultiModule.get().outputDirectory) {
		into("javadocs")
	}
}

subprojects {
	if (plugins.hasPlugin("org.jetbrains.dokka"))
		dependencies {
			"dokkaPlugin"("org.jetbrains.dokka:kotlin-as-java-plugin:1.9.10")
		}
}
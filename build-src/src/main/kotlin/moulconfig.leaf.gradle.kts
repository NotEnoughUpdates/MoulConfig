import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import xyz.wagyourtail.unimined.api.uniminedMaybe
import xyz.wagyourtail.unimined.util.sourceSets

plugins {
	`maven-publish`
	idea
	id("moulconfig.base")
	id("com.gradleup.shadow")
}

val shadowInclude by configurations.creating
dependencies {
	"implementation"(project(":common"))
	shadowInclude(project(":common", configuration = "singleFile"))
	"implementation"(Dependencies.LIB_NINE_PATCH)
	shadowInclude(Dependencies.LIB_NINE_PATCH)
}

val shadowJar by tasks.named("shadowJar", ShadowJar::class) {
	configurations = listOf(shadowInclude)
	archiveClassifier.set("dev")
}
val processResources = tasks.named("processResources", Copy::class) {
	from(project(":common").tasks.named("processResources"))
}

val sourcesJar by tasks.creating(Jar::class) {
	from(sourceSets.named("main").map { it.allSource })
	from(project(":common").the<SourceSetContainer>().getByName("main").allSource)
	archiveClassifier.set("sources")
}

configure<PublishingExtension> {
	publications {
		defaultMaven {
			artifact(shadowJar) {
				classifier = "named"
			}
			artifact(sourcesJar) {
				classifier = "sources"
			}
		}
	}
}

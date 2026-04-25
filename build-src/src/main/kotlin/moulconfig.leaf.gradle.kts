import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
	java
	`maven-publish`
	idea
	id("moulconfig.base")
	id("xyz.wagyourtail.unimined")
	id("com.gradleup.shadow")
}

val shadowInclude by configurations.creating
pluginManager.apply("java")
dependencies {
	"implementation"(project(":common"))
	shadowInclude(project(":common", configuration = "singleFile"))
	"implementation"(Dependencies.LIB_NINE_PATCH)
	shadowInclude(Dependencies.LIB_NINE_PATCH)
	compileOnly(Dependencies.JB_ANNOTATIONS)
	compileOnly(Dependencies.JSPECIFY)
	"annotationProcessor"(Dependencies.LOMBOK)
	compileOnly(Dependencies.LOMBOK)
}

val shadowJar by tasks.named("shadowJar", ShadowJar::class) {
	configurations = listOf(shadowInclude)
	archiveClassifier.set("dev")
}
val processResources = tasks.named("processResources", Copy::class) {
	from(project(":common").tasks.named("processResources"))
}

val sourcesJar by tasks.creating(Jar::class) {
	from(file("src/main/java"))
	from(project(":common").file("src/main/java"))
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

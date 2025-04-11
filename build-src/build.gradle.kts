plugins {
	`kotlin-dsl`
	kotlin("jvm") version "2.1.20"
}
repositories {
	mavenCentral()
	gradlePluginPortal()
	maven("https://oss.sonatype.org/content/repositories/snapshots")
	maven("https://maven.architectury.dev/")
	maven("https://maven.fabricmc.net")
	maven("https://maven.wagyourtail.xyz/releases")
	maven("https://maven.wagyourtail.xyz/snapshots")
	maven("https://maven.neoforged.net/releases")
	maven("https://maven.minecraftforge.net/")
	maven("https://repo.spongepowered.org/maven/")
	maven("https://repo.sk1er.club/repository/maven-releases/")
}

dependencies {
	api("xyz.wagyourtail.unimined:unimined:1.3.14")
	api("commons-io:commons-io:2.16.1")
	api("org.ow2.asm:asm-util:9.7")
	api("org.ow2.asm:asm-tree:9.7")
	api("org.ow2.asm:asm:9.7")
	api("org.ow2.asm:asm-commons:9.7")
	api("org.ow2.asm:asm-analysis:9.7")
	api("com.gradleup.shadow:shadow-gradle-plugin:9.0.0-beta12")
	api("org.jetbrains.dokka:dokka-gradle-plugin:1.9.20")
	api("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:2.1.20")
}
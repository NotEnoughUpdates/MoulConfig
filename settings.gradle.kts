pluginManagement {
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
	resolutionStrategy {
		eachPlugin {
			when (requested.id.id) {
				"gg.essential.loom" -> useModule("gg.essential:architectury-loom:${requested.version}")
			}
		}
	}
}

rootProject.name = "MoulConfig"

include("common")
include("legacy")
include("modern")
listOf(
	"1.21.4",
	"1.21.5",
	"1.21.7",
).forEach { version ->
	val modPath = "modern:$version"
	include(modPath)
	project(":$modPath").name = "modern-$version"
}
includeBuild("build-src")

plugins {
	`kotlin-dsl`
	kotlin("jvm") version "2.3.20"
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
	maven {
        url = uri("https://repo.sk1er.club/repository/maven-releases/")
        content {
            excludeGroup("xyz.wagyourtail.unimined.mapping")
        }
    }
}

dependencies {
	api("xyz.wagyourtail.unimined:unimined:1.4.2-SNAPSHOT")
    implementation("net.fabricmc:class-tweaker:0.2")
    implementation("net.fabricmc:tiny-remapper:0.9.0")
    api("xyz.wagyourtail.unimined.mapping:unimined-mapping-library-jvm:1.2.2")
	api("commons-io:commons-io:2.16.1")
    val asmVersion = "9.9.1"
	api("org.ow2.asm:asm-util:$asmVersion")
	api("org.ow2.asm:asm-tree:$asmVersion")
	api("org.ow2.asm:asm:$asmVersion")
	api("org.ow2.asm:asm-commons:$asmVersion")
	api("org.ow2.asm:asm-analysis:$asmVersion")
	api("com.gradleup.shadow:shadow-gradle-plugin:9.0.0-beta12")
	api("org.jetbrains.dokka:dokka-gradle-plugin:2.1.0")
	val kotlinVersion = "2.3.20"
	api("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:$kotlinVersion")
	api("org.jetbrains.kotlin.plugin.lombok:org.jetbrains.kotlin.plugin.lombok.gradle.plugin:$kotlinVersion")
	api("systems.manifold.manifold-gradle-plugin:systems.manifold.manifold-gradle-plugin.gradle.plugin:0.0.2-alpha")
}
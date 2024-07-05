plugins {
	`kotlin-dsl`
	kotlin("jvm") version "1.8.10"
}
repositories {
	mavenCentral()
}

dependencies {
	implementation("commons-io:commons-io:2.16.1")
	implementation("org.ow2.asm:asm-util:9.7")
	implementation("org.ow2.asm:asm-tree:9.7")
	implementation("org.ow2.asm:asm:9.7")
	implementation("org.ow2.asm:asm-commons:9.7")
	implementation("org.ow2.asm:asm-analysis:9.7")
}

sourceSets.main {
	kotlin {
		srcDir(file("src"))
	}
}
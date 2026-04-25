import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.nio.charset.StandardCharsets
import java.util.zip.ZipFile

repositories {
	mavenLocal()
	mavenCentral()
	maven("https://repo.nea.moe/releases")
	maven("https://repo.spongepowered.org/maven/")
	maven("https://maven.neoforged.net/releases")
}


group = "org.notenoughupdates.moulconfig"
version = if (Version.isSnapshot) "9999.9999.9999" else Version.tag!!

tasks.withType(JavaCompile::class) {
	options.encoding = StandardCharsets.UTF_8.name()
}

tasks.withType(ShadowJar::class).configureEach {
	relocate("juuxel.libninepatch", "io.github.notenoughupdates.moulconfig.deps.libninepatch")
}

val checkJarForKotlinRuntime by tasks.registering {
	group = "verification"
	description = "Fails if built jars contain Kotlin runtime classes or Kotlin class references."
	val jarTasks = tasks.withType(Jar::class)
	dependsOn(jarTasks)
	doLast {
		fun ByteArray.containsBytes(needle: ByteArray): Boolean {
			if (needle.isEmpty() || needle.size > size) return false
			for (i in 0..(size - needle.size)) {
				var matches = true
				for (j in needle.indices) {
					if (this[i + j] != needle[j]) {
						matches = false
						break
					}
				}
				if (matches) return true
			}
			return false
		}
		jarTasks.forEach { jarTask ->
			val jar = jarTask.archiveFile.get().asFile
			if (!jar.exists() || jarTask.archiveClassifier.orNull == "sources") return@forEach
			ZipFile(jar).use { zip ->
				val badEntries = zip.entries().asSequence()
					.map { it.name }
					.filter { it.startsWith("kotlin/") || it.startsWith("kotlinx/") || it.endsWith(".kotlin_module") }
					.toList()
				if (badEntries.isNotEmpty()) {
					error("Kotlin runtime content found in ${jar.name}: ${badEntries.take(10)}")
				}
				val badClass = zip.entries().asSequence()
					.filter { !it.isDirectory && it.name.endsWith(".class") }
					.firstOrNull { entry ->
						val bytes = zip.getInputStream(entry).readBytes()
						bytes.containsBytes("kotlin/".toByteArray()) || bytes.containsBytes("kotlin.".toByteArray()) ||
							bytes.containsBytes("kotlinx/".toByteArray()) || bytes.containsBytes("kotlinx.".toByteArray())
					}
				if (badClass != null) {
					error("Kotlin class reference found in ${jar.name}: ${badClass.name}")
				}
			}
		}
	}
}

tasks.matching { it.name == "check" }.configureEach {
	dependsOn(checkJarForKotlinRuntime)
}
afterEvaluate {
	extensions.findByType<PublishingExtension>()?.apply {
		repositories {
			if (project.hasProperty("moulconfigPassword") && !Version.isSnapshot) {
				maven {
					url = uri("https://maven.notenoughupdates.org/releases")
					name = "moulconfig"
					credentials(PasswordCredentials::class)
					authentication {
						create<BasicAuthentication>("basic")
					}
				}
			}
		}
		publications.filterIsInstance<MavenPublication>().forEach {
			it.pom {
				licenses {
					license {
						name.set("LGPL-3.0 or later")
						url.set("https://github.com/NotEnoughUpdates/NotEnoughUpdates/blob/HEAD/COPYING.LESSER")
					}
				}
				developers {
					developer {
						name.set("NotEnoughUpdates contributors")
					}
					developer {
						name.set("Linnea Gräf")
					}
				}
				scm {
					url.set("https://github.com/NotEnoughUpdates/MoulConfig")
				}
			}
		}
	}
}

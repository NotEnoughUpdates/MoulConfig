plugins {
    id("fabric-loom") version "1.2-SNAPSHOT"
    id("maven-publish")
    id("org.cadixdev.licenser") version "0.6.1"
    id("org.jetbrains.dokka") version "1.8.10"
}

version = project.properties["mod_version"]!!
group = project.properties["maven_group"]!!

base {
    archivesName.set(project.properties["archives_base_name"]!! as String)
}

repositories {
    maven { url = uri("https://maven.wispforest.io") }
}

dependencies {
    minecraft("com.mojang:minecraft:${project.properties["minecraft_version"]}")
    mappings("net.fabricmc:yarn:${project.properties["yarn_mappings"]}:v2")
    modCompileOnly("net.fabricmc:fabric-loader:${project.properties["loader_version"]}")
    modCompileOnly("net.fabricmc.fabric-api:fabric-api:${project.properties["fabric_version"]}")

    annotationProcessor("org.projectlombok:lombok:1.18.26")
    compileOnly("org.projectlombok:lombok:1.18.26")
}

tasks.getByName<ProcessResources>("processResources") {
    inputs.property("version", project.properties["version"])

    filesMatching("fabric.mod.json") {
        expand(Pair("version", project.properties["version"]))
    }
}

tasks.withType(JavaCompile::class.java).configureEach {
    this.options.release.set(17)
    this.options.encoding = "UTF-8"
}


java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

sourceSets.main {
    output.setResourcesDir("$buildDir/classes/java/main")
}

tasks.dokkaHtml {
    dokkaSourceSets {
        create("main") {
            moduleName.set("moulconfig")
            sourceRoots.from(sourceSets.main.get().allSource)
            classpath.from(tasks.compileJava.get().classpath)

            includes.from(fileTree("docs") { include("*.md") })

            sourceLink {
                localDirectory.set(file("src/main"))
            }
        }
    }
}

val noTestJar by tasks.creating(Jar::class) {
    from(zipTree(tasks.remapJar.map { it.archiveFile }))
    archiveClassifier.set("notest")
    exclude("io/github/moulberry/moulconfig/test/*")
    exclude("fabric.mod.json")
    exclude("moulconfig-refmap.json")
}

val nonMappedJar by tasks.creating(Jar::class) {
    from(zipTree(tasks.jar.map { it.archiveFile }))
    archiveClassifier.set("non-mapped")
    exclude("io/github/moulberry/moulconfig/test/*")
    exclude("fabric.mod.json")
    exclude("moulconfig-refmap.json")
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(noTestJar) {
                classifier = ""
            }
            artifact(tasks.remapJar) {
                classifier = "test"
            }
            artifact(tasks.jar) {
                classifier = "named"
            }
            artifact(nonMappedJar) {
                classifier = "dev"
            }
            pom {
                licenses {
                    license {
                        name.set("LGPL-3.0 or later")
                        url.set("https://github.com/Morazzer/MoulConfig/blob/HEAD/LICENSE")
                    }
                }
                developers {
                    developer {
                        name.set("NotEnoughUpdates contributors")
                    }
                    developer {
                        name.set("Linnea Gräf")
                    }
                    developer {
                        name.set("Morazzer")
                    }
                }
                scm {
                    url.set("https://github.com/Morazzer/MoulConfig")
                }
            }
        }
    }
    repositories {
        maven {
            url = uri("https://repo.morazzer.dev/releases")
            name = "moulconfig"
            credentials {
                username = System.getenv("moulconfigUsername")
                password = System.getenv("moulconfigPassword")
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}
plugins {
	id("moulconfig.fabric")
}

dependencies {
	"modImplementation"(fabricApi.fabricModule("fabric-command-api-v2", libs.versions.fabric.api.get()))
	"modImplementation"(fabricApi.fabricModule("fabric-resource-loader-v0", libs.versions.fabric.api.get()))
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))


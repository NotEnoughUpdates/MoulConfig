plugins {
	id("moulconfig.fabric")
}
fabricDeps {
	impl("fabric-command-api-v2")
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))


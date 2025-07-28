plugins {
	id("moulconfig.fabric")
	id("moulconfig.manifold")
}
fabricDeps {
	impl("fabric-command-api-v2")
}

preprocessorArgs.forDefaultCompilation {
	define("MC", 12107)
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))


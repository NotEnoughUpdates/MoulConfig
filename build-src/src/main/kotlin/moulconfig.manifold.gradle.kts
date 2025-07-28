plugins {
	id("systems.manifold.manifold-gradle-plugin")
}

manifold {
	manifoldVersion.set("2025.1.25")
}

dependencies {
	val manifoldSubSystems = listOf(
		"strings" to false,
		"preprocessor" to false,
	)
	manifoldSubSystems.forEach { (name, rt) ->
		if (rt)
			implementation("systems.manifold:manifold-$name-rt:${manifold.manifoldVersion.get()}")
		annotationProcessor("systems.manifold:manifold-$name:${manifold.manifoldVersion.get()}")
	}
}
extensions.create<PreProcessorArgs>("preprocessorArgs", PreProcessorArgs::class)
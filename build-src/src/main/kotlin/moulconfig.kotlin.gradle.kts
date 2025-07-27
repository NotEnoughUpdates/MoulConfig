plugins {
	id("org.jetbrains.kotlin.jvm")
	id("org.jetbrains.kotlin.plugin.lombok")
}

dependencies {
	annotationProcessor(Dependencies.LOMBOK)
	compileOnly(Dependencies.LOMBOK)
}
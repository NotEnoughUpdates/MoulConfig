tasks.withType<Test> {
    useJUnitPlatform()
}
dependencies {
    val junit5Version = "5.8.1"
    "testImplementation"("org.junit.jupiter:junit-jupiter-api:$junit5Version")
    "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:$junit5Version")
}
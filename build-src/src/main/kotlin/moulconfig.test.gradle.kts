tasks.withType<Test> {
    useJUnitPlatform()
}
dependencies {
    "testImplementation"(platform("org.junit:junit-bom:5.8.1"))
    "testImplementation"("org.junit.jupiter:junit-jupiter")
    "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
}
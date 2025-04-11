import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.maven.MavenPublication

fun PublicationContainer.defaultMaven(configure: MavenPublication.() -> Unit) {
    val publication = maybeCreate(SharedNames.MAVEN_PUBLICATION_NAME, MavenPublication::class.java)
    configure(publication)
}

import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask
import java.net.URL

plugins {
	id("moulconfig.dokka.base")
}
afterEvaluate {
	tasks.withType(AbstractDokkaLeafTask::class).configureEach {
		dokkaSourceSets.configureEach {
			println("Configuring $this")
			sourceLink {
				localDirectory.set(project.file("src/"))
				remoteUrl.set(URL("https://github.com/NotEnoughUpdates/MoulConfig/blob/${Version.hash}/${project.name}/src"))
				remoteLineSuffix.set("#L")
			}
		}
	}
}

dependencies {
	"dokkaPlugin"("org.jetbrains.dokka:kotlin-as-java-plugin:1.9.20")
}

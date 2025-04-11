import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import xyz.wagyourtail.unimined.api.minecraft.patch.fabric.FabricLikeApiExtension
import javax.inject.Inject

abstract class FabricUtils(
    val fabricVersion: String
) {
    @get:Inject
    abstract val project: Project
    val fabricApi: FabricLikeApiExtension get() = project.the()
    fun impl(name: String) {
        project.dependencies.add("modImplementation", fabricApi.fabricModule(name, fabricVersion))
    }
}

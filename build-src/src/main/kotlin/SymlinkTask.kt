import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files

abstract class SymlinkTask : DefaultTask() {
    @get:InputDirectory
    abstract val from: DirectoryProperty

    @get:OutputDirectory
    abstract val into: DirectoryProperty

    @TaskAction
    fun doSymlink() {
        val dest = into.asFile.get()
        val src = from.asFile.get()
        if (Files.isSymbolicLink(dest.toPath())) {
            return
        } else {
            dest.deleteRecursively()
        }
        Files.createSymbolicLink(dest.toPath(), src.toPath())
    }
}

import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.getByName
import javax.inject.Inject

abstract class PreProcessorArgs {
    @get:Inject
    protected abstract val project: Project

    companion object {
        val nameRegex = "^[A-Z_][A-Z_0-9]*$".toRegex()
    }

    class Args(val task: JavaCompile) {
        fun define(name: String, value: String) {
            require(name.matches(nameRegex))
            task.options.compilerArgs.add("-A$name=$value")
        }

        fun define(name: String, value: Int) {
            define(name, value.toString())
        }
    }

    fun forCompilation(task: JavaCompile, init: Args.() -> Unit): Args {
        return Args(task).also(init)
    }

    fun forDefaultCompilation(init: Args.() -> Unit): Args {
        return forCompilation(project.tasks.getByName<JavaCompile>("compileJava"), init)
    }
}


fun cmd(vararg args: String): String? {
    val process = ProcessBuilder()
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .command(*args)
        .start()
    val p = process.waitFor()
    if (p != 0) {
        println("${args.toList()} encountered error")
        return null
    }
    return process.inputStream.readAllBytes().decodeToString().trim()
}

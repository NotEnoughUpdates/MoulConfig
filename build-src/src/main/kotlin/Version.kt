object Version {
    val tag = cmd("git", "describe", "--tags", "HEAD")
    val hash = cmd("git", "rev-parse", "--short", "HEAD")!!
    val isSnapshot = tag == null || hash in tag
}

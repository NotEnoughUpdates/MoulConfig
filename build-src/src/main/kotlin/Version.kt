object Version {
    val tag = cmd("git", "describe", "--tags", "HEAD")
    val hash = cmd("git", "rev-parse", "--short", "HEAD")!!
    val shortHash = hash
    val isSnapshot = tag == null || shortHash in tag
}

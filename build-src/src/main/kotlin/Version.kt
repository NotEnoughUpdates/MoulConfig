object Version {
    val tag = System.getProperty("moulconfig.portVersion") ?: cmd("git", "describe", "--tags", "HEAD")
    val hash = cmd("git", "rev-parse", "--short", "HEAD") ?: "bd7b9aa3-source-archive"
    val shortHash = hash
    val isSnapshot = tag == null || shortHash in tag
}

plugins {
    base
    id("moulconfig.base")
    id("moulconfig.dokka.base")
}
dokka {
    modulePath.set("modern")
    moduleName.set("modern")
}

plugins {
    id("moulconfig.base")
    id("org.jetbrains.dokka")
}

dokka {
    dokkaSourceSets.configureEach {
        this.perPackageOption {
            this.matchingRegex.set(".*\\btest\\b.*")
            this.suppress.set(true)
        }
        this.sourceLink {
            this.localDirectory.set(project.file("src/"))
            this.remoteUrl("https://github.com/NotEnoughUpdates/MoulConfig/blob/${Version.hash}/${project.projectDir.relativeTo(rootProject.projectDir)}/src")
            this.remoteLineSuffix.set("#L")
        }
        println("[${project.name}] Dokka Source Set Id: ${sourceSetId.get()}")
    }
    this.moduleVersion.set(project.version.toString() + "+" + Version.shortHash)
    dokkaPublications.html {
        this.suppressInheritedMembers.set(true)
        this.suppressObviousFunctions.set(true)
    }
    pluginsConfiguration.html {
        this.homepageLink.set("https://notenoughupdates.org/MoulConfig/")
        this.separateInheritedMembers.set(true)
        this.mergeImplicitExpectActualDeclarations.set(true)
    }
}
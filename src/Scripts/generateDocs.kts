import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.DokkaConfiguration.*
import java.io.File

val dokkaMarkdown = DokkaTask().apply {
    outputDirectory.set(file("build/dokkaMarkdown"))
    format.set(org.jetbrains.dokka.DokkaConfiguration.Format.GFM)
    dokkaSourceSets {
        configureEach {
            includeNonPublic.set(false)
            skipDeprecated.set(true)
            reportUndocumented.set(true)
            skipEmptyPackages.set(true)
            noStdlibLink.set(false)
        }
    }
}

dokkaMarkdown.run()
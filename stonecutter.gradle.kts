plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.10-SNAPSHOT" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.8.1" apply false
}

stonecutter active file("active.stonecutter")

stonecutter parameters {
    constants {
        match(node.metadata.project.substringAfterLast("-"), "fabric", "neoforge", "forge")
    }
}

stonecutter tasks {
    order("publishMods")
}

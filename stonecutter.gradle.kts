plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.10-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.8.1" apply false
}

stonecutter active file("active.stonecutter")

/*
stonecutter registerChiseled tasks.register("chiseledRunDatagen", stonecutter.chiseled) {
    group = "project"
    versions { branch, _ -> branch == "api" }
    ofTask("runDatagen")
}
*/

// Runs active versions for each loader
for (node in stonecutter.tree.nodes) {
    if (!node.metadata.isActive || node.branch.id.isEmpty()) continue
    for (type in listOf("Client", "Server")) tasks.register("runActive$type${node.branch.id.upperCaseFirst()}") {
        group = "project"
        dependsOn("${node.hierarchy}:run$type")
    }
}

allprojects {
    repositories {
        mavenLocal()
        maven("https://thedarkcolour.github.io/KotlinForForge/")
        maven("https://maven.isxander.dev/releases")
        maven {
            name = "Modrinth"
            setUrl("https://api.modrinth.com/maven")
        }
        maven {
            url = uri("https://cursemaven.com")
            content {
                includeGroup("curse.maven")
            }
        }
    }
}

stonecutter parameters {
    constants {
        // TODO api
        match(branch.id, "fabric", "neoforge", "forge")
    }
}

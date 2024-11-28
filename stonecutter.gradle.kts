plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.7-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.8.1" apply false
}
stonecutter active "1.20.1" /* [SC] DO NOT EDIT */
stonecutter.automaticPlatformConstants = true

// Builds every version into `build/libs/{mod.version}/{loader}`
stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "project"
    ofTask("buildAndCollect")
}

// Builds loader-specific versions into `build/libs/{mod.version}/{loader}`
for (it in stonecutter.tree.branches) {
    if (it.id.isEmpty()) continue
    val loader = it.id.upperCaseFirst()
    stonecutter registerChiseled tasks.register("chiseledBuild$loader", stonecutter.chiseled) {
        group = "project"
        versions { branch, _ -> branch == it.id }
        ofTask("buildAndCollect")
    }
}

stonecutter registerChiseled tasks.register("chiseledPublishToMavenLocal", stonecutter.chiseled) {
    group = "project"
    ofTask("publishToMavenLocal")
}

stonecutter registerChiseled tasks.register("chiseledPublish", stonecutter.chiseled) {
    group = "project"
    ofTask("publishMods")
}

stonecutter registerChiseled tasks.register("chiseledRunDatagen", stonecutter.chiseled) {
    group = "project"
    versions { branch, _ -> branch == "api" }
    ofTask("runDatagen")
}

// Runs active versions for each loader
for (it in stonecutter.tree.nodes) {
    if (it.metadata != stonecutter.current || it.branch.id.isEmpty()) continue
    val types = listOf("Client", "Server")
    val loader = it.branch.id.upperCaseFirst()
    for (type in types) it.tasks.register("runActive$type$loader") {
        group = "project"
        dependsOn("run$type")
    }
}

allprojects {
    repositories {
        mavenLocal()
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

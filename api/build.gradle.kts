@file:Suppress("UnstableApiUsage")

plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom")
    id("com.github.johnrengelman.shadow")
    id("maven-publish")
}

val minecraft: String = stonecutter.current.version
val loader = Loader.of(stonecutter.current.project)
val base: Project = requireNotNull(stonecutter.node.sibling("")?.project) {
    "No base project for $project"
}

version = "${mod.version}+$minecraft"
group = "${mod.group}.${mod.id}.api.$loader"
base {
    archivesName.set("${mod.id}-api")
}

val shadowBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

val finishedBundle: Configuration by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

val dataBundle: Configuration by configurations.creating {
    isCanBeConsumed = (loader == Loader.Fabric)
    isCanBeResolved = (loader != Loader.Fabric)
}

repositories {
    when (loader) {
        Loader.Forge -> maven("https://maven.minecraftforge.net")
        Loader.NeoForge -> maven("https://maven.neoforged.net/releases/")
        else -> {}
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings(loom.layered {
        mappings("net.fabricmc:yarn:$minecraft+build.${base.mod.dep("yarn_build")}:v2")
        base.mod.dep("neoforge_patch").takeUnless { it.startsWith('[') }?.let {
            mappings("dev.architectury:yarn-mappings-patch-neoforge:$it")
        }
    })

    when (loader) {
        Loader.Fabric -> {
            modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")
            modImplementation(fabricApi.module("fabric-convention-tags-v1", base.mod.dep("fabric_api")))
            modImplementation(fabricApi.module("fabric-data-generation-api-v1", base.mod.dep("fabric_api")))
            modImplementation(fabricApi.module("fabric-transfer-api-v1", base.mod.dep("fabric_api")))
        }
        Loader.Forge -> {
            "forge"("net.minecraftforge:forge:$minecraft-${base.mod.dep("forge_loader")}")
        }
        Loader.NeoForge -> {
            "neoForge"("net.neoforged:neoforge:${base.mod.dep("neoforge_loader")}")
        }
    }

    if (!loader.isFabric()) {
        var fabricProject = stonecutter.node.peer("$minecraft-fabric")!!.project
        dataBundle(project(fabricProject.path, "dataBundle"))
    }
}

if (loader.isFabric()) {
    fabricApi {
        configureDataGeneration()
    }

    tasks.register<Jar>("bundleData") {
        from(fileTree("src/main/generated"))
        exclude(".cache")
        archiveClassifier = "data"
        //dependsOn("runDatagen")
    }
    artifacts {
        add(dataBundle.name, tasks.getByName("bundleData").outputs.files.singleFile) {
            builtBy(tasks.getByName("bundleData"))
        }
    }
}

loom {
    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }

    if (loader.isForge()) {
        forge.convertAccessWideners = true
    }

    runConfigs.all {
        isIdeConfigGenerated = true
        runDir = "../../../run"
        vmArgs("-Dmixin.debug.export=false")
    }
}

java {
    withSourcesJar()
    val java = if (stonecutter.eval(minecraft, ">=1.20.5"))
        JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

tasks.remapJar {
    injectAccessWidener = true
    inputFile = tasks.shadowJar.get().archiveFile
    archiveClassifier = loader.toString()
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    configurations = if (loader == Loader.Fabric) listOf(shadowBundle) else listOf(shadowBundle, dataBundle)
    archiveClassifier = "dev-shadow"
    when (loader) {
        Loader.Fabric -> exclude(".cache", "META-INF", "pack.mcmeta", "architectury.common.json", "mod/crend/dynamiccrosshairapi/internal/datagen")
        Loader.Forge -> exclude("fabric.mod.json", "META-INF/neoforge.mods.toml", "architectury.common.json")
        Loader.NeoForge -> exclude("fabric.mod.json", "META-INF/mods.toml", "architectury.common.json")
    }
}

tasks.processResources {
    properties(listOf("fabric.mod.json", "META-INF/mods.toml", "META-INF/neoforge.mods.toml", "pack.mcmeta"),
        "id" to mod.id,
        "name" to mod.name,
        "api_version" to mod.apiVersion,
        "minecraft" to base.mod.prop("mc_dep")
    )
}

artifacts {
    add(finishedBundle.name, tasks.remapJar.get().archiveFile) {
        builtBy(tasks.remapJar)
    }
}

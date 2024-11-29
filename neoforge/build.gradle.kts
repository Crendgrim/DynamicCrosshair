@file:Suppress("UnstableApiUsage")

plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.github.johnrengelman.shadow")
    id("me.modmuss50.mod-publish-plugin")
}

val loader = prop("loom.platform")!!
val minecraft: String = stonecutter.current.version
val apicommon: Project = requireNotNull(stonecutter.node.sibling("api")) {
    "No common api project for $project"
}
val apineoforge: Project = requireNotNull(stonecutter.node.sibling("api-neoforge")) {
    "No api project for $project"
}
val common: Project = requireNotNull(stonecutter.node.sibling("")) {
    "No common project for $project"
}

version = "${mod.version}+$minecraft"
group = "${mod.group}.$loader"
base {
    archivesName.set(mod.id)
}
architectury {
    platformSetupLoomIde()
    neoForge()
}

val commonBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

val shadowBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

configurations {
    compileClasspath.get().extendsFrom(commonBundle)
    runtimeClasspath.get().extendsFrom(commonBundle)
    get("developmentNeoForge").extendsFrom(commonBundle)
}

repositories {
    maven("https://maven.neoforged.net/releases/")
    maven {
        name = "Kotlin for Forge"
        setUrl("https://thedarkcolour.github.io/KotlinForForge/")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings(loom.layered {
        mappings("net.fabricmc:yarn:$minecraft+build.${common.mod.dep("yarn_build")}:v2")
        common.mod.dep("neoforge_patch").takeUnless { it.startsWith('[') }?.let {
            mappings("dev.architectury:yarn-mappings-patch-neoforge:$it")
        }
    })
    "neoForge"("net.neoforged:neoforge:${common.mod.dep("neoforge_loader")}")

    modImplementation("dev.isxander:yet-another-config-lib:${common.mod.dep("yacl")}-neoforge")

    modImplementation(name="libbamboo", group="mod.crend", version="${common.mod.dep("libbamboo")}-neoforge")
    include(name="libbamboo", group="mod.crend", version="${common.mod.dep("libbamboo")}-neoforge")

    modCompileOnly(name="autohud", group="mod.crend", version="${common.mod.dep("autohud")}-neoforge")

    api(project(apicommon.path, "namedElements")) { isTransitive = false }
    api(project(apineoforge.path, "namedElements")) { isTransitive = false }
    include(project(apineoforge.path, "finishedBundle"))

    commonBundle(project(common.path, "namedElements")) { isTransitive = false }
    shadowBundle(project(common.path, "transformProductionNeoForge")) { isTransitive = false }
}

loom {
    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }

    runConfigs.all {
        isIdeConfigGenerated = true
        runDir = "../../../run"
        vmArgs("-Dmixin.debug.export=true")
    }
}

java {
    withSourcesJar()
    val java = if (stonecutter.eval(minecraft, ">=1.20.5"))
        JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

tasks.jar {
    archiveClassifier = "dev"
}

tasks.remapJar {
    injectAccessWidener = true
    input = tasks.shadowJar.get().archiveFile
    archiveClassifier = loader
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    configurations = listOf(shadowBundle)
    archiveClassifier = "dev-shadow"
    exclude(
        "fabric.mod.json",
        "architectury.common.json",
        if (stonecutter.eval(minecraft, "<=1.20.4")) "META-INF/neoforge.mods.toml" else "META-INF/mods.toml"
    )
}

tasks.processResources {
    properties(
        listOf(
            if (stonecutter.eval(minecraft, "<=1.20.4")) "META-INF/mods.toml" else "META-INF/neoforge.mods.toml",
            "pack.mcmeta"
        ),
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "minecraft" to common.mod.prop("mc_dep_forgelike")
    )
}

tasks.build {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
}

tasks.register<Copy>("buildAndCollect") {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
    from(tasks.remapJar.get().archiveFile, tasks.remapSourcesJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}/$loader"))
    dependsOn("build")
}

if (common.mod.publish("neoforge")) {
    publishMods {
        displayName = "[NeoForge ${common.mod.prop("mc_title")}] ${mod.name} ${mod.version}"

        val modrinthToken = providers.gradleProperty("MODRINTH_TOKEN").orNull
        val curseforgeToken = providers.gradleProperty("CURSEFORGE_TOKEN").orNull
        dryRun = common.mod.publish("dryrun") || modrinthToken == null || curseforgeToken == null

        file = tasks.remapJar.get().archiveFile
        val apiFile = apineoforge.tasks.remapJar.get().archiveFile
        version = "${mod.version}+$minecraft-$loader"
        changelog = mod.prop("changelog")
        type = STABLE
        modLoaders.add(loader)

        val supportedVersions = common.mod.prop("mc_targets").split(" ")

        modrinth {
            projectId = property("publish.modrinth").toString()
            accessToken = modrinthToken
            minecraftVersions.addAll(supportedVersions)
            additionalFiles.from(apiFile)

            optional("yacl")
        }
        curseforge {
            projectId = property("publish.curseforge").toString()
            projectSlug = property("publish.curseforge_slug").toString()
            accessToken = curseforgeToken
            minecraftVersions.addAll(supportedVersions)
            additionalFiles.from(apiFile)
            clientRequired = true
            serverRequired = false

            optional("yacl")
        }
    }
}

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
val apiforge: Project = requireNotNull(stonecutter.node.sibling("api-forge")) {
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
    forge()
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
    get("developmentForge").extendsFrom(commonBundle)
}

repositories {
    maven("https://maven.minecraftforge.net")
    maven {
        name = "Kotlin for Forge"
        setUrl("https://thedarkcolour.github.io/KotlinForForge/")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings("net.fabricmc:yarn:$minecraft+build.${common.mod.dep("yarn_build")}:v2")
    "forge"("net.minecraftforge:forge:$minecraft-${common.mod.dep("forge_loader")}")
    "io.github.llamalad7:mixinextras-forge:${mod.dep("mixin_extras")}".let {
        implementation(it)
        include(it)
    }

    if (stonecutter.eval(minecraft, "<1.20.3")) {
        modRuntimeOnly("dev.isxander:yet-another-config-lib:${common.mod.dep("yacl")}-forge")
    }

    modImplementation(name="libbamboo", group="mod.crend", version="${common.mod.dep("libbamboo")}-forge")
    include(name="libbamboo", group="mod.crend", version="${common.mod.dep("libbamboo")}-forge")

    modCompileOnly(name="autohud", group="mod.crend", version="${common.mod.dep("autohud")}-forge")

    api(project(apicommon.path, "namedElements")) { isTransitive = false }
    api(project(apiforge.path, "namedElements")) { isTransitive = false }
    include(project(apiforge.path, "finishedBundle"))

    commonBundle(project(common.path, "namedElements")) { isTransitive = false }
    shadowBundle(project(common.path, "transformProductionForge")) { isTransitive = false }
}

loom {
    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }

    forge.convertAccessWideners = true
    forge.mixinConfigs(
        "dynamiccrosshair-common.mixins.json",
        "dynamiccrosshair-forge.mixins.json"
    )

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
    exclude("fabric.mod.json", "architectury.common.json")
}

tasks.processResources {
    properties(listOf("META-INF/mods.toml", "pack.mcmeta"),
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

if (common.mod.publish("forge")) {
    publishMods {
        displayName = "[Forge ${common.mod.prop("mc_title")}] ${mod.name} ${mod.version}"

        val modrinthToken = providers.gradleProperty("MODRINTH_TOKEN").orNull
        val curseforgeToken = providers.gradleProperty("CURSEFORGE_TOKEN").orNull
        dryRun = common.mod.publish("dryrun") || modrinthToken == null || curseforgeToken == null

        file = tasks.remapJar.get().archiveFile
        val apiFile = apiforge.tasks.remapJar.get().archiveFile
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

            requires("fabric-api")
            optional("dynamiccrosshaircompat")
            optional("yacl")
            optional("modmenu")
        }
        curseforge {
            projectId = property("publish.curseforge").toString()
            projectSlug = property("publish.curseforge_slug").toString()
            accessToken = curseforgeToken
            minecraftVersions.addAll(supportedVersions)
            additionalFiles.from(apiFile)
            clientRequired = true
            serverRequired = false

            requires("fabric-api")
            optional("dynamic-crosshair-compat")
            optional("yacl")
            optional("modmenu")
        }
    }
}

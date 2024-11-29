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
val apifabric: Project = requireNotNull(stonecutter.node.sibling("api-fabric")) {
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
    fabric()
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
    get("developmentFabric").extendsFrom(commonBundle)
}

repositories {
    maven("https://maven.terraformersmc.com/")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings("net.fabricmc:yarn:$minecraft+build.${common.mod.dep("yarn_build")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")

    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", common.mod.dep("fabric_api")))
    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:${common.mod.dep("fabric_api")}")

    modImplementation("dev.isxander:yet-another-config-lib:${common.mod.dep("yacl")}-fabric")

    modImplementation(name="libbamboo", group="mod.crend", version="${common.mod.dep("libbamboo")}-fabric")
    include(name="libbamboo", group="mod.crend", version="${common.mod.dep("libbamboo")}-fabric")

    modImplementation("com.terraformersmc:modmenu:${common.mod.dep("modmenu")}")

    // Compatibility because they cause a crash
    modCompileOnly("curse.maven:mythic-mounts-${common.mod.dep("mythicmounts")}")

    api(project(apicommon.path, "namedElements")) { isTransitive = false }
    api(project(apifabric.path, "namedElements")) { isTransitive = false }
    include(project(apifabric.path, "finishedBundle"))
    commonBundle(project(common.path, "namedElements")) { isTransitive = false }
    shadowBundle(project(common.path, "transformProductionFabric")) { isTransitive = false }
    //runtimeOnly(project(apicommon.path, "namedElements"))
    //runtimeOnly(project(common.path, "namedElements"))
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

tasks.shadowJar {
    configurations = listOf(shadowBundle)
    archiveClassifier = "dev-shadow"
}

tasks.remapJar {
    injectAccessWidener = true
    input = tasks.shadowJar.get().archiveFile
    archiveClassifier = loader
    dependsOn(tasks.shadowJar)
}

tasks.jar {
    archiveClassifier = "dev"
}

tasks.processResources {
    properties(listOf("fabric.mod.json"),
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "minecraft" to common.mod.prop("mc_dep_fabric")
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

if (common.mod.publish("fabric")) {
    publishMods {
        displayName = "[Fabric ${common.mod.prop("mc_title")}] ${mod.name} ${mod.version}"

        val modrinthToken = providers.gradleProperty("MODRINTH_TOKEN").orNull
        val curseforgeToken = providers.gradleProperty("CURSEFORGE_TOKEN").orNull
        dryRun = common.mod.publish("dryrun") || modrinthToken == null || curseforgeToken == null

        file = tasks.remapJar.get().archiveFile
        val apiFile = apifabric.tasks.remapJar.get().archiveFile
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
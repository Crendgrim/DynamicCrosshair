plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
}

val minecraft = stonecutter.current.version
val apiCommon: Project = requireNotNull(stonecutter.node.sibling("api")) {
    "No common api project for $project"
}

version = "${mod.version}+$minecraft"
group = "${mod.group}.common"
base {
    archivesName.set("${mod.id}-common")
}

architectury.common(stonecutter.tree.branches.mapNotNull {
    if (stonecutter.current.project !in it) null
    else if (stonecutter.current.project.startsWith("api")) null
    else it.prop("loom.platform")
})

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
}


repositories {
    maven("https://maven.isxander.dev/releases")
    flatDir {
        dirs("${rootProject.projectDir}/lib")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings("net.fabricmc:yarn:$minecraft+build.${mod.dep("yarn_build")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")
    /*
    "io.github.llamalad7:mixinextras-common:${mod.dep("mixin_extras")}".let {
        annotationProcessor(it)
        implementation(it)
    }
     */

    modImplementation(name="libbamboo", group="mod.crend.libbamboo", version="fabric-${mod.dep("libbamboo")}")
    modCompileOnly(name="autohud", group="mod.crend.autohud", version="${mod.dep("autohud")}-fabric")
    modImplementation("dev.isxander:yet-another-config-lib:${mod.dep("yacl")}-fabric")

    commonBundle(project(apiCommon.path, "namedElements")) { isTransitive = false }
    shadowBundle(project(apiCommon.path, "transformProductionFabric")) { isTransitive = false }
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/dynamiccrosshair.accesswidener")

    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }
}

java {
    withSourcesJar()
    val java = if (stonecutter.eval(minecraft, ">=1.20.5"))
        JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

tasks.build {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
}
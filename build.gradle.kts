@file:Suppress("UnstableApiUsage")

plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom")
    id("com.github.johnrengelman.shadow")
    id("me.modmuss50.mod-publish-plugin")
}

val minecraft = stonecutter.current.version
val loader = Loader.of(stonecutter.current.project)
val dcApi: Project = requireNotNull(stonecutter.node.sibling("api")?.project) {
    "No api project for $project"
}
class ModDependencies {
    operator fun get(name: String) = property("deps.$name").toString()
}
val deps = ModDependencies()
enum class DependencyLevel {
    Include,
    Implementation,
    CompileOnly
}
fun modDependency(modId: String, url: String, level: DependencyLevel) {
    val isPresent = !deps[modId].startsWith("[")
    stonecutter {
        constants {
            put(modId, isPresent)
        }
    }

    if (isPresent) {
        val resolvedUrl = url.replace("{}", deps[modId])
        dependencies {
            when (level) {
                DependencyLevel.Include -> {
                    modImplementation(resolvedUrl)
                    include(resolvedUrl)
                }

                DependencyLevel.Implementation -> modImplementation(resolvedUrl)
                DependencyLevel.CompileOnly -> modCompileOnly(resolvedUrl)
            }
        }
    }
}

version = "${mod.version}+$minecraft"
group = "${mod.group}.$loader"
base {
    archivesName.set(mod.id)
}

val shadowBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

repositories {
    mavenLocal()
    maven("https://repo.spongepowered.org/maven")

    //strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
    strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")

    when (loader) {
        Loader.Forge -> {
            maven("https://maven.minecraftforge.net")
            maven("https://thedarkcolour.github.io/KotlinForForge/")
        }

        Loader.NeoForge -> {
            maven("https://maven.neoforged.net/releases/")
            maven("https://thedarkcolour.github.io/KotlinForForge/")
        }

        Loader.Fabric -> {
            maven("https://maven.terraformersmc.com/")
        }
    }

    // load this after KotlinForForge
    maven("https://maven.isxander.dev/releases")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings(loom.layered {
        mappings("net.fabricmc:yarn:$minecraft+build.${mod.dep("yarn_build")}:v2")
        mod.dep("neoforge_patch").takeUnless { it.startsWith('[') }?.let {
            mappings("dev.architectury:yarn-mappings-patch-neoforge:$it")
        }
    })

    when (loader) {
        Loader.Fabric -> {
            modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")
        }
        Loader.Forge -> {
            "forge"("net.minecraftforge:forge:$minecraft-${mod.dep("forge_loader")}")
            "io.github.llamalad7:mixinextras-common:${mod.dep("mixin_extras")}".let {
                annotationProcessor(it)
                compileOnly(it)
            }
            "io.github.llamalad7:mixinextras-forge:${mod.dep("mixin_extras")}".let {
                implementation(it)
                include(it)
            }
            if (stonecutter.eval(minecraft, ">=1.21.6")) {
                annotationProcessor("net.minecraftforge:eventbus-validator:7.0-beta.7")
            }
        }
        Loader.NeoForge -> {
            "neoForge"("net.neoforged:neoforge:${mod.dep("neoforge_loader")}")
        }
    }

    modDependency("fabric_api", "net.fabricmc.fabric-api:fabric-api:{}", DependencyLevel.Implementation)
    modDependency("modmenu", "com.terraformersmc:modmenu:{}", DependencyLevel.Implementation)
    modDependency("autohud", "mod.crend:autohud:{}", DependencyLevel.CompileOnly)
    modDependency("libbamboo", "mod.crend:libbamboo:{}", DependencyLevel.Include)
    modDependency("yacl", "dev.isxander:yet-another-config-lib:{}", DependencyLevel.CompileOnly)
    // Compatibility because they cause a crash
    modDependency("mythicmounts", "curse.maven:mythic-mounts-{}", DependencyLevel.CompileOnly)

    api(project(dcApi.path, "namedElements")) { isTransitive = false }
    include(project(dcApi.path, "finishedBundle"))
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/dynamiccrosshair.accesswidener")

    if (loader.isForge()) {
        forge.convertAccessWideners = true
        forge.mixinConfigs(
            "dynamiccrosshair.mixins.json",
            "dynamiccrosshair.compat.mixins.json"
        )
    }

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

tasks.remapJar {
    injectAccessWidener = true
    inputFile = tasks.shadowJar.get().archiveFile
    archiveClassifier = loader.toString()
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    configurations = listOf(shadowBundle)
    archiveClassifier = "dev-shadow"
    when (loader) {
        Loader.Fabric -> exclude("META-INF", "pack.mcmeta", "architectury.common.json")
        Loader.Forge -> exclude("fabric.mod.json", "META-INF/neoforge.mods.toml", "architectury.common.json")
        Loader.NeoForge -> exclude( "fabric.mod.json", "META-INF/mods.toml", "architectury.common.json")
    }
}

tasks.processResources {
    properties(
        listOf("fabric.mod.json", "META-INF/mods.toml", "META-INF/neoforge.mods.toml", "pack.mcmeta"),
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "minecraft" to mod.prop("mc_dep")
    )

    filterVersionedMixins(layout.buildDirectory.get())
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    description = "Builds all versions and copies them to build/libs/<version>"
    from(tasks.remapJar.get().archiveFile, tasks.remapSourcesJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}/$loader"))
    dependsOn("build")
}

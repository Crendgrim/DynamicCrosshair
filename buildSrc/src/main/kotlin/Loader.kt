enum class Loader {
    Fabric,
    Forge,
    NeoForge;

    companion object {
        fun of(projectName: String): Loader {
            return requireNotNull(
                when (projectName.substringAfterLast("-")) {
                    "fabric" -> Fabric
                    "forge" -> Forge
                    "neoforge" -> NeoForge
                    else -> null
                }
            ) {
                "Unknown loader specified"
            }
        }
    }

    fun isFabric(): Boolean {
        return this == Fabric;
    }

    fun isForge(): Boolean {
        return this == Forge;
    }

    fun isNeoforge(): Boolean {
        return this == NeoForge;
    }

    override fun toString(): String {
        return super.toString().lowercase()
    }
}
# Dynamic Crosshair

Dynamic Crosshair is a Minecraft mod for the Fabric launcher.
It hides or changes the crosshair dependent on context.

## Features
* Hide the crosshair when not looking at anything.
* Hide or show it depending on what is being looked at.
* Hide or show it depending on the currently equipped item.
* Change the crosshair style for various situations.
* Highly configurable.

## Screenshots
Hides the crosshair when not targetting anything:
![Not targetting anything](screenshot1.png)

Facing a log, wielding an axe, in the default configuration.
The square tool crosshair has a dot to mark this block as breakable,
and there is a rounded bracket around it to indicate a "use" action: stripping the log.
![Targetting log](screenshot2.png)

## Installation
This mod has been written for Fabric for Minecraft 1.18.2.

Get packaged versions from [Modrinth](https://modrinth.com/mod/dynamiccrosshair) or [Curseforge](https://www.curseforge.com/minecraft/mc-mods/dynamic-crosshair)!

* requires [Fabric API](https://modrinth.com/mod/fabric-api)
* requires [ClothConfig](https://modrinth.com/mod/cloth-config)
* not required: [Mod Menu](https://modrinth.com/mod/modmenu) for ingame configuration screen

## Mod Compatibility
This mod is very strongly coupled to vanilla Minecraft and has to hardcode a lot of interaction rules.
Thus, playing on a non-vanilla world will make the advanced features (context-dependent style) unreliable.

I highly suggest my other mod [Auto HUD](https://modrinth.com/mod/autohud), which allows to dynamically hide and reveal HUD elements
based on context.

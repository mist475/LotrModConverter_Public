# LotrWorldConverter_Public
Public repository for the lotr world converter
World fixer for the LOTR mod by Mevans. This converter modifies the .DAT files from old legacy lotr-mod worlds so they can be read in the 1.16.5-version of the mod.

Currently working:
- Alignment
- Waypoint usage (only mod-ones, no custom ones)
- Various other middle-earth stats
- World-generation settings (Seed,classic middle-earth, flatworld, spawn directly into me)

Not working:
- Blocks
- Entities
- Biomes


Included in the zip-file:
- The Jar itself
- Launch scripts for Windows/linux/macOS
- Json file for the conversions

Usage instructions:
-create a new world in renewed, place this world in the same folder as the world you want to convert
-unzip zip-file such that the folder it creates is in the same folder as the world you want to convert
-open the folder and launch the .bat (windows) or .sh (linux/macOS) - file
-if there is only one legacy/renewed world found, conversion will happen automatically
-otherwise enter the number of the world you want to convert
-the end result will be placed in a new folder called $whateveryourworldwascalled_Converted

To open in renewed: Open the new folder in the same version of Renewed, have fun

This project uses JNBT (https://github.com/Morlok8k/JNBT)

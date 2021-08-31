# LotrWorldConverter_Public
Public repository for the lotr world converter
World fixer for the LOTR mod by Mevans This converter modifies the .DAT files from old legacy lotr-mod worlds so they can be read in the 1.16.5-version of the mod

Currently working: -LOTRTime,LOTR.DAT & playerdata(lotr) fixes, meaning alignment and such is ported over, no items or blocks yet

Usage instructions: -unzip zip-file such that the folder it creates is in the same folder as the world you want to convert -open the folder and launch the .bat (windows) or .sh (linux/macOS) - file -if there is only one legacy world found, conversion will happen automatically -otherwise enter the number of the world you want to convert -the end result will be placed in a new folder called $whateveryourworldwascalled_Converted

To open in renewed: create a new world, copy the contents of the converted folder into the new one (there still are some opening issues with blocks, hence the copy part instead of opening the actual folder)

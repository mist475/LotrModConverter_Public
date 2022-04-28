# LotrWorldConverter_Public
Public repository for the lotr world converter
World fixer for the LOTR mod by Mevans. This converter modifies the .DAT files from old legacy lotr-mod worlds, so they can be read in the 1.16.5-version of the mod.

Currently working:
- Player data (inventory, position, alignment, waypoint usages etc.)
- World Generation Settings (mostly, flat worlds get the default flat generation without custom parameters)

Work in Progress:
- Blocks
- Entities

Not working:
- Biomes

Included in the zip-file:
- The Jar itself
- Launch script for Windows
- Json file with settings and mappings

Prepping your world/inventory:
- Get a mount with inventory slots, mounts get ported along with the player, so you can carry more stuff to renewed (If said mount hasn't been ported over it'll become a Donkey)
- Put as many items as possible in pouches, these get turned into shulkerboxes with their inventory staying the same
- In Utumno? No problem, you'll appear in Forodwaith at the rough Coordinates of where the pits are in Legacy, so bring fur armour)
- Lotr modifiers haven't been ported yet, if you want enchanted items get vanilla enchantments
- If an item hasn't been ported over yet, it will get deleted (or changed to a similar item in some cases {poisoned Daggers to regular daggers})

How To use:
- Download the latest release of the Converter
- Create a new World in the latest version of Renewed
- Copy the world you want to Convert to the same folder as the world you just created
- Unzip the Converter and place the created folder in the same folder as these 2 worlds (see Image)
- Run the converter using the .bat file if you're on Windows or via the command line if you're on Linux or macOS(tested on linux)
- The converted world will be in a folder called $worldname_Converted, open this in renewed to see if everything went well

The Folder structure should look like this (only the last 2 folder matter, the rest can be ignored):

![InstructionsConverter_1](https://user-images.githubusercontent.com/70655895/137728941-998e6bcf-83e9-45a1-b737-157df25eacee.png)

![InstructionsConverter_2](https://user-images.githubusercontent.com/70655895/137729521-2969a7ff-a063-414b-bfb4-7a4e00410189.png)

The end result will look like this:

![image](https://user-images.githubusercontent.com/70655895/137729597-a4040637-a969-4169-9500-ab74d9cd1bcd.png)

Optional settings:
- Debug Messages, set this to 0 to disable, to 1 for basic debug (default). 2 for full debug messages, only use this one when reporting a crash
- Cache debug Messages, toggles if identical messages will be printed (default: true)
- Recursion Depth, sets the amount of stacked items you can have (only effects server owners with annoying players) (default: 7)
- Creative Mode Spawn, toggles if players spawn in creative after converting to prevent dying (default: true, when blocks are also working the default will become false)
- If you want to add support for items from other mods, simply add them to the items' dictionary in the JSON file

Crashes/Errors:
- In case you get a crash while running the Convert, please se Debug Messages to 2, run the converter again and ask for help on the #help channel on my discord server (https://discord.gg/rppMgSHaTe)
- In case you find and a different issue (specific items getting deleted, some settings not working) or you have questions feel free to ask them on my discord server

Some Pictures:

![image](https://user-images.githubusercontent.com/70655895/137734741-f2b2d62e-c1cd-4a34-b1d6-547afce3e0b5.png)

![image](https://user-images.githubusercontent.com/70655895/137734761-e293deab-9655-4265-b24a-1a810c10bbf4.png)

![image](https://user-images.githubusercontent.com/70655895/137734797-2b28ba5a-d8ea-4352-b7d3-8ec9bed9be4e.png)

![image](https://user-images.githubusercontent.com/70655895/137734828-e1c4dc77-2cf7-4b57-8104-22522dd22c26.png)

Edge cases:
- Mounts are the only entities currently supported, get them to carry more with you into renewed
- Maps are supported, you can keep your map art
- Coloured item names are supported
- Vanilla enchantments are working fully
- Drinks also work (provided said drink has been ported)
- If you're in Utumno, you log out at the rough location of the Pits in Legacy
- Waypoint usages are supported, however, custom waypoint usages are not, as these waypoints will be invalid due to the stricter waypoint requirements anyway

Have Fun in Renewed!

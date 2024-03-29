package Convertors;

import misterymob475.Data;
import org.jnbt.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static misterymob475.Main.PrintLine;

//this class fixes the regular player data (the files in the playerdata folder) and the level.dat file (mainly because playerdata is also stored in there)

/**
 * Copies and fixes the regular player data
 */
public class PlayerData implements Convertor {
    private final Data Data;
    private final Map<Integer,String> LegacyIds;

    /**
     * Creates an instance of PlayerData
     * @param data instance of {@link Data}
     * @param legacyIds Dictionary with the ind id's as key and the (old) string-id as the value
     */
    public PlayerData(Data data,Map<Integer,String> legacyIds) {
        this.Data = data;
        LegacyIds = legacyIds;
    }

    /**
     *
     * @param p path of the folder where files are copied
     * @param FileName name of the to be modified files
     * @throws IOException if something fails
     */
    @Override
    public void modifier(Path p, String FileName) throws IOException {
        //Map<Integer,String> LegacyIds = misterymob475.Data.LegacyIds(Paths.get(p + "/" + FileName+ "/level.dat").toAbsolutePath().toString());
        Map<String,List<String>> ItemNames = Data.ItemNames();
        Files.createDirectory(Paths.get(p +"/"+FileName+"_Converted/playerdata"));
        //level.dat fixer/modifier
        //File renewedWorld = new File(p+"/"+this.pathName+"/level.dat");

        //try {
            //heavier filter on here to only use the current .dat's and not the old ones
            File currentFolder = new File(Paths.get(p +"/"+FileName+"/playerdata").toString());
            File[] curDirList = currentFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".dat"));
            int i = 1;
            assert curDirList != null;
            for (File f : curDirList) {
                i++;
                final NBTInputStream input = new NBTInputStream(new FileInputStream(f));
                final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
                input.close();

                //because of the way playerdata is stored in the level.dat I have moved the fixer to a slightly different function lmao
                Map<String, Tag> originalData = originalTopLevelTag.getValue();
                //this way I can modify the map directly, instead of regenerating it every time
                Map <String, Tag> newData = new HashMap<>(originalData);
//
                playerFixer(newData, LegacyIds, ItemNames, Data);
//

                final CompoundTag newTopLevelTag = new CompoundTag("", newData);
                //(new File(Paths.get(p +"/"+FileName+"_Converted/playerdata/" + f.getName()).toString())).getAbsolutePath()
                //final NBTOutputStream output = new NBTOutputStream(new FileOutputStream(f));
                final NBTOutputStream output = new NBTOutputStream(new FileOutputStream((new File(Paths.get(p +"/"+FileName+"_Converted/playerdata/" + f.getName()).toString())).getAbsolutePath()));
                output.writeTag(newTopLevelTag);
                output.close();
                PrintLine("Converted " + (i-1) + "/ " + Objects.requireNonNull(currentFolder.listFiles()).length + " player data files",Data,true);
            }
            System.out.println("converted all the playerdata");
            /*


        }
        //took this out of an example I found, changed it as my ide wanted me to
        catch (final ClassCastException | NullPointerException ex) {
            throw new IOException("Error during playerdata fixing");
        }
             */
        //planning for playerdata:
        //note: for blocks the "Damage" ShortTag is actually the second id: regular dwarven brick = (Usually) 186 with 'Damage' 6, 'Damage' is always the same, id is not though
        //items are present in "EnderItems" & "Inventory" (+ tile entities and such ofc, but those will only get looked at once regular blocks have been converted which will take a very long time)

        // 'fixer' will have to be called recursively due to pouches/ cracker with a max conversion depth (probably 7 or something like that), might put in a pieced of paper saying max depth reached or something similar
        // will have to look up 'special tags', so I have to modify the existing stream (i.e. make a map of it and replace it)
    }


    /**
     * Fixes the player inventory
     * @param newData  {@link Map} with key {@link String} and value {@link Tag} containing the to be fixed data
     * @param legacyids {@link Map} with key {@link Integer} and value {@link String} Containing the int ids and the old string ids
     * @param itemnames {@link Map} with key {@link String} and value {@link String} Containing the old and new string ids
     * @throws IOException if something fails
     */
    public static void playerFixer(Map<String,Tag> newData, Map<Integer,String> legacyids, Map<String, List<String>> itemnames, Data Data) throws IOException {
        boolean inUtumno = false;
        //not needed in renewed
        newData.remove("ForgeData");
        //changed too much to bother with, especially as the game will recreate the property
        newData.remove("Attributes");

        if (newData.containsKey("Riding")) {
            //call to entity fixer, this means the player is riding on a mount (fixer will temporarily replace said mount with a donkey)
            Map<String,Tag> Riding = new HashMap<>(((CompoundTag)newData.get("Riding")).getValue());
            Riding = EntityData.RiderEntityFixer(Riding,legacyids,Data);
            if (Riding != null) {
                CompoundTag RootVehicle = new CompoundTag("RootVehicle",Riding);
                newData.replace("Riding",RootVehicle);
            }
            else newData.remove("Riding");
        }
        if (Data.Settings().containsKey("Creative Mode spawn")) {
            if ((Boolean) Data.Settings().get("Creative Mode spawn")) {
                newData.replace("playerGameType",new IntTag("playerGameType",1));
            }
        }


        if (newData.containsKey("EnderItems")) {
            newData.replace("EnderItems",new ListTag("EnderItems",CompoundTag.class,RecurItemFixer((((ListTag) newData.get("EnderItems")).getValue()),legacyids,itemnames, (double) 0,"Exception during Ender chest conversion", Data)));
        }
        if (newData.containsKey("Inventory")) {
            //List<Tag> Invtemp = ((ListTag) newData.get("Inventory")).getValue();
            newData.replace("Inventory",new ListTag("Inventory",CompoundTag.class,RecurItemFixer((((ListTag) newData.get("Inventory")).getValue()),legacyids,itemnames,(double) 0,"Exception during inventory conversion", Data)));
            //List<Tag> debug2 =RecurItemFixer(Invtemp,LegacyIds,ItemNames,0,"Exception during Inventory Conversion");
        }

        newData.remove("Attack Time");
        if (! newData.containsKey("DataVersion")) {newData.put("DataVersion",new IntTag("DataVersion",2586));}

        if (newData.containsKey("Dimension") ) {
            //fixer here int --> string
            Integer Dimension = ((IntTag) newData.get("Dimension")).getValue();
            String newDimension;
            if (Dimension == 0) newDimension = "minecraft:overworld";
            else if (Dimension == 1) newDimension = "Minecraft:the_nether";
            else if (Dimension == 2) newDimension = "Minecraft:the_end";
            else if (Dimension == 100) newDimension = "lotr:middle_earth";
            else if (Dimension == 101) {
                newDimension = "lotr:middle_earth"; //utumno doesn't exist yet
                inUtumno = true;
            }
            else newDimension = "minecraft:overworld";
            newData.replace("Dimension",new StringTag("Dimension",newDimension));
        }
        if (inUtumno) {
            //sets the player coordinates at the coordinates of the pit if they're currently in Utumno (roughly, they'll be moved in renewed I've heard)
            //ListTag Pos1 = (ListTag) newData.get("Pos");
            ArrayList<Tag> Pos = new ArrayList<Tag>(1) {};
            Pos.add(new DoubleTag("",46158.0));
            Pos.add(new DoubleTag("",80.0));
            Pos.add(new DoubleTag("",-40274.0));
            newData.replace("Pos",new ListTag("Pos",DoubleTag.class,Pos));

        }
        newData.remove("HealF");
        newData.remove("Sleeping");
        if (newData.containsKey("UUIDLeast")) {
            newData.put("UUID", misterymob475.Data.UUIDFixer((LongTag) newData.get("UUIDMost"),(LongTag) newData.get("UUIDLeast")));
            newData.remove("UUIDLeast");
            newData.remove("UUIDMost");
        }

    }

    /**
     * Fixes the display {@link CompoundTag} with the new formatting
     * @param display the display {@link CompoundTag} used for items
     * @return the display {@link CompoundTag}, but with fixed formatting to prevent custom names getting cut off
     */
    public static CompoundTag nameFixer(CompoundTag display, Data Data) {
        Map<String,Tag> display_map = new HashMap<>(display.getValue());
        if (display_map.containsKey("Name" )) {
            String name = (String) display_map.get("Name").getValue();
            String colour= "";
            if (name.contains("§")) {
                //Fixes coloured items, might have to fix 'Lore' items too. Not sure how those are saved yet
                if (Data.Colours().containsKey(name.substring(0,2))) {
                    colour = "," + '"'  + "color"  + '"'+ ':'+ '"'+Data.Colours().get(name.substring(0,2)) + '"';
                }
                name = name.substring(2);
            }
            if (display_map.containsKey("Name")) {
                display_map.replace("Name", new StringTag("Name",("{" + '"' + "text" + '"' +':' + '"' + name + '"'+ colour +'}')));
            }
        }
        return new CompoundTag("display",display_map);
    }

    /**
     * Recursively runs through the provided inventory (recursive because of shulkerboxes/pouches/crackers)
     * @param l {@link List} of type {@link Tag} of the given inventory
     * @param legacyids {@link Map} with key {@link Integer} and value {@link String} Containing the int ids and the old string ids
     * @param itemnames {@link Map} with key {@link String} and value {@link String} Containing the old and new string ids
     * @param depth Maximum recursive depth
     * @param exceptionMessage String printed when exception is thrown
     * @return {@link List} of type {@link Tag} of the modified inventory
     * @throws IOException if something fails
     */
    public static List<Tag> RecurItemFixer(List<Tag> l, Map<Integer,String> legacyids, Map<String, List<String>> itemnames, Double depth, String exceptionMessage, Data Data) throws IOException {
        try {
            List<Tag> builder = new ArrayList<>();

            if (depth++<(Double) Data.Settings().get("Recursion Depth")) {
                for (Tag t : l) {
                    if (! (((CompoundTag) t).getValue()).isEmpty()) {

                        ShortTag id = (ShortTag) ((CompoundTag)t).getValue().get("id");
                        //use this map instead of t and replace t with it as t is not modifiable, this map is though
                        Map<String, Tag> tMap = new HashMap<>(((CompoundTag) t).getValue());

                        //statement for pouches/cracker
                        Integer compare1 = ((int)id.getValue());
                        if (legacyids.containsKey( compare1)) {
                            if (itemnames.containsKey(legacyids.get(compare1))) {
                                List<String> item = itemnames.get(legacyids.get(compare1));
                                //recursive call 1 (Pouches)
                                if (item.get(0).equals("minecraft:shulker_box")) {
                                    if (tMap.containsKey("tag")) {
                                        Map<String,Tag> filler = new HashMap<>(((CompoundTag) tMap.get("tag")).getValue());
                                        //nameFixer
                                        if (filler.containsKey("display")) {
                                            filler.replace("display",nameFixer((CompoundTag) filler.get("display"),Data));
                                        }
                                        if (filler.containsKey("LOTRPouchData")) {
                                            Map<String,Tag> LOTRPouchData = new HashMap<>(((CompoundTag) filler.get("LOTRPouchData")).getValue());
                                            ListTag Items_tag = (ListTag) LOTRPouchData.get("Items");
                                            //
                                            List<Tag> Items = Items_tag.getValue();
                                            Items = RecurItemFixer(Items,legacyids,itemnames,depth,exceptionMessage, Data);
                                            //
                                            LOTRPouchData.replace("Items",new ListTag("Items",CompoundTag.class,Items));
                                            CompoundTag BlockEntityTag = new CompoundTag("BlockEntityTag",LOTRPouchData);
                                            filler.replace("LOTRPouchData",BlockEntityTag);
                                            tMap.replace("tag",new CompoundTag("tag",filler));
                                        }

                                    }
                                    tMap.remove("Damage");
                                    tMap.replace("id",new StringTag("id","minecraft:shulker_box"));
                                    builder.add(new CompoundTag("",tMap));

                                }

                                //recursive call 2 (Barrels/Kegs)
                                else if (item.get(0).equals("lotr:keg")) {
                                    Map<String,Tag> filler = new HashMap<>();
                                    if (tMap.containsKey("tag")) {
                                        filler = new HashMap<>(((CompoundTag) tMap.get("tag")).getValue());
                                        //nameFixer
                                        if (filler.containsKey("display")) {
                                            filler.replace("display",nameFixer((CompoundTag) filler.get("display"),Data));
                                        }
                                        Map<String,Tag> KegDroppableData_Map = new HashMap<>();
                                        if (filler.containsKey("LOTRBarrelData")) {
                                            Map<String,Tag> LOTRBarrelData = new HashMap<>(((CompoundTag) filler.get("LOTRBarrelData")).getValue());
                                            ListTag Items_tag = (ListTag) LOTRBarrelData.get("Items");
                                            //
                                            List<Tag> Items = Items_tag.getValue();
                                            Items = RecurItemFixer(Items,legacyids,itemnames,depth,exceptionMessage, Data);
                                            //
                                            LOTRBarrelData.replace("Items",new ListTag("Items",CompoundTag.class,Items));
                                            LOTRBarrelData.put("BrewingTimeTotal",new IntTag("BrewingTimeTotal",(int)(LOTRBarrelData.get("BrewingTime").getValue())));
                                            LOTRBarrelData.replace("BarrelMode",new ByteTag("KegMode",(byte)(LOTRBarrelData.get("BarrelMode")).getValue()));
                                            CompoundTag KegDroppableData = new CompoundTag("KegDroppableData",LOTRBarrelData);

                                            KegDroppableData_Map.put("KegDroppableData",KegDroppableData);
                                            CompoundTag BlockEntityTag = new CompoundTag("BlockEntityTag",KegDroppableData_Map);
                                            filler.replace("LOTRBarrelData",BlockEntityTag);
                                        }
                                    }
                                    tMap.remove("Damage");
                                    tMap.replace("id",new StringTag("id","lotr:keg"));
                                    tMap.replace("tag",new CompoundTag("tag",filler));
                                    builder.add(new CompoundTag("",tMap));
                                }



                                //Player head fixer (Apparently the game fixes this one automatically, except for custom names. So I added the full thing except the killed by message as I don't know how that is formatted)
                                else if (item.get(0).equals("minecraft:skeleton_skull")) {
                                    Map<String,Tag> filler = new HashMap<>();
                                    if (tMap.containsKey("tag")) {
                                        filler = new HashMap<>(((CompoundTag) tMap.get("tag")).getValue());
                                        if (filler.containsKey("display")) {
                                            filler.replace("display",nameFixer((CompoundTag) filler.get("display"),Data));
                                        }
                                        if (filler.containsKey("SkullOwner")) {
                                            String owner = (String) filler.get("SkullOwner").getValue();
                                            Map<String,Tag> SkullOwner = new HashMap<>();
                                            SkullOwner.put("Id",new StringTag("Name",owner));
                                            filler.replace("SkullOwner",new CompoundTag("SkullOwner",SkullOwner));
                                        }
                                    }
                                    tMap.replace("id",new StringTag("id",item.get((Short) tMap.get("Damage").getValue())));
                                    tMap.remove("Damage");
                                    tMap.replace("tag",new CompoundTag("tag",filler));
                                    builder.add(new CompoundTag("",tMap));
                                }

                                //recursive call 3? (Crackers)

                                    else if (item.size() <= 1) {

                                    //code for single id values (mostly items, stairs) here
                                    //simply carries over all the tags, except the id, which gets modified to the new one. moves the damage tag to its new location and changes it to an IntTag(was ShortTag before)
                                    if (! Objects.equals(item.get(0), "")) {
                                        boolean drink = new ArrayList<>(Arrays.asList(
                                                "lotr:ale",
                                                "lotr:apple_juice",
                                                "lotr:athelas_brew",
                                                "lotr:cactus_liqueur",
                                                "lotr:carrot_wine",
                                                "lotr:cherry_liqueur",
                                                "lotr:cider",
                                                "lotr:chocolate_drink",
                                                "lotr:dwarven_ale",
                                                "lotr:dwarven_tonic",
                                                "lotr:maple_beer",
                                                "lotr:mead",
                                                "lotr:melon_liqueur",
                                                "lotr:milk_drink",
                                                "lotr:miruvor",
                                                "lotr:morgul_draught",
                                                "lotr:perry",
                                                "lotr:rum",
                                                "lotr:soured_milk",
                                                "lotr:sweet_berry_juice",
                                                "lotr:vodka",
                                                "lotr:water_drink"
                                        )).contains(item.get(0));

                                        if (tMap.containsKey("tag")) {
                                            Map<String,Tag> filler = new HashMap<>(((CompoundTag) tMap.get("tag")).getValue());
                                            //itemFixer
                                            if (filler.containsKey("display")) {
                                                filler.replace("display",nameFixer((CompoundTag) filler.get("display"),Data));
                                            }
                                            //pipe fixer
                                            if (filler.containsKey("SmokeColour")) {
                                                Map<String,Tag> pipeMap = new HashMap<>();
                                                String color = (new ArrayList<>(Arrays.asList(
                                                        "white",
                                                        "orange",
                                                        "magenta",
                                                        "light_blue",
                                                        "yellow",
                                                        "lime",
                                                        "pink",
                                                        "gray",
                                                        "light_gray",
                                                        "cyan",
                                                        "purple",
                                                        "blue",
                                                        "brown",
                                                        "green",
                                                        "red",
                                                        "black",
                                                        "magic"
                                                ))).get((Integer) filler.get("SmokeColour").getValue());
                                                if (color.equals("magic")) pipeMap.put("magic",new ByteTag("magic",(byte)1));
                                                pipeMap.put("color",new StringTag("color",color));
                                                filler.replace("SmokeColour",new CompoundTag("pipe",pipeMap));
                                            }
                                            if (drink) {
                                                Map<String,Tag> vesselMap = new HashMap<>();
                                                if (tMap.containsKey("Damage")) {
                                                    Short Damage = (Short) tMap.get("Damage").getValue();
                                                    //Code for determining the strength of the drink
                                                    if (Damage.toString().endsWith("0")) vesselMap.put("potency",new StringTag("potency","weak"));
                                                    else if (Damage.toString().endsWith("1")) vesselMap.put("potency",new StringTag("potency","light"));
                                                    else if (Damage.toString().endsWith("2")) vesselMap.put("potency",new StringTag("potency","moderate"));
                                                    else if (Damage.toString().endsWith("3")) vesselMap.put("potency",new StringTag("potency","string"));
                                                    else if (Damage.toString().endsWith("4")) vesselMap.put("potency",new StringTag("potency","potent"));
                                                    //Code for determining the vessel (wooden mug, goblet etc.)
                                                    if (Damage < 100) vesselMap.put("type",new StringTag("type","wooden_mug"));
                                                    else if (Damage < 200) vesselMap.put("type",new StringTag("type","ceramic_mug"));
                                                    else if (Damage < 300) vesselMap.put("type",new StringTag("type","golden_goblet"));
                                                    else if (Damage < 400) vesselMap.put("type",new StringTag("type","silver_goblet"));
                                                    else if (Damage < 500) vesselMap.put("type",new StringTag("type","copper_goblet"));
                                                    else if (Damage < 600) vesselMap.put("type",new StringTag("type","wooden_cup"));
                                                    else if (Damage < 700) vesselMap.put("type",new StringTag("type","wooden_mug")); //skull cups not in yet
                                                    else if (Damage < 800) vesselMap.put("type",new StringTag("type","bottle")); //wine glasses not in yet
                                                    else if (Damage < 900) vesselMap.put("type",new StringTag("type","bottle"));
                                                    else if (Damage < 1000) vesselMap.put("type",new StringTag("type","waterskin"));
                                                    else if (Damage < 1100) vesselMap.put("type",new StringTag("type","ale_horn"));
                                                    else if (Damage < 1200) vesselMap.put("type",new StringTag("type","golden_ale_horn"));
                                                }
                                                CompoundTag vessel = new CompoundTag("vessel",vesselMap);
                                                filler.put("vessel",vessel);
                                            }
                                            //potion fixer
                                            else if (item.get(0).equals("minecraft:potion")) {
                                                if (Data.Potions().containsKey(tMap.get("Damage").getValue().toString())) {
                                                    filler.put("Potion",new StringTag("Potion",(String)(Data.Potions().get(tMap.get("Damage").getValue().toString())).get("Name")));
                                                }
                                            }
                                            //Book fixer
                                            else if (filler.containsKey("pages")) {
                                                //without this if book & quills get messed up
                                                if (Objects.equals(item.get(0), "minecraft:written_book")) {
                                                    List<Tag> pages = new ArrayList<>();
                                                    for (Tag st : (List<Tag>) filler.get("pages").getValue()) {
                                                        pages.add(new StringTag("",("{" + '"' + "text" + '"' +':' + '"' + st.getValue()  + '"'+ '}')));
                                                    }
                                                    filler.replace("pages",new ListTag("pages",StringTag.class,pages));
                                                }
                                            }
                                            //Enchantments fixer
                                            else if (filler.containsKey("ench") || filler.containsKey("StoredEnchantments")) {
                                                List<Tag> ench_filler = new ArrayList<>();
                                                if (filler.containsKey("ench")) {
                                                    //enchanted items
                                                    for (Tag ench_t : ((ListTag) filler.get("ench")).getValue()) {
                                                        Map<String,Tag> ench = new HashMap<>((((CompoundTag) ench_t).getValue()));
                                                        ench.replace("id",new StringTag("id",Data.Enchantments().get((((ShortTag) ench.get("id")).getValue().toString()))));
                                                        ench_filler.add(new CompoundTag("",ench));
                                                    }
                                                    filler.replace("ench",new ListTag("Enchantments",CompoundTag.class,ench_filler));
                                                    filler.put("Damage", (new IntTag("Damage", (((ShortTag) tMap.get("Damage")).getValue()))));
                                                }
                                                else {
                                                    //enchanted books
                                                    for (Tag ench_t : ((ListTag) filler.get("StoredEnchantments")).getValue()) {
                                                        Map<String,Tag> ench = new HashMap<>((((CompoundTag) ench_t).getValue()));
                                                        ench.replace("id",new StringTag("id",Data.Enchantments().get((((ShortTag) ench.get("id")).getValue().toString()))));
                                                        ench_filler.add(new CompoundTag("",ench));
                                                    }
                                                    filler.replace("StoredEnchantments",new ListTag("StoredEnchantments",CompoundTag.class,ench_filler));
                                                }
                                                filler.remove("LOTRRandomEnch");
                                                filler.remove("LOTRRepairCost");
                                            }
                                            //map fixer (very simple thankfully)
                                            else if (item.get(0).equals("minecraft:filled_map")) {
                                                if (tMap.containsKey("Damage")) {
                                                    filler.put("map",new IntTag("map",(int)((Short)tMap.get("Damage").getValue())));
                                                }
                                            }
                                            else if (tMap.containsKey("Damage")) {
                                                //check to prevent non-weapons to get a nbt-tag, making them unable to stack with regular items
                                                if ((((ShortTag) tMap.get("Damage")).getValue()) != 0) {
                                                    filler.put("Damage", (new IntTag("Damage", (((ShortTag) tMap.get("Damage")).getValue()))));
                                                }
                                            }
                                            tMap.replace("tag",new CompoundTag("tag",filler));
                                        }
                                        else {
                                            Map<String,Tag> filler = new HashMap<>();
                                            if (drink) {
                                                Map<String,Tag> vesselMap = new HashMap<>();
                                                if (tMap.containsKey("Damage")) {
                                                    //I know, I don't like code repetition either, but I couldn't be bothered to write a function that will only be called twice
                                                    Short Damage = (Short) tMap.get("Damage").getValue();
                                                    //Code for determining the strength of the drink
                                                    if (Damage.toString().endsWith("0")) vesselMap.put("potency",new StringTag("potency","weak"));
                                                    else if (Damage.toString().endsWith("1")) vesselMap.put("potency",new StringTag("potency","light"));
                                                    else if (Damage.toString().endsWith("2")) vesselMap.put("potency",new StringTag("potency","moderate"));
                                                    else if (Damage.toString().endsWith("3")) vesselMap.put("potency",new StringTag("potency","string"));
                                                    else if (Damage.toString().endsWith("4")) vesselMap.put("potency",new StringTag("potency","potent"));
                                                    //Code for determining the vessel (wooden mug, goblet etc.)
                                                    if (Damage < 100) vesselMap.put("type",new StringTag("type","wooden_mug"));
                                                    else if (Damage < 200) vesselMap.put("type",new StringTag("type","ceramic_mug"));
                                                    else if (Damage < 300) vesselMap.put("type",new StringTag("type","golden_goblet"));
                                                    else if (Damage < 400) vesselMap.put("type",new StringTag("type","silver_goblet"));
                                                    else if (Damage < 500) vesselMap.put("type",new StringTag("type","copper_goblet"));
                                                    else if (Damage < 600) vesselMap.put("type",new StringTag("type","wooden_cup"));
                                                    else if (Damage < 700) vesselMap.put("type",new StringTag("type","wooden_mug")); //skull cups not in yet
                                                    else if (Damage < 800) vesselMap.put("type",new StringTag("type","bottle")); //wine glasses not in yet
                                                    else if (Damage < 900) vesselMap.put("type",new StringTag("type","bottle"));
                                                    else if (Damage < 1000) vesselMap.put("type",new StringTag("type","waterskin"));
                                                    else if (Damage < 1100) vesselMap.put("type",new StringTag("type","ale_horn"));
                                                    else if (Damage < 1200) vesselMap.put("type",new StringTag("type","golden_ale_horn"));
                                                }
                                                CompoundTag vessel = new CompoundTag("vessel",vesselMap);
                                                filler.put("vessel",vessel);
                                            }
                                            //potion
                                            else if (item.get(0).equals("minecraft:potion")) {
                                                if (tMap.containsKey("Damage")) {
                                                    if (Data.Potions().containsKey(tMap.get("Damage").getValue().toString())) {
                                                        filler.put("Potion",new StringTag("Potion",(String)(Data.Potions().get(tMap.get("Damage").getValue().toString())).get("Name")));
                                                    }
                                                }
                                            }
                                            //map fixer (very simple thankfully)
                                            else if (item.get(0).equals("minecraft:filled_map")) {
                                                if (tMap.containsKey("Damage")) {
                                                    filler.put("map",new IntTag("map",(int)((Short)tMap.get("Damage").getValue())));
                                                }
                                            }
                                            else {
                                                if ((((ShortTag) tMap.get("Damage")).getValue()) != 0) {
                                                    filler.put("Damage", (new IntTag("Damage", (((ShortTag) tMap.get("Damage")).getValue()))));
                                                }
                                            }
                                            if (! filler.isEmpty()) {
                                                tMap.put("tag",new CompoundTag("tag",filler));
                                            }
                                        }

                                        //sets name to potion or splash potion
                                        if (item.get(0).equals("minecraft:potion")) {
                                            if (Data.Potions().containsKey(tMap.get("Damage").getValue().toString())) {
                                                //Boolean Splash = (boolean) (Data.Potions().get(tMap.get("Damage").getValue().toString())).get("Splash");
                                                if ((Boolean) (Data.Potions().get(tMap.get("Damage").getValue().toString())).get("Splash"))tMap.replace("id",new StringTag("id","minecraft:splash_potion"));
                                                else tMap.replace("id",new StringTag("id","minecraft:potion"));
                                            }
                                            else tMap.replace("id",new StringTag("id","minecraft:potion"));
                                        }
                                        else tMap.replace("id",new StringTag("id",item.get(0)));
                                        tMap.remove("Damage");
                                        builder.add(new CompoundTag("",tMap));
                                    }
                                    //vanilla spawn egg handler
                                    else if (legacyids.get(compare1).equals("minecraft:spawn_egg")) {
                                        //itemFixer
                                        if (tMap.containsKey("tag")) {
                                            Map<String,Tag> filler = new HashMap<>(((CompoundTag) tMap.get("tag")).getValue());
                                            if (filler.containsKey("display")) {
                                                filler.replace("display",nameFixer((CompoundTag) filler.get("display"),Data));
                                            }
                                            tMap.replace("tag",new CompoundTag("tag",filler));
                                        }
                                        if (Data.Vanilla_mob_ids().containsKey(((Short) tMap.get("Damage").getValue()).toString())) {
                                            tMap.replace("id",new StringTag("id",Data.Vanilla_mob_ids().get(((Short) tMap.get("Damage").getValue()).toString())));
                                            tMap.remove("Damage");
                                            builder.add(new CompoundTag("",tMap));
                                        }
                                        else PrintLine("No vanilla spawn Egg found for Damage value : " +tMap.get("Damage").getValue() ,Data,false);
                                    }
                                    //lotr spawn egg handler
                                    else if (legacyids.get(compare1).equals("lotr:item.spawnEgg")) {
                                        //itemFixer
                                        if (tMap.containsKey("tag")) {
                                            Map<String,Tag> filler = new HashMap<>(((CompoundTag) tMap.get("tag")).getValue());
                                            if (filler.containsKey("display")) {
                                                filler.replace("display",nameFixer((CompoundTag) filler.get("display"),Data));
                                            }
                                            tMap.replace("tag",new CompoundTag("tag",filler));
                                        }
                                        if (Data.Mod_mob_ids().containsKey(((Short) tMap.get("Damage").getValue()).toString())) {
                                            tMap.replace("id",new StringTag("id",Data.Mod_mob_ids().get(((Short) tMap.get("Damage").getValue()).toString())));
                                            tMap.remove("Damage");
                                            builder.add(new CompoundTag("",tMap));
                                        }
                                        else PrintLine("No lotr mod spawn Egg found for Damage value : " +tMap.get("Damage").getValue() ,Data,false);
                                    }
                                    else {
                                        PrintLine("No mapping found for legacy id: " + legacyids.get(compare1),Data,false);
                                    }

                                }
                                else {
                                    //code for blocks/some items here
                                    Short Damage = ((ShortTag) ((CompoundTag)t).getValue().get("Damage")).getValue();
                                    //Check if block is actually in the list and not just a placeholder
                                    if (! itemnames.get(legacyids.get(compare1)).get(Damage).equals("")) {
                                        if (tMap.containsKey("tag")) {
                                            //itemFixer
                                            Map<String,Tag> filler = new HashMap<>(((CompoundTag) tMap.get("tag")).getValue());
                                            if (filler.containsKey("display") && !filler.containsKey("SkullOwner")) {
                                                filler.replace("display",nameFixer((CompoundTag) filler.get("display"),Data));
                                            }
                                            tMap.replace("tag",new CompoundTag("tag",filler));
                                        }
                                        tMap.remove("Damage");
                                        tMap.replace("id",new StringTag("id",item.get(Damage)));
                                        builder.add(new CompoundTag("",tMap));
                                    }
                                    else PrintLine("No mapping found for " + legacyids.get(compare1) + ":" + Damage,Data,false);
                                }
                            }

                            else {
                                PrintLine("No mapping found for id: " + legacyids.get(compare1), Data,false);
                            }
                        }
                        else {
                            //this should never happen as I gather these ids dynamically
                            PrintLine("No string id found for id: " + compare1,Data,false);
                        }
                    }
                    else {
                        PrintLine("Empty tag found, skipping",Data,true);
                    }
                }
            }
            else {
                //if this actually gets triggered someone has been annoying on purpose
                System.out.println("Maximum set recursion depth reached (default = 7, defined in JSON)");
            }
            return builder;

        }
        catch (final ClassCastException | NullPointerException ex) {
            throw new IOException(exceptionMessage);
        }


        }
}

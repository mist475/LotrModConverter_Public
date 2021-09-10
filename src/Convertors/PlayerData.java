package Convertors;

import misterymob475.Data;
import lib.jnbt.jnbt.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

//this class fixes the regular player data (the files in the playerdata folder) and the level.dat file (mainly because playerdata is also stored in there)
public class PlayerData implements Convertor {
    Data Data;
    Map<Integer,String> LegacyIds;

    public PlayerData(Data data,Map<Integer,String> legacyIds) {
        this.Data = data;
        LegacyIds = legacyIds;
    }
    public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation)
            throws IOException {
        Files.walk(Paths.get(sourceDirectoryLocation))
                .forEach(source -> {
                    Path destination = Paths.get(destinationDirectoryLocation, source.toString()
                            .substring(sourceDirectoryLocation.length()));
                    try {
                        Files.copy(source, destination);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
    @Override
    public void copier(Path p, String FileName) throws IOException {
        //copies over all the files in the LOTR folder to the lotr folder
        File src = new File(Paths.get(p.toString()+"/"+FileName+"/playerdata").toString());
        File out = new File(Paths.get(p +"/"+FileName+"_Converted/playerdata").toString());
        copyDirectory(src.getAbsolutePath(), out.getAbsolutePath());
    }

    @Override
    public void modifier(Path p, String FileName) throws IOException {
        Map<Integer,String> LegacyIds = misterymob475.Data.LegacyIds(Paths.get(p + "/" + FileName+ "/level.dat").toAbsolutePath().toString());
        Map<String,List<String>> ItemNames = Data.ItemNames();
        //level.dat fixer/modifier
        //File renewedWorld = new File(p+"/"+this.pathName+"/level.dat");






        try {
            //heavier filter on here to only use the current .dat's and not the old ones
            File currentFolder = new File(Paths.get(p +"/"+FileName+"_Converted/playerdata").toString());
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
                playerFixer(newData, LegacyIds, ItemNames);
//

                final CompoundTag newTopLevelTag = new CompoundTag("", newData);
                final NBTOutputStream output = new NBTOutputStream(new FileOutputStream(f));
                output.writeTag(newTopLevelTag);
                output.close();
                System.out.println("Converted " + (i-1) + "/ " + Objects.requireNonNull(currentFolder.listFiles()).length + " player data files");

            }
            System.out.println("converted all the playerdata");
        }
        //took this out of an example I found, changed it as my ide wanted me to
        catch (final ClassCastException | NullPointerException ex) {
            throw new IOException("Error during playerdata fixing");
        }

        //planning for playerdata:
        //note: for blocks the "Damage" ShortTag is actually the second id: regular dwarven brick = (Usually) 186 with 'Damage' 6, 'Damage' is always the same, id is not though
        //items are present in "EnderItems" & "Inventory" (+ tile entities and such ofc, but those will only get looked at once regular blocks have been converted which will take a very long time)

        // 'fixer' will have to be called recursively due to pouches/ cracker with a max conversion depth (probably 7 or something like that), might put in a pieced of paper saying max depth reached or something similar
        // will have to look up 'special tags', so I have to modify the existing stream (i.e. make a map of it and replace it)
    }



    public static void playerFixer(Map<String,Tag> newData, Map<Integer,String> legacyids, Map<String, List<String>> itemnames) throws IOException {
        boolean inUtumno = false;
        //not needed in renewed
        newData.remove("ForgeData");
        //changed too much to bother with, especially as the game will (hopefully) recreate the property
        newData.remove("Attributes");
        //if (newData.containsKey("Riding")) {
            //call to entity fixer, this means the player is riding on a mount (fixer will temporarily replace said mount with a donkey)
            newData.remove("Riding");
        //}
        if (newData.containsKey("EnderItems")) {
            newData.replace("EnderItems",new ListTag("EnderItems",CompoundTag.class,RecurItemFixer((((ListTag) newData.get("EnderItems")).getValue()),legacyids,itemnames,0,"Exception during Ender chest conversion")));
        }
        if (newData.containsKey("Inventory")) {
            //List<Tag> Invtemp = ((ListTag) newData.get("Inventory")).getValue();
            newData.replace("Inventory",new ListTag("Inventory",CompoundTag.class,RecurItemFixer((((ListTag) newData.get("Inventory")).getValue()),legacyids,itemnames,0,"Exception during inventory conversion")));
            //List<Tag> debug2 =RecurItemFixer(Invtemp,LegacyIds,ItemNames,0,"Exception during Inventory Conversion");
        }
        newData.remove("Attack Time");
        newData.put("DataVersion",new IntTag("DataVersion",2586));
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
            ArrayList Pos = new ArrayList(1) {};
            Pos.add(new DoubleTag("",42000.0));
            Pos.add(new DoubleTag("",80.0));
            Pos.add(new DoubleTag("",43000));
            newData.replace("Pos",new ListTag("Pos",DoubleTag.class,Pos));

        }
        newData.remove("HealF");
        newData.remove("Sleeping");
        if (newData.containsKey("UUIDLeast")) {
            Long UUIDLeast = ((LongTag) newData.get("UUIDLeast")).getValue();
            Long UUIDMost = ((LongTag) newData.get("UUIDMost")).getValue();
            newData.remove("UUIDLeast");
            newData.remove("UUIDMost");
            newData.put("UUID",new IntArrayTag("UUID",new int[]{Long.valueOf(Long.toHexString(UUIDMost).substring(0,8),16).intValue(),Long.valueOf(Long.toHexString(UUIDMost).substring(8),16).intValue(),Long.valueOf(Long.toHexString(UUIDLeast).substring(0,8),16).intValue(),Long.valueOf(Long.toHexString(UUIDLeast).substring(8),16).intValue()}));
        }
    }




    public static List<Tag> RecurItemFixer(List<Tag> l, Map<Integer,String> legacyids, Map<String, List<String>> itemnames, Integer depth, String exceptionMessage) throws IOException {
        try {
            List<Tag> builder = new ArrayList<>();
            if (depth++<7) {
                for (Tag t : l) {
                    if (t.getClass() == CompoundTag.class) {
                        ShortTag id = (ShortTag) ((CompoundTag)t).getValue().get("id");
                        //use this map instead of t and replace t with it as t is not modifiable, this map is though
                        Map<String, Tag> tMap = new HashMap<>(((CompoundTag) t).getValue());
                        //statement for pouches/cracker

                        if (legacyids.containsKey((int) id.getValue())) {

                            if (itemnames.containsKey(legacyids.get((int)id.getValue()))) {
                                List<String> item = itemnames.get(legacyids.get((int)id.getValue()));
                                //recursive call
                                if (item.get(0).equals("minecraft:shulker_box")) {
                                    Map<String,Tag> filler = new HashMap<>(((CompoundTag) tMap.get("tag")).getValue());
                                    Map<String,Tag> LOTRPouchData = new HashMap<>(((CompoundTag) filler.get("LOTRPouchData")).getValue());
                                    ListTag Items_tag = (ListTag) LOTRPouchData.get("Items");
                                    //
                                    List<Tag> Items = Items_tag.getValue();
                                    Items = RecurItemFixer(Items,legacyids,itemnames,depth,exceptionMessage);
                                    //
                                    LOTRPouchData.replace("Items",new ListTag("Items",CompoundTag.class,Items));
                                    CompoundTag BlockEntityTag = new CompoundTag("BlockEntityTag",LOTRPouchData);
                                    filler.replace("LOTRPouchData",BlockEntityTag);
                                    tMap.remove("Damage");
                                    tMap.replace("id",new StringTag("id","minecraft:shulker_box"));
                                    tMap.replace("tag",new CompoundTag("tag",filler));
                                    builder.add(new CompoundTag("",tMap));

                                }

                                else if (item.size() <= 1) {
                                    //code for items here
                                    //simply carries over all the tags, except the id, which gets modified to the new one. moves the damage tag to its new location and changes it to an IntTag(was ShortTag before)
                                    if (!Objects.equals(item.get(0), "")) {

                                        if (tMap.containsKey("tag")) {
                                            Map<String,Tag> filler = new HashMap<>(((CompoundTag) tMap.get("tag")).getValue());
                                            filler.put("Damage",(new IntTag("Damage",((int)((ShortTag) tMap.get("Damage")).getValue()))));
                                            tMap.replace("tag",new CompoundTag("tag",filler));
                                        }
                                        else {
                                            Map<String,Tag> filler = new HashMap<>();
                                            filler.put("Damage",(new IntTag("Damage",((int)((ShortTag) tMap.get("Damage")).getValue()))));
                                            tMap.put("",new CompoundTag("tag",filler));
                                        }

                                        tMap.remove("Damage");
                                        tMap.replace("id",new StringTag("id",item.get(0)));
                                        builder.add(new CompoundTag("",tMap));
                                        //System.out.println(item.get(0));
                                    }
                                }
                                else {
                                    //code for blocks here
                                    Short Damage = ((ShortTag) ((CompoundTag)t).getValue().get("Damage")).getValue();
                                    //Check if block is actually in the list and not just a placeholder
                                    //System.out.println(itemnames.get(legacyids.get((int)id.getValue())).get(Damage));
                                    if (! itemnames.get(legacyids.get((int)id.getValue())).get(Damage).equals("")) {
                                        tMap.remove("Damage");
                                        tMap.replace("id",new StringTag("id",item.get(Damage)));
                                        builder.add(new CompoundTag("",tMap));
                                    }
                                    //System.out.println(item);
                                }

                                //System.out.println(itemnames.get(legacyids.get((int)id.getValue())));
                            }
                            else {
                                //Debug msg
                                System.out.println("No mapping found for id: " + legacyids.get((int)id.getValue()));
                                //tMap.remove(id);
                            }
                        }
                        else {
                            //this should never happen as I gather these ids dynamically
                            System.out.println("No string id found for id: " + id.getValue());
                            //tMap.remove(id);
                        }
                        //no easy replacement apparently
                        //l.remove(t);

                        //t = new CompoundTag("",tMap);
                    }

                }
            }
            else {
                //if this actually gets triggered someone has been annoying on purpose
                System.out.println("Maximum set recursion depth reached (default = 7, code has to be recompiled for change)");
            }
            return builder;
        }
        catch (final ClassCastException | NullPointerException ex) {
            throw new IOException(exceptionMessage);
        }
        }

}

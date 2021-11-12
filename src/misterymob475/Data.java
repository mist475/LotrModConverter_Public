package misterymob475;

import de.piegames.nbt.*;
import de.piegames.nbt.stream.NBTInputStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Manages the mappings
 */
public class Data {
    public volatile List<String> stringCache = new ArrayList<>();
    public Map<String,String> Waypoints;
    public Map<Integer,String> LegacyIds;
    public Map<String,String> Colours;
    public Map<String,?> Settings;
    public Map<String,String> FacNames;
    public Map<String,String> Mod_mob_ids;
    public Map<String,String> Vanilla_mob_ids;
    public Map<String,String> Entities;
    public Map<String,String> Regions;
    public Map<String, List<String>> ItemNames;
    public Map<String,String> Enchantments;
    public Map<String,Map<String,?>> Potions;
    public List<String> AuthorBlacklist;
    public List<String> TitleBlacklist;

    /**
     * Initializes Data
     * @param conversions JSON loaded LinkedTreeMap containing the mappings
     */
    @SuppressWarnings("unchecked")
    public Data(Map<?,?> conversions) {
        this.Waypoints = (Map<String, String>) conversions.get("Waypoints");
        this.Colours = (Map<String,String>) conversions.get("Colours");
        this.Settings = (Map<String, ?>) conversions.get("Settings");
        this.FacNames = (Map<String, String>) conversions.get("Factions");
        this.Vanilla_mob_ids = (Map<String, String>) conversions.get("Vanilla_mob_ids");
        this.Mod_mob_ids = (Map<String, String>) conversions.get("Mod_mob_ids");
        this.Entities = (Map<String, String>) conversions.get("Entities");
        this.Regions = (Map<String, String>) conversions.get("Regions");
        this.ItemNames = (Map<String, List<String>>) conversions.get("Items");
        this.Enchantments = (Map<String,String>) conversions.get("Enchantments");
        this.Potions = (Map<String,Map<String,?>>) conversions.get("Potions");
        this.AuthorBlacklist = (List<String>) conversions.get("AuthorBlacklist");
        this.TitleBlacklist = (List<String>) conversions.get("TitleBlacklist");
    }


    //legacy id HashMap generator, ids can vary, hence the dynamic generation

    /**
     * Dynamically creates and returns a Map containing the int ids and the old string ids
     * @param levelDat Path of the old level.dat file
     * @throws IOException if something fails
     */
    @SuppressWarnings("unchecked")
    public void LegacyIds(String levelDat) throws IOException {
        HashMap<Integer,String> LegacyIds_builder = new HashMap<>();
        try {
            final NBTInputStream input = new NBTInputStream(new FileInputStream(levelDat));
            final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
            input.close();

            List<CompoundTag> ItemDataList = ((ListTag<CompoundTag>) ((CompoundTag) (originalTopLevelTag.getValue()).get("FML")).getValue().get("ItemData")).getValue();
            for (CompoundTag t : ItemDataList) {
                LegacyIds_builder.put(((IntTag) t.getValue().get("V")).getValue(),((StringTag) t.getValue().get("K")).getValue().substring(1));
            }

            System.out.println("got legacy id's");
        }
        //took this out of an example I found, changed it as my ide wanted me to
        catch (final ClassCastException | NullPointerException ex) {
            throw new IOException("Error during legacy id gathering");
        }
        this.LegacyIds = LegacyIds_builder;
    }

    //renewed id HashMap generator, ids can vary, hence the dynamic generation
    //will not be used though as apparently I forgot to actually check how the stuff is saved (as strings, not as int, though the ints are also saved)

    /**
     * Dynamically creates and returns a map containing the new string ids and the new int ids
     * @param levelDat Path of the new level.dat file
     * @return Map with key String and value Integer containing the new string ids and the new int ids (used for blocks)
     * @throws IOException if something fails
     */
    @SuppressWarnings("unchecked")
    public static HashMap<String,Integer> RenewedIds(String levelDat) throws IOException {
        HashMap<String,Integer> RenewedIds = new HashMap<>();
        try {
            final NBTInputStream input = new NBTInputStream(new FileInputStream(levelDat));
            final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
            input.close();

            CompoundMap originalData = originalTopLevelTag.getValue();
            CompoundTag fml = (CompoundTag) originalData.get("fml");
            CompoundTag Registries = (CompoundTag) fml.getValue().get("Registries");
            CompoundTag minecraft_item = (CompoundTag) Registries.getValue().get("minecraft:block");
            ListTag<CompoundTag> ids = (ListTag<CompoundTag>) minecraft_item.getValue().get("ids");
            List<CompoundTag> ids_List = ids.getValue();
            //showcase Map<String, Tag> originalData = originalTopLevelTag.getValue();
            //showcase CompoundTag fml = (CompoundTag) originalData.get("fml");
            //showcase CompoundTag Registries = (CompoundTag) fml.getValue().get("Registries");
            //showcase CompoundTag minecraft_item = (CompoundTag) Registries.getValue().get("minecraft:item");
            //showcase ListTag ids = (ListTag) minecraft_item.getValue().get("ids");
            //showcase List<Tag> ids_List = ids.getValue();
            //showcase List<Tag> ItemDataList = ((ListTag) ((CompoundTag) (originalTopLevelTag.getValue()).get("FML")).getValue().get("ItemData")).getValue();
            for (CompoundTag t : ids_List) {
                RenewedIds.put(((StringTag) t.getValue().get("K")).getValue(),((IntTag) t.getValue().get("V")).getValue());
            }

            System.out.println("got renewed id's");
        }
        //took this out of an example I found, changed it as my ide wanted me to
        catch (final ClassCastException | NullPointerException ex) {
            throw new IOException("Error during renewed id gathering");
        }
        return RenewedIds;
    }
    /*
    A note on blocks:
    Block storage isn't as easy as it used to be, it is however probably more efficient which is why it was changed over versions.
    To grasp the current system this wiki page was extremely helpful:
    https://wiki.vg/Chunk_Format#Compacted_data_array
    transformation will require looping through blockData and blocks at the same time
     */


    //instructions for calling:
    //make 1 per page instead of loading it everytime
    //make this null after the last usage
    //error handling, use ifExists() on the HashMap itself, check if list items != ""



}


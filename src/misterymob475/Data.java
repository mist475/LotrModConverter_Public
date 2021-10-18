package misterymob475;

import org.jnbt.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Manages the mappings
 */
public class Data {
    private final Map<?,?> Conversions;

    /**
     * Initializes Data
     * @param conversions JSON loaded LinkedTreeMap containing the mappings
     */
    public Data(Map<?,?> conversions) {
        this.Conversions = conversions;
    }

    public List<String> stringCache = new ArrayList<>();

    /**
     * Returns a Map of the waypoints
     * @return Map with key String and value String with the waypoint mappings
     */
    public Map<String,String> Waypoints() {
        //missing:
        //lotr:ajtiaz_an_ahar
        //lotr:hirluins_halls
        return (Map<String, String>) Conversions.get("Waypoints");
     }

    /**
     * Map of the colour for fixing text
     * @return Map with new colour names for use in json
     */
    public Map<String,String> Colours() {
        return (Map<String,String>) Conversions.get("Colours");
    }
     public Map<String,?> Settings() {
        return (Map<String, ?>) Conversions.get("Settings");
     }

    /**
     * returns a Map of the faction names
     * @return Map with key String and value String with the FactionName mappings
     */
    public Map<String,String> FacNames() {
        return (Map<String, String>) Conversions.get("Factions");

    }

    /**
     * returns a Map of the lotr mod spawn egg mappings
     * @return Map with key String and value String with the lotr mop spawn egg mappings
     */
    public Map<String,String> Mod_mob_ids() {
        return (Map<String, String>) Conversions.get("Mod_mob_ids");

    }
    /**
     * returns a Map of the vanilla spawn egg mappings
     * @return Map with key String and value String with the vanilla spawn egg mappings
     */
    public Map<String,String> Vanilla_mob_ids() {
        return (Map<String, String>) Conversions.get("Vanilla_mob_ids");

    }
    /**
     * returns a Map of the entity names
     * @return Map with key String and value String with the Entity names
     */
    public Map<String,String> Entities() {
        return (Map<String, String>) Conversions.get("Entities");

    }

    /**
     * returns a Map of the regions
     * @return Map with key String and value String with the Region mappings
     */
    public Map<String,String> Regions() {
        return (Map<String, String>) Conversions.get("Regions");
    }
    //returns a list, list pos = damage value = variant
    //if no value is present, code will be commented

    //legacy id HashMap generator, ids can vary, hence the dynamic generation

    /**
     * Dynamically creates and returns a Map containing the int ids and the old string ids
     * @param levelDat Path of the old level.dat file
     * @return Map with key Integer and value String containing the int ids and the old string ids
     * @throws IOException if something fails
     */
    public static Map<Integer,String> LegacyIds(String levelDat) throws IOException {
        HashMap<Integer,String> LegacyIds = new HashMap<>();
        try {
            final NBTInputStream input = new NBTInputStream(new FileInputStream(levelDat));
            final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
            input.close();

            List<Tag> ItemDataList = ((ListTag) ((CompoundTag) (originalTopLevelTag.getValue()).get("FML")).getValue().get("ItemData")).getValue();
            for (Tag t : ItemDataList) {
                LegacyIds.put(((IntTag) ((CompoundTag)t).getValue().get("V")).getValue(),((StringTag) ((CompoundTag)t).getValue().get("K")).getValue().substring(1));
            }

            System.out.println("got legacy id's");
        }
        //took this out of an example I found, changed it as my ide wanted me to
        catch (final ClassCastException | NullPointerException ex) {
            throw new IOException("Error during legacy id gathering");
        }
        return LegacyIds;
    }

    //renewed id HashMap generator, ids can vary, hence the dynamic generation
    //will not be used though as apparently I forgot to actually check how the stuff is saved (as strings, not as int, though the ints are also saved)

    /**
     * Dynamically creates and returns a map containing the new string ids and the new int ids
     * @param levelDat Path of the new level.dat file
     * @return Map with key String and value Integer containing the new string ids and the new int ids (used for blocks)
     * @throws IOException if something fails
     */
    public static HashMap<String,Integer> RenewedIds(String levelDat) throws IOException {
        HashMap<String,Integer> RenewedIds = new HashMap<>();
        try {
            final NBTInputStream input = new NBTInputStream(new FileInputStream(levelDat));
            final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
            input.close();

            Map<String, Tag> originalData = originalTopLevelTag.getValue();
            CompoundTag fml = (CompoundTag) originalData.get("fml");
            CompoundTag Registries = (CompoundTag) fml.getValue().get("Registries");
            CompoundTag minecraft_item = (CompoundTag) Registries.getValue().get("minecraft:block");
            ListTag ids = (ListTag) minecraft_item.getValue().get("ids");
            List<Tag> ids_List = ids.getValue();
            //showcase Map<String, Tag> originalData = originalTopLevelTag.getValue();
            //showcase CompoundTag fml = (CompoundTag) originalData.get("fml");
            //showcase CompoundTag Registries = (CompoundTag) fml.getValue().get("Registries");
            //showcase CompoundTag minecraft_item = (CompoundTag) Registries.getValue().get("minecraft:item");
            //showcase ListTag ids = (ListTag) minecraft_item.getValue().get("ids");
            //showcase List<Tag> ids_List = ids.getValue();
            //showcase List<Tag> ItemDataList = ((ListTag) ((CompoundTag) (originalTopLevelTag.getValue()).get("FML")).getValue().get("ItemData")).getValue();
            for (Tag t : ids_List) {
                RenewedIds.put(((StringTag) ((CompoundTag)t).getValue().get("K")).getValue(),((IntTag) ((CompoundTag)t).getValue().get("V")).getValue());
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
    //error handling, use ifExists() on the HasMap itself, check if list items != ""

    /**
     * returns a map, with a String as the Key and a String List as the value, with the item mappings
     * @return Map with key String and value List of Type String with the item mappings
     */
    public Map<String, List<String>> ItemNames() {
        return (Map<String, List<String>>) Conversions.get("Items");

    }

    /**
     * returns a dict with int id to string id
     * @return a string dict
     */
    public Map<String,String> Enchantments() {
        return (Map<String,String>) Conversions.get("Enchantments");
    }

    public Map<String,Map<String,?>> Potions() {
        return (Map<String,Map<String,?>>) Conversions.get("Potions");
    }
    /**
     * Function which returns a new IntArrayTag based off the given LongTags and name
     * @param UUIDLeast {@link LongTag}
     * @param UUIDMost {@link LongTag}
     * @param name {@link String} name
     * @return {@link IntArrayTag} with given name and param inputs
     */
    public static IntArrayTag UUIDFixer(LongTag UUIDMost, LongTag UUIDLeast, String name) {
        //Creates the UUID in the new format based with name being the name of the intArrayTag
        //Might have reversed the order though
        long v1 = UUIDMost.getValue();
        long v2 = UUIDLeast.getValue();
        return new IntArrayTag(name,new int[]{(int)(v1 >> 32),(int)v1,(int)(v2 >> 32),(int)v2});
    }

    //Overload for when no special name is required (name is "UUID")
    /**
     * Overload for when name is "UUID"
     * @param UUIDLeast {@link LongTag}
     * @param UUIDMost {@link LongTag}
     * @return {@link IntArrayTag} with name "UUID" and param inputs
     */
    public static IntArrayTag UUIDFixer(LongTag UUIDMost, LongTag UUIDLeast) {
        return UUIDFixer(UUIDMost,UUIDLeast,"UUID");
    }

    /**
     * Overload for StringTags
     * @param UUID_t {@link StringTag}
     * @param name String
     * @return {@link IntArrayTag} with name as name and param inputs
     */
    public static IntArrayTag UUIDFixer(StringTag UUID_t, String name) {
            UUID uuid = UUID.fromString(UUID_t.getValue());
            return UUIDFixer(new LongTag("",uuid.getMostSignificantBits()),new LongTag("",uuid.getLeastSignificantBits()),name);
    }

}


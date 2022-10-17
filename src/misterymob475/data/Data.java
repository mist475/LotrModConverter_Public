package misterymob475.data;

import com.google.gson.Gson;
import de.piegames.nbt.*;
import de.piegames.nbt.stream.NBTInputStream;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the mappings
 */
public class Data {
    private static Data INSTANCE;
    public Map<String, String> waypoints;
    public Map<Integer, String> legacyIds;
    public Map<String, String> colours;
    public Map<String, String> facNames;
    public Map<String, String> modMobIds;
    public Map<String, String> vanillaMobIds;
    public Map<String, String> entities;
    public Map<String, String> regions;
    public Map<String, List<String>> itemNames;
    public Map<String, String> enchantments;
    public List<String> authorBlacklist;
    public List<String> titleBlacklist;
    public Map<String, String> blockIdToName;
    public Map<String, String> blockEntityMappings;
    public Map<String, Conversions.Potion> potions;
    public misterymob475.data.Settings settings;
    public Map<String, Map<String, Conversions.BlockMapping>> blockMappings;

    private Data() {
        Path path = Paths.get("Conversions.json");
        try {
            Reader reader = Files.newBufferedReader(path);
            Gson gson = new Gson();
            Conversions conversions = gson.fromJson(reader, Conversions.class);
            this.waypoints = conversions.getWaypoints();
            this.colours = conversions.getColours();
            this.settings = conversions.getSettings();
            this.facNames = conversions.getFactions();
            this.vanillaMobIds = conversions.getVanillaMobIds();
            this.modMobIds = conversions.getLotrModMobIds();
            this.entities = conversions.getEntities();
            this.regions = conversions.getRegions();
            this.itemNames = conversions.getItems();
            this.enchantments = conversions.getEnchantmentIds();
            this.potions = conversions.getPotions();
            this.authorBlacklist = conversions.getAuthorBlackList();
            this.titleBlacklist = conversions.getTitleBlacklist();
            this.blockMappings = conversions.getBlockMappings();
            this.blockIdToName = conversions.getBlockIdToName();
            this.blockEntityMappings = conversions.getBlockEntityMappings();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException("Error initializing json file, closing program");
        }
    }

    public static Data getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Data();
        }
        return INSTANCE;
    }

    /**
     * Dynamically creates and returns a map containing the new string ids and the new int ids
     *
     * @param levelDat Path of the new level.dat file
     * @return Map with key String and value Integer containing the new string ids and the new int ids (used for blocks)
     * @throws IOException if something fails
     */
    @SuppressWarnings("unchecked")
    public static HashMap<String, Integer> renewedIds(String levelDat) throws IOException {
        HashMap<String, Integer> RenewedIds = new HashMap<>();
        try {
            final NBTInputStream input = new NBTInputStream(Files.newInputStream(Paths.get(levelDat)));
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
                RenewedIds.put(((StringTag) t.getValue().get("K")).getValue(), ((IntTag) t.getValue()
                        .get("V")).getValue());
            }

            System.out.println("got renewed id's");
        }
        //took this out of an example I found, changed it as my ide wanted me to
        catch (final ClassCastException | NullPointerException ex) {
            throw new IOException("Error during renewed id gathering");
        }
        return RenewedIds;
    }

    /**
     * Dynamically creates and returns a Map containing the int ids and the old string ids
     *
     * @param levelDat Path of the old level.dat file
     * @throws IOException if something fails
     */
    @SuppressWarnings("unchecked")
    public void legacyIds(String levelDat) throws IOException {
        HashMap<Integer, String> LegacyIds_builder = new HashMap<>();
        try {
            final NBTInputStream input = new NBTInputStream(Files.newInputStream(Paths.get(levelDat)));
            final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
            input.close();

            List<CompoundTag> ItemDataList = ((ListTag<CompoundTag>) ((CompoundTag) (originalTopLevelTag.getValue()).get("FML")).getValue()
                    .get("ItemData")).getValue();
            for (CompoundTag t : ItemDataList) {
                LegacyIds_builder.put(((IntTag) t.getValue().get("V")).getValue(), ((StringTag) t.getValue()
                        .get("K")).getValue().substring(1));
            }

            System.out.println("got legacy id's");
        }
        //took this out of an example I found, changed it as my ide wanted me to
        catch (final ClassCastException | NullPointerException ex) {
            throw new IOException("Error during legacy id gathering");
        }
        this.legacyIds = LegacyIds_builder;
    }
}


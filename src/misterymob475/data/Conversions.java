package misterymob475.data;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Conversions {
    @SerializedName("Additional Comments")
    private final String comments;
    @SerializedName("AuthorBlacklist")
    private final List<String> authorBlackList;
    @SerializedName("BlockEntityMappings")
    private final Map<String, String> blockEntityMappings;

    @SerializedName("BlockMappings")
    private final Map<String, Map<String, BlockMapping>> blockMappings;

    @SerializedName("Colours")
    private final Map<String, String> colours;
    @SerializedName("Enchantments")
    private final Map<String, String> enchantmentIds;
    @SerializedName("Entities")
    private final Map<String, String> entities;
    @SerializedName("Factions")
    private final Map<String, String> factions;
    @SerializedName("Items")
    private final Map<String, List<String>> items;
    @SerializedName("Mod_mob_ids")
    private final Map<String, String> lotrModMobIds;
    @SerializedName("Potions")
    private final Map<String, Potion> potions;
    @SerializedName("Regions")
    private final Map<String, String> regions;

    @SerializedName("Settings")
    private final Settings settings;
    @SerializedName("Text_Formatting")
    private final Map<String, Object> textFormatting;
    @SerializedName("TitleBlacklist")
    private final List<String> titleBlacklist;
    @SerializedName("Vanilla_mob_ids")
    private final Map<String, String> vanillaMobIds;
    @SerializedName("Waypoints")
    private final Map<String, String> waypoints;

    public Conversions(String comments, List<String> authorBlackList, Map<String, String> blockEntityMappings, Map<String, Map<String, BlockMapping>> blockMappings, Map<String, String> colours, Map<String, String> enchantmentIds, Map<String, String> entities, Map<String, String> factions, Map<String, List<String>> items, Map<String, String> lotrModMobIds, Map<String, Potion> potions, Map<String, String> regions, Settings settings, Map<String, Object> textFormatting, List<String> titleBlacklist, Map<String, String> vanillaMobIds, Map<String, String> waypoints) {
        this.comments = comments;
        this.authorBlackList = authorBlackList;
        this.blockEntityMappings = blockEntityMappings;
        this.blockMappings = blockMappings;
        this.colours = colours;
        this.enchantmentIds = enchantmentIds;
        this.entities = entities;
        this.factions = factions;
        this.items = items;
        this.lotrModMobIds = lotrModMobIds;
        this.potions = potions;
        this.regions = regions;
        this.settings = settings;
        this.textFormatting = textFormatting;
        this.titleBlacklist = titleBlacklist;
        this.vanillaMobIds = vanillaMobIds;
        this.waypoints = waypoints;
    }

    public String getComments() {
        return comments;
    }

    public List<String> getAuthorBlackList() {
        return authorBlackList;
    }

    public List<String> getTitleBlacklist() {
        return titleBlacklist;
    }

    public Map<String, List<String>> getItems() {
        return items;
    }

    public Map<String, Map<String, BlockMapping>> getBlockMappings() {
        return blockMappings;
    }

    public Map<String, Object> getTextFormatting() {
        return textFormatting;
    }

    public Map<String, Potion> getPotions() {
        return potions;
    }

    public Map<String, String> getBlockEntityMappings() {
        return blockEntityMappings;
    }

    public Map<String, String> getColours() {
        return colours;
    }

    public Map<String, String> getEnchantmentIds() {
        return enchantmentIds;
    }

    public Map<String, String> getEntities() {
        return entities;
    }

    public Map<String, String> getFactions() {
        return factions;
    }

    public Map<String, String> getLotrModMobIds() {
        return lotrModMobIds;
    }

    public Map<String, String> getRegions() {
        return regions;
    }

    public Map<String, String> getVanillaMobIds() {
        return vanillaMobIds;
    }

    public Map<String, String> getWaypoints() {
        return waypoints;
    }

    public Settings getSettings() {
        return settings;
    }

    public static class Potion {
        @SerializedName("Name")
        private String name;
        @SerializedName("Splash")
        private boolean splash;

        public String getName() {
            return name;
        }

        public boolean isSplash() {
            return splash;
        }
    }

    public static class BlockMapping {
        @SerializedName("name")
        private final String name;
        @SerializedName("properties")
        private final Map<String, Object> properties;

        public BlockMapping(String name, Map<String, Object> properties) {
            this.name = name;
            this.properties = properties;
        }

        public String getName() {
            return name;
        }

        public Map<String, Object> getProperties() {
            if (properties != null) {
                return properties;
            }
            return Collections.emptyMap();
        }

        @Override
        public String toString() {
            return "BlockMapping{" + "name='" + name + '\'' + ", properties=" + properties + '}';
        }
    }
}

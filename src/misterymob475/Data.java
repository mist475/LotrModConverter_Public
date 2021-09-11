package misterymob475;

import lib.jnbt.jnbt.*;
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

    /**
     * Returns a Map of the waypoints
     * @return Map with key String and value String with the waypoint mappings
     */
    public Map<String,String> Waypoints() {
        //missing:
        //Waypoints.put("","lotr:ajtiaz_an_ahar");
        //Waypoints.put("","lotr:hirluins_halls");
        return (Map<String, String>) Conversions.get("Waypoints");
     }


    /**
     * returns a Map of the faction names
     * @return Map with key String and value String with the FactionName mappings
     */
    public Map<String,String> FacNames() {
        return (Map<String, String>) Conversions.get("Factions");

    }

    /**
     * returns a Map of the regions
     * @return Map with key String and value String with the Region mappings
     */
    public Map<String,String> Regions() {
        return (Map<String, String>) Conversions.get("Regions");
        //Regions.put("","lotr:andrast"); not in legacy, put with gondor
        //Regions.put("","lotr:anfalas); noy in legacy, put with gondor
        //Regions.put("","lotr:anorien");not in legacy, put with gondor
        //Regions.put("","lotr:forochel"); put with forodwaith
        //Regions.put("","lotr:northlands"); put with forodwaith
        //Regions.put("","lotr:western_gondor");
        //Regions.put("","lotr:western_isles"); combine this with sea
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
            CompoundTag minecraft_item = (CompoundTag) Registries.getValue().get("minecraft:item");
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
        //ItemNames.put("",);
//ItemNames.put("lotr:item.almond", Arrays.asList(""));
//ItemNames.put("lotr:item.amber", Arrays.asList(""));
//ItemNames.put("lotr:item.amethyst", Arrays.asList(""));
//ItemNames.put("lotr:item.ancient", Arrays.asList(""));
//ItemNames.put("lotr:item.ancientParts", Arrays.asList(""));
//ItemNames.put("lotr:item.anduril", Arrays.asList(""));


//could technically make this a tipped arrow of poison, depending on the amount of time I have
//ItemNames.put("lotr:item.arrowPoisoned", Arrays.asList(""));
//ItemNames.put("lotr:item.axeAngmar", Arrays.asList(""));

//ItemNames.put("lotr:item.axeDolGuldur", Arrays.asList(""));

//ItemNames.put("lotr:item.axeTauredain", Arrays.asList(""));

//ItemNames.put("lotr:item.balrogFire", Arrays.asList(""));
//ItemNames.put("lotr:item.balrogWhip", Arrays.asList(""));
//ItemNames.put("lotr:item.banana", Arrays.asList(""));
//ItemNames.put("lotr:item.bananaBread", Arrays.asList(""));
//ItemNames.put("lotr:item.bananaCake", Arrays.asList(""));
//ItemNames.put("lotr:item.banner", Arrays.asList(""));
//ItemNames.put("lotr:item.battleaxeAngmar", Arrays.asList(""));
//ItemNames.put("lotr:item.battleaxeBlackUruk", Arrays.asList(""));
//ItemNames.put("lotr:item.battleaxeBlueDwarven", Arrays.asList(""));
//ItemNames.put("lotr:item.battleaxeBronze", Arrays.asList(""));
//ItemNames.put("lotr:item.battleaxeCorsair", Arrays.asList(""));
//ItemNames.put("lotr:item.battleaxeDale", Arrays.asList(""));
//ItemNames.put("lotr:item.battleaxeDolGuldur", Arrays.asList(""));
//ItemNames.put("lotr:item.battleaxeDwarven", Arrays.asList(""));
//ItemNames.put("lotr:item.battleaxeGundabadUruk", Arrays.asList(""));
//ItemNames.put("lotr:item.battleaxeHalfTroll", Arrays.asList(""));
//ItemNames.put("lotr:item.battleaxeIron", Arrays.asList(""));
//ItemNames.put("lotr:item.battleaxeLossarnach", Arrays.asList(""));
//ItemNames.put("lotr:item.battleaxeMithril", Arrays.asList(""));
//ItemNames.put("lotr:item.battleaxeMoredain", Arrays.asList(""));
//ItemNames.put("lotr:item.battleaxeOrc", Arrays.asList(""));
//ItemNames.put("lotr:item.battleaxeRhun", Arrays.asList(""));
//ItemNames.put("lotr:item.battleaxeRohan", Arrays.asList(""));
//ItemNames.put("lotr:item.battleaxeTauredain", Arrays.asList(""));
//ItemNames.put("lotr:item.battleaxeUruk", Arrays.asList(""));
//ItemNames.put("lotr:item.battleaxeUtumno", Arrays.asList(""));
//ItemNames.put("lotr:item.bearRug", Arrays.asList(""));
//ItemNames.put("lotr:item.berryPie", Arrays.asList(""));
//ItemNames.put("lotr:item.blackUrukBow", Arrays.asList(""));
//ItemNames.put("lotr:item.blackUrukSteel", Arrays.asList(""));
//ItemNames.put("lotr:item.blackberry", Arrays.asList(""));
//ItemNames.put("lotr:item.blackrootBow", Arrays.asList(""));

//deliberate change by Mevans and co.

//ItemNames.put("lotr:item.blueberry", Arrays.asList(""));
//ItemNames.put("lotr:item.boarArmorBlueDwarven", Arrays.asList(""));
//ItemNames.put("lotr:item.boarArmorDwarven", Arrays.asList(""));
//ItemNames.put("lotr:item.bodyAngmar", Arrays.asList(""));

//ItemNames.put("lotr:item.bodyBlackNumenorean", Arrays.asList(""));
//ItemNames.put("lotr:item.bodyBlackUruk", Arrays.asList(""));
//ItemNames.put("lotr:item.bodyBlackroot", Arrays.asList(""));

//ItemNames.put("lotr:item.bodyCorsair", Arrays.asList(""));
//ItemNames.put("lotr:item.bodyDale", Arrays.asList("lotr:dale_chestplate"));
//ItemNames.put("lotr:item.bodyDaleGambeson", Arrays.asList(""));

//ItemNames.put("lotr:item.bodyDolAmrothGambeson", Arrays.asList(""));
//ItemNames.put("lotr:item.bodyDolGuldur", Arrays.asList(""));

//ItemNames.put("lotr:item.bodyGalvorn", Arrays.asList(""));
//ItemNames.put("lotr:item.bodyGemsbok", Arrays.asList(""));
//ItemNames.put("lotr:item.bodyGondolin", Arrays.asList(""));

//ItemNames.put("lotr:item.bodyGondorGambeson", Arrays.asList(""));
//ItemNames.put("lotr:item.bodyGulfHarad", Arrays.asList(""));
//ItemNames.put("lotr:item.bodyGundabadUruk", Arrays.asList(""));
//ItemNames.put("lotr:item.bodyHalfTroll", Arrays.asList(""));
//ItemNames.put("lotr:item.bodyHaradRobes", Arrays.asList(""));

//ItemNames.put("lotr:item.bodyHithlain", Arrays.asList(""));
//ItemNames.put("lotr:item.bodyKaftan", Arrays.asList(""));
//ItemNames.put("lotr:item.bodyLamedon", Arrays.asList(""));
//ItemNames.put("lotr:item.bodyLamedonJacket", Arrays.asList(""));
//ItemNames.put("lotr:item.bodyLebenninGambeson", Arrays.asList(""));
//ItemNames.put("lotr:item.bodyLossarnach", Arrays.asList(""));

//ItemNames.put("lotr:item.bodyMoredain", Arrays.asList(""));
//ItemNames.put("lotr:item.bodyMoredainLion", Arrays.asList(""));
//ItemNames.put("lotr:item.bodyMorgul", Arrays.asList(""));

//ItemNames.put("lotr:item.bodyNomad", Arrays.asList(""));

//ItemNames.put("lotr:item.bodyPelargir", Arrays.asList(""));
//ItemNames.put("lotr:item.bodyPinnathGelin", Arrays.asList(""));

//ItemNames.put("lotr:item.bodyRhun", Arrays.asList(""));
//ItemNames.put("lotr:item.bodyRhunGold", Arrays.asList(""));

//ItemNames.put("lotr:item.bodyTauredain", Arrays.asList(""));
//ItemNames.put("lotr:item.bodyTauredainGold", Arrays.asList(""));

//ItemNames.put("lotr:item.bodyUtumno", Arrays.asList(""));

//ItemNames.put("lotr:item.bodyWoodElvenScout", Arrays.asList(""));
//ItemNames.put("lotr:item.bootsAngmar", Arrays.asList(""));
//ItemNames.put("lotr:item.bootsArnor", Arrays.asList(""));
//ItemNames.put("lotr:item.bootsBlackNumenorean", Arrays.asList(""));
//ItemNames.put("lotr:item.bootsBlackUruk", Arrays.asList(""));
//ItemNames.put("lotr:item.bootsBlackroot", Arrays.asList(""));

//ItemNames.put("lotr:item.bootsCorsair", Arrays.asList(""));

//ItemNames.put("lotr:item.bootsDolGuldur", Arrays.asList(""));

//ItemNames.put("lotr:item.bootsGalvorn", Arrays.asList(""));
//ItemNames.put("lotr:item.bootsGemsbok", Arrays.asList(""));
//ItemNames.put("lotr:item.bootsGondolin", Arrays.asList(""));

//ItemNames.put("lotr:item.bootsGulfHarad", Arrays.asList(""));
//ItemNames.put("lotr:item.bootsGundabadUruk", Arrays.asList(""));
//ItemNames.put("lotr:item.bootsHalfTroll", Arrays.asList(""));
//ItemNames.put("lotr:item.bootsHaradRobes", Arrays.asList(""));

//ItemNames.put("lotr:item.bootsHithlain", Arrays.asList(""));
//ItemNames.put("lotr:item.bootsLamedon", Arrays.asList(""));
//ItemNames.put("lotr:item.bootsLossarnach", Arrays.asList(""));

//ItemNames.put("lotr:item.bootsMoredain", Arrays.asList(""));
//ItemNames.put("lotr:item.bootsMoredainLion", Arrays.asList(""));
//ItemNames.put("lotr:item.bootsMorgul", Arrays.asList(""));

//ItemNames.put("lotr:item.bootsNomad", Arrays.asList(""));

//ItemNames.put("lotr:item.bootsPelargir", Arrays.asList(""));
//ItemNames.put("lotr:item.bootsPinnathGelin", Arrays.asList(""));

//ItemNames.put("lotr:item.bootsRhun", Arrays.asList(""));
//ItemNames.put("lotr:item.bootsRhunGold", Arrays.asList(""));

//ItemNames.put("lotr:item.bootsTauredain", Arrays.asList(""));
//ItemNames.put("lotr:item.bootsTauredainGold", Arrays.asList(""));

//ItemNames.put("lotr:item.bootsUruk", Arrays.asList("lotr:uruk_boots"));
//ItemNames.put("lotr:item.bootsUtumno", Arrays.asList(""));

//ItemNames.put("lotr:item.bootsWoodElvenScout", Arrays.asList(""));
//ItemNames.put("lotr:item.bossTrophy", Arrays.asList(""));
//ItemNames.put("lotr:item.bottlePoison", Arrays.asList(""));
//ItemNames.put("lotr:item.bountyTrophy", Arrays.asList(""));
//ItemNames.put("lotr:item.brandingIron", Arrays.asList(""));

//ItemNames.put("lotr:item.bronzeCrossbow", Arrays.asList(""));
//ItemNames.put("lotr:item.camelCooked", Arrays.asList(""));
//ItemNames.put("lotr:item.camelRaw", Arrays.asList(""));

//ItemNames.put("lotr:item.ceramicPlate", Arrays.asList(""));

//ItemNames.put("lotr:item.chestnut", Arrays.asList(""));
//ItemNames.put("lotr:item.chestnutRoast", Arrays.asList(""));
//ItemNames.put("lotr:item.chilling", Arrays.asList(""));
//ItemNames.put("lotr:item.chisel", Arrays.asList(""));
//ItemNames.put("lotr:item.chiselIthildin", Arrays.asList(""));

//ItemNames.put("lotr:item.clubMoredain", Arrays.asList(""));
//ItemNames.put("lotr:item.coin", Arrays.asList(""));
//ItemNames.put("lotr:item.commandHorn", Arrays.asList(""));
//ItemNames.put("lotr:item.commandSword", Arrays.asList(""));
//ItemNames.put("lotr:item.conquestHorn", Arrays.asList(""));

//could technically make this a list and distribute evenly, don't see the point though

//ItemNames.put("lotr:item.corn", Arrays.asList(""));
//ItemNames.put("lotr:item.cornBread", Arrays.asList(""));
//ItemNames.put("lotr:item.cornCooked", Arrays.asList(""));

//ItemNames.put("lotr:item.cranberry", Arrays.asList(""));
//vanilla crossbows use regular arrows, so...

//could make this tipped poison arrow, again...
//ItemNames.put("lotr:item.crossbowBoltPoisoned", Arrays.asList(""));
//the poisoned ones could transfer to regular ones or be left empty, not sure yet
//ItemNames.put("lotr:item.daggerAncientHarad", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerAngmar", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerAngmarPoisoned", Arrays.asList(""));

//ItemNames.put("lotr:item.daggerArnorPoisoned", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerBarrow", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerBarrowPoisoned", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerBlackNumenorean", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerBlackNumenoreanPoisoned", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerBlackUruk", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerBlackUrukPoisoned", Arrays.asList(""));

//ItemNames.put("lotr:item.daggerBlueDwarvenPoisoned", Arrays.asList(""));

//ItemNames.put("lotr:item.daggerBronzePoisoned", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerCorsair", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerCorsairPoisoned", Arrays.asList(""));

//ItemNames.put("lotr:item.daggerDalePoisoned", Arrays.asList(""));

//ItemNames.put("lotr:item.daggerDolAmrothPoisoned", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerDolGuldur", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerDolGuldurPoisoned", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerDorwinionElf", Arrays.asList(""));

//ItemNames.put("lotr:item.daggerDwarvenPoisoned", Arrays.asList(""));

//ItemNames.put("lotr:item.daggerElvenPoisoned", Arrays.asList(""));

//ItemNames.put("lotr:item.daggerGondorPoisoned", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerGundabadUruk", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerGundabadUrukPoisoned", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerHalfTroll", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerHalfTrollPoisoned", Arrays.asList(""));

//ItemNames.put("lotr:item.daggerHaradPoisoned", Arrays.asList(""));

//ItemNames.put("lotr:item.daggerHighElvenPoisoned", Arrays.asList(""));

//ItemNames.put("lotr:item.daggerIronPoisoned", Arrays.asList(""));

//ItemNames.put("lotr:item.daggerMithrilPoisoned", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerMoredain", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerMoredainPoisoned", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerNearHarad", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerNearHaradPoisoned", Arrays.asList(""));

//ItemNames.put("lotr:item.daggerOrcPoisoned", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerRhun", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerRhunPoisoned", Arrays.asList(""));

//ItemNames.put("lotr:item.daggerRivendellPoisoned", Arrays.asList(""));

//ItemNames.put("lotr:item.daggerRohanPoisoned", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerTauredain", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerTauredainPoisoned", Arrays.asList(""));

//ItemNames.put("lotr:item.daggerUrukPoisoned", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerUtumno", Arrays.asList(""));
//ItemNames.put("lotr:item.daggerUtumnoPoisoned", Arrays.asList(""));

//ItemNames.put("lotr:item.daggerWoodElvenPoisoned", Arrays.asList(""));
//ItemNames.put("lotr:item.daleBow", Arrays.asList(""));
//ItemNames.put("lotr:item.daleCracker", Arrays.asList(""));
//ItemNames.put("lotr:item.dalishPastry", Arrays.asList(""));
//ItemNames.put("lotr:item.date", Arrays.asList(""));
//ItemNames.put("lotr:item.deerCooked", Arrays.asList(""));
//ItemNames.put("lotr:item.deerRaw", Arrays.asList(""));
//ItemNames.put("lotr:item.diamond", Arrays.asList(""));
//ItemNames.put("lotr:item.dorwinionElfBow", Arrays.asList(""));

//ItemNames.put("lotr:item.dunlendingTrident", Arrays.asList(""));
//ItemNames.put("lotr:item.dwarfBone", Arrays.asList(""));

//ItemNames.put("lotr:item.dwarvenBed", Arrays.asList(""));
//ItemNames.put("lotr:item.dwarvenRing", Arrays.asList(""));
//TODO: figure out later
//ItemNames.put("lotr:item.dye", Arrays.asList(""));

//ItemNames.put("lotr:item.elderberry", Arrays.asList(""));
//ItemNames.put("lotr:item.elfBone", Arrays.asList(""));
//ItemNames.put("lotr:item.elfSteel", Arrays.asList(""));
//ItemNames.put("lotr:item.elkArmorWoodElven", Arrays.asList(""));
//ItemNames.put("lotr:item.elvenBed", Arrays.asList(""));
//ItemNames.put("lotr:item.elvenBow", Arrays.asList(""));
//ItemNames.put("lotr:item.emerald", Arrays.asList(""));
//ItemNames.put("lotr:item.entDraught", Arrays.asList(""));
//ItemNames.put("lotr:item.featherDyed", Arrays.asList(""));

//ItemNames.put("lotr:item.galvorn", Arrays.asList(""));

//ItemNames.put("lotr:item.gandalfStaffGrey", Arrays.asList(""));
//ItemNames.put("lotr:item.gandalfStaffWhite", Arrays.asList(""));

//ItemNames.put("lotr:item.gemsbokHide", Arrays.asList(""));
//ItemNames.put("lotr:item.gemsbokHorn", Arrays.asList(""));
//ItemNames.put("lotr:item.gildedIron", Arrays.asList(""));
//ItemNames.put("lotr:item.giraffeRug", Arrays.asList(""));
//ItemNames.put("lotr:item.glamdring", Arrays.asList(""));

//ItemNames.put("lotr:item.gondorBow", Arrays.asList(""));
//ItemNames.put("lotr:item.grapeRed", Arrays.asList(""));
//ItemNames.put("lotr:item.grapeWhite", Arrays.asList(""));
//not sure to make this gulduril or crystal version

//ItemNames.put("lotr:item.gundabadUrukBow", Arrays.asList(""));
//ItemNames.put("lotr:item.halberdMithril", Arrays.asList(""));
//ItemNames.put("lotr:item.hammerAngmar", Arrays.asList(""));
//ItemNames.put("lotr:item.hammerBlackUruk", Arrays.asList(""));
//ItemNames.put("lotr:item.hammerBlueDwarven", Arrays.asList(""));
//ItemNames.put("lotr:item.hammerDolGuldur", Arrays.asList(""));
//ItemNames.put("lotr:item.hammerDwarven", Arrays.asList(""));
//ItemNames.put("lotr:item.hammerGondor", Arrays.asList(""));
//ItemNames.put("lotr:item.hammerGundabadUruk", Arrays.asList(""));
//ItemNames.put("lotr:item.hammerHalfTroll", Arrays.asList(""));
//ItemNames.put("lotr:item.hammerMithril", Arrays.asList(""));
//ItemNames.put("lotr:item.hammerOrc", Arrays.asList(""));
//ItemNames.put("lotr:item.hammerTauredain", Arrays.asList(""));
//ItemNames.put("lotr:item.hammerUruk", Arrays.asList(""));
//ItemNames.put("lotr:item.hammerUtumno", Arrays.asList(""));
//ItemNames.put("lotr:item.helmetAngmar", Arrays.asList(""));

//ItemNames.put("lotr:item.helmetBlackNumenorean", Arrays.asList(""));
//ItemNames.put("lotr:item.helmetBlackUruk", Arrays.asList(""));
//ItemNames.put("lotr:item.helmetBlackroot", Arrays.asList(""));

//ItemNames.put("lotr:item.helmetCorsair", Arrays.asList(""));

//ItemNames.put("lotr:item.helmetDolGuldur", Arrays.asList(""));

//ItemNames.put("lotr:item.helmetGalvorn", Arrays.asList(""));
//ItemNames.put("lotr:item.helmetGemsbok", Arrays.asList(""));
//ItemNames.put("lotr:item.helmetGondolin", Arrays.asList(""));

//ItemNames.put("lotr:item.helmetGulfHarad", Arrays.asList(""));
//ItemNames.put("lotr:item.helmetGundabadUruk", Arrays.asList(""));
//ItemNames.put("lotr:item.helmetHalfTroll", Arrays.asList(""));
//ItemNames.put("lotr:item.helmetHaradRobes", Arrays.asList(""));

//ItemNames.put("lotr:item.helmetHithlain", Arrays.asList(""));
//ItemNames.put("lotr:item.helmetLamedon", Arrays.asList(""));
//ItemNames.put("lotr:item.helmetLossarnach", Arrays.asList(""));

//ItemNames.put("lotr:item.helmetMoredain", Arrays.asList(""));
//ItemNames.put("lotr:item.helmetMoredainLion", Arrays.asList(""));
//ItemNames.put("lotr:item.helmetMorgul", Arrays.asList(""));

//ItemNames.put("lotr:item.helmetNomad", Arrays.asList(""));

//ItemNames.put("lotr:item.helmetPelargir", Arrays.asList(""));
//ItemNames.put("lotr:item.helmetPinnathGelin", Arrays.asList(""));

//ItemNames.put("lotr:item.helmetRhun", Arrays.asList(""));
//ItemNames.put("lotr:item.helmetRhunGold", Arrays.asList(""));
//ItemNames.put("lotr:item.helmetRhunWarlord", Arrays.asList(""));

//ItemNames.put("lotr:item.helmetTauredain", Arrays.asList(""));
//ItemNames.put("lotr:item.helmetTauredainChieftain", Arrays.asList(""));
//ItemNames.put("lotr:item.helmetTauredainGold", Arrays.asList(""));

//ItemNames.put("lotr:item.helmetUtumno", Arrays.asList(""));

//ItemNames.put("lotr:item.helmetWoodElvenScout", Arrays.asList(""));
//ItemNames.put("lotr:item.highElvenBed", Arrays.asList(""));
//ItemNames.put("lotr:item.highElvenBow", Arrays.asList(""));
//ItemNames.put("lotr:item.hithlain", Arrays.asList(""));
//ItemNames.put("lotr:item.hobbitBone", Arrays.asList(""));
//ItemNames.put("lotr:item.hobbitPancake", Arrays.asList(""));
//ItemNames.put("lotr:item.hobbitPancakeMapleSyrup", Arrays.asList(""));

//ItemNames.put("lotr:item.hobbitRing", Arrays.asList(""));
//ItemNames.put("lotr:item.hoeAngmar", Arrays.asList(""));
//ItemNames.put("lotr:item.hoeBlueDwarven", Arrays.asList(""));

//ItemNames.put("lotr:item.hoeDolGuldur", Arrays.asList(""));

//ItemNames.put("lotr:item.hoeTauredain", Arrays.asList(""));

//ItemNames.put("lotr:item.horseArmorDale", Arrays.asList(""));
//ItemNames.put("lotr:item.horseArmorDiamond", Arrays.asList(""));
//ItemNames.put("lotr:item.horseArmorDolAmroth", Arrays.asList(""));
//ItemNames.put("lotr:item.horseArmorGaladhrim", Arrays.asList(""));
//ItemNames.put("lotr:item.horseArmorGold", Arrays.asList(""));
//ItemNames.put("lotr:item.horseArmorGondor", Arrays.asList(""));
//ItemNames.put("lotr:item.horseArmorHighElven", Arrays.asList(""));
//ItemNames.put("lotr:item.horseArmorIron", Arrays.asList(""));
//ItemNames.put("lotr:item.horseArmorLamedon", Arrays.asList(""));
//ItemNames.put("lotr:item.horseArmorMithril", Arrays.asList(""));
//ItemNames.put("lotr:item.horseArmorMorgul", Arrays.asList(""));
//ItemNames.put("lotr:item.horseArmorNearHarad", Arrays.asList(""));
//ItemNames.put("lotr:item.horseArmorRhunGold", Arrays.asList(""));
//ItemNames.put("lotr:item.horseArmorRivendell", Arrays.asList(""));
//ItemNames.put("lotr:item.horseArmorRohan", Arrays.asList(""));
//ItemNames.put("lotr:item.horseArmorUmbar", Arrays.asList(""));
//ItemNames.put("lotr:item.ironCrossbow", Arrays.asList(""));

//ItemNames.put("lotr:item.ithildin", Arrays.asList(""));
//ItemNames.put("lotr:item.kebab", Arrays.asList(""));

//ItemNames.put("lotr:item.lanceDolAmroth", Arrays.asList(""));
//ItemNames.put("lotr:item.lanceGondor", Arrays.asList(""));
//ItemNames.put("lotr:item.lanceRohan", Arrays.asList(""));
//ItemNames.put("lotr:item.leatherHat", Arrays.asList(""));
//ItemNames.put("lotr:item.leek", Arrays.asList(""));
//ItemNames.put("lotr:item.leekSoup", Arrays.asList(""));
//ItemNames.put("lotr:item.legsAngmar", Arrays.asList(""));

//ItemNames.put("lotr:item.legsBlackNumenorean", Arrays.asList(""));
//ItemNames.put("lotr:item.legsBlackUruk", Arrays.asList(""));
//ItemNames.put("lotr:item.legsBlackroot", Arrays.asList(""));

//ItemNames.put("lotr:item.legsCorsair", Arrays.asList(""));

//ItemNames.put("lotr:item.legsDolAmrothGambeson", Arrays.asList(""));
//ItemNames.put("lotr:item.legsDolGuldur", Arrays.asList(""));

//ItemNames.put("lotr:item.legsGalvorn", Arrays.asList(""));
//ItemNames.put("lotr:item.legsGemsbok", Arrays.asList(""));
//ItemNames.put("lotr:item.legsGondolin", Arrays.asList(""));

//ItemNames.put("lotr:item.legsGulfHarad", Arrays.asList(""));
//ItemNames.put("lotr:item.legsGundabadUruk", Arrays.asList(""));
//ItemNames.put("lotr:item.legsHalfTroll", Arrays.asList(""));
//ItemNames.put("lotr:item.legsHaradRobes", Arrays.asList(""));

//ItemNames.put("lotr:item.legsHithlain", Arrays.asList(""));
//ItemNames.put("lotr:item.legsKaftan", Arrays.asList(""));
//ItemNames.put("lotr:item.legsLamedon", Arrays.asList(""));
//ItemNames.put("lotr:item.legsLossarnach", Arrays.asList(""));

//ItemNames.put("lotr:item.legsMoredain", Arrays.asList(""));
//ItemNames.put("lotr:item.legsMoredainLion", Arrays.asList(""));
//ItemNames.put("lotr:item.legsMorgul", Arrays.asList(""));

//ItemNames.put("lotr:item.legsNomad", Arrays.asList(""));

//ItemNames.put("lotr:item.legsPelargir", Arrays.asList(""));
//ItemNames.put("lotr:item.legsPinnathGelin", Arrays.asList(""));

//ItemNames.put("lotr:item.legsRhun", Arrays.asList(""));
//ItemNames.put("lotr:item.legsRhunGold", Arrays.asList(""));

//ItemNames.put("lotr:item.legsTauredain", Arrays.asList(""));
//ItemNames.put("lotr:item.legsTauredainGold", Arrays.asList(""));

//ItemNames.put("lotr:item.legsUtumno", Arrays.asList(""));

//ItemNames.put("lotr:item.legsWoodElvenScout", Arrays.asList(""));

//ItemNames.put("lotr:item.lemon", Arrays.asList(""));
//ItemNames.put("lotr:item.lemonCake", Arrays.asList(""));

//ItemNames.put("lotr:item.lime", Arrays.asList(""));
//ItemNames.put("lotr:item.lionBed", Arrays.asList(""));
//ItemNames.put("lotr:item.lionCooked", Arrays.asList(""));
//ItemNames.put("lotr:item.lionFur", Arrays.asList(""));
//ItemNames.put("lotr:item.lionRaw", Arrays.asList(""));
//ItemNames.put("lotr:item.lionRug", Arrays.asList(""));
//ItemNames.put("lotr:item.longspearDolAmroth", Arrays.asList(""));
//ItemNames.put("lotr:item.longspearElven", Arrays.asList(""));
//ItemNames.put("lotr:item.longspearHighElven", Arrays.asList(""));
//ItemNames.put("lotr:item.longspearRivendell", Arrays.asList(""));
//ItemNames.put("lotr:item.longspearWoodElven", Arrays.asList(""));
//ItemNames.put("lotr:item.maceBlackNumenorean", Arrays.asList(""));
//ItemNames.put("lotr:item.maceHalfTroll", Arrays.asList(""));
//ItemNames.put("lotr:item.maceMallornCharred", Arrays.asList(""));
//ItemNames.put("lotr:item.maceNearHarad", Arrays.asList(""));

//ItemNames.put("lotr:item.mallornBow", Arrays.asList(""));

//ItemNames.put("lotr:item.mango", Arrays.asList(""));

//ItemNames.put("lotr:item.marzipan", Arrays.asList(""));
//ItemNames.put("lotr:item.marzipanChocolate", Arrays.asList(""));
//ItemNames.put("lotr:item.mattockBlueDwarven", Arrays.asList(""));
//ItemNames.put("lotr:item.mattockDwarven", Arrays.asList(""));
//ItemNames.put("lotr:item.mattockMithril", Arrays.asList(""));
//ItemNames.put("lotr:item.mechanism", Arrays.asList(""));
//ItemNames.put("lotr:item.melonSoup", Arrays.asList(""));
//ItemNames.put("lotr:item.mirkwoodBow", Arrays.asList(""));

//ItemNames.put("lotr:item.mithrilBook", Arrays.asList(""));
//ItemNames.put("lotr:item.mithrilCrossbow", Arrays.asList(""));

//hmm...
//ItemNames.put("lotr:item.modTemplate", Arrays.asList(""));
//ItemNames.put("lotr:item.morgulBlade", Arrays.asList(""));
//ItemNames.put("lotr:item.morgulSteel", Arrays.asList(""));
//TODO: different alcohol changer
//ItemNames.put("lotr:item.mugAle", Arrays.asList(""));
//ItemNames.put("lotr:item.mugAppleJuice", Arrays.asList(""));
//ItemNames.put("lotr:item.mugAraq", Arrays.asList(""));
//ItemNames.put("lotr:item.mugAthelasBrew", Arrays.asList(""));
//ItemNames.put("lotr:item.mugBananaBeer", Arrays.asList(""));
//ItemNames.put("lotr:item.mugBlackberryJuice", Arrays.asList(""));
//ItemNames.put("lotr:item.mugBlueberryJuice", Arrays.asList(""));
//ItemNames.put("lotr:item.mugCactusLiqueur", Arrays.asList(""));
//ItemNames.put("lotr:item.mugCarrotWine", Arrays.asList(""));
//ItemNames.put("lotr:item.mugCherryLiqueur", Arrays.asList(""));
//ItemNames.put("lotr:item.mugChocolate", Arrays.asList(""));
//ItemNames.put("lotr:item.mugCider", Arrays.asList(""));
//ItemNames.put("lotr:item.mugCornLiquor", Arrays.asList(""));
//ItemNames.put("lotr:item.mugCranberryJuice", Arrays.asList(""));
//ItemNames.put("lotr:item.mugDwarvenAle", Arrays.asList(""));
//ItemNames.put("lotr:item.mugDwarvenTonic", Arrays.asList(""));
//ItemNames.put("lotr:item.mugElderberryJuice", Arrays.asList(""));
//ItemNames.put("lotr:item.mugLemonLiqueur", Arrays.asList(""));
//ItemNames.put("lotr:item.mugLemonade", Arrays.asList(""));
//ItemNames.put("lotr:item.mugLimeLiqueur", Arrays.asList(""));
//ItemNames.put("lotr:item.mugMangoJuice", Arrays.asList(""));
//ItemNames.put("lotr:item.mugMapleBeer", Arrays.asList(""));
//ItemNames.put("lotr:item.mugMead", Arrays.asList(""));
//ItemNames.put("lotr:item.mugMelonLiqueur", Arrays.asList(""));
//ItemNames.put("lotr:item.mugMilk", Arrays.asList(""));
//ItemNames.put("lotr:item.mugMiruvor", Arrays.asList(""));
//ItemNames.put("lotr:item.mugMorgulDraught", Arrays.asList(""));
//ItemNames.put("lotr:item.mugOrangeJuice", Arrays.asList(""));
//ItemNames.put("lotr:item.mugOrcDraught", Arrays.asList(""));
//ItemNames.put("lotr:item.mugPerry", Arrays.asList(""));
//ItemNames.put("lotr:item.mugPlumKvass", Arrays.asList(""));
//ItemNames.put("lotr:item.mugPomegranateJuice", Arrays.asList(""));
//ItemNames.put("lotr:item.mugPomegranateWine", Arrays.asList(""));
//ItemNames.put("lotr:item.mugRaspberryJuice", Arrays.asList(""));
//ItemNames.put("lotr:item.mugRedGrapeJuice", Arrays.asList(""));
//ItemNames.put("lotr:item.mugRedWine", Arrays.asList(""));
//ItemNames.put("lotr:item.mugRum", Arrays.asList(""));
//ItemNames.put("lotr:item.mugSourMilk", Arrays.asList(""));
//ItemNames.put("lotr:item.mugTauredainCocoa", Arrays.asList(""));
//ItemNames.put("lotr:item.mugTauredainCure", Arrays.asList(""));
//ItemNames.put("lotr:item.mugTermiteTequila", Arrays.asList(""));
//ItemNames.put("lotr:item.mugTorogDraught", Arrays.asList(""));
//ItemNames.put("lotr:item.mugVodka", Arrays.asList(""));
//ItemNames.put("lotr:item.mugWater", Arrays.asList(""));
//ItemNames.put("lotr:item.mugWhiteGrapeJuice", Arrays.asList(""));
//ItemNames.put("lotr:item.mugWhiteWine", Arrays.asList(""));

//ItemNames.put("lotr:item.mushroomPie", Arrays.asList(""));

//ItemNames.put("lotr:item.mysteryWeb", Arrays.asList(""));

//ItemNames.put("lotr:item.nearHaradBow", Arrays.asList(""));
//ItemNames.put("lotr:item.npcRespawner", Arrays.asList(""));
//ItemNames.put("lotr:item.obsidianShard", Arrays.asList(""));
//ItemNames.put("lotr:item.olive", Arrays.asList(""));
//ItemNames.put("lotr:item.oliveBread", Arrays.asList(""));
//ItemNames.put("lotr:item.opal", Arrays.asList(""));
//ItemNames.put("lotr:item.orange", Arrays.asList(""));
//ItemNames.put("lotr:item.orcBed", Arrays.asList(""));
//ItemNames.put("lotr:item.orcBone", Arrays.asList(""));
//ItemNames.put("lotr:item.orcBow", Arrays.asList(""));
//ItemNames.put("lotr:item.orcSkullStaff", Arrays.asList(""));

//ItemNames.put("lotr:item.partyHat", Arrays.asList(""));

//ItemNames.put("lotr:item.pearl", Arrays.asList(""));
//ItemNames.put("lotr:item.pebble", Arrays.asList(""));
//ItemNames.put("lotr:item.pickaxeAngmar", Arrays.asList(""));

//ItemNames.put("lotr:item.pickaxeDolGuldur", Arrays.asList(""));

//ItemNames.put("lotr:item.pickaxeTauredain", Arrays.asList(""));

//ItemNames.put("lotr:item.pikeBlueDwarven", Arrays.asList(""));
//ItemNames.put("lotr:item.pikeDale", Arrays.asList(""));
//ItemNames.put("lotr:item.pikeDolGuldur", Arrays.asList(""));
//ItemNames.put("lotr:item.pikeDwarven", Arrays.asList(""));
//ItemNames.put("lotr:item.pikeGondor", Arrays.asList(""));
//ItemNames.put("lotr:item.pikeGundabadUruk", Arrays.asList(""));
//ItemNames.put("lotr:item.pikeHalfTroll", Arrays.asList(""));
//ItemNames.put("lotr:item.pikeHarad", Arrays.asList(""));
//ItemNames.put("lotr:item.pikeIron", Arrays.asList(""));
//ItemNames.put("lotr:item.pikeNearHarad", Arrays.asList(""));
//ItemNames.put("lotr:item.pikeRhun", Arrays.asList(""));
//ItemNames.put("lotr:item.pikeTauredain", Arrays.asList(""));
//ItemNames.put("lotr:item.pikeUruk", Arrays.asList(""));

//ItemNames.put("lotr:item.plate", Arrays.asList(""));
//ItemNames.put("lotr:item.plum", Arrays.asList(""));
//ItemNames.put("lotr:item.polearmAngmar", Arrays.asList(""));
//ItemNames.put("lotr:item.polearmElven", Arrays.asList(""));
//ItemNames.put("lotr:item.polearmHighElven", Arrays.asList(""));
//ItemNames.put("lotr:item.polearmOrc", Arrays.asList(""));
//ItemNames.put("lotr:item.polearmRhun", Arrays.asList(""));
//ItemNames.put("lotr:item.polearmRivendell", Arrays.asList(""));
//ItemNames.put("lotr:item.polearmWoodElven", Arrays.asList(""));
//ItemNames.put("lotr:item.poleaxeNearHarad", Arrays.asList(""));
//ItemNames.put("lotr:item.pomegranate", Arrays.asList(""));
// TODO pouch as pouch, not shulkerbox

//ItemNames.put("lotr:item.raisins", Arrays.asList(""));
//ItemNames.put("lotr:item.rangerBow", Arrays.asList(""));
//ItemNames.put("lotr:item.raspberry", Arrays.asList(""));

//ItemNames.put("lotr:item.redClayBall", Arrays.asList(""));
//ItemNames.put("lotr:item.rhinoArmorHalfTroll", Arrays.asList(""));
//ItemNames.put("lotr:item.rhinoCooked", Arrays.asList(""));
//ItemNames.put("lotr:item.rhinoHorn", Arrays.asList(""));
//ItemNames.put("lotr:item.rhinoRaw", Arrays.asList(""));
//ItemNames.put("lotr:item.rhunBow", Arrays.asList(""));
//ItemNames.put("lotr:item.rhunFirePot", Arrays.asList(""));
//ItemNames.put("lotr:item.ringil", Arrays.asList(""));
//ItemNames.put("lotr:item.rivendellBow", Arrays.asList(""));
//ItemNames.put("lotr:item.rohanBow", Arrays.asList(""));
//ItemNames.put("lotr:item.rollingPin", Arrays.asList(""));
//ItemNames.put("lotr:item.ruby", Arrays.asList(""));

//ItemNames.put("lotr:item.saltedFlesh", Arrays.asList(""));
//ItemNames.put("lotr:item.saltpeter", Arrays.asList(""));
//ItemNames.put("lotr:item.sapphire", Arrays.asList(""));
//ItemNames.put("lotr:item.sauronMace", Arrays.asList(""));
//ItemNames.put("lotr:item.scimitarBlackUruk", Arrays.asList(""));
//ItemNames.put("lotr:item.scimitarHalfTroll", Arrays.asList(""));
//ItemNames.put("lotr:item.scimitarNearHarad", Arrays.asList(""));

//TODO:umbar_scimitar background
//ItemNames.put("lotr:item.scimitarUruk", Arrays.asList(""));
//ItemNames.put("lotr:item.scimitarUrukBerserker", Arrays.asList(""));
//ItemNames.put("lotr:item.seedsGrapeRed", Arrays.asList(""));
//ItemNames.put("lotr:item.seedsGrapeWhite", Arrays.asList(""));
//ItemNames.put("lotr:item.shishKebab", Arrays.asList(""));
//ItemNames.put("lotr:item.shovelAngmar", Arrays.asList(""));

//ItemNames.put("lotr:item.shovelDolGuldur", Arrays.asList(""));

//ItemNames.put("lotr:item.shovelTauredain", Arrays.asList(""));

//ItemNames.put("lotr:item.skullCup", Arrays.asList(""));
//ItemNames.put("lotr:item.sling", Arrays.asList(""));
// TODO:spawn egg handler
//ItemNames.put("lotr:item.spawnEgg", Arrays.asList(""));

//ItemNames.put("lotr:item.spearAngmar", Arrays.asList(""));

//ItemNames.put("lotr:item.spearBlackNumenorean", Arrays.asList(""));
//ItemNames.put("lotr:item.spearBlackUruk", Arrays.asList(""));
//ItemNames.put("lotr:item.spearBladorthin", Arrays.asList(""));

//ItemNames.put("lotr:item.spearCorsair", Arrays.asList(""));

//ItemNames.put("lotr:item.spearDolGuldur", Arrays.asList(""));

//ItemNames.put("lotr:item.spearGundabadUruk", Arrays.asList(""));

//ItemNames.put("lotr:item.spearMoredain", Arrays.asList(""));
//ItemNames.put("lotr:item.spearNearHarad", Arrays.asList(""));

//ItemNames.put("lotr:item.spearRhun", Arrays.asList(""));

//ItemNames.put("lotr:item.spearTauredain", Arrays.asList(""));

//ItemNames.put("lotr:item.spearUtumno", Arrays.asList(""));

//ItemNames.put("lotr:item.sting", Arrays.asList(""));
//ItemNames.put("lotr:item.strawBed", Arrays.asList(""));
//ItemNames.put("lotr:item.structureSpawner", Arrays.asList(""));

//ItemNames.put("lotr:item.swordAngmar", Arrays.asList(""));

//ItemNames.put("lotr:item.swordBlackNumenorean", Arrays.asList(""));

//ItemNames.put("lotr:item.swordCorsair", Arrays.asList(""));

//ItemNames.put("lotr:item.swordDolGuldur", Arrays.asList(""));

//ItemNames.put("lotr:item.swordGondolin", Arrays.asList(""));

//ItemNames.put("lotr:item.swordGulfHarad", Arrays.asList(""));
//ItemNames.put("lotr:item.swordGundabadUruk", Arrays.asList(""));

//ItemNames.put("lotr:item.swordMoredain", Arrays.asList(""));
//ItemNames.put("lotr:item.swordPelargir", Arrays.asList(""));
//ItemNames.put("lotr:item.swordRhun", Arrays.asList(""));

//ItemNames.put("lotr:item.swordTauredain", Arrays.asList(""));
//ItemNames.put("lotr:item.swordUtumno", Arrays.asList(""));

//ItemNames.put("lotr:item.tauredainAmulet", Arrays.asList(""));
//ItemNames.put("lotr:item.tauredainBlowgun", Arrays.asList(""));
//ItemNames.put("lotr:item.tauredainDart", Arrays.asList(""));
//ItemNames.put("lotr:item.tauredainDartPoisoned", Arrays.asList(""));
//ItemNames.put("lotr:item.tauredainDoubleTorch", Arrays.asList(""));
//ItemNames.put("lotr:item.termite", Arrays.asList(""));
//ItemNames.put("lotr:item.throwingAxeBlueDwarven", Arrays.asList(""));
//ItemNames.put("lotr:item.throwingAxeBronze", Arrays.asList(""));
//ItemNames.put("lotr:item.throwingAxeDwarven", Arrays.asList(""));
//ItemNames.put("lotr:item.throwingAxeIron", Arrays.asList(""));
//ItemNames.put("lotr:item.throwingAxeLossarnach", Arrays.asList(""));

//ItemNames.put("lotr:item.topaz", Arrays.asList(""));
//ItemNames.put("lotr:item.torogStew", Arrays.asList(""));
//ItemNames.put("lotr:item.tridentPelargir", Arrays.asList(""));
//ItemNames.put("lotr:item.trollBone", Arrays.asList(""));
//ItemNames.put("lotr:item.trollStatue", Arrays.asList(""));
//ItemNames.put("lotr:item.turnip", Arrays.asList(""));
//ItemNames.put("lotr:item.turnipCooked", Arrays.asList(""));
//ItemNames.put("lotr:item.urukCrossbow", Arrays.asList(""));

//ItemNames.put("lotr:item.utumnoBow", Arrays.asList(""));
//ItemNames.put("lotr:item.utumnoKey", Arrays.asList(""));
//ItemNames.put("lotr:item.utumnoPickaxe", Arrays.asList(""));
//ItemNames.put("lotr:item.wargArmorAngmar", Arrays.asList(""));
//ItemNames.put("lotr:item.wargArmorMordor", Arrays.asList(""));
//ItemNames.put("lotr:item.wargArmorUruk", Arrays.asList(""));
//ItemNames.put("lotr:item.wargBone", Arrays.asList(""));
//ItemNames.put("lotr:item.wargFur", Arrays.asList(""));
//ItemNames.put("lotr:item.wargFurBed", Arrays.asList(""));
//ItemNames.put("lotr:item.wargskinRug", Arrays.asList(""));

//ItemNames.put("lotr:item.wildberry", Arrays.asList(""));
//ItemNames.put("lotr:item.wineGlass", Arrays.asList(""));
//ItemNames.put("lotr:item.woodElvenBed", Arrays.asList(""));

//ItemNames.put("lotr:item.yam", Arrays.asList(""));
//ItemNames.put("lotr:item.yamRoast", Arrays.asList(""));
//ItemNames.put("lotr:item.zebraCooked", Arrays.asList(""));
//ItemNames.put("lotr:item.zebraRaw", Arrays.asList(""));

//some of these might be redundant as they have a separate item which is what this list is about

//ItemNames.put("lotr:tile.banana", Arrays.asList(""));
//ItemNames.put("lotr:tile.bananaCake", Arrays.asList(""));
//TODO: alcohol handler
//ItemNames.put("lotr:tile.barrel", Collections.singletonList("lotr:keg"));


//ItemNames.put("lotr:tile.berryBush", Arrays.asList(""));
//ItemNames.put("lotr:tile.berryPie", Arrays.asList(""));
//ItemNames.put("lotr:tile.birdCage", Arrays.asList(""));
//ItemNames.put("lotr:tile.birdCageWood", Arrays.asList(""));

//ItemNames.put("lotr:tile.blockGem", Arrays.asList(""));

//ItemNames.put("lotr:tile.breeCraftingTable", Arrays.asList(""));
//the real thing starts here

//ItemNames.put("lotr:tile.butterflyJar", Arrays.asList(""));

//ItemNames.put("lotr:tile.ceramicPlate", Arrays.asList(""));

//ItemNames.put("lotr:tile.chestAncientHarad", Arrays.asList(""));
//ItemNames.put("lotr:tile.chestBasket", Arrays.asList(""));
//ItemNames.put("lotr:tile.chestLebethron", Arrays.asList(""));
//ItemNames.put("lotr:tile.chestMallorn", Arrays.asList(""));
//ItemNames.put("lotr:tile.chestStone", Arrays.asList(""));

//ItemNames.put("lotr:tile.commandTable", Arrays.asList(""));

//ItemNames.put("lotr:tile.cornStalk", Arrays.asList(""));
//ItemNames.put("lotr:tile.corruptMallorn", Arrays.asList(""));

//ItemNames.put("lotr:tile.dalishPastry", Arrays.asList(""));
//ItemNames.put("lotr:tile.date", Arrays.asList(""));

//ItemNames.put("lotr:tile.deadMarshPlant", Arrays.asList(""));

//ItemNames.put("lotr:tile.dolGuldurCraftingTable", Arrays.asList(""));

//ItemNames.put("lotr:tile.doorAlmond", Arrays.asList(""));

//ItemNames.put("lotr:tile.doorBanana", Arrays.asList(""));
//ItemNames.put("lotr:tile.doorBaobab", Arrays.asList(""));

//ItemNames.put("lotr:tile.doorChestnut", Arrays.asList(""));

//ItemNames.put("lotr:tile.doorDatePalm", Arrays.asList(""));
//ItemNames.put("lotr:tile.doorDragon", Arrays.asList(""));

//ItemNames.put("lotr:tile.doorKanuka", Arrays.asList(""));

//ItemNames.put("lotr:tile.doorLemon", Arrays.asList(""));
//ItemNames.put("lotr:tile.doorLime", Arrays.asList(""));
//ItemNames.put("lotr:tile.doorMahogany", Arrays.asList(""));

//ItemNames.put("lotr:tile.doorMango", Arrays.asList(""));
//ItemNames.put("lotr:tile.doorMangrove", Arrays.asList(""));

//ItemNames.put("lotr:tile.doorOlive", Arrays.asList(""));
//ItemNames.put("lotr:tile.doorOrange", Arrays.asList(""));
//ItemNames.put("lotr:tile.doorPalm", Arrays.asList(""));

//ItemNames.put("lotr:tile.doorPlum", Arrays.asList(""));
//ItemNames.put("lotr:tile.doorPomegranate", Arrays.asList(""));
//ItemNames.put("lotr:tile.doorRedwood", Arrays.asList(""));

//ItemNames.put("lotr:tile.doorWillow", Arrays.asList(""));

//ItemNames.put("lotr:tile.dwarvenBed", Arrays.asList(""));

//ItemNames.put("lotr:tile.dwarvenDoor", Arrays.asList(""));
//ItemNames.put("lotr:tile.dwarvenDoorIthildin", Arrays.asList(""));

//ItemNames.put("lotr:tile.elvenBed", Arrays.asList(""));

//ItemNames.put("lotr:tile.elvenPortal", Arrays.asList(""));
//ItemNames.put("lotr:tile.entJar", Arrays.asList(""));

//ItemNames.put("lotr:tile.fangornPlant", Arrays.asList(""));
//ItemNames.put("lotr:tile.fangornRiverweed", Arrays.asList(""));

//ItemNames.put("lotr:tile.fenceGateAlmond", Arrays.asList(""));

//ItemNames.put("lotr:tile.fenceGateBanana", Arrays.asList(""));
//ItemNames.put("lotr:tile.fenceGateBaobab", Arrays.asList(""));

//ItemNames.put("lotr:tile.fenceGateChestnut", Arrays.asList(""));

//ItemNames.put("lotr:tile.fenceGateDatePalm", Arrays.asList(""));
//ItemNames.put("lotr:tile.fenceGateDragon", Arrays.asList(""));

//ItemNames.put("lotr:tile.fenceGateKanuka", Arrays.asList(""));

//ItemNames.put("lotr:tile.fenceGateLemon", Arrays.asList(""));
//ItemNames.put("lotr:tile.fenceGateLime", Arrays.asList(""));
//ItemNames.put("lotr:tile.fenceGateMahogany", Arrays.asList(""));

//ItemNames.put("lotr:tile.fenceGateMango", Arrays.asList(""));
//ItemNames.put("lotr:tile.fenceGateMangrove", Arrays.asList(""));

//ItemNames.put("lotr:tile.fenceGateOlive", Arrays.asList(""));
//ItemNames.put("lotr:tile.fenceGateOrange", Arrays.asList(""));
//ItemNames.put("lotr:tile.fenceGatePalm", Arrays.asList(""));
//ItemNames.put("lotr:tile.fenceGatePlum", Arrays.asList(""));
//ItemNames.put("lotr:tile.fenceGatePomegranate", Arrays.asList(""));
//ItemNames.put("lotr:tile.fenceGateRedwood", Arrays.asList(""));
//ItemNames.put("lotr:tile.fenceGateWillow", Arrays.asList(""));
//ItemNames.put("lotr:tile.flax", Arrays.asList(""));
//ItemNames.put("lotr:tile.flaxPlant", Arrays.asList(""));
//ItemNames.put("lotr:tile.flowerPot", Arrays.asList(""));
//ItemNames.put("lotr:tile.fruitLeaves", Arrays.asList(""));
//ItemNames.put("lotr:tile.fruitSapling", Arrays.asList(""));
//ItemNames.put("lotr:tile.fruitWood", Arrays.asList(""));
//ItemNames.put("lotr:tile.galadhrimBars", Arrays.asList(""));
//ItemNames.put("lotr:tile.galadhrimWoodBars", Arrays.asList(""));
//ItemNames.put("lotr:tile.gateBronzeBars", Arrays.asList(""));
//ItemNames.put("lotr:tile.gateDolAmroth", Arrays.asList(""));
//ItemNames.put("lotr:tile.gateDwarven", Arrays.asList(""));
//ItemNames.put("lotr:tile.gateElven", Arrays.asList(""));
//ItemNames.put("lotr:tile.gateGold", Arrays.asList(""));
//ItemNames.put("lotr:tile.gateGondor", Arrays.asList(""));
//ItemNames.put("lotr:tile.gateHighElven", Arrays.asList(""));
//ItemNames.put("lotr:tile.gateHobbitBlue", Arrays.asList(""));
//ItemNames.put("lotr:tile.gateHobbitGreen", Arrays.asList(""));
//ItemNames.put("lotr:tile.gateHobbitRed", Arrays.asList(""));
//ItemNames.put("lotr:tile.gateHobbitYellow", Arrays.asList(""));
//ItemNames.put("lotr:tile.gateIronBars", Arrays.asList(""));
//ItemNames.put("lotr:tile.gateMithril", Arrays.asList(""));
//ItemNames.put("lotr:tile.gateNearHarad", Arrays.asList(""));
//ItemNames.put("lotr:tile.gateOrc", Arrays.asList(""));
//ItemNames.put("lotr:tile.gateRhun", Arrays.asList(""));
//ItemNames.put("lotr:tile.gateRohan", Arrays.asList(""));
//ItemNames.put("lotr:tile.gateSilver", Arrays.asList(""));
//ItemNames.put("lotr:tile.gateTauredain", Arrays.asList(""));
//ItemNames.put("lotr:tile.gateUruk", Arrays.asList(""));
//ItemNames.put("lotr:tile.gateWoodElven", Arrays.asList(""));
//ItemNames.put("lotr:tile.gateWooden", Arrays.asList(""));
//ItemNames.put("lotr:tile.gateWoodenCross", Arrays.asList(""));
//ItemNames.put("lotr:tile.glass", Arrays.asList(""));
//ItemNames.put("lotr:tile.glassBottle", Arrays.asList(""));
//ItemNames.put("lotr:tile.glassPane", Arrays.asList(""));
//ItemNames.put("lotr:tile.gobletCopper", Arrays.asList(""));
//ItemNames.put("lotr:tile.gobletGold", Arrays.asList(""));
//ItemNames.put("lotr:tile.gobletSilver", Arrays.asList(""));
//ItemNames.put("lotr:tile.gobletWood", Arrays.asList(""));
//ItemNames.put("lotr:tile.goldBars", Arrays.asList(""));
//ItemNames.put("lotr:tile.gondorianCraftingTable", Arrays.asList(""));
//ItemNames.put("lotr:tile.goran", Arrays.asList(""));
//ItemNames.put("lotr:tile.grapevine", Arrays.asList(""));
//ItemNames.put("lotr:tile.grapevineRed", Arrays.asList(""));
//ItemNames.put("lotr:tile.grapevineWhite", Arrays.asList(""));
//ItemNames.put("lotr:tile.guldurilBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.gulfCraftingTable", Arrays.asList(""));
//ItemNames.put("lotr:tile.gundabadCraftingTable", Arrays.asList(""));
//ItemNames.put("lotr:tile.halfTrollCraftingTable", Arrays.asList(""));
//ItemNames.put("lotr:tile.haradFlower", Arrays.asList(""));
//ItemNames.put("lotr:tile.hearth", Arrays.asList(""));
//ItemNames.put("lotr:tile.highElfBars", Arrays.asList(""));
//ItemNames.put("lotr:tile.highElfWoodBars", Arrays.asList(""));
//ItemNames.put("lotr:tile.highElvenBed", Arrays.asList(""));
//ItemNames.put("lotr:tile.highElvenCraftingTable", Arrays.asList(""));
//ItemNames.put("lotr:tile.highElvenTorch", Arrays.asList(""));
//ItemNames.put("lotr:tile.hithlainLadder", Arrays.asList(""));
//ItemNames.put("lotr:tile.hobbitCraftingTable", Arrays.asList(""));
//ItemNames.put("lotr:tile.hobbitOven", Arrays.asList(""));
//ItemNames.put("lotr:tile.kebabBlock", Arrays.asList(""));
//ItemNames.put("lotr:tile.kebabStand", Arrays.asList(""));
//ItemNames.put("lotr:tile.kebabStandSand", Arrays.asList(""));
//ItemNames.put("lotr:tile.lavender", Arrays.asList(""));
//ItemNames.put("lotr:tile.leaves", Arrays.asList(""));
//ItemNames.put("lotr:tile.leaves2", Arrays.asList(""));
//ItemNames.put("lotr:tile.leaves3", Arrays.asList(""));
//ItemNames.put("lotr:tile.leaves4", Arrays.asList(""));
//ItemNames.put("lotr:tile.leaves5", Arrays.asList(""));
//ItemNames.put("lotr:tile.leaves6", Arrays.asList(""));
//ItemNames.put("lotr:tile.leaves7", Arrays.asList(""));
//ItemNames.put("lotr:tile.leaves8", Arrays.asList(""));
//ItemNames.put("lotr:tile.leaves9", Arrays.asList(""));
//ItemNames.put("lotr:tile.leek", Arrays.asList(""));
//ItemNames.put("lotr:tile.lemonCake", Arrays.asList(""));
//ItemNames.put("lotr:tile.lettuce", Arrays.asList(""));
//ItemNames.put("lotr:tile.lionBed", Arrays.asList(""));
//ItemNames.put("lotr:tile.mallornLadder", Arrays.asList(""));
//ItemNames.put("lotr:tile.mallornTorch", Arrays.asList(""));
//ItemNames.put("lotr:tile.mallornTorchBlue", Arrays.asList(""));
//ItemNames.put("lotr:tile.mallornTorchGold", Arrays.asList(""));
//ItemNames.put("lotr:tile.mallornTorchGreen", Arrays.asList(""));
//ItemNames.put("lotr:tile.marigold", Arrays.asList(""));
//ItemNames.put("lotr:tile.marshLights", Arrays.asList(""));
//ItemNames.put("lotr:tile.marzipanBlock", Arrays.asList(""));
//ItemNames.put("lotr:tile.mechanisedRailOff", Arrays.asList(""));
//ItemNames.put("lotr:tile.mechanisedRailOn", Arrays.asList(""));
//ItemNames.put("lotr:tile.millstone", Arrays.asList(""));
//ItemNames.put("lotr:tile.mirkVines", Arrays.asList(""));
//ItemNames.put("lotr:tile.mithrilBars", Arrays.asList(""));
//ItemNames.put("lotr:tile.mobSpawner", Arrays.asList(""));
//ItemNames.put("lotr:tile.mordorDirt", Arrays.asList(""));
//ItemNames.put("lotr:tile.mordorGrass", Arrays.asList(""));
//ItemNames.put("lotr:tile.mordorGravel", Arrays.asList(""));
//ItemNames.put("lotr:tile.mordorMoss", Arrays.asList(""));
//ItemNames.put("lotr:tile.mordorThorn", Arrays.asList(""));
//ItemNames.put("lotr:tile.moredainCraftingTable", Arrays.asList(""));
//ItemNames.put("lotr:tile.morgulCraftingTable", Arrays.asList(""));
//ItemNames.put("lotr:tile.morgulFlower", Arrays.asList(""));
//ItemNames.put("lotr:tile.morgulPortal", Arrays.asList(""));
//ItemNames.put("lotr:tile.morgulShroom", Arrays.asList(""));
//ItemNames.put("lotr:tile.morgulTorch", Arrays.asList(""));
//ItemNames.put("lotr:tile.mud", Arrays.asList(""));
//ItemNames.put("lotr:tile.mudFarmland", Arrays.asList(""));
//ItemNames.put("lotr:tile.mudGrass", Arrays.asList(""));
//ItemNames.put("lotr:tile.mug", Arrays.asList(""));
//ItemNames.put("lotr:tile.nearHaradCraftingTable", Arrays.asList(""));
//ItemNames.put("lotr:tile.niphredil", Arrays.asList(""));
//ItemNames.put("lotr:tile.obsidianGravel", Arrays.asList(""));
//ItemNames.put("lotr:tile.orcBed", Arrays.asList(""));
//ItemNames.put("lotr:tile.orcBomb", Arrays.asList(""));
//ItemNames.put("lotr:tile.orcChain", Arrays.asList(""));
//ItemNames.put("lotr:tile.orcForge", Arrays.asList(""));
//ItemNames.put("lotr:tile.orcPlating", Arrays.asList(""));
//ItemNames.put("lotr:tile.orcSteelBars", Arrays.asList(""));
//ItemNames.put("lotr:tile.orcTorch", Arrays.asList(""));
//ItemNames.put("lotr:tile.oreCopper", Arrays.asList(""));
//ItemNames.put("lotr:tile.oreGem", Arrays.asList(""));
//ItemNames.put("lotr:tile.oreGlowstone", Arrays.asList(""));
//ItemNames.put("lotr:tile.oreGulduril", Arrays.asList(""));
//ItemNames.put("lotr:tile.oreMithril", Arrays.asList(""));
//ItemNames.put("lotr:tile.oreMorgulIron", Arrays.asList(""));
//ItemNames.put("lotr:tile.oreNaurite", Arrays.asList(""));
//ItemNames.put("lotr:tile.oreQuendite", Arrays.asList(""));
//ItemNames.put("lotr:tile.oreSalt", Arrays.asList(""));
//ItemNames.put("lotr:tile.oreSaltpeter", Arrays.asList(""));
//ItemNames.put("lotr:tile.oreSilver", Arrays.asList(""));
//ItemNames.put("lotr:tile.oreStorage", Arrays.asList(""));
//ItemNames.put("lotr:tile.oreStorage2", Arrays.asList(""));
//ItemNames.put("lotr:tile.oreSulfur", Arrays.asList(""));
//ItemNames.put("lotr:tile.oreTin", Arrays.asList(""));
//ItemNames.put("lotr:tile.pillar", Arrays.asList(""));
//ItemNames.put("lotr:tile.pillar2", Arrays.asList(""));
//ItemNames.put("lotr:tile.pillar3", Arrays.asList(""));
//ItemNames.put("lotr:tile.pipeweed", Arrays.asList(""));
//ItemNames.put("lotr:tile.pipeweedPlant", Arrays.asList(""));
//ItemNames.put("lotr:tile.planks", Arrays.asList(""));
//ItemNames.put("lotr:tile.planks2", Arrays.asList(""));
//ItemNames.put("lotr:tile.planks3", Arrays.asList(""));
//ItemNames.put("lotr:tile.planksRotten", Arrays.asList(""));
//ItemNames.put("lotr:tile.plate", Arrays.asList(""));
//ItemNames.put("lotr:tile.pressurePlateBlueRock", Arrays.asList(""));
//ItemNames.put("lotr:tile.pressurePlateChalk", Arrays.asList(""));
//ItemNames.put("lotr:tile.pressurePlateGondorRock", Arrays.asList(""));
//ItemNames.put("lotr:tile.pressurePlateMordorRock", Arrays.asList(""));
//ItemNames.put("lotr:tile.pressurePlateRedRock", Arrays.asList(""));
//ItemNames.put("lotr:tile.pressurePlateRohanRock", Arrays.asList(""));
//ItemNames.put("lotr:tile.quagmire", Arrays.asList(""));
//ItemNames.put("lotr:tile.quenditeGrass", Arrays.asList(""));
//ItemNames.put("lotr:tile.rangerCraftingTable", Arrays.asList(""));
//ItemNames.put("lotr:tile.redBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.redClay", Arrays.asList(""));
//ItemNames.put("lotr:tile.redSandstone", Arrays.asList(""));
//ItemNames.put("lotr:tile.reedBars", Arrays.asList(""));
//ItemNames.put("lotr:tile.reeds", Arrays.asList(""));
//ItemNames.put("lotr:tile.remains", Arrays.asList(""));
//ItemNames.put("lotr:tile.rhunCraftingTable", Arrays.asList(""));
//ItemNames.put("lotr:tile.rhunFire", Arrays.asList(""));
//ItemNames.put("lotr:tile.rhunFireJar", Arrays.asList(""));
//ItemNames.put("lotr:tile.rhunFlower", Arrays.asList(""));
//ItemNames.put("lotr:tile.rivendellCraftingTable", Arrays.asList(""));
//ItemNames.put("lotr:tile.rock", Arrays.asList(""));
//ItemNames.put("lotr:tile.rohirricCraftingTable", Arrays.asList(""));
//ItemNames.put("lotr:tile.rope", Arrays.asList(""));
//ItemNames.put("lotr:tile.rottenLog", Arrays.asList(""));
//ItemNames.put("lotr:tile.rottenSlabDouble", Arrays.asList(""));
//ItemNames.put("lotr:tile.rottenSlabSingle", Arrays.asList(""));
//ItemNames.put("lotr:tile.sapling", Arrays.asList(""));
//ItemNames.put("lotr:tile.sapling2", Arrays.asList(""));
//ItemNames.put("lotr:tile.sapling3", Arrays.asList(""));
//ItemNames.put("lotr:tile.sapling4", Arrays.asList(""));
//ItemNames.put("lotr:tile.sapling5", Arrays.asList(""));
//ItemNames.put("lotr:tile.sapling6", Arrays.asList(""));
//ItemNames.put("lotr:tile.sapling7", Arrays.asList(""));
//ItemNames.put("lotr:tile.sapling8", Arrays.asList(""));
//ItemNames.put("lotr:tile.sapling9", Arrays.asList(""));
//ItemNames.put("lotr:tile.scorchedSlabDouble", Arrays.asList(""));
//ItemNames.put("lotr:tile.scorchedSlabSingle", Arrays.asList(""));
//ItemNames.put("lotr:tile.scorchedStone", Arrays.asList(""));
//ItemNames.put("lotr:tile.scorchedWall", Arrays.asList(""));
//ItemNames.put("lotr:tile.shireHeather", Arrays.asList(""));
//ItemNames.put("lotr:tile.signCarved", Arrays.asList(""));
//ItemNames.put("lotr:tile.signCarvedIthildin", Arrays.asList(""));
//ItemNames.put("lotr:tile.silverBars", Arrays.asList(""));
//ItemNames.put("lotr:tile.simbelmyne", Arrays.asList(""));
//ItemNames.put("lotr:tile.skullCup", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabBoneDouble", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabBoneSingle", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabClayTileDouble", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabClayTileDyedDouble", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabClayTileDyedDouble2", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabClayTileDyedSingle", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabClayTileDyedSingle2", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabClayTileSingle", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabDouble", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabDouble10", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabDouble11", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabDouble12", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabDouble13", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabDouble14", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabDouble2", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabDouble3", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabDouble4", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabDouble5", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabDouble6", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabDouble7", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabDouble8", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabDouble9", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabDoubleDirt", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabDoubleGravel", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabDoubleSand", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabDoubleThatch", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabDoubleV", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabSingle", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabSingle10", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabSingle11", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabSingle12", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabSingle13", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabSingle14", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabSingle2", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabSingle3", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabSingle4", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabSingle5", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabSingle6", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabSingle7", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabSingle8", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabSingle9", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabSingleDirt", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabSingleGravel", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabSingleSand", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabSingleThatch", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabSingleV", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabUtumnoDouble", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabUtumnoDouble2", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabUtumnoSingle", Arrays.asList(""));
//ItemNames.put("lotr:tile.slabUtumnoSingle2", Arrays.asList(""));
//ItemNames.put("lotr:tile.smoothStone", Arrays.asList(""));
//ItemNames.put("lotr:tile.smoothStoneV", Arrays.asList(""));
//ItemNames.put("lotr:tile.spawnerChest", Arrays.asList(""));
//ItemNames.put("lotr:tile.spawnerChestAncientHarad", Arrays.asList(""));
//ItemNames.put("lotr:tile.spawnerChestStone", Arrays.asList(""));
//ItemNames.put("lotr:tile.stainedGlass", Arrays.asList(""));
//ItemNames.put("lotr:tile.stainedGlassPane", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsAlmond", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsAngmarBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsAngmarBrickCracked", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsAngmarBrickSnow", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsApple", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsArnorBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsArnorBrickCracked", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsArnorBrickMossy", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsAspen", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsBanana", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsBaobab", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsBeech", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsBlackGondorBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsBlueRock", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsBlueRockBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsBone", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsBrickCracked", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsBrickMossy", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsCedar", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsChalk", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsChalkBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsCharred", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsCherry", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsChestnut", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsClayTile", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsClayTileDyedBlack", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsClayTileDyedBlue", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsClayTileDyedBrown", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsClayTileDyedCyan", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsClayTileDyedGray", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsClayTileDyedGreen", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsClayTileDyedLightBlue", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsClayTileDyedLightGray", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsClayTileDyedLime", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsClayTileDyedMagenta", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsClayTileDyedOrange", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsClayTileDyedPink", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsClayTileDyedPurple", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsClayTileDyedRed", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsClayTileDyedWhite", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsClayTileDyedYellow", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsCobblestoneMossy", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsCypress", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsDaleBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsDaleBrickCracked", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsDaleBrickMossy", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsDatePalm", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsDolAmrothBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsDolGuldurBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsDolGuldurBrickCracked", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsDolGuldurBrickMossy", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsDorwinionBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsDorwinionBrickCracked", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsDorwinionBrickFlowers", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsDorwinionBrickMossy", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsDragon", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsDwarvenBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsDwarvenBrickCracked", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsDwarvenBrickObsidian", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsElvenBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsElvenBrickCracked", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsElvenBrickMossy", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsFir", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsGondorBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsGondorBrickCracked", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsGondorBrickMossy", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsGondorBrickRustic", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsGondorBrickRusticCracked", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsGondorBrickRusticMossy", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsGondorRock", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsGreenOak", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsHighElvenBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsHighElvenBrickCracked", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsHighElvenBrickMossy", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsHolly", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsKanuka", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsLairelosse", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsLarch", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsLebethron", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsLemon", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsLime", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsMahogany", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsMallorn", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsMango", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsMangrove", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsMaple", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsMirkOak", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsMordorBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsMordorBrickCracked", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsMordorRock", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsMoredainBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsMorwaithBrickCracked", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsMudBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsNearHaradBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsNearHaradBrickCracked", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsNearHaradBrickRed", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsNearHaradBrickRedCracked", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsOlive", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsOrange", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsPalm", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsPear", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsPine", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsPinePine", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsPlum", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsPomegranate", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsRedRock", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsRedRockBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsRedSandstone", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsRedwood", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsReed", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsRhunBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsRhunBrickCracked", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsRhunBrickFlowers", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsRhunBrickMossy", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsRhunBrickRed", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsRohanBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsRohanRock", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsRotten", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsScorchedStone", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsStone", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsStoneBrickCracked", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsStoneBrickMossy", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsTauredainBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsTauredainBrickCracked", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsTauredainBrickGold", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsTauredainBrickMossy", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsTauredainBrickObsidian", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsThatch", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsUmbarBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsUmbarBrickCracked", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsUrukBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsUtumnoBrickFire", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsUtumnoBrickIce", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsUtumnoBrickObsidian", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsUtumnoTileFire", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsUtumnoTileIce", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsUtumnoTileObsidian", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsWhiteSandstone", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsWillow", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsWoodElvenBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsWoodElvenBrickCracked", Arrays.asList(""));
//ItemNames.put("lotr:tile.stairsWoodElvenBrickMossy", Arrays.asList(""));
//ItemNames.put("lotr:tile.stalactite", Arrays.asList(""));
//ItemNames.put("lotr:tile.stalactiteIce", Arrays.asList(""));
//ItemNames.put("lotr:tile.stalactiteObsidian", Arrays.asList(""));
//ItemNames.put("lotr:tile.strawBed", Arrays.asList(""));
//ItemNames.put("lotr:tile.tallGrass", Arrays.asList(""));
//ItemNames.put("lotr:tile.tauredainCraftingTable", Arrays.asList(""));
//ItemNames.put("lotr:tile.tauredainDartTrap", Arrays.asList(""));
//ItemNames.put("lotr:tile.tauredainDartTrapGold", Arrays.asList(""));
//ItemNames.put("lotr:tile.tauredainDartTrapObsidian", Arrays.asList(""));
//ItemNames.put("lotr:tile.tauredainDoubleTorch", Arrays.asList(""));
//ItemNames.put("lotr:tile.termiteMound", Arrays.asList(""));
//ItemNames.put("lotr:tile.thatch", Arrays.asList(""));
//ItemNames.put("lotr:tile.thatchFloor", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorAcacia", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorAlmond", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorApple", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorAspen", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorBanana", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorBaobab", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorBeech", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorBirch", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorCedar", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorCharred", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorCherry", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorChestnut", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorCypress", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorDarkOak", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorDatePalm", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorDragon", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorFir", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorGreenOak", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorHolly", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorJungle", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorKanuka", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorLairelosse", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorLarch", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorLebethron", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorLemon", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorLime", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorMahogany", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorMallorn", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorMango", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorMangrove", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorMaple", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorMirkOak", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorOlive", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorOrange", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorPalm", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorPear", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorPine", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorPlum", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorPomegranate", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorRedwood", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorRotten", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorShirePine", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorSpruce", Arrays.asList(""));
//ItemNames.put("lotr:tile.trapdoorWillow", Arrays.asList(""));
//ItemNames.put("lotr:tile.treasureCopper", Arrays.asList(""));
//ItemNames.put("lotr:tile.treasureGold", Arrays.asList(""));
//ItemNames.put("lotr:tile.treasureSilver", Arrays.asList(""));
//ItemNames.put("lotr:tile.trollTotem", Arrays.asList(""));
//ItemNames.put("lotr:tile.turnip", Arrays.asList(""));
//ItemNames.put("lotr:tile.umbarCraftingTable", Arrays.asList(""));
//ItemNames.put("lotr:tile.unsmeltery", Arrays.asList(""));
//ItemNames.put("lotr:tile.urukBars", Arrays.asList(""));
//ItemNames.put("lotr:tile.urukCraftingTable", Arrays.asList(""));
//ItemNames.put("lotr:tile.utumnoBrick", Arrays.asList(""));
//ItemNames.put("lotr:tile.utumnoBrickEntrance", Arrays.asList(""));
//ItemNames.put("lotr:tile.utumnoPillar", Arrays.asList(""));
//ItemNames.put("lotr:tile.utumnoPortal", Arrays.asList(""));
//ItemNames.put("lotr:tile.utumnoReturnLight", Arrays.asList(""));
//ItemNames.put("lotr:tile.utumnoReturnPortal", Arrays.asList(""));
//ItemNames.put("lotr:tile.utumnoReturnPortalBase", Arrays.asList(""));
//ItemNames.put("lotr:tile.wallBone", Arrays.asList(""));
//ItemNames.put("lotr:tile.wallClayTile", Arrays.asList(""));
//ItemNames.put("lotr:tile.wallClayTileDyed", Arrays.asList(""));
//ItemNames.put("lotr:tile.wallStone", Arrays.asList(""));
//ItemNames.put("lotr:tile.wallStone2", Arrays.asList(""));
//ItemNames.put("lotr:tile.wallStone3", Arrays.asList(""));
//ItemNames.put("lotr:tile.wallStone4", Arrays.asList(""));
//ItemNames.put("lotr:tile.wallStone5", Arrays.asList(""));
//ItemNames.put("lotr:tile.wallStoneV", Arrays.asList(""));
//ItemNames.put("lotr:tile.wallUtumno", Arrays.asList(""));
//ItemNames.put("lotr:tile.wargFurBed", Arrays.asList(""));
//ItemNames.put("lotr:tile.wasteBlock", Arrays.asList(""));
//ItemNames.put("lotr:tile.weaponRack", Arrays.asList(""));
//ItemNames.put("lotr:tile.webUngoliant", Arrays.asList(""));
//ItemNames.put("lotr:tile.whiteSand", Arrays.asList(""));
//ItemNames.put("lotr:tile.whiteSandstone", Arrays.asList(""));
//ItemNames.put("lotr:tile.willowVines", Arrays.asList(""));
//ItemNames.put("lotr:tile.wineGlass", Arrays.asList(""));
//ItemNames.put("lotr:tile.wood", Arrays.asList(""));
//ItemNames.put("lotr:tile.wood2", Arrays.asList(""));
//ItemNames.put("lotr:tile.wood3", Arrays.asList(""));
//ItemNames.put("lotr:tile.wood4", Arrays.asList(""));
//ItemNames.put("lotr:tile.wood5", Arrays.asList(""));
//ItemNames.put("lotr:tile.wood6", Arrays.asList(""));
//ItemNames.put("lotr:tile.wood7", Arrays.asList(""));
//ItemNames.put("lotr:tile.wood8", Arrays.asList(""));
//ItemNames.put("lotr:tile.wood9", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodBeam1", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodBeam2", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodBeam3", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodBeam4", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodBeam5", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodBeam6", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodBeam7", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodBeam8", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodBeam9", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodBeamFruit", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodBeamRotten", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodBeamS", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodBeamV1", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodBeamV2", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodElfBars", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodElfWoodBars", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodElvenBed", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodElvenCraftingTable", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodElvenTorch", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodPlate", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodSlabDouble", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodSlabDouble2", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodSlabDouble3", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodSlabDouble4", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodSlabDouble5", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodSlabSingle", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodSlabSingle2", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodSlabSingle3", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodSlabSingle4", Arrays.asList(""));
//ItemNames.put("lotr:tile.woodSlabSingle5", Arrays.asList(""));
//ItemNames.put("lotr:tile.yam", Arrays.asList(""));

//ItemNames.put("minecraft:acacia_stairs", Arrays.asList(""));
//ItemNames.put("minecraft:activator_rail", Arrays.asList(""));
//ItemNames.put("minecraft:anvil", Arrays.asList(""));
//ItemNames.put("minecraft:apple", Arrays.asList(""));
//ItemNames.put("minecraft:arrow", Arrays.asList(""));
//ItemNames.put("minecraft:baked_potato", Arrays.asList(""));
//ItemNames.put("minecraft:beacon", Arrays.asList(""));
//ItemNames.put("minecraft:bed", Arrays.asList(""));
//ItemNames.put("minecraft:bedrock", Arrays.asList(""));
//ItemNames.put("minecraft:beef", Arrays.asList(""));
//ItemNames.put("minecraft:birch_stairs", Arrays.asList(""));
//ItemNames.put("minecraft:blaze_powder", Arrays.asList(""));
//ItemNames.put("minecraft:blaze_rod", Arrays.asList(""));
//ItemNames.put("minecraft:boat", Arrays.asList(""));
//ItemNames.put("minecraft:bone", Arrays.asList(""));
//ItemNames.put("minecraft:book", Arrays.asList(""));
//ItemNames.put("minecraft:bookshelf", Arrays.asList(""));
//ItemNames.put("minecraft:bow", Arrays.asList(""));
//ItemNames.put("minecraft:bowl", Arrays.asList(""));
//ItemNames.put("minecraft:bread", Arrays.asList(""));
//ItemNames.put("minecraft:brewing_stand", Arrays.asList(""));
//ItemNames.put("minecraft:brick", Arrays.asList(""));
//ItemNames.put("minecraft:brick_block", Arrays.asList(""));
//ItemNames.put("minecraft:brick_stairs", Arrays.asList(""));
//ItemNames.put("minecraft:brown_mushroom", Arrays.asList(""));
//ItemNames.put("minecraft:brown_mushroom_block", Arrays.asList(""));
//ItemNames.put("minecraft:bucket", Arrays.asList(""));
//ItemNames.put("minecraft:cactus", Arrays.asList(""));
//ItemNames.put("minecraft:cake", Arrays.asList(""));
//ItemNames.put("minecraft:carpet", Arrays.asList(""));
//ItemNames.put("minecraft:carrot", Arrays.asList(""));
//ItemNames.put("minecraft:carrot_on_a_stick", Arrays.asList(""));
//ItemNames.put("minecraft:carrots", Arrays.asList(""));
//ItemNames.put("minecraft:cauldron", Arrays.asList(""));
//ItemNames.put("minecraft:chainmail_boots", Arrays.asList(""));
//ItemNames.put("minecraft:chainmail_chestplate", Arrays.asList(""));
//ItemNames.put("minecraft:chainmail_helmet", Arrays.asList(""));
//ItemNames.put("minecraft:chainmail_leggings", Arrays.asList(""));
//ItemNames.put("minecraft:chest", Arrays.asList(""));
//ItemNames.put("minecraft:chest_minecart", Arrays.asList(""));
//ItemNames.put("minecraft:chicken", Arrays.asList(""));
//ItemNames.put("minecraft:clay", Arrays.asList(""));
//ItemNames.put("minecraft:clay_ball", Arrays.asList(""));
//ItemNames.put("minecraft:clock", Arrays.asList(""));
//ItemNames.put("minecraft:coal", Arrays.asList(""));
//ItemNames.put("minecraft:coal_block", Arrays.asList(""));
//ItemNames.put("minecraft:coal_ore", Arrays.asList(""));
//ItemNames.put("minecraft:cobblestone", Arrays.asList(""));
//ItemNames.put("minecraft:cobblestone_wall", Arrays.asList(""));
//ItemNames.put("minecraft:cocoa", Arrays.asList(""));
//ItemNames.put("minecraft:command_block", Arrays.asList(""));
//ItemNames.put("minecraft:command_block_minecart", Arrays.asList(""));
//ItemNames.put("minecraft:comparator", Arrays.asList(""));
//ItemNames.put("minecraft:compass", Arrays.asList(""));
//ItemNames.put("minecraft:cooked_beef", Arrays.asList(""));
//ItemNames.put("minecraft:cooked_chicken", Arrays.asList(""));
//ItemNames.put("minecraft:cooked_fished", Arrays.asList(""));
//ItemNames.put("minecraft:cooked_porkchop", Arrays.asList(""));
//ItemNames.put("minecraft:cookie", Arrays.asList(""));
//ItemNames.put("minecraft:crafting_table", Arrays.asList(""));
//ItemNames.put("minecraft:dark_oak_stairs", Arrays.asList(""));
//ItemNames.put("minecraft:daylight_detector", Arrays.asList(""));
//ItemNames.put("minecraft:deadbush", Arrays.asList(""));
//ItemNames.put("minecraft:detector_rail", Arrays.asList(""));
//ItemNames.put("minecraft:diamond", Arrays.asList(""));
//ItemNames.put("minecraft:diamond_axe", Arrays.asList(""));
//ItemNames.put("minecraft:diamond_block", Arrays.asList(""));
//ItemNames.put("minecraft:diamond_boots", Arrays.asList(""));
//ItemNames.put("minecraft:diamond_chestplate", Arrays.asList(""));
//ItemNames.put("minecraft:diamond_helmet", Arrays.asList(""));
//ItemNames.put("minecraft:diamond_hoe", Arrays.asList(""));
//ItemNames.put("minecraft:diamond_horse_armor", Arrays.asList(""));
//ItemNames.put("minecraft:diamond_leggings", Arrays.asList(""));
//ItemNames.put("minecraft:diamond_ore", Arrays.asList(""));
//ItemNames.put("minecraft:diamond_pickaxe", Arrays.asList(""));
//ItemNames.put("minecraft:diamond_shovel", Arrays.asList(""));
//ItemNames.put("minecraft:diamond_sword", Arrays.asList(""));
//ItemNames.put("minecraft:dirt", Arrays.asList(""));
//ItemNames.put("minecraft:dispenser", Arrays.asList(""));
//ItemNames.put("minecraft:double_plant", Arrays.asList(""));
//ItemNames.put("minecraft:double_stone_slab", Arrays.asList(""));
//ItemNames.put("minecraft:double_wooden_slab", Arrays.asList(""));
//ItemNames.put("minecraft:dragon_egg", Arrays.asList(""));
//ItemNames.put("minecraft:dropper", Arrays.asList(""));
//ItemNames.put("minecraft:dye", Arrays.asList(""));
//ItemNames.put("minecraft:egg", Arrays.asList(""));
//ItemNames.put("minecraft:emerald", Arrays.asList(""));
//ItemNames.put("minecraft:emerald_block", Arrays.asList(""));
//ItemNames.put("minecraft:emerald_ore", Arrays.asList(""));
//ItemNames.put("minecraft:enchanted_book", Arrays.asList(""));
//ItemNames.put("minecraft:enchanting_table", Arrays.asList(""));
//ItemNames.put("minecraft:end_portal", Arrays.asList(""));
//ItemNames.put("minecraft:end_portal_frame", Arrays.asList(""));
//ItemNames.put("minecraft:end_stone", Arrays.asList(""));
//ItemNames.put("minecraft:ender_chest", Arrays.asList(""));
//ItemNames.put("minecraft:ender_eye", Arrays.asList(""));
//ItemNames.put("minecraft:ender_pearl", Arrays.asList(""));
//ItemNames.put("minecraft:experience_bottle", Arrays.asList(""));
//ItemNames.put("minecraft:farmland", Arrays.asList(""));
//ItemNames.put("minecraft:feather", Arrays.asList(""));
//ItemNames.put("minecraft:fence", Arrays.asList(""));
//ItemNames.put("minecraft:fence_gate", Arrays.asList(""));
//ItemNames.put("minecraft:fermented_spider_eye", Arrays.asList(""));
//ItemNames.put("minecraft:filled_map", Arrays.asList(""));
//ItemNames.put("minecraft:fire", Arrays.asList(""));
//ItemNames.put("minecraft:fire_charge", Arrays.asList(""));
//ItemNames.put("minecraft:firework_charge", Arrays.asList(""));
//ItemNames.put("minecraft:fireworks", Arrays.asList(""));
//ItemNames.put("minecraft:fish", Arrays.asList(""));
//ItemNames.put("minecraft:fishing_rod", Arrays.asList(""));
//ItemNames.put("minecraft:flint", Arrays.asList(""));
//ItemNames.put("minecraft:flint_and_steel", Arrays.asList(""));
//ItemNames.put("minecraft:flower_pot", Arrays.asList(""));
//ItemNames.put("minecraft:flowing_lava", Arrays.asList(""));
//ItemNames.put("minecraft:flowing_water", Arrays.asList(""));
//ItemNames.put("minecraft:furnace", Arrays.asList(""));
//ItemNames.put("minecraft:furnace_minecart", Arrays.asList(""));
//ItemNames.put("minecraft:ghast_tear", Arrays.asList(""));
//ItemNames.put("minecraft:glass", Arrays.asList(""));
//ItemNames.put("minecraft:glass_bottle", Arrays.asList(""));
//ItemNames.put("minecraft:glass_pane", Arrays.asList(""));
//ItemNames.put("minecraft:glowstone", Arrays.asList(""));
//ItemNames.put("minecraft:glowstone_dust", Arrays.asList(""));
//ItemNames.put("minecraft:gold_block", Arrays.asList(""));
//ItemNames.put("minecraft:gold_ingot", Arrays.asList(""));
//ItemNames.put("minecraft:gold_nugget", Arrays.asList(""));
//ItemNames.put("minecraft:gold_ore", Arrays.asList(""));
//ItemNames.put("minecraft:golden_apple", Arrays.asList(""));
//ItemNames.put("minecraft:golden_axe", Arrays.asList(""));
//ItemNames.put("minecraft:golden_boots", Arrays.asList(""));
//ItemNames.put("minecraft:golden_carrot", Arrays.asList(""));
//ItemNames.put("minecraft:golden_chestplate", Arrays.asList(""));
//ItemNames.put("minecraft:golden_helmet", Arrays.asList(""));
//ItemNames.put("minecraft:golden_hoe", Arrays.asList(""));
//ItemNames.put("minecraft:golden_horse_armor", Arrays.asList(""));
//ItemNames.put("minecraft:golden_leggings", Arrays.asList(""));
//ItemNames.put("minecraft:golden_pickaxe", Arrays.asList(""));
//ItemNames.put("minecraft:golden_rail", Arrays.asList(""));
//ItemNames.put("minecraft:golden_shovel", Arrays.asList(""));
//ItemNames.put("minecraft:golden_sword", Arrays.asList(""));
//ItemNames.put("minecraft:grass", Arrays.asList(""));
//ItemNames.put("minecraft:gravel", Arrays.asList(""));
//ItemNames.put("minecraft:gunpowder", Arrays.asList(""));
//ItemNames.put("minecraft:hardened_clay", Arrays.asList(""));
//ItemNames.put("minecraft:hay_block", Arrays.asList(""));
//ItemNames.put("minecraft:heavy_weighted_pressure_plate", Arrays.asList(""));
//ItemNames.put("minecraft:hopper", Arrays.asList(""));
//ItemNames.put("minecraft:hopper_minecart", Arrays.asList(""));
//ItemNames.put("minecraft:ice", Arrays.asList(""));
//ItemNames.put("minecraft:iron_axe", Arrays.asList(""));
//ItemNames.put("minecraft:iron_bars", Arrays.asList(""));
//ItemNames.put("minecraft:iron_block", Arrays.asList(""));
//ItemNames.put("minecraft:iron_boots", Arrays.asList(""));
//ItemNames.put("minecraft:iron_chestplate", Arrays.asList(""));
//ItemNames.put("minecraft:iron_door", Arrays.asList(""));
//ItemNames.put("minecraft:iron_helmet", Arrays.asList(""));
//ItemNames.put("minecraft:iron_hoe", Arrays.asList(""));
//ItemNames.put("minecraft:iron_horse_armor", Arrays.asList(""));
//ItemNames.put("minecraft:iron_ingot", Arrays.asList(""));
//ItemNames.put("minecraft:iron_leggings", Arrays.asList(""));
//ItemNames.put("minecraft:iron_ore", Arrays.asList(""));
//ItemNames.put("minecraft:iron_pickaxe", Arrays.asList(""));
//ItemNames.put("minecraft:iron_shovel", Arrays.asList(""));
//ItemNames.put("minecraft:iron_sword", Arrays.asList(""));
//ItemNames.put("minecraft:item_frame", Arrays.asList(""));
//ItemNames.put("minecraft:jukebox", Arrays.asList(""));
//ItemNames.put("minecraft:jungle_stairs", Arrays.asList(""));
//ItemNames.put("minecraft:ladder", Arrays.asList(""));
//ItemNames.put("minecraft:lapis_block", Arrays.asList(""));
//ItemNames.put("minecraft:lapis_ore", Arrays.asList(""));
//ItemNames.put("minecraft:lava", Arrays.asList(""));
//ItemNames.put("minecraft:lava_bucket", Arrays.asList(""));
//ItemNames.put("minecraft:lead", Arrays.asList(""));
//ItemNames.put("minecraft:leather", Arrays.asList(""));
//ItemNames.put("minecraft:leather_boots", Arrays.asList(""));
//ItemNames.put("minecraft:leather_chestplate", Arrays.asList(""));
//ItemNames.put("minecraft:leather_helmet", Arrays.asList(""));
//ItemNames.put("minecraft:leather_leggings", Arrays.asList(""));
//ItemNames.put("minecraft:leaves", Arrays.asList(""));
//ItemNames.put("minecraft:leaves2", Arrays.asList(""));
//ItemNames.put("minecraft:lever", Arrays.asList(""));
//ItemNames.put("minecraft:light_weighted_pressure_plate", Arrays.asList(""));
//ItemNames.put("minecraft:lit_furnace", Arrays.asList(""));
//ItemNames.put("minecraft:lit_pumpkin", Arrays.asList(""));
//ItemNames.put("minecraft:log", Arrays.asList(""));
//ItemNames.put("minecraft:log2", Arrays.asList(""));
//ItemNames.put("minecraft:magma_cream", Arrays.asList(""));
//ItemNames.put("minecraft:map", Arrays.asList(""));
//ItemNames.put("minecraft:melon", Arrays.asList(""));
//ItemNames.put("minecraft:melon_block", Arrays.asList(""));
//ItemNames.put("minecraft:melon_seeds", Arrays.asList(""));
//ItemNames.put("minecraft:milk_bucket", Arrays.asList(""));
//ItemNames.put("minecraft:minecart", Arrays.asList(""));
//ItemNames.put("minecraft:mob_spawner", Arrays.asList(""));
//ItemNames.put("minecraft:monster_egg", Arrays.asList(""));
//ItemNames.put("minecraft:mossy_cobblestone", Arrays.asList(""));
//ItemNames.put("minecraft:mushroom_stew", Arrays.asList(""));
//ItemNames.put("minecraft:mycelium", Arrays.asList(""));
//ItemNames.put("minecraft:name_tag", Arrays.asList(""));
//ItemNames.put("minecraft:nether_brick", Arrays.asList(""));
//ItemNames.put("minecraft:nether_brick_fence", Arrays.asList(""));
//ItemNames.put("minecraft:nether_brick_stairs", Arrays.asList(""));
//ItemNames.put("minecraft:nether_star", Arrays.asList(""));
//ItemNames.put("minecraft:nether_wart", Arrays.asList(""));
//ItemNames.put("minecraft:netherbrick", Arrays.asList(""));
//ItemNames.put("minecraft:netherrack", Arrays.asList(""));
//ItemNames.put("minecraft:noteblock", Arrays.asList(""));
//ItemNames.put("minecraft:oak_stairs", Arrays.asList(""));
//ItemNames.put("minecraft:obsidian", Arrays.asList(""));
//ItemNames.put("minecraft:packed_ice", Arrays.asList(""));
//ItemNames.put("minecraft:painting", Arrays.asList(""));
//ItemNames.put("minecraft:paper", Arrays.asList(""));
//ItemNames.put("minecraft:piston", Arrays.asList(""));
//ItemNames.put("minecraft:planks", Arrays.asList(""));
//ItemNames.put("minecraft:poisonous_potato", Arrays.asList(""));
//ItemNames.put("minecraft:porkchop", Arrays.asList(""));
//ItemNames.put("minecraft:portal", Arrays.asList(""));
//ItemNames.put("minecraft:potato", Arrays.asList(""));
//ItemNames.put("minecraft:potatoes", Arrays.asList(""));
//ItemNames.put("minecraft:potion", Arrays.asList(""));
//ItemNames.put("minecraft:pumpkin", Arrays.asList(""));
//ItemNames.put("minecraft:pumpkin_pie", Arrays.asList(""));
//ItemNames.put("minecraft:pumpkin_seeds", Arrays.asList(""));
//ItemNames.put("minecraft:quartz", Arrays.asList(""));
//ItemNames.put("minecraft:quartz_block", Arrays.asList(""));
//ItemNames.put("minecraft:quartz_ore", Arrays.asList(""));
//ItemNames.put("minecraft:quartz_stairs", Arrays.asList(""));
//ItemNames.put("minecraft:rail", Arrays.asList(""));
//ItemNames.put("minecraft:record_11", Arrays.asList(""));
//ItemNames.put("minecraft:record_13", Arrays.asList(""));
//ItemNames.put("minecraft:record_blocks", Arrays.asList(""));
//ItemNames.put("minecraft:record_cat", Arrays.asList(""));
//ItemNames.put("minecraft:record_chirp", Arrays.asList(""));
//ItemNames.put("minecraft:record_far", Arrays.asList(""));
//ItemNames.put("minecraft:record_mall", Arrays.asList(""));
//ItemNames.put("minecraft:record_mellohi", Arrays.asList(""));
//ItemNames.put("minecraft:record_stal", Arrays.asList(""));
//ItemNames.put("minecraft:record_strad", Arrays.asList(""));
//ItemNames.put("minecraft:record_wait", Arrays.asList(""));
//ItemNames.put("minecraft:record_ward", Arrays.asList(""));
//ItemNames.put("minecraft:red_flower", Arrays.asList(""));
//ItemNames.put("minecraft:red_mushroom", Arrays.asList(""));
//ItemNames.put("minecraft:red_mushroom_block", Arrays.asList(""));
//ItemNames.put("minecraft:redstone", Arrays.asList(""));
//ItemNames.put("minecraft:redstone_block", Arrays.asList(""));
//ItemNames.put("minecraft:redstone_lamp", Arrays.asList(""));
//ItemNames.put("minecraft:redstone_ore", Arrays.asList(""));
//ItemNames.put("minecraft:redstone_torch", Arrays.asList(""));
//ItemNames.put("minecraft:reeds", Arrays.asList(""));
//ItemNames.put("minecraft:repeater", Arrays.asList(""));
//ItemNames.put("minecraft:rotten_flesh", Arrays.asList(""));
//ItemNames.put("minecraft:saddle", Arrays.asList(""));
//ItemNames.put("minecraft:sand", Arrays.asList(""));
//ItemNames.put("minecraft:sandstone", Arrays.asList(""));
//ItemNames.put("minecraft:sandstone_stairs", Arrays.asList(""));
//ItemNames.put("minecraft:sapling", Arrays.asList(""));
//ItemNames.put("minecraft:shears", Arrays.asList(""));
//ItemNames.put("minecraft:sign", Arrays.asList(""));
//ItemNames.put("minecraft:skull", Arrays.asList(""));
//ItemNames.put("minecraft:slime_ball", Arrays.asList(""));
//ItemNames.put("minecraft:snow", Arrays.asList(""));
//ItemNames.put("minecraft:snow_layer", Arrays.asList(""));
//ItemNames.put("minecraft:snowball", Arrays.asList(""));
//ItemNames.put("minecraft:soul_sand", Arrays.asList(""));
//ItemNames.put("minecraft:spawn_egg", Arrays.asList(""));
//ItemNames.put("minecraft:speckled_melon", Arrays.asList(""));
//ItemNames.put("minecraft:spider_eye", Arrays.asList(""));
//ItemNames.put("minecraft:sponge", Arrays.asList(""));
//ItemNames.put("minecraft:spruce_stairs", Arrays.asList(""));
//ItemNames.put("minecraft:stained_glass", Arrays.asList(""));
//ItemNames.put("minecraft:stained_glass_pane", Arrays.asList(""));
//ItemNames.put("minecraft:stained_hardened_clay", Arrays.asList(""));
//ItemNames.put("minecraft:stick", Arrays.asList(""));
//ItemNames.put("minecraft:sticky_piston", Arrays.asList(""));
//ItemNames.put("minecraft:stone", Arrays.asList(""));
//ItemNames.put("minecraft:stone_axe", Arrays.asList(""));
//ItemNames.put("minecraft:stone_brick_stairs", Arrays.asList(""));
//ItemNames.put("minecraft:stone_button", Arrays.asList(""));
//ItemNames.put("minecraft:stone_hoe", Arrays.asList(""));
//ItemNames.put("minecraft:stone_pickaxe", Arrays.asList(""));
//ItemNames.put("minecraft:stone_pressure_plate", Arrays.asList(""));
//ItemNames.put("minecraft:stone_shovel", Arrays.asList(""));
//ItemNames.put("minecraft:stone_slab", Arrays.asList(""));
//ItemNames.put("minecraft:stone_stairs", Arrays.asList(""));
//ItemNames.put("minecraft:stone_sword", Arrays.asList(""));
//ItemNames.put("minecraft:stonebrick", Arrays.asList(""));
//ItemNames.put("minecraft:string", Arrays.asList(""));
//ItemNames.put("minecraft:sugar", Arrays.asList(""));
//ItemNames.put("minecraft:tallgrass", Arrays.asList(""));
    }
}


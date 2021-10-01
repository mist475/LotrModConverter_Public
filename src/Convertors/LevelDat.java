package Convertors;

import org.jnbt.*;
import misterymob475.Data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copies {@literal &} Fixes the level.dat file
 */
public class LevelDat implements Convertor{
    final private String pathName;
    final private Map<Integer,String> LegacyIds;
    final private Data Data;

    /**
     * Creates an instance of LevelDat using the provided parameters
     * @param data Instance of {@link Data}
     * @param pathname String of the pathname, see {@link Convertor}
     * @param legacyIds Map of the legacy ids
     */
    public LevelDat(Data data, String pathname,Map<Integer,String> legacyIds) {
        this.Data = data;
        pathName = pathname;
        LegacyIds = legacyIds;
    }

    /**
     * @param p path of the folder where files are copied
     * @param FileName name of the to be copied file
     * @throws IOException if something fails
     */
    @Override
    public void copier(Path p, String FileName) throws IOException {
        //copies over all the files in the LOTR folder to the lotr folder
        Files.copy(Paths.get(p+"/"+this.pathName+"/level.dat"),Paths.get(p +"/"+FileName+"_Converted/level.dat"));
    }

    /**
     *
     * @param p path of the folder where files are copied
     * @param FileName name of the to be modified files
     * @throws IOException if something fails
     */
    @Override
    public void modifier(Path p, String FileName) throws IOException {
        try {

            //new level.dat
            final NBTInputStream input = new NBTInputStream(new FileInputStream(Paths.get(p +"/"+FileName+"_Converted/level.dat").toString()));
            final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
            input.close();

            //old level.dat
            final NBTInputStream input1 = new NBTInputStream(new FileInputStream(Paths.get(p + "/" + FileName+ "/level.dat").toString()));
            final CompoundTag originalTopLevelTag1 = (CompoundTag) input1.readTag();
            input1.close();

            //because of the way playerdata is stored in the level.dat I have moved the fixer to a slightly different function
            Map<String, Tag> originalData = originalTopLevelTag.getValue();

            //this way I can modify the map directly, instead of regenerating it every time
            Map <String, Tag> newData = new HashMap<>(originalData);
            if (newData.containsKey("Data") && (originalTopLevelTag1.getValue()).containsKey("Data")) {
                Map<String, Tag> Data = new HashMap<>(((CompoundTag) newData.get("Data")).getValue());
                Map<String,Tag> Data1 = new HashMap<>(((CompoundTag) (originalTopLevelTag1.getValue()).get("Data")).getValue());




                //GameRules fix (only 9 added in 1.7.10, keeping rest of the selected Renewed World)
                CompoundTag GameRules1_tag = (CompoundTag) Data1.get("GameRules");
                Map<String,Tag> GameRules = new HashMap<>((((CompoundTag) Data.get("GameRules")).getValue()));
                GameRules.replace("commandBlockOutput",GameRules1_tag.getValue().get("commandBlockOutput"));
                GameRules.replace("doDaylightCycle",GameRules1_tag.getValue().get("doDaylightCycle"));
                GameRules.replace("doFireTick",GameRules1_tag.getValue().get("doFireTick"));
                GameRules.replace("doMobLoot",GameRules1_tag.getValue().get("doMobLoot"));
                GameRules.replace("doMobSpawning",GameRules1_tag.getValue().get("doMobSpawning"));
                GameRules.replace("doTileDrops",GameRules1_tag.getValue().get("doTileDrops"));
                GameRules.replace("keepInventory",GameRules1_tag.getValue().get("keepInventory"));
                GameRules.replace("mobGriefing",GameRules1_tag.getValue().get("mobGriefing"));
                GameRules.replace("naturalRegeneration",GameRules1_tag.getValue().get("naturalRegeneration"));
                newData.replace("GameRules",new CompoundTag("GameRules",GameRules));

                Map<String,Tag> WorldGenSettings = new HashMap<>((((CompoundTag) Data.get("WorldGenSettings")).getValue()));
                WorldGenSettings.replace("generate_features",Data1.get("MapFeatures"));
                WorldGenSettings.replace("seed",new LongTag("seed", (Long) Data1.get("RandomSeed").getValue()));
                //dimensions
                CompoundTag dimensions = (CompoundTag) WorldGenSettings.get("dimensions");
                Map<String,Tag> dimensions_map = new HashMap<>(dimensions.getValue());

                //should have made this a loop in hindsight, oh well...

                Map<String,Tag> meDimension = new HashMap<>(((CompoundTag) dimensions.getValue().get("lotr:middle_earth")).getValue());
                Map<String,Tag> overworldDimension = new HashMap<>(((CompoundTag) dimensions.getValue().get("minecraft:overworld")).getValue());
                Map<String,Tag> endDimension = new HashMap<>(((CompoundTag) dimensions.getValue().get("minecraft:the_end")).getValue());
                Map<String,Tag> netherDimension = new HashMap<>(((CompoundTag) dimensions.getValue().get("minecraft:the_nether")).getValue());

                CompoundTag Generator1 = (CompoundTag) ((CompoundTag) dimensions.getValue().get("lotr:middle_earth")).getValue().get("generator");
                CompoundTag Generator2 = (CompoundTag) ((CompoundTag) dimensions.getValue().get("minecraft:overworld")).getValue().get("generator");
                CompoundTag Generator3 = (CompoundTag) ((CompoundTag) dimensions.getValue().get("minecraft:the_end")).getValue().get("generator");
                CompoundTag Generator4 = (CompoundTag) ((CompoundTag) dimensions.getValue().get("minecraft:the_nether")).getValue().get("generator");

                Map<String,Tag> generatormap1 = new HashMap<>(Generator1.getValue());
                Map<String,Tag> generatormap2 = new HashMap<>(Generator2.getValue());
                Map<String,Tag> generatormap3 = new HashMap<>(Generator3.getValue());
                Map<String,Tag> generatormap4 = new HashMap<>(Generator4.getValue());

                //lotr:middle_earth
                //generatormap1.replace("seed",Data1.get("RandomSeed"));
                generatormap1.replace("seed",new LongTag("seed", (Long) Data1.get("RandomSeed").getValue()));
                Map<String,Tag> biome_source1 = new HashMap<>((Map<String, Tag>) generatormap1.get("biome_source").getValue());
                biome_source1.replace("seed",new LongTag("seed", (Long) Data1.get("RandomSeed").getValue()));
                //sets instant_middle_earth right in lotr:middle_earth
                //meClassic apparently doesn't use this tag, even though you definitely spawn directly into middle-earth
                //Data1.get("generatorName").getValue().equals("meClassic") ||
                if (Data1.get("generatorName").getValue().equals("middleEarth")) {
                    generatormap1.replace("instant_middle_earth",new ByteTag("instant_middle_earth",(byte)1));
                    if (Data1.get("generatorName").getValue().equals("meClassic")) biome_source1.replace("classic_biomes",new ByteTag("classic_biomes",(byte)1));
                    else biome_source1.replace("classic_biomes",new ByteTag("classic_biomes",(byte)0));
                }
                else generatormap1.replace("instant_middle_earth",new ByteTag("instant_middle_earth",(byte)0));

                generatormap1.replace("biome_source",new CompoundTag("biome_source",biome_source1));
                meDimension.replace("generator",new CompoundTag("generator",generatormap1));
                dimensions_map.replace("lotr:middle_earth",new CompoundTag("lotr:middle_earth",meDimension));

                //minecraft:overworld
                if ((Data1.get("generatorName").getValue().equals("flat"))) {
                    //handles flat-worlds, hardcodes the default values as transcribing them is beyond the scope of the convertor, salt might be the seed and not actually this odd value
                    generatormap2.replace("type",new StringTag("type","minecraft:flat"));
                    generatormap2.remove("biome_source");
                    generatormap2.remove("seed");
                    generatormap2.remove("settings");
                    Map<String,Tag> settings_map = new HashMap<>();

                    Map<String,Tag> structures1_map = new HashMap<>();

                    Map<String,Tag> stronghold_map = new HashMap<>();
                    stronghold_map.put("count",new IntTag("count",128));
                    stronghold_map.put("distance",new IntTag("distance",32));
                    stronghold_map.put("spread",new IntTag("spread",3));
                    structures1_map.put("stronghold",new CompoundTag("stronghold",stronghold_map));

                    Map<String,Tag> structures2_map = new HashMap<>();
                    Map<String,Tag> village_map = new HashMap<>();
                    village_map.put("salt",new IntTag("salt",10387312));
                    village_map.put("separation",new IntTag("separation",8));
                    village_map.put("spacing",new IntTag("spacing",32));
                    structures2_map.put("minecraft:village", new CompoundTag("minecraft:village",village_map));
                    structures1_map.put("structures",new CompoundTag("structures",structures2_map));

                    settings_map.put("structures",new CompoundTag("structures",structures1_map));

                    List<Tag> layers_list = new ArrayList<>();
                    Map<String,Tag> layer1_map = new HashMap<>();
                    layer1_map.put("block",new StringTag("block","minecraft:bedrock"));
                    layer1_map.put("height", new IntTag("height",1));
                    layers_list.add(new CompoundTag("",layer1_map));
                    Map<String,Tag> layer2_map = new HashMap<>();
                    layer2_map.put("block", new StringTag("block","minecraft:dirt"));
                    layer2_map.put("height",new IntTag("height",2));
                    layers_list.add(new CompoundTag("",layer2_map));
                    Map<String,Tag> layer3_map = new HashMap<>();
                    layer3_map.put("block", new StringTag("block","minecraft:grass_block"));
                    layer3_map.put("height",new IntTag("height",1));
                    layers_list.add(new CompoundTag("",layer3_map));
                    settings_map.put("layers",new ListTag("layers",CompoundTag.class,layers_list));

                    settings_map.put("biome",new StringTag("biome","minecraft:plains"));
                    settings_map.put("features",new ByteTag("features",(byte)0));
                    settings_map.put("lakes",new ByteTag("lakes",(byte)0));
                    generatormap2.put("settings",new CompoundTag("settings",settings_map));
                }
                else {
                    generatormap2.replace("seed",new LongTag("seed", (Long) Data1.get("RandomSeed").getValue()));
                    Map<String,Tag> biome_source2 = new HashMap<>((Map<String, Tag>) generatormap2.get("biome_source").getValue());
                    biome_source2.replace("seed",new LongTag("seed", (Long) Data1.get("RandomSeed").getValue()));
                    generatormap2.replace("biome_source",new CompoundTag("biome_source",biome_source2));
                    if (Data1.get("generatorName").getValue().equals("largeBiomes"))generatormap2.replace("large_biomes",new ByteTag("large_biomes",(byte)1));
                    else generatormap2.replace("large_biomes",new ByteTag("large_biomes",(byte)0));
                }
                overworldDimension.replace("generator",new CompoundTag("generator",generatormap2));
                dimensions_map.replace("minecraft:overworld",new CompoundTag("minecraft:overworld",overworldDimension));

                //minecraft:the_end
                generatormap3.replace("seed",new LongTag("seed", (Long) Data1.get("RandomSeed").getValue()));
                Map<String,Tag> biome_source3 = new HashMap<>((Map<String, Tag>) generatormap3.get("biome_source").getValue());
                biome_source3.replace("seed",new LongTag("seed", (Long) Data1.get("RandomSeed").getValue()));
                generatormap3.replace("biome_source",new CompoundTag("biome_source",biome_source3));
                endDimension.replace("generator",new CompoundTag("generator",generatormap3));
                dimensions_map.replace("minecraft:the_end",new CompoundTag("minecraft:the_end",endDimension));

                //minecraft:the_nether
                generatormap4.replace("seed",new LongTag("seed", (Long) Data1.get("RandomSeed").getValue()));
                HashMap<String, Tag> biome_source4 = new HashMap<>((Map<String, Tag>) generatormap4.get("biome_source").getValue());
                biome_source4.replace("seed",new LongTag("seed", (Long) Data1.get("RandomSeed").getValue()));
                generatormap4.replace("biome_source",new CompoundTag("biome_source",biome_source4));
                netherDimension.replace("generator",new CompoundTag("generator",generatormap4));
                dimensions_map.replace("minecraft:the_nether",new CompoundTag("minecraft:the_nether",netherDimension));

                WorldGenSettings.replace("dimensions",new CompoundTag("dimensions",dimensions_map));
                Data.replace("WorldGenSettings",new CompoundTag("WorldGenSettings",WorldGenSettings));
                //rest of 'Data' fix
                Data.replace("DayTime",Data1.get("DayTime"));
                Data.replace("GameType",Data1.get("GameType"));
                Data.replace("hardcore",Data1.get("hardcore"));
                Data.replace("initialized",Data1.get("initialized"));
                Data.replace("LastPlayed",Data1.get("LastPlayed"));
                Data.replace("LevelName",Data1.get("LevelName"));
                Data.replace("raining",Data1.get("raining"));
                Data.replace("rainTime",Data1.get("rainTime"));
                Data.replace("SpawnX",Data1.get("SpawnX"));
                Data.replace("SpawnY",Data1.get("SpawnY"));
                Data.replace("SpawnZ",Data1.get("SpawnZ"));
                Data.replace("thundering",Data1.get("thundering"));
                Data.replace("thunderTime",Data1.get("thunderTime"));
                Data.replace("Time",Data1.get("Time"));
                Data.replace("version",Data1.get("version"));

                if (Data.containsKey("Player") && Data1.containsKey("Player")) {
                    CompoundTag Player_tag = (CompoundTag) Data1.get("Player");
                    Map<String,Tag> Player = new HashMap<>(Player_tag.getValue());
                    PlayerData.playerFixer(Player, LegacyIds, this.Data.ItemNames(), this.Data);
                    Data.replace("Player",new CompoundTag("Player",Player));
                }
                newData.replace("Data",new CompoundTag("Data",Data));
            }
            final CompoundTag newTopLevelTag = new CompoundTag("", newData);
            final NBTOutputStream output = new NBTOutputStream(new FileOutputStream(Paths.get(p +"/"+FileName+"_Converted/level.dat").toString()));
            output.writeTag(newTopLevelTag);
            output.close();
            System.out.println("Converted the level.dat file");
        }
        catch (final ClassCastException | NullPointerException ex) {
            throw new IOException("Error during level.dat fixing");
        }
    }

}

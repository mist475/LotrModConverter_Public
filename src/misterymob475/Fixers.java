package misterymob475;

//import org.jnbt.*;

import com.google.gson.internal.LinkedTreeMap;
import de.piegames.nbt.*;
import de.piegames.nbt.regionfile.Chunk;

import java.io.IOException;
import java.util.*;

//all static functions
public class Fixers {
    /**
     * Fixes entities
     *
     * @param Entity    Map with key String and value Tag (.getValue() of CompoundTag)
     * @param Data      instance of {@link Data}
     * @param Important Bool stating if mob should be removed if in Utumno
     * @return Fixed Map
     * @throws IOException if something fails
     */
    @SuppressWarnings("unchecked")
    public static CompoundMap EntityFixer(CompoundMap Entity, Data Data, StringCache stringCache, Boolean Important) throws IOException {
        //Determines the actual mob
        if (Entity.containsKey("id")) {
            if (Data.Entities.containsKey((String) (Entity.get("id").getValue()))) {
                if (!Data.Entities.get((String) (Entity.get("id").getValue())).equals("")) {
                    //code for split types here (horses mainly, I'm not gonna bother with zombie villagers)
                    if (Data.Entities.get((String) (Entity.get("id").getValue())).equals("minecraft:horse")) {
                        if (Entity.get("Type").getValue().equals((byte) 1)) {
                            Entity.replace("id", new StringTag("id", "minecraft:donkey"));
                            Entity.remove("Variant");
                        } else if (Entity.get("Type").getValue().equals((byte) 2)) {
                            Entity.replace("id", new StringTag("id", "minecraft:mule"));
                            Entity.remove("Variant");
                        } else if (Entity.get("Type").getValue().equals((byte) 3)) {
                            Entity.replace("id", new StringTag("id", "minecraft:zombie_horse"));
                            Entity.remove("Variant");
                        } else if (Entity.get("Type").getValue().equals((byte) 1)) {
                            Entity.replace("id", new StringTag("id", "minecraft:skeleton_horse"));
                            Entity.remove("Variant");
                        } else Entity.replace("id", new StringTag("id", "minecraft:horse"));
                    }

                    //I think these are actually bypassed but oh well
                    //code for turning camels into donkeys (to keep the storage)
                    else if (Entity.get("id").getValue().equals("lotr.Camel")) {
                        Entity.remove("Type");
                        Entity.replace("id", new StringTag("id", "minecraft:donkey"));
                    } else if (Entity.get("id").getValue().equals("lotr.Horse")) {
                        //this is temporary, when blocks work I'm gonna finish this function, there's still lotr-related stuff missing
                        Entity.remove("Type");
                        Entity.replace("id", new StringTag("id", "minecraft:donkey"));
                    } else {
                        Entity.replace("id", new StringTag("id", Data.Entities.get((String) (Entity.get("id").getValue()))));
                    }
                    Entity.remove("Type");
                } else
                    stringCache.PrintLine("No mapping found for Entity: " + Entity.get("id").getValue() + " - It probably hasn't been ported yet", false);
            } else {
                stringCache.PrintLine("No mapping found for Entity: " + Entity.get("id").getValue(), false);
                return null;
            }
        } else return null;
        //if Important, entity will always be saved, otherwise entity will only be saved if it maxes sense (Utumno mobs will get deleted)
        boolean inUtumno = false;


        if (Entity.containsKey("Dimension")) {
            Integer Dimension = ((IntTag) Entity.get("Dimension")).getValue();
            String newDimension;
            if (Dimension == 0) newDimension = "minecraft:overworld";
            else if (Dimension == 1) newDimension = "Minecraft:the_nether";
            else if (Dimension == -1) newDimension = "Minecraft:the_end";
            else if (Dimension == 100) newDimension = "lotr:middle_earth";
            else if (Dimension == 101) {
                //should it be saved?
                if (Important) {
                    newDimension = "lotr:middle_earth"; //utumno doesn't exist yet
                    inUtumno = true;
                } else return null;
            } else newDimension = "minecraft:overworld";
            Entity.replace("Dimension", new StringTag("Dimension", newDimension));
        }
        if (inUtumno) {
            //sets the player coordinates at the coordinates of the pit if they're currently in Utumno (roughly, they'll be moved in renewed I've heard)
            //ListTag Pos1 = (ListTag) newData.get("Pos");
            ArrayList<DoubleTag> Pos = new ArrayList<DoubleTag>(1) {
            };
            Pos.add(new DoubleTag("", 46158.0));
            Pos.add(new DoubleTag("", 80.0));
            Pos.add(new DoubleTag("", -40274.0));
            Entity.replace("Pos", new ListTag<>("Pos", TagType.TAG_DOUBLE, Pos));

        }

        if (Entity.containsKey("SaddleItem")) {
            Entity.replace("SaddleItem", new CompoundTag("SaddleItem", Util.CreateCompoundMapWithContents(new ByteTag("Count", (byte) 1), new StringTag("id", "minecraft:saddle"))));
        }

        // I've had enough of this for know
        /*
        Attributes fixer, I can leave unknown tags here as they will get deleted otherwise (new in 1.16)
        Zombie reinforcement caller charge
        Random zombie-spawn bonus
        Leader zombie bonus
        attack damage
        speed
        potion.moveSpeed
        potion.moveSlowdown
        potion.damageBoost
        potion.weakness
        sprinting speed boost
        fleeing speed bonus
        attacking speed boost (pigmem/endermen)
        drinking speed penalty
        baby speed boost
        Tool modifier
        Weapon modifier
        potion.healthBoost
         */
        if (Entity.containsKey("Attributes")) {
            Optional<ListTag<?>> O_Attributes = Entity.get("Attributes").getAsListTag();
            List<CompoundTag> Attributes_new = new ArrayList<>();
            if (O_Attributes.isPresent()) {
                List<CompoundTag> Attributes_old = (List<CompoundTag>) O_Attributes.get().getValue();
                for (CompoundTag t : Attributes_old) {
                    switch (((StringTag) t.getValue().get("Name")).getValue()) {

                        case "generic.attackDamage":
                            Attributes_new.add(new CompoundTag("", Util.CreateCompoundMapWithContents(modifierFixer(((ListTag<CompoundTag>) t.getValue().get("Modifiers"))), new StringTag("Name", "generic.attack_damage"))));
                            break;

                        //modifiers present here
                        case "zombie.spawnReinforcements":
                            CompoundMap spawnReinf = new CompoundMap(t.getValue());
                            spawnReinf.replace("Name", new StringTag("Name", "zombie.spawn_reinforcements"));
                            Attributes_new.add(new CompoundTag("", spawnReinf));
                            break;

                        case "generic.movementSpeed":
                            Attributes_new.add(new CompoundTag("", Util.CreateCompoundMapWithContents(t.getValue().get("Base"), new StringTag("Name", "minecraft:generic.movement_speed"))));
                            break;

                        case "generic.followRange":
                            CompoundMap followRange = Util.CreateCompoundMapWithContents(t.getValue().get("Base"), new StringTag("Name", "minecraft:generic.follow_range"));
                            //yet to be tested
                            Optional<ListTag<?>> Modifiers = t.getValue().get("Modifiers").getAsListTag();
                            Modifiers.ifPresent(listTag -> followRange.put(modifierFixer((ListTag<CompoundTag>) listTag)));

                            Attributes_new.add(new CompoundTag("", followRange));
                            break;

                        case "generic.maxHealth":
                            Attributes_new.add(new CompoundTag("", Util.CreateCompoundMapWithContents(t.getValue().get("Base"), new StringTag("Name", "minecraft:generic.max_health"))));
                            break;
                        case "generic.knockbackResistance":
                            Attributes_new.add(new CompoundTag("", Util.CreateCompoundMapWithContents(t.getValue().get("Base"), new StringTag("Name", "generic.knockback_resistance"))));
                            break;
                        case "horse.jumpStrength":
                            Attributes_new.add(new CompoundTag("", Util.CreateCompoundMapWithContents(t.getValue().get("Base"), new StringTag("Name", "horse.jump_strength"))));
                            break;
                        default:
                            //this is possible because unknown tags will get discarded by the game engine
                            Attributes_new.add(t);
                            break;
                    }
                }
            }
            Entity.replace("Attributes", (new ListTag<>("Attributes", TagType.TAG_COMPOUND, Attributes_new)));
        }


        if (Entity.containsKey("Equipment")) {
            Entity.replace("Equipment", new ListTag<>("Equipment", TagType.TAG_COMPOUND, RecurItemFixer((((ListTag<CompoundTag>) Entity.get("Equipment")).getValue()), (double) 0, "Exception during Entity Equipment Fix", stringCache, Data)));
        }
        //The sole reason I implemented this before I started working on fixing the world
        if (Entity.containsKey("Items")) {
            Entity.replace("Items", new ListTag<>("Items", TagType.TAG_COMPOUND, RecurItemFixer((((ListTag<CompoundTag>) Entity.get("Items")).getValue()), (double) 0, "Exception during Entity Inventory Fix", stringCache, Data)));
        }

        Entity.put("LeftHanded", new ByteTag("LeftHanded", (byte) 0));
        if (Entity.containsKey("OwnerUUID")) {
            Entity.put("Owner", UUIDFixer((StringTag) Entity.get("OwnerUUID"), "Owner"));
        }


/*
        Not needed anymore, I added these to debug inventories nog getting saved properly.
        However, that was because I called recurItemFixer the wrong way again
        Entity.put("CanUpdate",new ByteTag("CanUpdate",(byte)1));
        Entity.put("FallFlying",new ByteTag("FallFlying",(byte)0));
        Entity.put("ForcedAge",new IntTag("ForcedAge",0));
        Entity.put("HurtByTimestamp",new IntTag("HurtByTimestamp",0));
 */


        /*
        ForgeData has to do with the lotr mod, it's not present in renewed, so I simply removed it for now
        DropChances will regenerate, removed as I'm not sure if there's a format change or not
        AttackTime is not present in renewed (unless I've missed it)
        BelongsNPC is not present in renewed (yet), normally used on mounts
        HasReproduced, not encountered this yet in renewed, will have to test with some cows
        HealF, not sure what this does, but I haven't seen it in renewed
        Leashed, not yet encountered in renewed, should test
        Mountable, idem ditto
        OwnerUUID, value just used for the "Owner" tag, so it can be safely removed
        TicksSinceFeed, should test
        Type, should be implemented eventually, just got lazy when I did this
        Variant, idem ditto
        */
        Util.CMRemoveVarArgs(Entity, "ForgeData", "DropChances", "AttackTime", "BelongsNPC", "HasReproduced", "HealF", "Leashed", "Mountable", "OwnerUUID", "TicksSinceFeed", "Type", "Variant");

        if (Entity.containsKey("UUIDLeast")) {
            Entity.put("UUID", UUIDFixer((LongTag) Entity.get("UUIDMost"), (LongTag) Entity.get("UUIDLeast")));
            Util.CMRemoveVarArgs(Entity, "UUIDLeast", "UUIDMost");
        }
        return Entity;
    }

    public static CompoundMap RiderEntityFixer(CompoundMap Entity, StringCache stringCache, Data Data) throws IOException {
        CompoundMap RootVehicle = new CompoundMap();
        if (Entity.containsKey("UUIDLeast")) {
            RootVehicle.put("Attach", UUIDFixer((LongTag) Entity.get("UUIDMost"), (LongTag) Entity.get("UUIDLeast"), "Attach"));
        }

        //
        CompoundMap Entity_map = EntityFixer(Entity, Data, stringCache, true);
        if (Entity_map != null) {
            RootVehicle.put("Entity", new CompoundTag("Entity", Entity_map));
            return RootVehicle;
        } else return null;
    }

    public static ListTag<CompoundTag> modifierFixer(ListTag<CompoundTag> t) {
        List<CompoundTag> list = t.getValue();
        List<CompoundTag> newList = new ArrayList<>();
        for (CompoundTag c : list) {
            CompoundMap Modifier = new CompoundMap(c.getValue());
            if (Modifier.containsKey("UUIDLeast")) {
                Modifier.put("UUID", UUIDFixer((LongTag) Modifier.get("UUIDMost"), (LongTag) Modifier.get("UUIDLeast"), "UUID"));
                Modifier.remove("UUIDLeast");
                Modifier.remove("UUIDMost");
            }
            newList.add(new CompoundTag("", Modifier));
        }
        return new ListTag<>("Modifiers", TagType.TAG_COMPOUND, newList);
    }

    /**
     * Fixes the player inventory
     *
     * @param newData {@link Map} with key {@link String} and value {@link Tag} containing the to be fixed data
     * @throws IOException if something fails
     */
    @SuppressWarnings("unchecked")
    public static void playerFixer(CompoundMap newData, StringCache stringCache, Data Data) throws IOException {
        boolean inUtumno = false;
        //not needed in renewed
        newData.remove("ForgeData");
        //changed too much to bother with, especially as the game will recreate the property
        newData.remove("Attributes");

        if (newData.containsKey("Riding")) {
            //call to entity fixer, this means the player is riding on a mount (fixer will temporarily replace said mount with a donkey)
            CompoundMap Riding = new CompoundMap(((CompoundTag) newData.get("Riding")).getValue());
            Riding = Fixers.RiderEntityFixer(Riding, stringCache, Data);
            if (Riding != null) {
                CompoundTag RootVehicle = new CompoundTag("RootVehicle", Riding);
                newData.replace("Riding", RootVehicle);
            } else newData.remove("Riding");
        }
        if (Data.Settings.containsKey("Creative Mode spawn")) {
            if ((Boolean) Data.Settings.get("Creative Mode spawn")) {
                newData.replace("playerGameType", new IntTag("playerGameType", 1));
            }
        }

        if (newData.containsKey("EnderItems")) {
            newData.replace("EnderItems", new ListTag<>("EnderItems", TagType.TAG_COMPOUND, RecurItemFixer((((ListTag<CompoundTag>) newData.get("EnderItems")).getValue()), (double) 0, "Exception during Ender chest conversion", stringCache, Data)));
        }
        if (newData.containsKey("Inventory")) {
            newData.replace("Inventory", new ListTag<>("Inventory", TagType.TAG_COMPOUND, RecurItemFixer((((ListTag<CompoundTag>) newData.get("Inventory")).getValue()), (double) 0, "Exception during inventory conversion", stringCache, Data)));
        }

        newData.remove("Attack Time");
        if (!newData.containsKey("DataVersion")) {
            newData.put("DataVersion", new IntTag("DataVersion", 2586));
        }

        if (newData.containsKey("Dimension")) {
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
            } else newDimension = "minecraft:overworld";
            newData.replace("Dimension", new StringTag("Dimension", newDimension));
        }
        if (inUtumno) {
            //sets the player coordinates at the coordinates of the pit if they're currently in Utumno (roughly, they'll be moved in renewed I've heard)
            //ListTag Pos1 = (ListTag) newData.get("Pos");
            ArrayList<DoubleTag> Pos = new ArrayList<DoubleTag>(1) {
            };
            Pos.add(new DoubleTag("", 46158.0));
            Pos.add(new DoubleTag("", 80.0));
            Pos.add(new DoubleTag("", -40274.0));
            newData.replace("Pos", new ListTag<>("Pos", TagType.TAG_DOUBLE, Pos));

        }
        newData.remove("HealF");
        newData.remove("Sleeping");
        if (newData.containsKey("UUIDLeast")) {
            newData.put("UUID", UUIDFixer((LongTag) newData.get("UUIDMost"), (LongTag) newData.get("UUIDLeast")));
            newData.remove("UUIDLeast");
            newData.remove("UUIDMost");
        }

    }

    /**
     * Fixes the display {@link CompoundTag} with the new formatting
     *
     * @param display the display {@link CompoundTag} used for items
     * @return the display {@link CompoundTag}, but with fixed formatting to prevent custom names getting cut off
     */
    public static CompoundTag nameFixer(CompoundTag display, Data Data) {
        CompoundMap display_map = new CompoundMap(display.getValue());
        if (display_map.containsKey("Name")) {
            String name = (String) display_map.get("Name").getValue();
            String colour = "";
            if (name.contains("§")) {
                //Fixes coloured items, might have to fix 'Lore' items too. Not sure how those are saved yet
                if (Data.Colours.containsKey(name.substring(0, 2))) {
                    colour = "," + '"' + "color" + '"' + ':' + '"' + Data.Colours.get(name.substring(0, 2)) + '"';
                }
                name = name.substring(2);
            }
            if (display_map.containsKey("Name")) {
                display_map.replace("Name", new StringTag("Name", ("{" + '"' + "text" + '"' + ':' + '"' + name + '"' + colour + '}')));
            }
        }
        return new CompoundTag("display", display_map);
    }

    /**
     * Recursively runs through the provided inventory (recursive because of shulkerboxes/pouches/crackers)
     *
     * @param l                {@link List} of type {@link Tag} of the given inventory
     * @param depth            Maximum recursive depth
     * @param exceptionMessage String printed when exception is thrown
     * @return {@link List} of type {@link Tag} of the modified inventory
     * @throws IOException if something fails
     */
    @SuppressWarnings("unchecked")
    public static List<CompoundTag> RecurItemFixer(List<CompoundTag> l, Double depth, String exceptionMessage, StringCache stringCache, Data Data) throws IOException {
        try {
            List<CompoundTag> builder = new ArrayList<>();

            if (depth++ < (Double) Data.Settings.get("Recursion Depth")) {
                for (CompoundTag t : l) {
                    if (!(t.getValue()).isEmpty()) {
                        ShortTag id = (ShortTag) t.getValue().get("id");
                        boolean save = true;
                        //use this map instead of t and replace t with it as t is not modifiable, this map is though
                        CompoundMap tMap = new CompoundMap(t.getValue());
                        //statement for pouches/cracker
                        Integer compare1 = ((int) id.getValue());
                        if (Data.LegacyIds.containsKey(compare1)) {
                            if (Data.ItemNames.containsKey(Data.LegacyIds.get(compare1))) {
                                List<String> item = Data.ItemNames.get(Data.LegacyIds.get(compare1));
                                //recursive call 1 (Pouches)
                                if (item.get(0).equals("minecraft:shulker_box")) {
                                    if (tMap.containsKey("tag")) {
                                        CompoundMap filler = new CompoundMap(((CompoundTag) tMap.get("tag")).getValue());
                                        //nameFixer
                                        if (filler.containsKey("display")) {
                                            filler.replace("display", nameFixer((CompoundTag) filler.get("display"), Data));
                                        }
                                        if (filler.containsKey("LOTRPouchData")) {
                                            CompoundMap LOTRPouchData = new CompoundMap(((CompoundTag) filler.get("LOTRPouchData")).getValue());
                                            ListTag<CompoundTag> Items_tag = (ListTag<CompoundTag>) LOTRPouchData.get("Items");
                                            //
                                            List<CompoundTag> Items = Items_tag.getValue();
                                            Items = RecurItemFixer(Items, depth, exceptionMessage, stringCache, Data);
                                            //
                                            LOTRPouchData.replace("Items", new ListTag<>("Items", TagType.TAG_COMPOUND, Items));
                                            CompoundTag BlockEntityTag = new CompoundTag("BlockEntityTag", LOTRPouchData);
                                            filler.replace("LOTRPouchData", BlockEntityTag);
                                            tMap.replace("tag", new CompoundTag("tag", filler));
                                        }
                                    }
                                    tMap.remove("Damage");
                                    tMap.replace("id", new StringTag("id", "minecraft:shulker_box"));
                                    builder.add(new CompoundTag("", tMap));

                                }

                                //recursive call 2 (Barrels/Kegs)
                                else if (item.get(0).equals("lotr:keg")) {
                                    CompoundMap filler = new CompoundMap();
                                    if (tMap.containsKey("tag")) {
                                        filler = new CompoundMap(((CompoundTag) tMap.get("tag")).getValue());
                                        //nameFixer
                                        if (filler.containsKey("display")) {
                                            filler.replace("display", nameFixer((CompoundTag) filler.get("display"), Data));
                                        }
                                        CompoundMap KegDroppableData_Map = new CompoundMap();
                                        if (filler.containsKey("LOTRBarrelData")) {
                                            CompoundMap LOTRBarrelData = new CompoundMap(((CompoundTag) filler.get("LOTRBarrelData")).getValue());
                                            Optional<ListTag<?>> O_Items = LOTRBarrelData.get("Items").getAsListTag();
                                            if (O_Items.isPresent()) {
                                                //
                                                List<CompoundTag> Items = ((ListTag<CompoundTag>) O_Items.get()).getValue();
                                                Items = RecurItemFixer(Items, depth, exceptionMessage, stringCache, Data);
                                                //
                                                LOTRBarrelData.replace("Items", new ListTag<>("Items", TagType.TAG_COMPOUND, Items));
                                            }


                                            Optional<IntTag> O_BrewingTime = LOTRBarrelData.get("BrewingTime").getAsIntTag();
                                            O_BrewingTime.ifPresent(intTag -> LOTRBarrelData.put("BrewingTimeTotal", new IntTag("BrewingTimeTotal", (intTag).getValue())));

                                            Optional<ByteTag> O_BarrelMode = LOTRBarrelData.get("BarrelMode").getAsByteTag();
                                            O_BarrelMode.ifPresent(byteTag -> LOTRBarrelData.replace("BarrelMode", new ByteTag("KegMode", ((byteTag)).getValue())));
                                            CompoundTag KegDroppableData = new CompoundTag("KegDroppableData", LOTRBarrelData);

                                            KegDroppableData_Map.put("KegDroppableData", KegDroppableData);
                                            CompoundTag BlockEntityTag = new CompoundTag("BlockEntityTag", KegDroppableData_Map);
                                            filler.replace("LOTRBarrelData", BlockEntityTag);
                                        }
                                    }
                                    tMap.remove("Damage");
                                    tMap.replace("id", new StringTag("id", "lotr:keg"));
                                    tMap.replace("tag", new CompoundTag("tag", filler));
                                    builder.add(new CompoundTag("", tMap));
                                }


                                //Player head fixer (Apparently the game fixes this one automatically, except for custom names. So I added the full thing except the killed by message as I don't know how that is formatted)
                                else if (item.get(0).equals("minecraft:skeleton_skull")) {
                                    CompoundMap filler = new CompoundMap();
                                    if (tMap.containsKey("tag")) {
                                        filler = new CompoundMap(((CompoundTag) tMap.get("tag")).getValue());
                                        if (filler.containsKey("display")) {
                                            filler.replace("display", nameFixer((CompoundTag) filler.get("display"), Data));
                                        }
                                        if (filler.containsKey("SkullOwner")) {
                                            String owner = (String) filler.get("SkullOwner").getValue();
                                            CompoundMap SkullOwner = new CompoundMap();
                                            SkullOwner.put("Id", new StringTag("Name", owner));
                                            filler.replace("SkullOwner", new CompoundTag("SkullOwner", SkullOwner));
                                            //Should check if this works
                                            //filler.replace("SkullOwner", new CompoundTag("SkullOwner", Util.CreateCompoundMapWithContents(new StringTag("Id", owner))));
                                        }
                                    }
                                    tMap.replace("id", new StringTag("id", item.get((Short) tMap.get("Damage").getValue())));
                                    tMap.remove("Damage");
                                    tMap.replace("tag", new CompoundTag("tag", filler));
                                    builder.add(new CompoundTag("", tMap));
                                }

                                //recursive call 3? (Crackers)

                                else if (item.size() <= 1) {

                                    //code for single id values (mostly items, stairs) here
                                    //simply carries over all the tags, except the id, which gets modified to the new one. moves the damage tag to its new location and changes it to an IntTag(was ShortTag before)
                                    if (!Objects.equals(item.get(0), "")) {
                                        boolean drink = new ArrayList<>(Arrays.asList("lotr:ale", "lotr:apple_juice", "lotr:athelas_brew", "lotr:cactus_liqueur", "lotr:carrot_wine", "lotr:cherry_liqueur", "lotr:cider", "lotr:chocolate_drink", "lotr:dwarven_ale", "lotr:dwarven_tonic", "lotr:maple_beer", "lotr:mead", "lotr:melon_liqueur", "lotr:milk_drink", "lotr:miruvor", "lotr:morgul_draught", "lotr:perry", "lotr:rum", "lotr:soured_milk", "lotr:sweet_berry_juice", "lotr:vodka", "lotr:water_drink")).contains(item.get(0));

                                        if (tMap.containsKey("tag")) {
                                            CompoundMap filler = new CompoundMap(((CompoundTag) tMap.get("tag")).getValue());
                                            //itemFixer
                                            if (filler.containsKey("display")) {
                                                filler.replace("display", nameFixer((CompoundTag) filler.get("display"), Data));
                                            }
                                            //pipe fixer
                                            if (filler.containsKey("SmokeColour")) {
                                                String color = (new ArrayList<>(Arrays.asList("white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black", "magic"))).get((Integer) filler.get("SmokeColour").getValue());
                                                CompoundMap pipeMap = Util.CreateCompoundMapWithContents(new StringTag("color", color));
                                                if (color.equals("magic"))
                                                    pipeMap.put("magic", new ByteTag("magic", (byte) 1));
                                                filler.replace("SmokeColour", new CompoundTag("pipe", pipeMap));
                                            }
                                            if (drink) {
                                                CompoundMap vesselMap = new CompoundMap();
                                                if (tMap.containsKey("Damage")) {
                                                    Short Damage = (Short) tMap.get("Damage").getValue();
                                                    //Code for determining the strength of the drink
                                                    if (Damage.toString().endsWith("0"))
                                                        vesselMap.put("potency", new StringTag("potency", "weak"));
                                                    else if (Damage.toString().endsWith("1"))
                                                        vesselMap.put("potency", new StringTag("potency", "light"));
                                                    else if (Damage.toString().endsWith("2"))
                                                        vesselMap.put("potency", new StringTag("potency", "moderate"));
                                                    else if (Damage.toString().endsWith("3"))
                                                        vesselMap.put("potency", new StringTag("potency", "string"));
                                                    else if (Damage.toString().endsWith("4"))
                                                        vesselMap.put("potency", new StringTag("potency", "potent"));
                                                    //Code for determining the vessel (wooden mug, goblet etc.)
                                                    if (Damage < 100)
                                                        vesselMap.put("type", new StringTag("type", "wooden_mug"));
                                                    else if (Damage < 200)
                                                        vesselMap.put("type", new StringTag("type", "ceramic_mug"));
                                                    else if (Damage < 300)
                                                        vesselMap.put("type", new StringTag("type", "golden_goblet"));
                                                    else if (Damage < 400)
                                                        vesselMap.put("type", new StringTag("type", "silver_goblet"));
                                                    else if (Damage < 500)
                                                        vesselMap.put("type", new StringTag("type", "copper_goblet"));
                                                    else if (Damage < 600)
                                                        vesselMap.put("type", new StringTag("type", "wooden_cup"));
                                                    else if (Damage < 700)
                                                        vesselMap.put("type", new StringTag("type", "wooden_mug")); //skull cups not in yet
                                                    else if (Damage < 800)
                                                        vesselMap.put("type", new StringTag("type", "bottle")); //wine glasses not in yet
                                                    else if (Damage < 900)
                                                        vesselMap.put("type", new StringTag("type", "bottle"));
                                                    else if (Damage < 1000)
                                                        vesselMap.put("type", new StringTag("type", "waterskin"));
                                                    else if (Damage < 1100)
                                                        vesselMap.put("type", new StringTag("type", "ale_horn"));
                                                    else if (Damage < 1200)
                                                        vesselMap.put("type", new StringTag("type", "golden_ale_horn"));
                                                }
                                                CompoundTag vessel = new CompoundTag("vessel", vesselMap);
                                                filler.put("vessel", vessel);
                                            }
                                            //potion fixer
                                            else if (item.get(0).equals("minecraft:potion")) {
                                                if (Data.Potions.containsKey(tMap.get("Damage").getValue().toString())) {
                                                    filler.put("Potion", new StringTag("Potion", (String) (Data.Potions.get(tMap.get("Damage").getValue().toString())).get("Name")));
                                                }
                                            }
                                            //Book fixer
                                            else if (filler.containsKey("pages")) {
                                                if (filler.containsKey("author") || filler.containsKey("title")) {
                                                    if (filler.containsKey("author")) {
                                                        if (Data.AuthorBlacklist.contains((String) filler.get("author").getValue())) {
                                                            save = false;
                                                        }
                                                    }
                                                    if (filler.containsKey("title")) {
                                                        if (Data.TitleBlacklist.contains((String) filler.get("title").getValue())) {
                                                            save = false;
                                                        }
                                                    }
                                                }
                                                //without this if book & quills get messed up
                                                if (Objects.equals(item.get(0), "minecraft:written_book")) {
                                                    List<StringTag> pages = new ArrayList<>();
                                                    for (StringTag st : (List<StringTag>) filler.get("pages").getValue()) {
                                                        pages.add(new StringTag("", ("{" + '"' + "text" + '"' + ':' + '"' + st.getValue() + '"' + '}')));
                                                    }
                                                    filler.replace("pages", new ListTag<>("pages", TagType.TAG_STRING, pages));
                                                }
                                            }
                                            //Enchantments fixer
                                            else if (filler.containsKey("ench") || filler.containsKey("StoredEnchantments")) {
                                                List<CompoundTag> ench_filler = new ArrayList<>();
                                                if (filler.containsKey("ench")) {
                                                    Optional<ListTag<?>> O_ench = filler.get("ench").getAsListTag();
                                                    if (O_ench.isPresent()) {
                                                        for (Tag<?> ench_t : O_ench.get().getValue()) {
                                                            CompoundMap ench = new CompoundMap((((CompoundTag) ench_t).getValue()));
                                                            ench.replace("id", new StringTag("id", Data.Enchantments.get((((ShortTag) ench.get("id")).getValue().toString()))));
                                                            ench_filler.add(new CompoundTag("", ench));
                                                        }
                                                        filler.replace("ench", new ListTag<>("Enchantments", TagType.TAG_COMPOUND, ench_filler));
                                                        filler.put("Damage", (new IntTag("Damage", (((ShortTag) tMap.get("Damage")).getValue()))));
                                                    }
                                                    //enchanted items
                                                    //ListTag<CompoundTag> ench_t_2 = ((ListTag<CompoundTag>) filler.get("ench"));
                                                    //(Tag ench_t : (((ListTag<CompoundTag>) filler.get("ench")).getValue()))
                                                    //(Tag ench_t : filler.get("ench").getAsListTag().get().getValue())

                                                } else {
                                                    //enchanted books
                                                    for (CompoundTag ench_t : ((ListTag<CompoundTag>) filler.get("StoredEnchantments")).getValue()) {
                                                        CompoundMap ench = new CompoundMap((ench_t.getValue()));
                                                        ench.replace("id", new StringTag("id", Data.Enchantments.get((((ShortTag) ench.get("id")).getValue().toString()))));
                                                        ench_filler.add(new CompoundTag("", ench));
                                                    }
                                                    filler.replace("StoredEnchantments", new ListTag<>("StoredEnchantments", TagType.TAG_COMPOUND, ench_filler));
                                                }
                                                filler.remove("LOTRRandomEnch");
                                                filler.remove("LOTRRepairCost");
                                            }
                                            //map fixer (very simple thankfully)
                                            else if (item.get(0).equals("minecraft:filled_map")) {
                                                if (tMap.containsKey("Damage")) {
                                                    filler.put("map", new IntTag("map", (int) ((Short) tMap.get("Damage").getValue())));
                                                }
                                            } else if (tMap.containsKey("Damage")) {
                                                //check to prevent non-weapons to get a nbt-tag, making them unable to stack with regular items
                                                if ((((ShortTag) tMap.get("Damage")).getValue()) != 0) {
                                                    filler.put("Damage", (new IntTag("Damage", (((ShortTag) tMap.get("Damage")).getValue()))));
                                                }
                                            }
                                            tMap.replace("tag", new CompoundTag("tag", filler));
                                        } else {
                                            CompoundMap filler = new CompoundMap();
                                            if (drink) {
                                                CompoundMap vesselMap = new CompoundMap();
                                                if (tMap.containsKey("Damage")) {
                                                    //I know, I don't like code repetition either, but I couldn't be bothered to write a function that will only be called twice
                                                    Short Damage = (Short) tMap.get("Damage").getValue();
                                                    //Code for determining the strength of the drink
                                                    if (Damage.toString().endsWith("0"))
                                                        vesselMap.put("potency", new StringTag("potency", "weak"));
                                                    else if (Damage.toString().endsWith("1"))
                                                        vesselMap.put("potency", new StringTag("potency", "light"));
                                                    else if (Damage.toString().endsWith("2"))
                                                        vesselMap.put("potency", new StringTag("potency", "moderate"));
                                                    else if (Damage.toString().endsWith("3"))
                                                        vesselMap.put("potency", new StringTag("potency", "string"));
                                                    else if (Damage.toString().endsWith("4"))
                                                        vesselMap.put("potency", new StringTag("potency", "potent"));
                                                    //Code for determining the vessel (wooden mug, goblet etc.)
                                                    if (Damage < 100)
                                                        vesselMap.put("type", new StringTag("type", "wooden_mug"));
                                                    else if (Damage < 200)
                                                        vesselMap.put("type", new StringTag("type", "ceramic_mug"));
                                                    else if (Damage < 300)
                                                        vesselMap.put("type", new StringTag("type", "golden_goblet"));
                                                    else if (Damage < 400)
                                                        vesselMap.put("type", new StringTag("type", "silver_goblet"));
                                                    else if (Damage < 500)
                                                        vesselMap.put("type", new StringTag("type", "copper_goblet"));
                                                    else if (Damage < 600)
                                                        vesselMap.put("type", new StringTag("type", "wooden_cup"));
                                                    else if (Damage < 700)
                                                        vesselMap.put("type", new StringTag("type", "wooden_mug")); //skull cups not in yet
                                                    else if (Damage < 800)
                                                        vesselMap.put("type", new StringTag("type", "bottle")); //wine glasses not in yet
                                                    else if (Damage < 900)
                                                        vesselMap.put("type", new StringTag("type", "bottle"));
                                                    else if (Damage < 1000)
                                                        vesselMap.put("type", new StringTag("type", "waterskin"));
                                                    else if (Damage < 1100)
                                                        vesselMap.put("type", new StringTag("type", "ale_horn"));
                                                    else if (Damage < 1200)
                                                        vesselMap.put("type", new StringTag("type", "golden_ale_horn"));
                                                }
                                                filler.put("vessel", new CompoundTag("vessel", vesselMap));
                                            }
                                            //potion
                                            else if (item.get(0).equals("minecraft:potion")) {
                                                if (tMap.containsKey("Damage")) {
                                                    if (Data.Potions.containsKey(tMap.get("Damage").getValue().toString())) {
                                                        filler.put("Potion", new StringTag("Potion", (String) (Data.Potions.get(tMap.get("Damage").getValue().toString())).get("Name")));
                                                    }
                                                }
                                            }
                                            //map fixer (very simple thankfully)
                                            else if (item.get(0).equals("minecraft:filled_map")) {
                                                if (tMap.containsKey("Damage")) {
                                                    filler.put("map", new IntTag("map", (int) ((Short) tMap.get("Damage").getValue())));
                                                }
                                            } else {
                                                if ((((ShortTag) tMap.get("Damage")).getValue()) != 0) {
                                                    filler.put("Damage", (new IntTag("Damage", (((ShortTag) tMap.get("Damage")).getValue()))));
                                                }
                                            }
                                            if (!filler.isEmpty()) {
                                                tMap.put("tag", new CompoundTag("tag", filler));
                                            }
                                        }
                                        //sets name to potion or splash potion
                                        if (item.get(0).equals("minecraft:potion")) {
                                            if (Data.Potions.containsKey(tMap.get("Damage").getValue().toString())) {
                                                //Boolean Splash = (boolean) (Data.Potions().get(tMap.get("Damage").getValue().toString())).get("Splash");
                                                if ((Boolean) (Data.Potions.get(tMap.get("Damage").getValue().toString())).get("Splash"))
                                                    tMap.replace("id", new StringTag("id", "minecraft:splash_potion"));
                                                else tMap.replace("id", new StringTag("id", "minecraft:potion"));
                                            } else tMap.replace("id", new StringTag("id", "minecraft:potion"));
                                        } else tMap.replace("id", new StringTag("id", item.get(0)));
                                        tMap.remove("Damage");
                                        if (save) {
                                            builder.add(new CompoundTag("", tMap));
                                        }
                                    }
                                    //vanilla spawn egg handler
                                    else if (Data.LegacyIds.get(compare1).equals("minecraft:spawn_egg")) {
                                        //itemFixer
                                        if (tMap.containsKey("tag")) {
                                            CompoundMap filler = new CompoundMap(((CompoundTag) tMap.get("tag")).getValue());
                                            if (filler.containsKey("display")) {
                                                filler.replace("display", nameFixer((CompoundTag) filler.get("display"), Data));
                                            }
                                            tMap.replace("tag", new CompoundTag("tag", filler));
                                        }
                                        if (Data.Vanilla_mob_ids.containsKey(((Short) tMap.get("Damage").getValue()).toString())) {
                                            tMap.replace("id", new StringTag("id", Data.Vanilla_mob_ids.get(((Short) tMap.get("Damage").getValue()).toString())));
                                            tMap.remove("Damage");
                                            builder.add(new CompoundTag("", tMap));
                                        } else
                                            stringCache.PrintLine("No vanilla spawn Egg found for Damage value : " + tMap.get("Damage").getValue(), false);
                                    }
                                    //lotr spawn egg handler
                                    else if (Data.LegacyIds.get(compare1).equals("lotr:item.spawnEgg")) {
                                        //itemFixer
                                        if (tMap.containsKey("tag")) {
                                            CompoundMap filler = new CompoundMap(((CompoundTag) tMap.get("tag")).getValue());
                                            if (filler.containsKey("display")) {
                                                filler.replace("display", nameFixer((CompoundTag) filler.get("display"), Data));
                                            }
                                            tMap.replace("tag", new CompoundTag("tag", filler));
                                        }
                                        if (Data.Mod_mob_ids.containsKey(((Short) tMap.get("Damage").getValue()).toString())) {
                                            tMap.replace("id", new StringTag("id", Data.Mod_mob_ids.get(((Short) tMap.get("Damage").getValue()).toString())));
                                            tMap.remove("Damage");
                                            builder.add(new CompoundTag("", tMap));
                                        } else
                                            stringCache.PrintLine("No lotr mod spawn Egg found for Damage value : " + tMap.get("Damage").getValue(), false);
                                    } else {
                                        stringCache.PrintLine("No mapping found for legacy id: " + Data.LegacyIds.get(compare1), false);
                                    }
                                } else {
                                    //code for blocks/some items here
                                    Short Damage = ((ShortTag) t.getValue().get("Damage")).getValue();
                                    //Check if block is actually in the list and not just a placeholder
                                    if (!Data.ItemNames.get(Data.LegacyIds.get(compare1)).get(Damage).equals("")) {
                                        if (tMap.containsKey("tag")) {
                                            //itemFixer
                                            CompoundMap filler = new CompoundMap(((CompoundTag) tMap.get("tag")).getValue());
                                            if (filler.containsKey("display") && !filler.containsKey("SkullOwner")) {
                                                filler.replace("display", nameFixer((CompoundTag) filler.get("display"), Data));
                                            }
                                            tMap.replace("tag", new CompoundTag("tag", filler));
                                        }
                                        tMap.remove("Damage");
                                        tMap.replace("id", new StringTag("id", item.get(Damage)));
                                        builder.add(new CompoundTag("", tMap));
                                    } else
                                        stringCache.PrintLine("No mapping found for " + Data.LegacyIds.get(compare1) + ":" + Damage, false);
                                }
                            } else {
                                stringCache.PrintLine("No mapping found for id: " + Data.LegacyIds.get(compare1), false);
                            }
                        } else {
                            //this should never happen as I gather these ids dynamically
                            stringCache.PrintLine("No string id found for id: " + compare1, false);
                        }
                    } else {
                        stringCache.PrintLine("Empty tag found, skipping", false);
                    }
                }
            } else {
                //if this actually gets triggered someone has been annoying on purpose
                stringCache.PrintLine("Maximum set recursion depth reached (default = 7, defined in JSON)", false);
            }
            return builder;
        } catch (final ClassCastException | NullPointerException ex) {
            throw new IOException(exceptionMessage);
        }
    }

    /**
     * Function which returns a new IntArrayTag based off the given LongTags and name
     *
     * @param UUIDLeast {@link LongTag}
     * @param UUIDMost  {@link LongTag}
     * @param name      {@link String} name
     * @return {@link IntArrayTag} with given name and param inputs
     */
    public static IntArrayTag UUIDFixer(LongTag UUIDMost, LongTag UUIDLeast, String name) {
        //Creates the UUID in the new format based with name being the name of the intArrayTag
        long v1 = UUIDMost.getValue();
        long v2 = UUIDLeast.getValue();
        return new IntArrayTag(name, new int[]{(int) (v1 >> 32), (int) v1, (int) (v2 >> 32), (int) v2});
    }

    //Overload for when no special name is required (name is "UUID")

    /**
     * Overload for when name is "UUID"
     *
     * @param UUIDLeast {@link LongTag}
     * @param UUIDMost  {@link LongTag}
     * @return {@link IntArrayTag} with name "UUID" and param inputs
     */
    public static IntArrayTag UUIDFixer(LongTag UUIDMost, LongTag UUIDLeast) {
        return UUIDFixer(UUIDMost, UUIDLeast, "UUID");
    }

    /**
     * Overload for StringTags
     *
     * @param UUID_t {@link StringTag}
     * @param name   String
     * @return {@link IntArrayTag} with name as name and param inputs
     */
    public static IntArrayTag UUIDFixer(StringTag UUID_t, String name) {
        if (!UUID_t.getValue().equals("")) {
            UUID uuid = UUID.fromString(UUID_t.getValue());
            return UUIDFixer(new LongTag("", uuid.getMostSignificantBits()), new LongTag("", uuid.getLeastSignificantBits()), name);
        } else
            return new IntArrayTag(name, new int[]{0, 0, 0, 0}); //Not sure if game reads this correctly, otherwise the entire tag might have to be removed instead, this is purely to prevent a crash when the uuid is non-existent
    }

    /**
     * Fixes maps
     *
     * @param map {@link CompoundMap} with map data
     */
    public static void MapFixer(CompoundMap map) {
        //gets the values we want, note, = I'm doing the easy ones first (lists last) I'm keeping the order though as I've read somewhere that that matters
        if (map.containsKey("dimension")) {
            //fixer here int --> string
            Integer Dimension = ((IntTag) map.get("dimension")).getValue();
            String newDimension;
            if (Dimension == 0) newDimension = "minecraft:overworld";
            else if (Dimension == 1) newDimension = "Minecraft:the_nether";
            else if (Dimension == -1) newDimension = "Minecraft:the_end";
            else if (Dimension == 100) newDimension = "lotr:middle_earth";
                //not sure if this is gonna work, we'll see
            else if (Dimension == 101) newDimension = "lotr:utumno";
            else newDimension = "minecraft:overworld";
            map.replace("dimension", new StringTag("dimension", newDimension));
        }
        //hmm?
        map.remove("width");
        map.remove("height");
    }

    /**
     * Fixes the level.dat compoundMap using the existing map and a map from a renewed world
     *
     * @param newData              {@link CompoundMap} of the level.dat file
     * @param data                 {@link Data} instance of Data
     * @param originalTopLevelTag1 {@link CompoundTag} of a renewed level.dat file
     * @throws IOException when something goes wrong
     */
    public static void LevelDatFixer(CompoundMap newData, Data data, StringCache stringCache, CompoundTag originalTopLevelTag1) throws IOException {
        if (newData.containsKey("Data") && (originalTopLevelTag1.getValue()).containsKey("Data")) {
            CompoundMap Data = new CompoundMap(((CompoundTag) newData.get("Data")).getValue());
            CompoundMap Data1 = new CompoundMap(((CompoundTag) (originalTopLevelTag1.getValue()).get("Data")).getValue());


            //GameRules fix (only 9 added in 1.7.10, keeping rest of the selected Renewed World)
            if (Data.containsKey("GameRules") && Data1.containsKey("GameRules")) {
                CompoundTag GameRules1_tag = (CompoundTag) Data1.get("GameRules");
                CompoundMap GameRules = new CompoundMap((((CompoundTag) Data.get("GameRules")).getValue()));
                GameRules.replace("commandBlockOutput", GameRules1_tag.getValue().get("commandBlockOutput"));
                GameRules.replace("doDaylightCycle", GameRules1_tag.getValue().get("doDaylightCycle"));
                GameRules.replace("doFireTick", GameRules1_tag.getValue().get("doFireTick"));
                GameRules.replace("doMobLoot", GameRules1_tag.getValue().get("doMobLoot"));
                GameRules.replace("doMobSpawning", GameRules1_tag.getValue().get("doMobSpawning"));
                GameRules.replace("doTileDrops", GameRules1_tag.getValue().get("doTileDrops"));
                GameRules.replace("keepInventory", GameRules1_tag.getValue().get("keepInventory"));
                GameRules.replace("mobGriefing", GameRules1_tag.getValue().get("mobGriefing"));
                GameRules.replace("naturalRegeneration", GameRules1_tag.getValue().get("naturalRegeneration"));
                newData.replace("GameRules", new CompoundTag("GameRules", GameRules));
            }

            if (Data.containsKey("WorldGenSettings")) {
                CompoundMap WorldGenSettings = new CompoundMap((((CompoundTag) Data.get("WorldGenSettings")).getValue()));
                if (Data1.containsKey("MapFeatures")) {
                    WorldGenSettings.replace("generate_features", Data1.get("MapFeatures"));
                }
                if (Data1.containsKey("RandomSeed")) {
                    WorldGenSettings.replace("seed", new LongTag("seed", (Long) Data1.get("RandomSeed").getValue()));
                }

                //dimensions
                if (WorldGenSettings.containsKey("dimensions")) {
                    CompoundTag dimensions = (CompoundTag) WorldGenSettings.get("dimensions");
                    CompoundMap dimensions_map = new CompoundMap(dimensions.getValue());

                    //should have made this a loop in hindsight, oh well...

                    CompoundMap meDimension = new CompoundMap(((CompoundTag) dimensions.getValue().get("lotr:middle_earth")).getValue());
                    CompoundMap overworldDimension = new CompoundMap(((CompoundTag) dimensions.getValue().get("minecraft:overworld")).getValue());
                    CompoundMap endDimension = new CompoundMap(((CompoundTag) dimensions.getValue().get("minecraft:the_end")).getValue());
                    CompoundMap netherDimension = new CompoundMap(((CompoundTag) dimensions.getValue().get("minecraft:the_nether")).getValue());

                    CompoundTag Generator1 = (CompoundTag) ((CompoundTag) dimensions.getValue().get("lotr:middle_earth")).getValue().get("generator");
                    CompoundTag Generator2 = (CompoundTag) ((CompoundTag) dimensions.getValue().get("minecraft:overworld")).getValue().get("generator");
                    CompoundTag Generator3 = (CompoundTag) ((CompoundTag) dimensions.getValue().get("minecraft:the_end")).getValue().get("generator");
                    CompoundTag Generator4 = (CompoundTag) ((CompoundTag) dimensions.getValue().get("minecraft:the_nether")).getValue().get("generator");

                    CompoundMap generatormap1 = new CompoundMap(Generator1.getValue());
                    CompoundMap generatormap2 = new CompoundMap(Generator2.getValue());
                    CompoundMap generatormap3 = new CompoundMap(Generator3.getValue());
                    CompoundMap generatormap4 = new CompoundMap(Generator4.getValue());

                    //lotr:middle_earth
                    //generatormap1.replace("seed",Data1.get("RandomSeed"));
                    generatormap1.replace("seed", new LongTag("seed", (Long) Data1.get("RandomSeed").getValue()));
                    CompoundMap biome_source1 = new CompoundMap((CompoundMap) generatormap1.get("biome_source").getValue());
                    biome_source1.replace("seed", new LongTag("seed", (Long) Data1.get("RandomSeed").getValue()));
                    //sets instant_middle_earth right in lotr:middle_earth
                    //meClassic apparently doesn't use this tag, even though you definitely spawn directly into middle-earth
                    //Data1.get("generatorName").getValue().equals("meClassic") ||
                    if (Data1.get("generatorName").getValue().equals("middleEarth")) {
                        generatormap1.replace("instant_middle_earth", new ByteTag("instant_middle_earth", (byte) 1));
                        if (Data1.get("generatorName").getValue().equals("meClassic"))
                            biome_source1.replace("classic_biomes", new ByteTag("classic_biomes", (byte) 1));
                        else biome_source1.replace("classic_biomes", new ByteTag("classic_biomes", (byte) 0));
                    } else generatormap1.replace("instant_middle_earth", new ByteTag("instant_middle_earth", (byte) 0));

                    generatormap1.replace("biome_source", new CompoundTag("biome_source", biome_source1));
                    meDimension.replace("generator", new CompoundTag("generator", generatormap1));
                    dimensions_map.replace("lotr:middle_earth", new CompoundTag("lotr:middle_earth", meDimension));
                    //minecraft:overworld
                    if ((Data1.get("generatorName").getValue().equals("flat"))) {
                        //handles flat-worlds, hardcodes the default values as transcribing them is beyond the scope of the convertor, salt might be the seed and not actually this odd value
                        generatormap2.replace("type", new StringTag("type", "minecraft:flat"));
                        generatormap2.remove("biome_source");
                        generatormap2.remove("seed");
                        generatormap2.remove("settings");
                        CompoundMap settings_map = Util.CreateCompoundMapWithContents(new StringTag("biome", "minecraft:plains"), new ByteTag("features", (byte) 0), new ByteTag("lakes", (byte) 0));

                        CompoundMap stronghold_map = Util.CreateCompoundMapWithContents(new IntTag("count", 128), new IntTag("distance", 32), new IntTag("spread", 3));
                        CompoundMap structures1_map = Util.CreateCompoundMapWithContents(new CompoundTag("stronghold", stronghold_map));

                        //TODO: Fix salt to use seed
                        CompoundMap village_map = Util.CreateCompoundMapWithContents(new IntTag("salt", ((new Random()).nextInt(1000000000))), new IntTag("separation", 8), new IntTag("spacing", 32));

                        structures1_map.put("structures", new CompoundTag("structures", Util.CreateCompoundMapWithContents(new CompoundTag("minecraft:village", village_map))));

                        settings_map.put("structures", new CompoundTag("structures", structures1_map));

                        //Adds the entries for flatworld generation
                        List<CompoundTag> layers_list = new ArrayList<>();
                        layers_list.add(new CompoundTag("", Util.CreateCompoundMapWithContents(new StringTag("block", "minecraft:bedrock"), new IntTag("height", 1))));
                        layers_list.add(new CompoundTag("", Util.CreateCompoundMapWithContents(new StringTag("block", "minecraft:dirt"), new IntTag("height", 2))));
                        layers_list.add(new CompoundTag("", Util.CreateCompoundMapWithContents(new StringTag("block", "minecraft:grass_block"), new IntTag("height", 1))));

                        settings_map.put("layers", new ListTag<>("layers", TagType.TAG_COMPOUND, layers_list));

                        generatormap2.put("settings", new CompoundTag("settings", settings_map));
                    } else {
                        generatormap2.replace("seed", new LongTag("seed", (Long) Data1.get("RandomSeed").getValue()));
                        CompoundMap biome_source2 = new CompoundMap((CompoundMap) generatormap2.get("biome_source").getValue());
                        biome_source2.replace("seed", new LongTag("seed", (Long) Data1.get("RandomSeed").getValue()));
                        generatormap2.replace("biome_source", new CompoundTag("biome_source", biome_source2));
                        if (Data1.get("generatorName").getValue().equals("largeBiomes"))
                            generatormap2.replace("large_biomes", new ByteTag("large_biomes", (byte) 1));
                        else generatormap2.replace("large_biomes", new ByteTag("large_biomes", (byte) 0));
                    }
                    overworldDimension.replace("generator", new CompoundTag("generator", generatormap2));
                    dimensions_map.replace("minecraft:overworld", new CompoundTag("minecraft:overworld", overworldDimension));

                    //minecraft:the_end
                    generatormap3.replace("seed", new LongTag("seed", (Long) Data1.get("RandomSeed").getValue()));
                    CompoundMap biome_source3 = new CompoundMap((CompoundMap) generatormap3.get("biome_source").getValue());
                    biome_source3.replace("seed", new LongTag("seed", (Long) Data1.get("RandomSeed").getValue()));
                    generatormap3.replace("biome_source", new CompoundTag("biome_source", biome_source3));
                    endDimension.replace("generator", new CompoundTag("generator", generatormap3));
                    dimensions_map.replace("minecraft:the_end", new CompoundTag("minecraft:the_end", endDimension));

                    //minecraft:the_nether
                    generatormap4.replace("seed", new LongTag("seed", (Long) Data1.get("RandomSeed").getValue()));
                    CompoundMap biome_source4 = new CompoundMap((CompoundMap) generatormap4.get("biome_source").getValue());
                    biome_source4.replace("seed", new LongTag("seed", (Long) Data1.get("RandomSeed").getValue()));
                    generatormap4.replace("biome_source", new CompoundTag("biome_source", biome_source4));
                    netherDimension.replace("generator", new CompoundTag("generator", generatormap4));
                    dimensions_map.replace("minecraft:the_nether", new CompoundTag("minecraft:the_nether", netherDimension));

                    WorldGenSettings.replace("dimensions", new CompoundTag("dimensions", dimensions_map));
                    Data.replace("WorldGenSettings", new CompoundTag("WorldGenSettings", WorldGenSettings));
                }
            }


            //rest of 'Data' fix
            Data.replace("DayTime", Data1.get("DayTime"));
            Data.replace("GameType", Data1.get("GameType"));
            Data.replace("hardcore", Data1.get("hardcore"));
            Data.replace("initialized", Data1.get("initialized"));
            Data.replace("LastPlayed", Data1.get("LastPlayed"));
            Data.replace("LevelName", Data1.get("LevelName"));
            Data.replace("raining", Data1.get("raining"));
            Data.replace("rainTime", Data1.get("rainTime"));
            Data.replace("SpawnX", Data1.get("SpawnX"));
            Data.replace("SpawnY", Data1.get("SpawnY"));
            Data.replace("SpawnZ", Data1.get("SpawnZ"));
            Data.replace("thundering", Data1.get("thundering"));
            Data.replace("thunderTime", Data1.get("thunderTime"));
            Data.replace("Time", Data1.get("Time"));
            Data.replace("version", Data1.get("version"));
            if (Data.containsKey("Player") && Data1.containsKey("Player")) {
                CompoundTag Player_tag = (CompoundTag) Data1.get("Player");
                CompoundMap Player = new CompoundMap(Player_tag.getValue());
                Fixers.playerFixer(Player, stringCache, data);
                Data.replace("Player", new CompoundTag("Player", Player));
            }
            newData.replace("Data", new CompoundTag("Data", Data));
        }
    }

    /**
     * Fixes the LOTR.Dat file
     *
     * @param originalData {@link CompoundMap} the map of LOTR.dat
     */
    public static void LOTRDatFixer(CompoundMap originalData) {
        //discards: as they aren't in renewed yet or are now datapackable, if something gets ported to renewed in the exact same way as legacy I can simply uncomment these lines
        originalData.remove("TravellingTraders");
        originalData.remove("GreyWanderers");
        originalData.remove("AlignmentZones");
        originalData.remove("ConqRate");
        originalData.remove("DifficultyLock");
        originalData.remove("GollumSpawned");
        originalData.remove("GWSpawnTick");
        originalData.remove("StructuresBanned");

        CompoundTag Dates = new CompoundTag("Dates", Util.CreateCompoundMapWithContents(new IntTag("CurrentDay", ((IntTag) ((CompoundTag) originalData.get("Dates")).getValue().get("ShireDate")).getValue())));
        originalData.replace("Dates", Dates);
        (originalData.get("MadeMiddlePortal").getAsIntTag()).ifPresent(intTag -> originalData.replace("MadeMiddlePortal", new ByteTag("MadeMiddlePortal", (byte) (int) intTag.getValue())));
        //IntTag MadeMiddlePortal = originalData.get("MadeMiddlePortal").getAsIntTag().get();
        (originalData.get("MadePortal").getAsIntTag()).ifPresent(intTag -> originalData.replace("MadePortal", new ByteTag("MadePortal", (byte) (int) intTag.getValue())));
    }

    /**
     * Fixes the lotr playerData files
     *
     * @param originalData {@link CompoundMap} of lotr player data
     * @param Data         instance of {@link Data}
     */
    @SuppressWarnings("unchecked")
    public static void LOTRPlayerDataFixer(CompoundMap originalData, Data Data) {
        //gets the values we want, note, = I'm doing the easy ones first (lists last)
        //originalData.get("something").
        Optional<ListTag<?>> AlignmentMap = originalData.get("AlignmentMap").getAsListTag();
        List<CompoundTag> AlignmentMap_builder = new ArrayList<CompoundTag>(1) {
        };
        if (AlignmentMap.isPresent()) {
            ListTag<CompoundTag> AlignmentMap_old = (ListTag<CompoundTag>) AlignmentMap.get();
            for (CompoundTag tag : AlignmentMap_old.getValue()) {
                StringTag Faction_tag = (StringTag) tag.getValue().get("Faction");
                String Faction = Faction_tag.getValue();
                if (Data.FacNames.containsKey(Faction)) {
                    AlignmentMap_builder.add(new CompoundTag("", Util.CreateCompoundMapWithContents(tag.getValue().get("AlignF"), new StringTag("Faction", Data.FacNames.get(Faction)))));
                }
            }
        }

        //ListTag AlignmentMap = new ListTag("AlignmentMap",CompoundTag.class, AlignmentMap_builder);

        ListTag<CompoundTag> FactionStats_old = (ListTag<CompoundTag>) originalData.get("FactionData");
        List<CompoundTag> FactionStats_builder = new ArrayList<CompoundTag>(1) {
        };
        for (CompoundTag tag : FactionStats_old.getValue()) {
            StringTag Faction_tag_AL = (StringTag) tag.getValue().get("Faction");
            String Faction_AL = Faction_tag_AL.getValue();
            if (Data.FacNames.containsKey(Faction_AL)) {
                final CompoundMap newData_AL = Util.CreateCompoundMapWithContents(tag.getValue().get("ConquestHorn"), tag.getValue().get("EnemyKill"), new StringTag("Faction", Data.FacNames.get(Faction_AL)), tag.getValue().get("Hired"), tag.getValue().get("MiniQuests"), tag.getValue().get("Trades"));

                //Couldn't think of a way to do renaming implicitly
                newData_AL.put("MemberKill", tag.getValue().get("NPCKill"));
                CompoundTag AM_AL_Builder = new CompoundTag("", newData_AL);
                FactionStats_builder.add(AM_AL_Builder);
            }
        }
        //ListTag FactionStats = new ListTag("FactionStats",CompoundTag.class, FactionStats_builder);

        ListTag<CompoundTag> PrevRegionFactions_Old = (ListTag<CompoundTag>) originalData.get("PrevRegionFactions");
        List<CompoundTag> PrevRegionFactions_builder = new ArrayList<CompoundTag>(1) {
        };
        for (CompoundTag tag : PrevRegionFactions_Old.getValue()) {
            StringTag Faction_tag_PRF = (StringTag) tag.getValue().get("Faction");
            String Region_PRF = ((StringTag) tag.getValue().get("Region")).getValue();
            String Faction_PRF = Faction_tag_PRF.getValue();
            if (Data.FacNames.containsKey(Faction_PRF)) {
                final CompoundMap newData_PRF = Util.CreateCompoundMapWithContents(new StringTag("Faction", Data.FacNames.get(Faction_PRF)));
                switch (Region_PRF) {
                    case "west":
                        newData_PRF.put("Region", new StringTag("Region", "lotr:westlands"));
                        break;
                    case "east":
                        newData_PRF.put("Region", new StringTag("Region", "lotr:rhun"));
                        break;
                    case "south":
                        newData_PRF.put("Region", new StringTag("Region", "lotr:harad"));
                        break;
                }
                CompoundTag PRF_AL_Builder = new CompoundTag("", newData_PRF);
                PrevRegionFactions_builder.add(PRF_AL_Builder);
            }
        }
        //ListTag PrevRegionFactions = new ListTag("PrevRegionFactions",CompoundTag.class, PrevRegionFactions_builder);

        //SentMessageTypes

        ListTag<CompoundTag> UnlockedFTRegions_Old = (ListTag<CompoundTag>) originalData.get("UnlockedFTRegions");
        List<StringTag> UnlockedFTRegions_Builder = new ArrayList<StringTag>(0) {
        };
        for (CompoundTag tag : UnlockedFTRegions_Old.getValue()) {
            StringTag RegionName_Tag = (StringTag) tag.getValue().get("Name");
            String RegionName = RegionName_Tag.getValue();
            switch (RegionName) {
                case "GONDOR":

                    UnlockedFTRegions_Builder.add(new StringTag("", "lotr:andrast"));
                    UnlockedFTRegions_Builder.add(new StringTag("", "lotr:anfalas"));
                    UnlockedFTRegions_Builder.add(new StringTag("", "lotr:anorien"));
                    UnlockedFTRegions_Builder.add(new StringTag("", "lotr:western_gondor"));
                    //gondor itself already gets handles on the if below, hence the lack of it here
                    break;
                case "FORODWAITH":
                    UnlockedFTRegions_Builder.add(new StringTag("", "lotr:northlands"));
                    UnlockedFTRegions_Builder.add(new StringTag("", "lotr:forochel"));
                    break;
                case "OCEAN":
                    UnlockedFTRegions_Builder.add(new StringTag("", "lotr:western_isles"));
                    break;
            }
            if (Data.FacNames.containsKey(RegionName)) {
                StringTag Name = new StringTag("", Data.Regions.get(RegionName));
                UnlockedFTRegions_Builder.add(Name);

            }
        }
        //ListTag UnlockedFTRegions = new ListTag("UnlockedFTRegions",StringTag.class, UnlockedFTRegions_Builder);

        //get the old WPUses
        ListTag<CompoundTag> WPUses_old = (ListTag<CompoundTag>) originalData.get("WPUses");
        //create a new empty array put the new WPUses in
        List<CompoundTag> WPUses_builder = new ArrayList<CompoundTag>(1) {
        };
        //loop though the entries in the list
        for (CompoundTag tag : WPUses_old.getValue()) {
            //get the StringTag of the waypoint
            StringTag WPName_tag = (StringTag) tag.getValue().get("WPName");
            //convert to string
            String WPName = WPName_tag.getValue();
            //if the waypoint exists in renewed (not everything has been ported yet)
            if (Data.Waypoints.containsKey(WPName)) {
                //add the CompoundTag to the List
                //CompoundMap Info:
                //Var1: the amount of waypoint usage (cooldown depends on it)
                //Var2: the new name
                WPUses_builder.add(new CompoundTag("", Util.CreateCompoundMapWithContents(tag.getValue().get("Count"), new StringTag("WPName", Data.Waypoints.get(WPName)))));
            }
        }
        //create the ListTag from the List
        //ListTag WPUses = new ListTag("WPUses",CompoundTag.class, WPUses_builder);


        //the game will add missing items itself, hence the commented out fields
        //ByteTag ShowMapMarkers = new ByteTag("ShowMapMarkers", (byte) 1);

        //removes redundant data (for now, at least)
        Util.CMRemoveVarArgs(originalData, "QuestData", "Achievements", "SentMessageTypes", "BountiesPlaced", "CustomWayPoints", "CWPSharedHidden", "CWPSharedUnlocked", "CWPSharedUses", "CWPUses", "FellowshipInvites", "Fellowships", "MiniQuests", "MiniQuestsCompleted", "TakenAlignmentRewards", "AdminHideMap", "Chosen35Align", "ConquestKills", "HideAlignment", "HideOnMap", "HiredDeathMessages", "LastBiome", "MiniQuestTrack", "MQCompleteCount", "MQCompletedBounties", "Pre35Align", "ShowHiddenSWP", "StructuresBanned", "ChatBoundFellowship", "DeathDim");
        originalData.replace("AlignmentMap", new ListTag<>("AlignmentMap", TagType.TAG_COMPOUND, AlignmentMap_builder));
        originalData.replace("FactionStats", new ListTag<>("FactionStats", TagType.TAG_COMPOUND, FactionStats_builder));
        originalData.replace("PrevRegionFactions", new ListTag<>("PrevRegionFactions", TagType.TAG_COMPOUND, PrevRegionFactions_builder));
        originalData.replace("UnlockedFTRegions", new ListTag<>("UnlockedFTRegions", TagType.TAG_COMPOUND, UnlockedFTRegions_Builder));
        originalData.replace("WPUses", new ListTag<>("WPUses", TagType.TAG_COMPOUND, WPUses_builder));
        originalData.replace("CurrentFaction", new StringTag("CurrentFaction", Data.FacNames.getOrDefault(originalData.get("CurrentFaction").getValue().toString(), "lotr:hobbit")));

        if (Objects.equals(originalData.get("TeleportedME").getValue(), (byte) 1)) {
            originalData.replace("TeleportedME", (new ByteTag("InitialSpawnedIntoME", (byte) 0)));
        } else {
            originalData.replace("TeleportedME", (new ByteTag("InitialSpawnedIntoME", (byte) 1)));
        }

        //Byte in legacy, string in renewed, because of this you can replace it in the stream
        if (Objects.equals(originalData.get("FemRankOverride").getValue(), (byte) 0)) {
            originalData.put("RankGender", (new StringTag("RankGender", "M")));

        } else {
            originalData.put("RankGender", (new StringTag("RankGender", "F")));
            // "FLOPPA_CAT" Mevans, really?
        }

        originalData.remove("FemRankOverride");
        if (originalData.containsKey("HideOnMap")) {
            if (Objects.equals(originalData.get("HideOnMap").getValue(), (byte) 1)) {
                originalData.replace("HideOnMap", new ByteTag("ShowMapLocation", (byte) 0));
            } else {
                originalData.replace("HideOnMap", new ByteTag("ShowMapLocation", (byte) 1));
            }
        }
    }

    public static CompoundMap TileEntityFixer(CompoundMap map, Data Data) {
        //TODO: implementation
        //map.clear();
        return new CompoundMap();
    }

    public static void SectionMapFixer(List<CompoundTag> list, Data Data, StringCache stringCache) {
        /*
I created a flatworld with the 'the void' preset.
In this world chunk 0,0 had a small stone platform with one block of cobblestone, except for that the chunk is empty
The BlockStates Array has the following contents:
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
1229782938247303441  1229782938247303441
1229782938247303441  1229782938247303441
1229782938247303441  1229782938247303441
1229782938247303441  1229782938247303441
1229782942542270737  1229782938247303441
1229782938247303441  1229782938247303441
1229782938247303441  1229782938247303441
1229782938247303441  1229782938247303441
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0
0  0

This doesn't tell much on it's own though, here is the palette:
"minecraft:air"
"minecraft:stone"
"minecraft:cobblestone"

what we expect are a lot of zeros (we can see that, a few longs with only references to 1 (stone) and 1 with a reference to 2 (cobblestone))

disregarding the zeros, these longs count for this test:

1229782938247303441  1229782938247303441
1229782938247303441  1229782938247303441
1229782938247303441  1229782938247303441
1229782938247303441  1229782938247303441
1229782942542270737  1229782938247303441
1229782938247303441  1229782938247303441
1229782938247303441  1229782938247303441
1229782938247303441  1229782938247303441

as you can see, of them is different from the others.
Now lets view these in binary form:
1229782938247303441 -> 0001 0001 0001 0001 0001 0001 0001 0001 0001 0001 0001 0001 0001 0001 0001 0001
1229782942542270737 -> 0001 0001 0001 0001 0001 0001 0001 0010 0001 0001 0001 0001 0001 0001 0001 0001

as you can see, with 4 bits per block all of these belong to stone except for one

because of this, constructing the longs shouldn't be too difficult

except it's still difficult, as 4 bits per index only allows for 16 BlockStates per section, whereas you can have a lot more than that.
The format changes depending on the size of the palette, though more than 6 bits per index is pretty rare

as you always have 4096 blocks in a section (counting air obviously) you can have the following bits per index technically (minimum is 4)
* 1 (palette of up to 2)
* 2 (palette of up to 4)
* 3 (palette of up to 8)
* 4 (palette of up to 16, games minimum)
* 5 (palette of up to 32)
* 6 (palette of up to 64)
* 7 (palette of up to 128)
* 8 (palette of up to 256)
* 9 (palette of up to 512)
* 10 (palette of up to 1024)
* 11 (palette of up to 2048)
* 12 (palette of up to 4096)







issues:
1229782938247303441 -> 0001 0001 0001 0001 0001 0001 0001 0001 0001 0001 0001 0001 0001 0001 0001 0001
        */
        for (int i = 0; i < list.size(); i++) {
            CompoundMap SectionCompoundMap = list.get(i).getValue();
            List<CompoundTag> PaletteBuilderList = new ArrayList<>();

            //Apparently air is always in the palette, or once it's in it never leaves, I don't know yet
            PaletteBuilderList.add(new CompoundTag("", Util.CreateCompoundMapWithContents(new StringTag("Name", "minecraft:air"))));

            //used for making sure no identical palette entries exist
            List<String> PaletteCheckerList = new ArrayList<>(Collections.singleton("{name=minecraft:air}"));

            Optional<ByteArrayTag> OBlocksByteArray = SectionCompoundMap.get("Blocks").getAsByteArrayTag();
            Optional<ByteArrayTag> ODataByteArray = SectionCompoundMap.get("Data").getAsByteArrayTag();

            //TODO: test again, if it throws a NuLLPointer again write a bugreport as it shouldn't do that due to the Optional
            /*
            Optional<ByteArrayTag> OAddArray;
            if (SectionCompoundMap.containsKey("Add")) {
                OAddArray = SectionCompoundMap.get("Add").getAsByteArrayTag();
            } else OAddArray = Optional.empty();
             */


            if (OBlocksByteArray.isPresent() && ODataByteArray.isPresent()) {
                byte[] BlocksByteArray = OBlocksByteArray.get().getValue();
                byte[] DataByteArray = ODataByteArray.get().getValue();
                //initializes with 0 as default value, as air is always the first entry, nothing needs to happen with air
                int[] BlockPaletteReferences = new int[4096];
                //this should never fail as far as I know, purely redundancy
                if (BlocksByteArray.length == 4096 && DataByteArray.length == 2048) {
                    //to loop through both lists at once.

                    //if (OAddArray.isPresent()) {
                    /*
                        byte[] AddArray = OAddArray.get().getValue();
                        for (int DataCounter = 0; DataCounter < 4096; DataCounter++) {
                            int dataValue = Math.floorDiv(DataCounter, 2);
                            //I might've reversed this one accidentally, time will tell...
                            boolean SecondEntry = DataCounter % 2 == 1;
                            if (Data.BlockIdToName.containsKey(String.valueOf(BlocksByteArray[DataCounter]))) {
                                String LegacyId = Data.BlockIdToName.get(String.valueOf(BlocksByteArray[DataCounter]));

                                //Only print for debugging purposes, this is extremely slow (1 region file with this on takes 15 min, with this off it takes 15 seconds)
                                //stringCache.PrintLine(LegacyId, false);

                                if (Data.BlockMappings.containsKey(LegacyId)) {
                                    BlockPaletteReferences[DataCounter] = AddPaletteEntryIfNecessary(Data.BlockMappings.get(LegacyId), DataByteArray[dataValue], AddArray[dataValue], SecondEntry, PaletteCheckerList, PaletteBuilderList);
                                }
                            }
                            DataCounter++;
                        }
                     */

                    //} else {
                    for (int DataCounter = 0; DataCounter < 4096; DataCounter++) {
                        int dataValue = Math.floorDiv(DataCounter, 2);
                        //I might've reversed this one accidentally, time will tell...
                        boolean SecondEntry = DataCounter % 2 == 1;
                        if (Data.BlockIdToName.containsKey(String.valueOf(BlocksByteArray[DataCounter]))) {
                            String LegacyId = Data.BlockIdToName.get(String.valueOf(BlocksByteArray[DataCounter]));

                            //Only print for debugging purposes, this is extremely slow (1 region file with this on takes 15 min, with this off it takes 15 seconds)
                            //stringCache.PrintLine(LegacyId, false);

                            if (Data.BlockMappings.containsKey(LegacyId)) {
                                BlockPaletteReferences[DataCounter] = AddPaletteEntryIfNecessary(Data.BlockMappings.get(LegacyId), DataByteArray[dataValue], SecondEntry, PaletteCheckerList, PaletteBuilderList);
                            }
                        }
                    }
                    //}

                    ListTag<CompoundTag> Palette = new ListTag<>("Palette", TagType.TAG_COMPOUND, PaletteBuilderList);

                    SectionCompoundMap.remove("Blocks");
                    SectionCompoundMap.remove("Data");
                    //TODO: use add combined with Data as otherwise there will be bugs
                    SectionCompoundMap.remove("Add");
                    SectionCompoundMap.put(Palette);
                    SectionCompoundMap.put(new LongArrayTag("BlockStates", BlockStatesGenerator(PaletteCheckerList, BlockPaletteReferences)));
                    list.set(i, new CompoundTag("", SectionCompoundMap));

                } else {
                    stringCache.PrintLine("Invalid section format!", false);
                }
            }
        }
    }

    /**
     * Creates the long[] for the BlockStates Tag
     *
     * @param PaletteCheckerList {@link List<String>} with the palette entries stringified
     * @param PaletteReferences  int[] with the block values
     * @return long[] containing the encoded BlockStates
     */
    public static long[] BlockStatesGenerator(List<String> PaletteCheckerList, int[] PaletteReferences) {
        long[] BlockStates;
        //Should always be true due to where we call it, just making sure
        if (PaletteReferences.length == 4096) {
            //How many bits do you need to store the data of 1 block, minimum is 4
            int bitsPerIndex = BitsPerIndex(PaletteCheckerList.size());
            //How many blocks can you fit in 1 long, a long stores the data of 64 bits, so you simply divide and floor
            int BlocksPerLong = Math.floorDiv(64, bitsPerIndex);
            //How many longs do you need to store 4096 blocks with BlockPerLong
            int LongsNeeded = (int) Math.ceil((double) 4096 / BlocksPerLong);
            BlockStates = new long[LongsNeeded];

            //Which long should we write to, ranges from 0 up to LongsNeeded
            int ExternalLongPosition;
            //Which position within the long are we gonna use, value ranges from 0 to 16
            int InternalLongPosition;
            for (int i = 0; i < 4096; i++) {
                //progression for switching to the next long
                ExternalLongPosition = Math.floorDiv(i, BlocksPerLong);
                //progression within the long
                InternalLongPosition = i % BlocksPerLong;
                //Updates the long accordingly to the Palette reference, the BPI (Bits Per Index) & the internal position
                BlockStates[ExternalLongPosition] = BlockStateLongUpdater(BlockStates[ExternalLongPosition], PaletteReferences[i], bitsPerIndex, InternalLongPosition);
            }

        } else BlockStates = new long[256]; //returns an empty section with bpi of 4
        return BlockStates;
    }

    /**
     * Updates the value of a long with its new value depending on the bits per index (BPI), blocks per long (BPL) and the internal position
     *
     * @param Base                  the long to be updated
     * @param Value                 the value the long should be updated with
     * @param BPI                   Bits per Index
     * @param InternalBlockPosition Position of the Long that should be updated
     */
    public static long BlockStateLongUpdater(long Base, int Value, int BPI, int InternalBlockPosition) {
        //if value is 0 there is no need to update the value of the long
        if (Value != 0) {
            Base = Base | ((long) Value << (BPI * InternalBlockPosition));
        }
        return Base;
    }

    /**
     * @param PaletteCheckerListLength Length of the Palette
     * @return the bits per Index of the Palette
     * @author PieGames
     * Gets the Bits per Index, used in the extractFromLong1_16 method in {@link Chunk}, unfortunately it's not a seperate method, so I added it here
     */
    public static int BitsPerIndex(int PaletteCheckerListLength) {
        return Math.max(4, 32 - Integer.numberOfLeadingZeros(PaletteCheckerListLength - 1));
    }

    /**
     * Adds an entry to the Palette if it's necessary (it doesn't exist yet)
     *
     * @param BlockMapping       {@link Map<String>} Mapping of the main id
     * @param DataEntry          byte second id of the block
     * @param SecondEntry        werther the first or second id of DataEntry should be used
     * @param PaletteCheckerList {@link List<String>} containing String versions of the Palette entries, used for faster searching
     * @param PaletteBuilderList {@link List<CompoundTag>} containing the Palette entries
     */
    @SuppressWarnings("unchecked")
    public static int AddPaletteEntryIfNecessary(Map<String, ?> BlockMapping, byte DataEntry, boolean SecondEntry, List<String> PaletteCheckerList, List<CompoundTag> PaletteBuilderList) {
        int returner = 0;
        if (EntryExists(BlockMapping, DataEntry, SecondEntry)) {
            LinkedTreeMap<?, ?> Entry = (LinkedTreeMap<?, ?>) BlockMapping.get(String.valueOf((BlockDataSelector(DataEntry, SecondEntry))));
            if (!PaletteCheckerList.contains(Entry.toString())) {
                PaletteCheckerList.add(Entry.toString());
                CompoundMap map = new CompoundMap();
                if (Entry.containsKey("name")) {
                    map.put(new StringTag("Name", (String) Entry.get("name")));
                    if (Entry.containsKey("properties")) {
                        CompoundMap innerCompoundBuilder = new CompoundMap();
                        Map<String, ?> properties = (Map<String, ?>) Entry.get("properties");
                        for (Map.Entry<String, ?> property : properties.entrySet()) {
                            //probably always a string if I read the wiki correctly, just in case I put in the case of a Boolean
                            if (property.getValue() instanceof String)
                                innerCompoundBuilder.put(new StringTag(property.getKey(), (String) property.getValue()));
                            else if (property.getValue() instanceof Boolean)
                                innerCompoundBuilder.put(new ByteTag(property.getKey(), (Boolean) property.getValue()));
                        }
                        map.put(new CompoundTag("Properties", innerCompoundBuilder));
                    }
                    PaletteBuilderList.add(new CompoundTag("Palette", map));
                }
            }
            returner = PaletteCheckerList.indexOf(Entry.toString());
        }
        return returner;
    }

/*
    @SuppressWarnings("unchecked")
    public static int AddPaletteEntryIfNecessary(Map<String, ?> BlockMapping, byte DataEntry, byte AddEntry, boolean SecondEntry, List<String> PaletteCheckerList, List<CompoundTag> PaletteBuilderList) {
        int returner = 0;
        //blockId = ((add << 8) + baseId)
        //((AddEntry << 8) + DataEntry)
        if (EntryExists(BlockMapping, ((AddEntry << 8) + DataEntry), SecondEntry)) {
            LinkedTreeMap<?, ?> Entry = (LinkedTreeMap<?, ?>) BlockMapping.get(String.valueOf((BlockDataSelector(((AddEntry << 8) + DataEntry), SecondEntry))));
            if (!PaletteCheckerList.contains(Entry.toString())) {
                PaletteCheckerList.add(Entry.toString());
                CompoundMap map = new CompoundMap();
                if (Entry.containsKey("name")) {
                    map.put(new StringTag("Name", (String) Entry.get("name")));
                    if (Entry.containsKey("properties")) {
                        CompoundMap innerCompoundBuilder = new CompoundMap();
                        Map<String, ?> properties = (Map<String, ?>) Entry.get("properties");
                        for (Map.Entry<String, ?> property : properties.entrySet()) {
                            //probably always a string if I read the wiki correctly, just in case I put in the case of a Boolean
                            if (property.getValue() instanceof String)
                                innerCompoundBuilder.put(new StringTag(property.getKey(), (String) property.getValue()));
                            else if (property.getValue() instanceof Boolean)
                                innerCompoundBuilder.put(new ByteTag(property.getKey(), (Boolean) property.getValue()));
                        }
                        map.put(new CompoundTag("Properties", innerCompoundBuilder));
                    }
                    PaletteBuilderList.add(new CompoundTag("Palette", map));
                }
            }
            returner = PaletteCheckerList.indexOf(Entry.toString());
        }
        return returner;
    }
 */


    /**
     * Returns if the corresponding value exists
     *
     * @param Mapping     Entry
     * @param DataValue   byte containing the sub value
     * @param SecondEntry determines whether the first or last 4 bits of DataValue should be used
     * @return boolean confirming or denying existence of an entry.
     */
    public static boolean EntryExists(Map<String, ?> Mapping, byte DataValue, boolean SecondEntry) {
        return (Mapping.containsKey(String.valueOf((BlockDataSelector(DataValue, SecondEntry)))));
    }

    /**
     * Returns the bits that should be used
     *
     * @param DataValue   original byte
     * @param SecondEntry determines if the first or the last 4 bits will be picked
     * @return the selected bits from DataValue
     */
    public static byte BlockDataSelector(byte DataValue, boolean SecondEntry) {
        byte Result = (SecondEntry ? (byte) (DataValue >> 4) : (byte) (DataValue & 15));
        if (Result < 0) {
            return (byte) (Result + 16);
        } else return Result;
        //return (SecondEntry ? (byte) (DataValue >> 4) : (byte) (DataValue & 15));
    }


    /**
     * Fixes Chunk {@link CompoundMap}
     *
     * @param Chunk {@link CompoundMap} of a chunk
     * @param Data  instance of {@link Data}
     */
    @SuppressWarnings("unchecked")
    public static void ChunkFixer(CompoundMap Chunk, Data Data, StringCache stringCache) {
        if (Chunk.containsKey("Level")) {
            Optional<CompoundTag> OLevel = Chunk.get("Level").getAsCompoundTag();
            if (OLevel.isPresent()) {
                CompoundMap Level = OLevel.get().getValue();
                //Will regenerate the biomes automatically, might cause some weird things with older worlds, but it isn't a priority right now
                Level.remove("Biomes");
                //I don't know what this does, it isn't present in newer versions I know that for sure
                Level.remove("V");
                //Removed until I know exactly what it does (I don't know the modern format, again, not a priority)
                Level.remove("TileTicks");
                Level.remove("LightPopulated");
                Level.remove("TerrainPopulated");
                //Will hopefully regenerate, easily testable by just removing them from a new world via NBTExplorer, I'm just too lazy to do it.
                Level.remove("Heightmap");
                /*
                if (Level.containsKey("Entities")) {
                    Optional<ListTag<?>> OEntities = Level.get("Entities").getAsListTag();
                    if (OEntities.isPresent()) {
                        ListTag<CompoundTag> Entities = (ListTag<CompoundTag>) OEntities.get();
                        List<CompoundTag> EntityBuilder = new ArrayList<>();
                        for (CompoundTag t : Entities.getValue()) {
                            //EntityFixer was made in a hurry and is probably unfinished/ prone to crashing. For testing purposes you can disable this line if you get crashes
                            //CompoundMap Entity = EntityFixer(t.getValue(), Data, stringCache, false);
                            CompoundMap Entity = new CompoundMap();
                            //if (Entity != null) EntityBuilder.add(new CompoundTag("", Entity));
                            EntityBuilder.add(new CompoundTag("", Entity));
                        }
                        Level.replace("Entities", new ListTag<>("Entities", TagType.TAG_COMPOUND, EntityBuilder));
                    }
                }
                */
                Level.remove("Entities");
                if (Level.containsKey("TileEntities")) {
                    Optional<ListTag<?>> OTileEntities = Level.get("TileEntities").getAsListTag();
                    if (OTileEntities.isPresent()) {
                        ListTag<CompoundTag> TileEntities = (ListTag<CompoundTag>) OTileEntities.get();
                        List<CompoundTag> EntityBuilder = new ArrayList<>();
                        for (CompoundTag t : TileEntities.getValue()) {
                            EntityBuilder.add(new CompoundTag("", TileEntityFixer(t.getValue(), Data)));
                        }
                        Level.replace("TileEntities", new ListTag<>("TileEntities", TagType.TAG_COMPOUND, EntityBuilder));
                    }
                }
                if (Level.containsKey("Sections")) {
                    Optional<ListTag<?>> OSections = Level.get("Sections").getAsListTag();
                    if (OSections.isPresent()) {
                        ListTag<CompoundTag> SectionsTag = (ListTag<CompoundTag>) OSections.get();
                        List<CompoundTag> Sections = SectionsTag.getValue();
                        SectionMapFixer(Sections, Data, stringCache);
                        Level.replace("Sections", new ListTag<>("Sections", TagType.TAG_COMPOUND, Sections));
                    }
                }
                //Needed for the game to try to use the changed data, otherwise it'll regenerate the chunks
                Level.put(new StringTag("Status", "full"));
                Chunk.replace("Level", new CompoundTag("Level", Level));
                //This value triggers Mojangs own DataFixers so use with caution
                Chunk.put(new IntTag("DataVersion", 2586));
            }
        }
    }

    /**
     * Fixes Regions
     *
     * @param Chunks {@link HashMap} with key Position and Value Chunk
     * @param Data   Instance of {@link Data}
     * @return {@link HashMap} with the fixed chunks
     */
    public static HashMap<Integer, Chunk> regionFixer(HashMap<Integer, Chunk> Chunks, Data Data, StringCache stringCache) throws IOException {
        for (Map.Entry<Integer, Chunk> entry : Chunks.entrySet()) {
            Chunk chunk = entry.getValue();
            CompoundTag tag = chunk.readTag();
            CompoundMap map = tag.getValue();
            ChunkFixer(map, Data, stringCache);
            tag.setValue(map);
            //Chunk(int x, int z, int timestamp, Tag<?> data, byte compression)
            chunk = new Chunk(chunk.x, chunk.z, chunk.timestamp, tag, chunk.getCompression());
            entry.setValue(chunk);
        }
        return Chunks;
    }
}

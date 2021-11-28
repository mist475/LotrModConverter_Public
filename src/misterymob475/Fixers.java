package misterymob475;

//import org.jnbt.*;

import de.piegames.nbt.*;

import java.io.IOException;
import java.util.*;

import static misterymob475.Main.PrintLine;

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
    public static CompoundMap EntityFixer(CompoundMap Entity, Data Data, Boolean Important) throws IOException {
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
                        } else
                            Entity.replace("id", new StringTag("id", "minecraft:horse"));
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
                    PrintLine("No mapping found for Entity: " + Entity.get("id").getValue() + " - It probably hasn't been ported yet", Data, false);
            } else {
                PrintLine("No mapping found for Entity: " + Entity.get("id").getValue(), Data, false);
                return null;
            }
        } else return null;
        //if Important, entity will always be saved, otherwise entity will only be saved if it maxes sense (Utumno mobs will get deleted)
        boolean inUtumno = false;
        //has something to do with the lotrmod
        Entity.remove("ForgeData");


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
            CompoundMap newSaddleItem = new CompoundMap();
            newSaddleItem.put("Count", new ByteTag("Count", (byte) 1));
            newSaddleItem.put("id", new StringTag("id", "minecraft:saddle"));

            Entity.replace("SaddleItem", new CompoundTag("SaddleItem", newSaddleItem));
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
                            CompoundMap attackDamage = new CompoundMap();
                            attackDamage.put("Modifiers", modifierFixer(((ListTag<CompoundTag>) t.getValue().get("Modifiers"))));
                            attackDamage.put("Name", new StringTag("Name", "generic.attack_damage"));
                            Attributes_new.add(new CompoundTag("", attackDamage));
                            break;

                        //modifiers present here
                        case "zombie.spawnReinforcements":
                            CompoundMap spawnReinf = new CompoundMap(t.getValue());
                            spawnReinf.replace("Name", new StringTag("Name", "zombie.spawn_reinforcements"));
                            Attributes_new.add(new CompoundTag("", spawnReinf));
                            break;

                        case "generic.movementSpeed":
                            CompoundMap movSpeed = new CompoundMap();
                            movSpeed.put("Base", t.getValue().get("Base"));
                            movSpeed.put("Name", new StringTag("Name", "minecraft:generic.movement_speed"));
                            Attributes_new.add(new CompoundTag("", movSpeed));
                            break;

                        case "generic.followRange":
                            CompoundMap followRange = new CompoundMap();
                            //yet to be tested
                            followRange.put("Modifiers", modifierFixer(((ListTag<CompoundTag>) t.getValue().get("Modifiers"))));
                            followRange.put("Base", t.getValue().get("Base"));
                            followRange.put("Name", new StringTag("Name", "minecraft:generic.follow_range"));
                            Attributes_new.add(new CompoundTag("", followRange));
                            break;

                        case "generic.maxHealth":
                            CompoundMap maxHealth = new CompoundMap();
                            maxHealth.put("Base", t.getValue().get("Base"));
                            maxHealth.put("Name", new StringTag("Name", "minecraft:generic.max_health"));
                            Attributes_new.add(new CompoundTag("", maxHealth));
                            break;
                        case "generic.knockbackResistance":
                            CompoundMap knockbackResistance = new CompoundMap();
                            knockbackResistance.put("Base", t.getValue().get("Base"));
                            knockbackResistance.put("Name", new StringTag("Name", "generic.knockback_resistance"));
                            Attributes_new.add(new CompoundTag("", knockbackResistance));
                            break;
                        case "horse.jumpStrength":
                            CompoundMap jumpStrength = new CompoundMap();
                            jumpStrength.put("Base", t.getValue().get("Base"));
                            jumpStrength.put("Name", new StringTag("Name", "horse.jump_strength"));
                            Attributes_new.add(new CompoundTag("", jumpStrength));
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

        //will easily regenerate I hope
        Entity.remove("DropChances");
        if (Entity.containsKey("Equipment")) {
            Entity.replace("Equipment", new ListTag<>("Equipment", TagType.TAG_COMPOUND, RecurItemFixer((((ListTag<CompoundTag>) Entity.get("Equipment")).getValue()), (double) 0, "Exception during Entity Equipment Fix", Data)));
        }
        //The sole reason I implemented this before I started working on fixing the world
        if (Entity.containsKey("Items")) {
            Entity.replace("Items", new ListTag<>("Items", TagType.TAG_COMPOUND, RecurItemFixer((((ListTag<CompoundTag>) Entity.get("Items")).getValue()), (double) 0, "Exception during Entity Inventory Fix", Data)));
        }
        Entity.remove("AttackTime");
        //LOTR mod related
        Entity.remove("BelongsNPC");

        Entity.remove("HasReproduced");

        Entity.remove("HealF");
/*
        Not needed anymore, I added these to debug inventories nog getting saved properly.
        However, that was because I called recurItemFixer the wrong way again
        Entity.put("CanUpdate",new ByteTag("CanUpdate",(byte)1));
        Entity.put("FallFlying",new ByteTag("FallFlying",(byte)0));
        Entity.put("ForcedAge",new IntTag("ForcedAge",0));
        Entity.put("HurtByTimestamp",new IntTag("HurtByTimestamp",0));
 */
        Entity.remove("Leashed");
        Entity.remove("Mountable");

        Entity.put("LeftHanded", new ByteTag("LeftHanded", (byte) 0));
        if (Entity.containsKey("OwnerUUID")) {
            Entity.put("Owner", UUIDFixer((StringTag) Entity.get("OwnerUUID"), "Owner"));
        }

        Entity.remove("OwnerUUID");
        Entity.remove("TicksSinceFeed");

        Entity.remove("Type");
        //has to do with splitting horses into donkeys and such
        Entity.remove("Variant");

        if (Entity.containsKey("UUIDLeast")) {
            Entity.put("UUID", UUIDFixer((LongTag) Entity.get("UUIDMost"), (LongTag) Entity.get("UUIDLeast")));
            Entity.remove("UUIDLeast");
            Entity.remove("UUIDMost");
        }
        return Entity;
    }

    public static CompoundMap RiderEntityFixer(CompoundMap Entity, Data Data) throws IOException {
        CompoundMap RootVehicle = new CompoundMap();
        if (Entity.containsKey("UUIDLeast")) {
            RootVehicle.put("Attach", UUIDFixer((LongTag) Entity.get("UUIDMost"), (LongTag) Entity.get("UUIDLeast"), "Attach"));
        }

        //
        CompoundMap Entity_map = EntityFixer(Entity, Data, true);
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
    public static void playerFixer(CompoundMap newData, Data Data) throws IOException {
        boolean inUtumno = false;
        //not needed in renewed
        newData.remove("ForgeData");
        //changed too much to bother with, especially as the game will recreate the property
        newData.remove("Attributes");

        if (newData.containsKey("Riding")) {
            //call to entity fixer, this means the player is riding on a mount (fixer will temporarily replace said mount with a donkey)
            CompoundMap Riding = new CompoundMap(((CompoundTag) newData.get("Riding")).getValue());
            Riding = Fixers.RiderEntityFixer(Riding, Data);
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
            newData.replace("EnderItems", new ListTag<>("EnderItems", TagType.TAG_COMPOUND, RecurItemFixer((((ListTag<CompoundTag>) newData.get("EnderItems")).getValue()), (double) 0, "Exception during Ender chest conversion", Data)));
        }
        if (newData.containsKey("Inventory")) {
            newData.replace("Inventory", new ListTag<>("Inventory", TagType.TAG_COMPOUND, RecurItemFixer((((ListTag<CompoundTag>) newData.get("Inventory")).getValue()), (double) 0, "Exception during inventory conversion", Data)));
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
    public static List<CompoundTag> RecurItemFixer(List<CompoundTag> l, Double depth, String exceptionMessage, Data Data) throws IOException {
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
                                            Items = RecurItemFixer(Items, depth, exceptionMessage, Data);
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
                                                Items = RecurItemFixer(Items, depth, exceptionMessage, Data);
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
                                            CompoundMap filler = new CompoundMap(((CompoundTag) tMap.get("tag")).getValue());
                                            //itemFixer
                                            if (filler.containsKey("display")) {
                                                filler.replace("display", nameFixer((CompoundTag) filler.get("display"), Data));
                                            }
                                            //pipe fixer
                                            if (filler.containsKey("SmokeColour")) {
                                                CompoundMap pipeMap = new CompoundMap();
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
                                                if (color.equals("magic"))
                                                    pipeMap.put("magic", new ByteTag("magic", (byte) 1));
                                                pipeMap.put("color", new StringTag("color", color));
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
                                                CompoundTag vessel = new CompoundTag("vessel", vesselMap);
                                                filler.put("vessel", vessel);
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
                                            PrintLine("No vanilla spawn Egg found for Damage value : " + tMap.get("Damage").getValue(), Data, false);
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
                                            PrintLine("No lotr mod spawn Egg found for Damage value : " + tMap.get("Damage").getValue(), Data, false);
                                    } else {
                                        PrintLine("No mapping found for legacy id: " + Data.LegacyIds.get(compare1), Data, false);
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
                                        PrintLine("No mapping found for " + Data.LegacyIds.get(compare1) + ":" + Damage, Data, false);
                                }
                            } else {
                                PrintLine("No mapping found for id: " + Data.LegacyIds.get(compare1), Data, false);
                            }
                        } else {
                            //this should never happen as I gather these ids dynamically
                            PrintLine("No string id found for id: " + compare1, Data, false);
                        }
                    } else {
                        PrintLine("Empty tag found, skipping", Data, true);
                    }
                }
            } else {
                //if this actually gets triggered someone has been annoying on purpose
                System.out.println("Maximum set recursion depth reached (default = 7, defined in JSON)");
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
        //Might have reversed the order though
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
        UUID uuid = UUID.fromString(UUID_t.getValue());
        return UUIDFixer(new LongTag("", uuid.getMostSignificantBits()), new LongTag("", uuid.getLeastSignificantBits()), name);
    }

    /**
     * Fixer level LevelFixer
     *
     * @param Chunk map of CompoundTag of chunk
     * @param Data  instance of Data
     * @return fixed map
     */
    public static CompoundMap ChunkFixer(CompoundMap Chunk, Data Data) {
        //TODO: Proper implementation
        return Chunk;
    }
}

package misterymob475;

import de.piegames.nbt.*;
import de.piegames.nbt.regionfile.Chunk;
import misterymob475.data.Conversions.BlockMapping;
import misterymob475.data.Conversions;
import misterymob475.data.Data;

import java.io.IOException;
import java.util.*;

/**
 * Functionally static class containing all the fixes that are applied*.
 * <p>
 * * (Except for fixing idcount as that requires file interaction which is explicitly not handled in this class)
 */
public class Fixers {
    private static final Data DATA = Data.getInstance();
    private static final StringCache STRINGCACHE = StringCache.getInstance();

    public Fixers() {}

    /**
     * Fixes entities
     *
     * @param Entity    Map with key String and value Tag (.getValue() of CompoundTag)
     * @param Important Bool stating if mob should be removed if in Utumno
     * @return Fixed Map
     */
    @SuppressWarnings("unchecked")
    public Optional<CompoundMap> EntityFixer(CompoundMap Entity, Boolean Important) throws IOException {
        //return Optional.empty();
        //Temporary measure to prevent crashes as this is still unfinished atm
        //Determines the actual mob
        if (Entity.containsKey("id")) {
            if (DATA.entities.containsKey((String) (Entity.get("id").getValue()))) {
                if (!DATA.entities.get((String) (Entity.get("id").getValue())).equals("")) {
                    //code for split types here (horses mainly, I'm not gonna bother with zombie villagers)
                    if (DATA.entities.get((String) (Entity.get("id").getValue())).equals("minecraft:horse")) {
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
                        Entity.replace("id", new StringTag("id", DATA.entities.get((String) (Entity.get("id")
                                .getValue()))));
                    }
                    Entity.remove("Type");
                } else STRINGCACHE.printLine("No mapping found for Entity: " + Entity.get("id")
                        .getValue() + " - It probably hasn't been ported yet", false);
            } else {
                STRINGCACHE.printLine("No mapping found for Entity: " + Entity.get("id").getValue(), false);
                return Optional.empty();
            }
        } else return Optional.empty();
        //if Important, entity will always be saved, otherwise entity will only be saved if it maxes sense (Utumno mobs will get deleted)
        boolean inUtumno = false;

        //With helper method
        Optional<Tag<?>> ODimension = Util.getAsTagIfExists(Entity, TagType.TAG_INT, "Dimension");
        if (ODimension.isPresent()) {
            int Dimension = ((IntTag) ODimension.get()).getValue();
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
                } else return Optional.empty();
            } else newDimension = "minecraft:overworld";
            Entity.replace("Dimension", new StringTag("Dimension", newDimension));
        }

        if (inUtumno) {
            //sets the player coordinates at the coordinates of the pit if they're currently in Utumno (roughly, they'll be moved in renewed I've heard)
            //ListTag Pos1 = (ListTag) newData.get("Pos");
            ArrayList<DoubleTag> Pos = new ArrayList<DoubleTag>(3) {
            };
            Pos.add(new DoubleTag("", 46158.0));
            Pos.add(new DoubleTag("", 80.0));
            Pos.add(new DoubleTag("", -40274.0));
            Entity.replace("Pos", new ListTag<>("Pos", TagType.TAG_DOUBLE, Pos));

        }

        if (Entity.containsKey("SaddleItem")) {
            Entity.replace("SaddleItem", new CompoundTag("SaddleItem", Util.createCompoundMapWithContents(new ByteTag("Count", (byte) 1), new StringTag("id", "minecraft:saddle"))));
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
        attacking speed boost (pigmen/endermen)
        drinking speed penalty
        baby speed boost
        Tool modifier
        Weapon modifier
        potion.healthBoost
         */
        Optional<Tag<?>> OAttributes = Util.getAsTagIfExists(Entity, TagType.TAG_LIST, "Attributes");
        if (OAttributes.isPresent()) {
            List<CompoundTag> attributesOld = (List<CompoundTag>) OAttributes.get().getValue();
            List<CompoundTag> attributesNew = new ArrayList<>();
            for (CompoundTag t : attributesOld) {
                CompoundMap map = t.getValue();
                Optional<Tag<?>> OName = Util.getAsTagIfExists(Entity, TagType.TAG_STRING, "Name");
                if (OName.isPresent()) {
                    switch ((String) OName.get().getValue()) {

                        case "generic.attackDamage":
                            //attributesNew.add(new CompoundTag("", Util.createCompoundMapWithContents(modifierFixer(((ListTag<CompoundTag>) t.getValue().get("Modifiers"))), new StringTag("Name", "generic.attack_damage"))));
                            break;

                        //modifiers present here
                        case "zombie.spawnReinforcements":
                            map.replace("Name", new StringTag("Name", "zombie.spawn_reinforcements"));
                            attributesNew.add(new CompoundTag("", map));
                            break;

                        case "generic.movementSpeed":
                            attributesNew.add(new CompoundTag("", Util.createCompoundMapWithContents(t.getValue()
                                                                                                             .get("Base"), new StringTag("Name", "minecraft:generic.movement_speed"))));
                            break;

                        case "generic.followRange":
                            CompoundMap followRange = Util.createCompoundMapWithContents(t.getValue()
                                                                                                 .get("Base"), new StringTag("Name", "minecraft:generic.follow_range"));
                            (t.getValue().get("Modifiers")
                                    .getAsListTag()).ifPresent(listTag -> followRange.put(modifierFixer((ListTag<CompoundTag>) listTag)));

                            attributesNew.add(new CompoundTag("", followRange));
                            break;

                        case "generic.maxHealth":
                            attributesNew.add(new CompoundTag("", Util.createCompoundMapWithContents(t.getValue()
                                                                                                             .get("Base"), new StringTag("Name", "minecraft:generic.max_health"))));
                            break;
                        case "generic.knockbackResistance":
                            attributesNew.add(new CompoundTag("", Util.createCompoundMapWithContents(t.getValue()
                                                                                                             .get("Base"), new StringTag("Name", "generic.knockback_resistance"))));
                            break;
                        case "horse.jumpStrength":
                            attributesNew.add(new CompoundTag("", Util.createCompoundMapWithContents(t.getValue()
                                                                                                             .get("Base"), new StringTag("Name", "horse.jump_strength"))));
                            break;
                        default:
                            //this is possible because unknown tags will get discarded by the game engine
                            attributesNew.add(t);
                            break;
                    }
                }
            }
            Entity.replace("Attributes", (new ListTag<>("Attributes", TagType.TAG_COMPOUND, attributesNew)));
        }


        if (Entity.containsKey("Equipment")) {
            Entity.replace("Equipment", new ListTag<>("Equipment", TagType.TAG_COMPOUND, recurItemFixerList((((ListTag<CompoundTag>) Entity.get("Equipment")).getValue()), 0, "Exception during Entity Equipment Fix")));
        }
        //The sole reason I implemented this before I started working on fixing the world
        if (Entity.containsKey("Items")) {
            Entity.replace("Items", new ListTag<>("Items", TagType.TAG_COMPOUND, recurItemFixerList((((ListTag<CompoundTag>) Entity.get("Items")).getValue()), 0, "Exception during Entity Inventory Fix")));
        }

        Entity.put("LeftHanded", new ByteTag("LeftHanded", (byte) 0));
        if (Entity.containsKey("OwnerUUID")) {
            (Entity.get("OwnerUUID")
                    .getAsStringTag()).ifPresent(stringTag -> Entity.put("Owner", uuidFixer(stringTag, "Owner")));
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

        Util.compoundMapVarArgRemover(Entity, "ForgeData", "DropChances", "AttackTime", "BelongsNPC", "HasReproduced", "HealF", "Leashed", "Mountable", "OwnerUUID", "TicksSinceFeed", "Type", "Variant");

        if (Entity.containsKey("UUIDLeast") && Entity.containsKey("UUIDMost")) {
            Optional<LongTag> OUUIDMost = Entity.get("UUIDMost").getAsLongTag();
            Optional<LongTag> OUUIDLeast = Entity.get("UUIDLeast").getAsLongTag();
            if (OUUIDMost.isPresent() && OUUIDLeast.isPresent()) {
                Entity.put("UUID", uuidFixer(OUUIDMost.get(), OUUIDLeast.get()));
                Util.compoundMapVarArgRemover(Entity, "UUIDLeast", "UUIDMost");
            }
        }
        return Optional.of(Entity);

    }

    /**
     * Fixes entities mounted by players or other entities
     *
     * @param Entity      {@link CompoundMap} containing entity data
     * @return fixed {@link CompoundMap} of entity content
     * @throws IOException if something fails
     */
    public Optional<CompoundMap> riderEntityFixer(CompoundMap Entity) throws IOException {
        CompoundMap RootVehicle = new CompoundMap();
        if (Entity.containsKey("UUIDLeast") && Entity.containsKey("UUIDMost")) {
            Optional<LongTag> OUUIDMost = Entity.get("UUIDMost").getAsLongTag();
            Optional<LongTag> OUUIDLeast = Entity.get("UUIDLeast").getAsLongTag();
            if (OUUIDMost.isPresent() && OUUIDLeast.isPresent()) {
                RootVehicle.put("Attach", uuidFixer(OUUIDMost.get(), OUUIDLeast.get(), "Attach"));
            }
        }
        //
        Optional<CompoundMap> OEntity_map = EntityFixer(Entity, true);
        if (OEntity_map.isPresent()) {
            RootVehicle.put("Entity", new CompoundTag("Entity", OEntity_map.get()));
            return Optional.of(RootVehicle);
        } else return Optional.empty();
    }

    /**
     * Fixes entity modifiers
     *
     * @param t {@link ListTag} of type {@link CompoundTag} containing the modifiers
     * @return fixed {@link ListTag} of type {@link CompoundTag}
     */
    public ListTag<CompoundTag> modifierFixer(ListTag<CompoundTag> t) {
        List<CompoundTag> newList = new ArrayList<>();
        for (CompoundTag c : t.getValue()) {
            CompoundMap Modifier = c.getValue();
            if (Modifier.containsKey("UUIDLeast") && Modifier.containsKey("UUIDMost")) {
                Optional<LongTag> OUUIDMost = Modifier.get("UUIDMost").getAsLongTag();
                Optional<LongTag> OUUIDLeast = Modifier.get("UUIDLeast").getAsLongTag();
                if (OUUIDMost.isPresent() && OUUIDLeast.isPresent()) {
                    Modifier.put("UUID", uuidFixer(OUUIDMost.get(), OUUIDLeast.get(), "UUID"));
                }
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
    public void playerFixer(CompoundMap newData) throws IOException {
        boolean inUtumno = false;
        //not needed in renewed
        newData.remove("ForgeData");
        //changed too much to bother with, especially as the game will recreate the property
        newData.remove("Attributes");

        Optional<Tag<?>> ORiding = Util.getAsTagIfExists(newData, TagType.TAG_COMPOUND, "Riding");
        if (ORiding.isPresent()) {
            //call to entity fixer, this means the player is riding on a mount (fixer will temporarily replace said mount with a donkey)
            Optional<CompoundMap> Riding = riderEntityFixer((CompoundMap) ORiding.get().getValue());
            if (Riding.isPresent()) {
                CompoundTag RootVehicle = new CompoundTag("RootVehicle", Riding.get());
                newData.replace("Riding", RootVehicle);
            } else newData.remove("Riding");
        }

        if (DATA.settings.isCreativeSpawn()) {
            newData.replace("playerGameType", new IntTag("playerGameType", 1));
        }

        if (newData.containsKey("EnderItems")) {
            newData.replace("EnderItems", new ListTag<>("EnderItems", TagType.TAG_COMPOUND, recurItemFixerList((((ListTag<CompoundTag>) newData.get("EnderItems")).getValue()), 0, "Exception during Ender chest conversion")));
        }
        if (newData.containsKey("Inventory")) {
            newData.replace("Inventory", new ListTag<>("Inventory", TagType.TAG_COMPOUND, recurItemFixerList((((ListTag<CompoundTag>) newData.get("Inventory")).getValue()), 0, "Exception during inventory conversion")));
        }

        newData.remove("Attack Time");
        if (!newData.containsKey("DataVersion")) {
            newData.put("DataVersion", new IntTag("DataVersion", 2586));
        }

        Optional<Tag<?>> ODimension = Util.getAsTagIfExists(newData, TagType.TAG_INT, "Dimension");
        if (ODimension.isPresent()) {
            int Dimension = ((IntTag) ODimension.get()).getValue();
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
            Optional<LongTag> OUUIDMost = newData.get("UUIDMost").getAsLongTag();
            Optional<LongTag> OUUIDLeast = newData.get("UUIDLeast").getAsLongTag();
            if (OUUIDMost.isPresent() && OUUIDLeast.isPresent()) {
                newData.put("UUID", uuidFixer(OUUIDMost.get(), OUUIDLeast.get()));
            }
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
    public CompoundTag nameFixer(CompoundTag display) {
        //TODO: full support, table already in Conversion but not yet hooked up
        CompoundMap display_map = new CompoundMap(display.getValue());
        if (display_map.containsKey("Name")) {
            String name = (String) display_map.get("Name").getValue();
            String colour = "";
            if (name.contains("§")) {
                //Fixes coloured items, might have to fix 'Lore' items too. Not sure how those are saved yet
                if (DATA.colours.containsKey(name.substring(0, 2))) {
                    colour = "," + '"' + "color" + '"' + ':' + '"' + DATA.colours.get(name.substring(0, 2)) + '"';
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
     * @param itemList         {@link List} of type {@link CompoundTag} of the given inventory
     * @param depth            Maximum recursive depth
     * @param exceptionMessage {@link String} printed when exception is thrown
     * @return {@link List} of type {@link CompoundTag} of the modified inventory
     * @throws IOException if something fails
     */
    public List<CompoundTag> recurItemFixerList(List<CompoundTag> itemList, double depth, String exceptionMessage) throws IOException {
        try {
            List<CompoundTag> itemListBuilder = new ArrayList<>();
            if (depth++ < DATA.settings.getItemRecursionDepth()) {
                for (CompoundTag itemCompoundTag : itemList) {
                    (recurItemFixer(itemCompoundTag, depth, exceptionMessage)).ifPresent(tags -> itemListBuilder.add(new CompoundTag("", tags)));
                }
            } else {
                //if this actually gets triggered someone has been annoying on purpose, and you're dealing with an old worlds as triggering this is only possible in older versions of the lotr mod
                STRINGCACHE.printLine("Maximum set recursion depth reached (default = 7, defined in JSON)", false);
            }
            return itemListBuilder;
        } catch (final ClassCastException | NullPointerException ex) {
            throw new IOException(exceptionMessage);
        }
    }

    /**
     * Recursively runs through the provided item (recursive because of shulkerboxes/pouches/crackers)
     *
     * @param itemCompoundTag  {@link CompoundTag} of the given item
     * @param depth            Maximum recursive depth
     * @param exceptionMessage {@link String} printed when exception is thrown
     * @return {@link List} of type {@link CompoundTag} of the modified inventory
     */
    @SuppressWarnings("unchecked")
    public Optional<CompoundMap> recurItemFixer(CompoundTag itemCompoundTag, double depth, String exceptionMessage) {
        try {
            if (depth++ < DATA.settings.getItemRecursionDepth()) {
                CompoundMap itemCompoundMap = itemCompoundTag.getValue();
                if (!(itemCompoundMap).isEmpty()) {
                    Optional<ShortTag> OShortIDTag = itemCompoundMap.get("id").getAsShortTag();
                    Optional<StringTag> OStringIDTag = itemCompoundMap.get("id").getAsStringTag();
                    Optional<String> OStringID = Optional.empty();
                    if (OShortIDTag.isPresent()) {
                        int idValue = OShortIDTag.get().getValue();
                        if (DATA.legacyIds.containsKey(idValue)) {
                            OStringID = Optional.of(DATA.legacyIds.get(idValue));
                        } else {
                            //this should never happen as I gather these ids dynamically
                            STRINGCACHE.printLine("No string id found for id: " + idValue);
                        }
                    } else if (OStringIDTag.isPresent()) {
                        //String id found instead of short id (apparently possible as I found my old world had this)
                        OStringID = Optional.of(OStringIDTag.get().getValue());
                    }

                    if (OStringID.isPresent()) {
                        String StringID = OStringID.get();
                        boolean save = true;

                        if (DATA.itemNames.containsKey(StringID)) {
                            List<String> item = DATA.itemNames.get(StringID);
                            //recursive call 1 (Pouches)
                            if (item.get(0).equals("lotr:small_pouch")) {
                                Optional<Tag<?>> OTag = Util.getAsTagIfExists(itemCompoundMap, TagType.TAG_COMPOUND, "tag");
                                if (OTag.isPresent()) {
                                    CompoundMap filler = baseTagItemFixer((CompoundMap) OTag.get().getValue());

                                    Optional<IntTag> OPouchColor;
                                    if (filler.containsKey("PouchColor")) {
                                        Optional<IntTag> CastPrevention = filler.get("PouchColor").getAsIntTag();
                                        OPouchColor = CastPrevention.map(intTag -> new IntTag("Color", intTag.getValue()));
                                        filler.remove("PouchColor");
                                    } else {
                                        OPouchColor = Optional.empty();
                                    }

                                    if (filler.containsKey("LOTRPouchData") || OPouchColor.isPresent()) {
                                        CompoundMap LOTRPouchData;
                                        if (filler.containsKey("LOTRPouchData")) {
                                            Optional<CompoundTag> OLOTRPouchData = filler.get("LOTRPouchData")
                                                    .getAsCompoundTag();
                                            if (OLOTRPouchData.isPresent()) {
                                                LOTRPouchData = OLOTRPouchData.get().getValue();
                                            } else LOTRPouchData = new CompoundMap();
                                        } else {
                                            LOTRPouchData = new CompoundMap();
                                        }

                                        if (LOTRPouchData.containsKey("Items")) {
                                            LOTRPouchData.replace("Items", new ListTag<>("Items", TagType.TAG_COMPOUND, recurItemFixerList(((ListTag<CompoundTag>) LOTRPouchData.get("Items")).getValue(), depth, exceptionMessage)));
                                        }

                                        OPouchColor.ifPresent(LOTRPouchData::put);
                                        filler.replace("LOTRPouchData", new CompoundTag("Pouch", LOTRPouchData));
                                    }
                                    itemCompoundMap.replace("tag", new CompoundTag("tag", filler));
                                }
                                itemCompoundMap.replace("id", new StringTag("id", item.get((Short) itemCompoundMap.get("Damage")
                                        .getValue())));
                                itemCompoundMap.remove("Damage");
                                return Optional.of(itemCompoundMap);
                            }

                            //recursive call 2 (Barrels/Kegs)
                            else if (item.get(0).equals("lotr:keg")) {
                                CompoundMap filler = new CompoundMap();
                                Optional<Tag<?>> OFiller = Util.getAsTagIfExists(itemCompoundMap, TagType.TAG_COMPOUND, "tag");
                                if (OFiller.isPresent()) {
                                    filler = baseTagItemFixer((CompoundMap) OFiller.get().getValue());
                                    Optional<Tag<?>> OLOTRBarrelData = Util.getAsTagIfExists(filler, TagType.TAG_COMPOUND, "LOTRBarrelData");
                                    if (OLOTRBarrelData.isPresent()) {
                                        CompoundMap LOTRBarrelData = (CompoundMap) OLOTRBarrelData.get().getValue();
                                        Optional<Tag<?>> OItems = Util.getAsTagIfExists(LOTRBarrelData, TagType.TAG_LIST, "Items");
                                        if (OItems.isPresent()) {
                                            //
                                            List<CompoundTag> Items = ((ListTag<CompoundTag>) OItems.get()).getValue();
                                            Items = recurItemFixerList(Items, depth, exceptionMessage);
                                            //
                                            LOTRBarrelData.replace("Items", new ListTag<>("Items", TagType.TAG_COMPOUND, Items));

                                        }
                                        if (LOTRBarrelData.containsKey("BrewingTime")) {
                                            (LOTRBarrelData.get("BrewingTime")
                                                    .getAsIntTag()).ifPresent(intTag -> LOTRBarrelData.put("BrewingTimeTotal", new IntTag("BrewingTimeTotal", (intTag).getValue())));
                                        }
                                        if (LOTRBarrelData.containsKey("BarrelMode")) {
                                            (LOTRBarrelData.get("BarrelMode")
                                                    .getAsByteTag()).ifPresent(byteTag -> LOTRBarrelData.replace("BarrelMode", new ByteTag("KegMode", ((byteTag)).getValue())));
                                        }

                                        filler.replace("LOTRBarrelData", new CompoundTag("BlockEntityTag", Util.createCompoundMapWithContents(new CompoundTag("KegDroppableData", LOTRBarrelData))));
                                    }
                                }
                                itemCompoundMap.remove("Damage");
                                itemCompoundMap.replace("id", new StringTag("id", "lotr:keg"));
                                itemCompoundMap.replace("tag", new CompoundTag("tag", filler));
                                return Optional.of(itemCompoundMap);
                            }
                            //recursive call 3? (Crackers)

                            //Player head fixer (Apparently the game fixes this one automatically, except for custom names. So I added the full thing except the killed by message as I don't know how that is formatted)
                            else if (item.get(0).equals("minecraft:skeleton_skull")) {
                                CompoundMap filler = new CompoundMap();
                                Optional<Tag<?>> OFiller = Util.getAsTagIfExists(itemCompoundMap, TagType.TAG_COMPOUND, "tag");
                                if (OFiller.isPresent()) {
                                    filler = baseTagItemFixer((CompoundMap) OFiller.get().getValue());
                                    if (filler.containsKey("SkullOwner")) {
                                        filler.replace("SkullOwner", new CompoundTag("SkullOwner", Util.createCompoundMapWithContents(new StringTag("Id", ((String) filler.get("SkullOwner")
                                                .getValue())))));
                                    }
                                }

                                itemCompoundMap.replace("id", new StringTag("id", item.get((Short) itemCompoundMap.get("Damage")
                                        .getValue())));
                                itemCompoundMap.remove("Damage");
                                itemCompoundMap.replace("tag", new CompoundTag("tag", filler));
                                return Optional.of(itemCompoundMap);
                            } else if (item.size() == 1) {

                                //code for single idTag values (mostly items, stairs) here
                                //simply carries over all the tags, except the idTag, which gets modified to the new one. moves the damage tag to its new location and changes it to an IntTag(was ShortTag before)

                                //If there is a mapping (empty mappings are "" in the JSON)
                                if (!Objects.equals(item.get(0), "")) {
                                    boolean drink = new ArrayList<>(Arrays.asList("lotr:ale", "lotr:orc_draught", "lotr:apple_juice", "lotr:athelas_brew", "lotr:cactus_liqueur", "lotr:carrot_wine", "lotr:cherry_liqueur", "lotr:cider", "lotr:chocolate_drink", "lotr:dwarven_ale", "lotr:dwarven_tonic", "lotr:maple_beer", "lotr:mead", "lotr:melon_liqueur", "lotr:milk_drink", "lotr:miruvor", "lotr:morgul_draught", "lotr:perry", "lotr:rum", "lotr:soured_milk", "lotr:sweet_berry_juice", "lotr:vodka", "lotr:water_drink")).contains(item.get(0));
                                    CompoundMap filler;
                                    Optional<Tag<?>> OFiller = Util.getAsTagIfExists(itemCompoundMap, TagType.TAG_COMPOUND, "tag");
                                    filler = new CompoundMap();
                                    if (OFiller.isPresent()) {
                                        filler = baseTagItemFixer((CompoundMap) OFiller.get().getValue());
                                        //pipe fixer
                                        if (filler.containsKey("SmokeColour")) {
                                            String color = (new ArrayList<>(Arrays.asList("white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black", "magic"))).get((Integer) filler.get("SmokeColour")
                                                    .getValue());
                                            CompoundMap pipeMap = Util.createCompoundMapWithContents(new StringTag("color", color));
                                            if (color.equals("magic"))
                                                pipeMap.put("magic", new ByteTag("magic", (byte) 1));
                                            filler.replace("SmokeColour", new CompoundTag("pipe", pipeMap));
                                        }
                                        //Book fixer
                                        else if (filler.containsKey("pages")) {
                                            if (filler.containsKey("author") || filler.containsKey("title")) {
                                                if (filler.containsKey("author")) {
                                                    if (DATA.authorBlacklist.contains((String) filler.get("author")
                                                            .getValue())) {
                                                        save = false;
                                                    }
                                                }
                                                if (filler.containsKey("title")) {
                                                    if (DATA.titleBlacklist.contains((String) filler.get("title")
                                                            .getValue())) {
                                                        save = false;
                                                    }
                                                }
                                            }
                                            //without this book & quills get messed up
                                            if (Objects.equals(item.get(0), "minecraft:written_book")) {
                                                List<StringTag> pages = new ArrayList<>();
                                                Optional<Tag<?>> OPages = Util.getAsTagIfExists(filler, TagType.TAG_LIST, "pages");
                                                if (OPages.isPresent()) {
                                                    List<StringTag> PageList = (List<StringTag>) OPages.get()
                                                            .getValue();
                                                    for (StringTag st : PageList) {
                                                        pages.add(new StringTag("", JSONTextFixer(st.getValue())));
                                                    }
                                                }
                                                filler.replace("pages", new ListTag<>("pages", TagType.TAG_STRING, pages));
                                            }
                                        }
                                        //Enchantments fixer
                                        else if (filler.containsKey("ench") || filler.containsKey("StoredEnchantments")) {
                                            List<CompoundTag> ench_filler = new ArrayList<>();

                                            Optional<Tag<?>> OEnch = Util.getAsTagIfExists(filler, TagType.TAG_LIST, "ench");
                                            if (OEnch.isPresent()) {
                                                for (CompoundTag enchT : ((ListTag<CompoundTag>) filler.get("ench")).getValue()) {
                                                    CompoundMap ench = enchT.getValue();
                                                    Optional<Tag<?>> OID = Util.getAsTagIfExists(ench, TagType.TAG_SHORT, "id");
                                                    if (OID.isPresent()) {
                                                        ench.replace("id", new StringTag("id", DATA.enchantments.get(OID.get()
                                                                                                                             .getValue()
                                                                                                                             .toString())));
                                                        ench_filler.add(new CompoundTag("", ench));
                                                    }
                                                }
                                                filler.replace("ench", new ListTag<>("Enchantments", TagType.TAG_COMPOUND, ench_filler));
                                                Optional<Tag<?>> ODamage = Util.getAsTagIfExists(itemCompoundMap, TagType.TAG_SHORT, "Damage");
                                                if (ODamage.isPresent()) {
                                                    filler.put("Damage", (new IntTag("Damage", ((ShortTag) ODamage.get()).getValue())));
                                                } else
                                                    STRINGCACHE.printLine("No damage tag found or damage is not a short");
                                            } else {
                                                //enchanted books
                                                for (CompoundTag ench_t : ((ListTag<CompoundTag>) filler.get("StoredEnchantments")).getValue()) {
                                                    CompoundMap ench = new CompoundMap((ench_t.getValue()));
                                                    Optional<Tag<?>> OID = Util.getAsTagIfExists(ench, TagType.TAG_SHORT, "id");
                                                    if (OID.isPresent()) {
                                                        ench.replace("id", new StringTag("id", DATA.enchantments.get(OID.get()
                                                                                                                             .getValue()
                                                                                                                             .toString())));
                                                        ench_filler.add(new CompoundTag("", ench));
                                                    }
                                                }
                                                filler.replace("StoredEnchantments", new ListTag<>("StoredEnchantments", TagType.TAG_COMPOUND, ench_filler));
                                            }
                                            filler.remove("LOTRRandomEnch");
                                            filler.remove("LOTRRepairCost");
                                        }
                                    }

                                    if (drink) {
                                        if (itemCompoundMap.containsKey("Damage")) {
                                            filler.put("vessel", vesselMapItemCreator((Short) itemCompoundMap.get("Damage")
                                                    .getValue()));
                                        }
                                    }
                                    //potion fixer
                                    else if (item.get(0).equals("minecraft:potion")) {
                                        if (DATA.potions.containsKey(itemCompoundMap.get("Damage").getValue()
                                                                             .toString())) {
                                            Conversions.Potion potion = DATA.potions.get(itemCompoundMap.get("Damage")
                                                                                                 .getValue()
                                                                                                 .toString());
                                            filler.put("Potion", new StringTag("Potion", potion.getName()));
                                            if (potion.isSplash())
                                                itemCompoundMap.replace("id", new StringTag("id", "minecraft:splash_potion"));
                                            else itemCompoundMap.replace("id", new StringTag("id", "minecraft:potion"));
                                        } else itemCompoundMap.replace("id", new StringTag("id", "minecraft:potion"));
                                    }
                                    //map fixer (very simple thankfully)
                                    else if (item.get(0).equals("minecraft:filled_map")) {
                                        if (itemCompoundMap.containsKey("Damage")) {
                                            filler.put("map", new IntTag("map", (int) ((Short) itemCompoundMap.get("Damage")
                                                    .getValue())));
                                        }
                                    } else {
                                        Optional<Tag<?>> ODamage = Util.getAsTagIfExists(itemCompoundMap, TagType.TAG_SHORT, "Damage");
                                        if (ODamage.isPresent()) {
                                            if (((ShortTag) ODamage.get()).getValue() != 0) {
                                                filler.put("Damage", (new IntTag("Damage", ((ShortTag) ODamage.get()).getValue())));
                                            }
                                        }
                                    }

                                    itemCompoundMap.remove("tag");
                                    if (!filler.isEmpty()) itemCompoundMap.put(new CompoundTag("tag", filler));

                                    //sets name to potion or splash potion
                                    if (!item.get(0).equals("minecraft:potion"))
                                        itemCompoundMap.replace("id", new StringTag("id", item.get(0)));
                                    itemCompoundMap.remove("Damage");

                                    if (save) {
                                        return Optional.of(itemCompoundMap);
                                    }
                                }
                                //vanilla spawn egg handler
                                else if (StringID.equals("minecraft:spawn_egg")) {
                                    //itemFixer
                                    if (itemCompoundMap.containsKey("tag")) {
                                        (itemCompoundMap.get("tag")
                                                .getAsCompoundTag()).ifPresent(compoundTag -> itemCompoundMap.replace("tag", new CompoundTag("tag", baseTagItemFixer((compoundTag.getValue())))));
                                    }
                                    if (DATA.vanillaMobIds.containsKey(((Short) itemCompoundMap.get("Damage")
                                            .getValue()).toString())) {
                                        itemCompoundMap.replace("id", new StringTag("id", DATA.vanillaMobIds.get(((Short) itemCompoundMap.get("Damage")
                                                .getValue()).toString())));
                                        itemCompoundMap.remove("Damage");
                                        return Optional.of(itemCompoundMap);
                                    } else
                                        STRINGCACHE.printLine("No vanilla spawn Egg found for Damage value : " + itemCompoundMap.get("Damage")
                                                .getValue(), false);
                                }
                                //lotr spawn egg handler
                                else if (StringID.equals("lotr:item.spawnEgg")) {
                                    //itemFixer
                                    if (itemCompoundMap.containsKey("tag")) {
                                        (itemCompoundMap.get("tag")
                                                .getAsCompoundTag()).ifPresent(compoundTag -> itemCompoundMap.replace("tag", new CompoundTag("tag", baseTagItemFixer((compoundTag.getValue())))));
                                    }
                                    if (DATA.modMobIds.containsKey(((Short) itemCompoundMap.get("Damage")
                                            .getValue()).toString())) {
                                        itemCompoundMap.replace("id", new StringTag("id", DATA.modMobIds.get(((Short) itemCompoundMap.get("Damage")
                                                .getValue()).toString())));
                                        itemCompoundMap.remove("Damage");
                                        return Optional.of(itemCompoundMap);
                                    } else
                                        STRINGCACHE.printLine("No lotr mod spawn Egg found for Damage value : " + itemCompoundMap.get("Damage")
                                                .getValue(), false);
                                } else {
                                    STRINGCACHE.printLine("No mapping found for legacy id: " + StringID, false);
                                }
                            } else {
                                Optional<Tag<?>> ODamage = Util.getAsTagIfExists(itemCompoundMap, TagType.TAG_SHORT, "Damage");
                                if (ODamage.isPresent()) {
                                    //code for blocks/some items here
                                    Short Damage = (Short) ODamage.get().getValue();
                                    //Check if block is actually in the list and not just a placeholder
                                    if (!DATA.itemNames.get(StringID).get(Damage).equals("")) {
                                        if (itemCompoundMap.containsKey("tag")) {
                                            (itemCompoundMap.get("tag")
                                                    .getAsCompoundTag()).ifPresent(compoundTag -> itemCompoundMap.replace("tag", new CompoundTag("tag", baseTagItemFixer((compoundTag.getValue())))));
                                        }
                                        itemCompoundMap.remove("Damage");
                                        itemCompoundMap.replace("id", new StringTag("id", item.get(Damage)));
                                        return Optional.of(itemCompoundMap);
                                    } else
                                        STRINGCACHE.printLine("No mapping found for " + StringID + ":" + Damage, false);
                                }
                            }
                        } else {
                            STRINGCACHE.printLine("No mapping found for id: " + StringID, false);
                        }
                    }

                } else {
                    STRINGCACHE.printLine("Empty tag found, skipping", false);
                }
            }
        } catch (Exception e) {
            System.out.println(exceptionMessage);
        }
        return Optional.empty();
    }

    /**
     * Function which returns a new IntArrayTag based off the given LongTags and name
     *
     * @param UUIDLeast {@link LongTag}
     * @param UUIDMost  {@link LongTag}
     * @param name      {@link String} name
     * @return {@link IntArrayTag} with given name and param inputs
     */
    public IntArrayTag uuidFixer(LongTag UUIDMost, LongTag UUIDLeast, String name) {
        //Creates the UUID in the new format based with name being the name of the intArrayTag
        long v1 = UUIDMost.getValue();
        long v2 = UUIDLeast.getValue();
        return new IntArrayTag(name, new int[]{(int) (v1 >> 32), (int) v1, (int) (v2 >> 32), (int) v2});
    }

    /**
     * Overload for when name is "UUID"
     *
     * @param UUIDLeast {@link LongTag}
     * @param UUIDMost  {@link LongTag}
     * @return {@link IntArrayTag} with name "UUID" and param inputs
     */
    public IntArrayTag uuidFixer(LongTag UUIDMost, LongTag UUIDLeast) {
        return uuidFixer(UUIDMost, UUIDLeast, "UUID");
    }

    /**
     * Overload for StringTags
     *
     * @param UUID_t {@link StringTag}
     * @param name   String
     * @return {@link IntArrayTag} with name as name and param inputs
     */
    public IntArrayTag uuidFixer(StringTag UUID_t, String name) {
        if (!UUID_t.getValue().equals("")) {
            UUID uuid = UUID.fromString(UUID_t.getValue());
            return uuidFixer(new LongTag("", uuid.getMostSignificantBits()), new LongTag("", uuid.getLeastSignificantBits()), name);
        } else
            return new IntArrayTag(name, new int[]{0, 0, 0, 0}); //Not sure if game reads this correctly, otherwise the entire tag might have to be removed instead, this is purely to prevent a crash when the uuid is non-existent
    }

    /**
     * Fixes the "tag" section of an Item
     *
     * @param filler {@link CompoundMap} to be fixed
     * @return fixed version of filler
     */
    @SuppressWarnings("unchecked")
    public CompoundMap baseTagItemFixer(CompoundMap filler) {
        if (filler.containsKey("display")) {
            (filler.get("display")
                    .getAsCompoundTag()).ifPresent(compoundTag -> filler.replace("display", nameFixer(compoundTag)));
        }

        //Optional<ListTag<?>> test = Util.GetAsTagTypeIfExists(filler,"LOTRPrevOwnerList",TagType.TAG_LIST);
        Optional<Tag<?>> OPreviousOwners = Util.getAsTagIfExists(filler, TagType.TAG_LIST, "LOTRPreviousOwnerList");
        if (OPreviousOwners.isPresent()) {
            List<StringTag> OwnerList = (List<StringTag>) OPreviousOwners.get().getValue();
            OwnerList.replaceAll(stringTag -> new StringTag(stringTag.getName(), JSONTextFixer(stringTag.getValue())));
            filler.put(new CompoundTag("LOTROwnership", Util.createCompoundMapWithContents(new ListTag<>("PreviousOwners", TagType.TAG_STRING, OwnerList))));
        }
        return filler;
    }

    /**
     * Creates a {@link CompoundTag} containing the special data for drink items
     *
     * @param Damage short storing the damage value determining the type & potency
     * @return {@link CompoundTag} containing the special data for drinks
     */
    public CompoundTag vesselMapItemCreator(short Damage) {
        CompoundMap vesselMap = new CompoundMap();

        //Code for determining the strength of the drink
        if ((Damage % 10) == 0) vesselMap.put("potency", new StringTag("potency", "weak"));
        else if ((Damage % 10) == 1) vesselMap.put("potency", new StringTag("potency", "light"));
        else if ((Damage % 10) == 2) vesselMap.put("potency", new StringTag("potency", "moderate"));
        else if ((Damage % 10) == 3) vesselMap.put("potency", new StringTag("potency", "string"));
        else if ((Damage % 10) == 4) vesselMap.put("potency", new StringTag("potency", "potent"));
        //Code for determining the vessel (wooden mug, goblet etc.)
        if (Damage < 100) vesselMap.put("type", new StringTag("type", "wooden_mug"));
        else if (Damage < 200) vesselMap.put("type", new StringTag("type", "ceramic_mug"));
        else if (Damage < 300) vesselMap.put("type", new StringTag("type", "golden_goblet"));
        else if (Damage < 400) vesselMap.put("type", new StringTag("type", "silver_goblet"));
        else if (Damage < 500) vesselMap.put("type", new StringTag("type", "copper_goblet"));
        else if (Damage < 600) vesselMap.put("type", new StringTag("type", "wooden_cup"));
        else if (Damage < 700) vesselMap.put("type", new StringTag("type", "wooden_mug")); //skull cups not in yet
        else if (Damage < 800) vesselMap.put("type", new StringTag("type", "bottle")); //wine glasses not in yet
        else if (Damage < 900) vesselMap.put("type", new StringTag("type", "bottle"));
        else if (Damage < 1000) vesselMap.put("type", new StringTag("type", "waterskin"));
        else if (Damage < 1100) vesselMap.put("type", new StringTag("type", "ale_horn"));
        else if (Damage < 1200) vesselMap.put("type", new StringTag("type", "golden_ale_horn"));

        return new CompoundTag("vessel", vesselMap);
    }

    /**
     * Fixes maps
     *
     * @param map {@link CompoundMap} with map data
     */
    public void mapFixer(CompoundMap map) {
        Optional<Tag<?>> ODimension = Util.getAsTagIfExists(map, TagType.TAG_INT, "Dimension");
        if (ODimension.isPresent()) {
            Integer Dimension = (Integer) ODimension.get().getValue();
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
     * @param originalTopLevelTag1 {@link CompoundTag} of a renewed level.dat file
     * @throws IOException when something goes wrong
     */
    public void levelDatFixer(CompoundMap newData, CompoundTag originalTopLevelTag1) throws IOException {
        Optional<Tag<?>> OData = Util.getAsTagIfExists(newData, TagType.TAG_COMPOUND, "Data");
        Optional<Tag<?>> OData1 = Util.getAsTagIfExists(originalTopLevelTag1.getValue(), TagType.TAG_COMPOUND, "Data");

        if (OData.isPresent() && OData1.isPresent()) {
            CompoundMap Data = (CompoundMap) OData.get().getValue();
            CompoundMap Data1 = (CompoundMap) OData1.get().getValue();

            //GameRules fix (only 9 added in 1.7.10, keeping rest of the selected Renewed World)
            Optional<Tag<?>> OGameRules1_tag = Util.getAsTagIfExists(Data, TagType.TAG_COMPOUND, "GameRules");
            Optional<Tag<?>> OGameRules = Util.getAsTagIfExists(Data1, TagType.TAG_COMPOUND, "GameRules");
            if (OGameRules1_tag.isPresent() && OGameRules.isPresent()) {
                CompoundMap GameRules1 = (CompoundMap) OGameRules1_tag.get().getValue();
                CompoundMap GameRules = (CompoundMap) OGameRules.get().getValue();
                if (GameRules.containsKey("commandBlockOutput") && GameRules1.containsKey("commandBlockOutput")) {
                    GameRules.replace("commandBlockOutput", GameRules1.get("commandBlockOutput"));
                }
                if (GameRules.containsKey("doDaylightCycle") && GameRules1.containsKey("doDaylightCycle")) {
                    GameRules.replace("doDaylightCycle", GameRules1.get("doDaylightCycle"));
                }
                if (GameRules.containsKey("doFireTick") && GameRules1.containsKey("doFireTick")) {
                    GameRules.replace("doFireTick", GameRules1.get("doFireTick"));
                }
                if (GameRules.containsKey("doMobLoot") && GameRules1.containsKey("doMobLoot")) {
                    GameRules.replace("doMobLoot", GameRules1.get("doMobLoot"));
                }
                if (GameRules.containsKey("doMobSpawning") && GameRules1.containsKey("doMobSpawning")) {
                    GameRules.replace("doMobSpawning", GameRules1.get("doMobSpawning"));
                }
                if (GameRules.containsKey("doTileDrops") && GameRules1.containsKey("doTileDrops")) {
                    GameRules.replace("doTileDrops", GameRules1.get("doTileDrops"));
                }
                if (GameRules.containsKey("keepInventory") && GameRules1.containsKey("keepInventory")) {
                    GameRules.replace("keepInventory", GameRules1.get("keepInventory"));
                }
                if (GameRules.containsKey("mobGriefing") && GameRules1.containsKey("mobGriefing")) {
                    GameRules.replace("mobGriefing", GameRules1.get("mobGriefing"));
                }
                if (GameRules.containsKey("naturalRegeneration") && GameRules1.containsKey("naturalRegeneration")) {
                    GameRules.replace("naturalRegeneration", GameRules1.get("naturalRegeneration"));
                }
                newData.replace("GameRules", new CompoundTag("GameRules", GameRules));
            }

            Optional<Tag<?>> OWorldGenSettings = Util.getAsTagIfExists(Data, TagType.TAG_COMPOUND, "WorldGenSettings");
            if (OWorldGenSettings.isPresent()) {
                CompoundMap worldGenSettings = (CompoundMap) OWorldGenSettings.get().getValue();
                if (Data1.containsKey("MapFeatures")) {
                    worldGenSettings.replace("generate_features", Data1.get("MapFeatures"));
                }
                if (Data1.containsKey("RandomSeed")) {
                    worldGenSettings.replace("seed", new LongTag("seed", (Long) Data1.get("RandomSeed").getValue()));
                }

                Optional<Tag<?>> ODimensions = Util.getAsTagIfExists(worldGenSettings, TagType.TAG_COMPOUND, "dimension");
                if (ODimensions.isPresent()) {
                    CompoundMap dimensions = (CompoundMap) (ODimensions.get()).getValue();

                    //should have made this a loop in hindsight, oh well...

                    CompoundMap meDimension = ((CompoundTag) dimensions.get("lotr:middle_earth")).getValue();
                    CompoundMap generatorMap1 = ((CompoundTag) meDimension.get("generator")).getValue();
                    //lotr:middle_earth
                    //generatorMap1.replace("seed",Data1.get("RandomSeed"));
                    generatorMap1.replace("seed", new LongTag("seed", (Long) Data1.get("RandomSeed").getValue()));

                    CompoundMap biome_source1 = new CompoundMap();
                    Optional<Tag<?>> OBiomeSource1 = Util.getAsTagIfExists(generatorMap1, TagType.TAG_COMPOUND, "biome_source");
                    if (OBiomeSource1.isPresent()) {
                        biome_source1 = (CompoundMap) OBiomeSource1.get().getValue();
                    }


                    biome_source1.replace("seed", new LongTag("seed", (Long) Data1.get("RandomSeed").getValue()));
                    //sets instant_middle_earth right in lotr:middle_earth
                    //meClassic apparently doesn't use this tag, even though you definitely spawn directly into middle-earth
                    //Data1.get("generatorName").getValue().equals("meClassic") ||
                    if (Data1.get("generatorName").getValue().equals("middleEarth")) {
                        generatorMap1.replace("instant_middle_earth", new ByteTag("instant_middle_earth", (byte) 1));
                        if (Data1.get("generatorName").getValue().equals("meClassic"))
                            biome_source1.replace("classic_biomes", new ByteTag("classic_biomes", (byte) 1));
                        else biome_source1.replace("classic_biomes", new ByteTag("classic_biomes", (byte) 0));
                    } else generatorMap1.replace("instant_middle_earth", new ByteTag("instant_middle_earth", (byte) 0));

                    generatorMap1.replace("biome_source", new CompoundTag("biome_source", biome_source1));
                    meDimension.replace("generator", new CompoundTag("generator", generatorMap1));
                    dimensions.replace("lotr:middle_earth", new CompoundTag("lotr:middle_earth", meDimension));

                    CompoundMap overworldDimension = ((CompoundTag) dimensions.get("minecraft:overworld")).getValue();
                    CompoundMap generatorMap2 = ((CompoundTag) overworldDimension.get("generator")).getValue();
                    //minecraft:overworld
                    if ((Data1.get("generatorName").getValue().equals("flat"))) {
                        //handles flat-worlds, hardcodes the default values as transcribing them is beyond the scope of the convertor, salt might be the seed and not actually this odd value
                        generatorMap2.replace("type", new StringTag("type", "minecraft:flat"));
                        generatorMap2.remove("biome_source");
                        generatorMap2.remove("seed");
                        generatorMap2.remove("settings");
                        CompoundMap settings_map = Util.createCompoundMapWithContents(new StringTag("biome", "minecraft:plains"), new ByteTag("features", (byte) 0), new ByteTag("lakes", (byte) 0));

                        CompoundMap stronghold_map = Util.createCompoundMapWithContents(new IntTag("count", 128), new IntTag("distance", 32), new IntTag("spread", 3));
                        CompoundMap structures1_map = Util.createCompoundMapWithContents(new CompoundTag("stronghold", stronghold_map));

                        //TODO: Fix salt to use seed
                        CompoundMap village_map = Util.createCompoundMapWithContents(new IntTag("salt", ((new Random()).nextInt(1000000000))), new IntTag("separation", 8), new IntTag("spacing", 32));

                        structures1_map.put("structures", new CompoundTag("structures", Util.createCompoundMapWithContents(new CompoundTag("minecraft:village", village_map))));

                        settings_map.put("structures", new CompoundTag("structures", structures1_map));

                        //Adds the entries for flatworld generation
                        List<CompoundTag> layers_list = new ArrayList<>();
                        layers_list.add(new CompoundTag("", Util.createCompoundMapWithContents(new StringTag("block", "minecraft:bedrock"), new IntTag("height", 1))));
                        layers_list.add(new CompoundTag("", Util.createCompoundMapWithContents(new StringTag("block", "minecraft:dirt"), new IntTag("height", 2))));
                        layers_list.add(new CompoundTag("", Util.createCompoundMapWithContents(new StringTag("block", "minecraft:grass_block"), new IntTag("height", 1))));

                        settings_map.put("layers", new ListTag<>("layers", TagType.TAG_COMPOUND, layers_list));

                        generatorMap2.put("settings", new CompoundTag("settings", settings_map));
                    } else {
                        generatorMap2.replace("seed", new LongTag("seed", (Long) Data1.get("RandomSeed").getValue()));
                        CompoundMap biome_source2;
                        Optional<Tag<?>> OBiome_source2 = Util.getAsTagIfExists(generatorMap2, TagType.TAG_COMPOUND, "biome_source");
                        if (OBiome_source2.isPresent()) {
                            biome_source2 = (CompoundMap) OBiome_source2.get().getValue();
                            biome_source2.replace("seed", new LongTag("seed", (Long) Data1.get("RandomSeed")
                                    .getValue()));
                            generatorMap2.replace("biome_source", new CompoundTag("biome_source", biome_source2));
                            if (Data1.get("generatorName").getValue().equals("largeBiomes"))
                                generatorMap2.replace("large_biomes", new ByteTag("large_biomes", (byte) 1));
                            else generatorMap2.replace("large_biomes", new ByteTag("large_biomes", (byte) 0));
                        }
                    }
                    overworldDimension.replace("generator", new CompoundTag("generator", generatorMap2));
                    dimensions.replace("minecraft:overworld", new CompoundTag("minecraft:overworld", overworldDimension));

                    //minecraft:the_end
                    Optional<Tag<?>> OEndDimension = Util.getAsTagIfExists(dimensions, TagType.TAG_COMPOUND, "minecraft:the_end");
                    if (OEndDimension.isPresent()) {
                        CompoundMap endDimension = (CompoundMap) OEndDimension.get().getValue();
                        Optional<Tag<?>> OGeneratorMap3 = Util.getAsTagIfExists(endDimension, TagType.TAG_COMPOUND, "generator");
                        if (OGeneratorMap3.isPresent()) {
                            CompoundMap generatorMap3 = (CompoundMap) OGeneratorMap3.get().getValue();
                            generatorMap3.replace("seed", new LongTag("seed", (Long) Data1.get("RandomSeed")
                                    .getValue()));
                            Optional<Tag<?>> OBiomeSource3 = Util.getAsTagIfExists(generatorMap3, TagType.TAG_COMPOUND, "biome_source");
                            if (OBiomeSource3.isPresent()) {
                                CompoundMap biome_source3 = (CompoundMap) OBiomeSource3.get().getValue();
                                biome_source3.replace("seed", new LongTag("seed", (Long) Data1.get("RandomSeed")
                                        .getValue()));
                                generatorMap3.replace("biome_source", new CompoundTag("biome_source", biome_source3));
                                endDimension.replace("generator", new CompoundTag("generator", generatorMap3));
                                dimensions.replace("minecraft:the_end", new CompoundTag("minecraft:the_end", endDimension));
                            }
                        }
                    }

                    //minecraft:the_nether
                    Optional<Tag<?>> ONetherDimension = Util.getAsTagIfExists(dimensions, TagType.TAG_COMPOUND, "minecraft:the_nether");
                    if (ONetherDimension.isPresent()) {
                        CompoundMap netherDimension = (CompoundMap) ONetherDimension.get().getValue();
                        Optional<Tag<?>> OGeneratorMap4 = Util.getAsTagIfExists(netherDimension, TagType.TAG_COMPOUND, "generator");
                        if (OGeneratorMap4.isPresent()) {
                            CompoundMap generatorMap4 = (CompoundMap) OGeneratorMap4.get().getValue();
                            generatorMap4.replace("seed", new LongTag("seed", (Long) Data1.get("RandomSeed")
                                    .getValue()));
                            Optional<Tag<?>> OBiomeSource4 = Util.getAsTagIfExists(generatorMap4, TagType.TAG_COMPOUND, "biome_source");
                            if (OBiomeSource4.isPresent()) {
                                CompoundMap biome_source4 = (CompoundMap) OBiomeSource4.get().getValue();
                                biome_source4.replace("seed", new LongTag("seed", (Long) Data1.get("RandomSeed")
                                        .getValue()));
                                generatorMap4.replace("biome_source", new CompoundTag("biome_source", biome_source4));
                                netherDimension.replace("generator", new CompoundTag("generator", generatorMap4));
                                dimensions.replace("minecraft:the_nether", new CompoundTag("minecraft:the_nether", netherDimension));
                            }
                        }
                    }
                    worldGenSettings.replace("dimensions", new CompoundTag("dimensions", dimensions));
                    Data.replace("WorldGenSettings", new CompoundTag("WorldGenSettings", worldGenSettings));
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
                Optional<CompoundTag> OPlayer = Data1.get("Player").getAsCompoundTag();
                if (OPlayer.isPresent()) {
                    CompoundMap Player = OPlayer.get().getValue();
                    playerFixer(Player);
                    Data.replace("Player", new CompoundTag("Player", Player));
                }
            }
            newData.replace("Data", new CompoundTag("Data", Data));
        }
    }

    /**
     * Fixes the LOTR.Dat file
     *
     * @param originalData {@link CompoundMap} the map of LOTR.dat
     */
    public void LOTRDatFixer(CompoundMap originalData) {
        //discards: as they aren't in renewed yet or are now datapackable, if something gets ported to renewed in the exact same way as legacy I can simply remove these lines
        originalData.remove("TravellingTraders");
        originalData.remove("GreyWanderers");
        originalData.remove("AlignmentZones");
        originalData.remove("ConqRate");
        originalData.remove("DifficultyLock");
        originalData.remove("GollumSpawned");
        originalData.remove("GWSpawnTick");
        originalData.remove("StructuresBanned");

        Optional<Tag<?>> ODates = Util.getAsTagIfExists(originalData, TagType.TAG_COMPOUND, "Dates");
        if (ODates.isPresent()) {
            CompoundMap Dates = (CompoundMap) ODates.get().getValue();
            if (Dates.containsKey("ShireData")) {
                (Dates.get("ShireDate")
                        .getAsIntTag()).ifPresent(intTag -> originalData.replace("Dates", new CompoundTag("Dates", Util.createCompoundMapWithContents(new IntTag("CurrentDay", intTag.getValue())))));
            }
        }

        (originalData.get("MadeMiddlePortal")
                .getAsIntTag()).ifPresent(intTag -> originalData.replace("MadeMiddlePortal", new ByteTag("MadeMiddlePortal", (byte) (int) intTag.getValue())));
        //IntTag MadeMiddlePortal = originalData.get("MadeMiddlePortal").getAsIntTag().get();
        (originalData.get("MadePortal")
                .getAsIntTag()).ifPresent(intTag -> originalData.replace("MadePortal", new ByteTag("MadePortal", (byte) (int) intTag.getValue())));
    }

    /**
     * Fixes the lotr playerData files
     *
     * @param originalData {@link CompoundMap} of lotr player data
     */
    @SuppressWarnings("unchecked")
    public void LOTRPlayerDataFixer(CompoundMap originalData) {
        //gets the values we want, note, = I'm doing the easy ones first (lists last)
        //originalData.get("something").
        Optional<Tag<?>> OAlignmentMap = Util.getAsTagIfExists(originalData, TagType.TAG_LIST, "AlignmentMap");
        List<CompoundTag> AlignmentMap_builder = new ArrayList<CompoundTag>(1) {
        };
        if (OAlignmentMap.isPresent()) {
            ListTag<CompoundTag> alignmentMapOld = (ListTag<CompoundTag>) OAlignmentMap.get();
            for (CompoundTag tag : alignmentMapOld.getValue()) {
                CompoundMap map = tag.getValue();
                Optional<Tag<?>> OFaction = Util.getAsTagIfExists(map, TagType.TAG_STRING, "Faction");
                if (OFaction.isPresent()) {
                    String Faction = (String) (OFaction.get()).getValue();
                    if (DATA.facNames.containsKey(Faction)) {
                        AlignmentMap_builder.add(new CompoundTag("", Util.createCompoundMapWithContents(map.get("AlignF"), new StringTag("Faction", DATA.facNames.get(Faction)))));
                    }
                }
            }
        }

        //ListTag AlignmentMap = new ListTag("AlignmentMap",CompoundTag.class, AlignmentMap_builder);

        ListTag<CompoundTag> FactionStats_old = (ListTag<CompoundTag>) originalData.get("FactionData");
        List<CompoundTag> FactionStats_builder = new ArrayList<CompoundTag>(1) {
        };
        for (CompoundTag tag : FactionStats_old.getValue()) {
            CompoundMap map = tag.getValue();
            Optional<Tag<?>> OFaction = Util.getAsTagIfExists(map, TagType.TAG_STRING, "Faction");
            if (OFaction.isPresent()) {
                String Faction_AL = (String) (OFaction.get()).getValue();
                if (DATA.facNames.containsKey(Faction_AL)) {
                    final CompoundMap newData_AL = Util.createCompoundMapWithContents(map.get("ConquestHorn"), map.get("EnemyKill"), new StringTag("Faction", DATA.facNames.get(Faction_AL)), map.get("Hired"), map.get("MiniQuests"), map.get("Trades"));

                    //Couldn't think of a way to do renaming implicitly
                    newData_AL.put("MemberKill", map.get("NPCKill"));
                    CompoundTag AM_AL_Builder = new CompoundTag("", newData_AL);
                    FactionStats_builder.add(AM_AL_Builder);
                }
            }
        }
        //ListTag FactionStats = new ListTag("FactionStats",CompoundTag.class, FactionStats_builder);

        ListTag<CompoundTag> PrevRegionFactions_Old = (ListTag<CompoundTag>) originalData.get("PrevRegionFactions");
        List<CompoundTag> PrevRegionFactions_builder = new ArrayList<CompoundTag>(1) {
        };
        for (CompoundTag tag : PrevRegionFactions_Old.getValue()) {
            CompoundMap map = tag.getValue();
            Optional<Tag<?>> ORegion = Util.getAsTagIfExists(map, TagType.TAG_STRING, "Region");
            Optional<Tag<?>> OFaction = Util.getAsTagIfExists(map, TagType.TAG_STRING, "Faction");
            if (ORegion.isPresent() && OFaction.isPresent()) {
                String Region_PRF = (String) ORegion.get().getValue();
                String Faction_PRF = (String) OFaction.get().getValue();
                if (DATA.facNames.containsKey(Faction_PRF)) {
                    final CompoundMap newData_PRF = Util.createCompoundMapWithContents(new StringTag("Faction", DATA.facNames.get(Faction_PRF)));
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
                    PrevRegionFactions_builder.add(new CompoundTag("", newData_PRF));
                }
            }
        }
        //ListTag PrevRegionFactions = new ListTag("PrevRegionFactions",CompoundTag.class, PrevRegionFactions_builder);

        //SentMessageTypes
        List<StringTag> UnlockedFTRegions_Builder = new ArrayList<>(0);
        Optional<Tag<?>> OUnlockedFTRegions = Util.getAsTagIfExists(originalData, TagType.TAG_LIST, "UnlockedFTRegions");
        if (OUnlockedFTRegions.isPresent()) {
            ListTag<CompoundTag> UnlockedFTRegions = (ListTag<CompoundTag>) OUnlockedFTRegions.get();
            for (CompoundTag tag : UnlockedFTRegions.getValue()) {
                CompoundMap map = tag.getValue();
                Optional<Tag<?>> ORegionName = Util.getAsTagIfExists(map, TagType.TAG_STRING, "Name");
                if (ORegionName.isPresent()) {
                    String RegionName = (String) ORegionName.get().getValue();
                    if (DATA.regions.containsKey(RegionName)) {
                        String NameValue = DATA.regions.get(RegionName);
                        if (!Objects.equals(NameValue, "")) {
                            StringTag Name = new StringTag("", NameValue);
                            UnlockedFTRegions_Builder.add(Name);
                        }
                    }
                    //Was used in the past to prevent regions being lost. However, after discussion with Smile I decided to remove it
            /*
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
             */
                }
            }
        }

        List<CompoundTag> WPUses_builder = new ArrayList<>(1);
        Optional<Tag<?>> OWPUses = Util.getAsTagIfExists(originalData, TagType.TAG_LIST, "WPUses");
        if (OWPUses.isPresent()) {
            ListTag<CompoundTag> WPUses_old = (ListTag<CompoundTag>) OWPUses.get();
            for (CompoundTag tag : WPUses_old.getValue()) {
                CompoundMap map = tag.getValue();
                Optional<Tag<?>> OWPName = Util.getAsTagIfExists(map, TagType.TAG_STRING, "WPName");
                if (OWPName.isPresent()) {
                    if (DATA.waypoints.containsKey(((StringTag) OWPName.get()).getValue())) {
                        //add the CompoundTag to the List
                        //CompoundMap Info:
                        //Var1: the amount of waypoint usage (cooldown depends on it)
                        //Var2: the new name
                        WPUses_builder.add(new CompoundTag("", Util.createCompoundMapWithContents(map.get("Count"), new StringTag("WPName", DATA.waypoints.get(((StringTag) OWPName.get()).getValue())))));
                    }
                }
            }
        }
        //get the old WPUses
        //ListTag<CompoundTag> WPUses_old = (ListTag<CompoundTag>) originalData.get("WPUses");
        //create a new empty array put the new WPUses in
        //List<CompoundTag> WPUses_builder = new ArrayList<CompoundTag>(1) {
        //};
        //loop though the entries in the list

        //create the ListTag from the List
        //ListTag WPUses = new ListTag("WPUses",CompoundTag.class, WPUses_builder);


        //the game will add missing items itself, hence the commented out fields
        //ByteTag ShowMapMarkers = new ByteTag("ShowMapMarkers", (byte) 1);

        //removes redundant data (for now, at least)
        Util.compoundMapVarArgRemover(originalData, "QuestData", "Achievements", "SentMessageTypes", "BountiesPlaced", "CustomWayPoints", "CWPSharedHidden", "CWPSharedUnlocked", "CWPSharedUses", "CWPUses", "FellowshipInvites", "Fellowships", "MiniQuests", "MiniQuestsCompleted", "TakenAlignmentRewards", "AdminHideMap", "Chosen35Align", "ConquestKills", "HideAlignment", "HideOnMap", "HiredDeathMessages", "LastBiome", "MiniQuestTrack", "MQCompleteCount", "MQCompletedBounties", "Pre35Align", "ShowHiddenSWP", "StructuresBanned", "ChatBoundFellowship", "DeathDim");
        originalData.replace("AlignmentMap", new ListTag<>("AlignmentMap", TagType.TAG_COMPOUND, AlignmentMap_builder));
        originalData.replace("FactionStats", new ListTag<>("FactionStats", TagType.TAG_COMPOUND, FactionStats_builder));
        originalData.replace("PrevRegionFactions", new ListTag<>("PrevRegionFactions", TagType.TAG_COMPOUND, PrevRegionFactions_builder));
        originalData.replace("UnlockedFTRegions", new ListTag<>("UnlockedFTRegions", TagType.TAG_COMPOUND, UnlockedFTRegions_Builder));
        originalData.replace("WPUses", new ListTag<>("WPUses", TagType.TAG_COMPOUND, WPUses_builder));
        originalData.replace("CurrentFaction", new StringTag("CurrentFaction", DATA.facNames.getOrDefault(originalData.get("CurrentFaction")
                                                                                                                  .getValue()
                                                                                                                  .toString(), "lotr:hobbit")));

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

    /**
     * Fixes tile/block entities
     *
     * @param map         {@link CompoundMap} containing the old TileEntity tag data
     * @return {@link Optional} of type {@link CompoundMap} with fixed data if present, empty otherwise
     */
    @SuppressWarnings("unchecked")
    public TileEntityResult blockEntityFixer(CompoundMap map) throws IOException {
        //This will come in handy, it might be outdated though so testing will bre required:
        //https://lotrminecraftmod.fandom.com/wiki/NBT-data/Blocks_and_TileEntities
        //https://lotr-minecraft-mod-exiles.fandom.com/wiki/Block_entity_NBT_format
        //https://minecraft.fandom.com/wiki/Chunk_format#Block_entity_format

        /*
        In time this will also have to communicate with EntityFixer and sectionMapFixer as some things changed namely:
        - Flower pots are no longer entities, instead everything that can be put into a flower pot has the placed id ...:potted_...
        - Cauldrons are also no longer tile/block entities
        - Chests and Trapped chests now have their own tile entity, there should be a way to determine which one needs to be picked. Tile entity coordinates are stored in their in game coordinates, not per chunk so that'll be interesting to fix
        - Lotr Armor stands have become vanilla armour stands. Should be an easy fix from tile entity to regular entity, except you need to know the direction so sectionMapFixer will have to get involved as well
         */
        Optional<CompoundMap> RegularResult = Optional.empty();
        TileEntityFixerReturnType Type = TileEntityFixerReturnType.Regular;
        Optional<Tag<?>> Oid = Util.getAsTagIfExists(map, TagType.TAG_STRING, "id");
        if (Oid.isPresent()) {
            String id = (String) Oid.get().getValue();
            if (DATA.blockEntityMappings.containsKey(id)) {
                String value = DATA.blockEntityMappings.get(id);
                if (!Objects.equals(value, "")) {
                    map.replace("id", new StringTag("id", value));
                    //case switch for ids here
                    switch (value) {
                        case "minecraft:hopper": {
                            Optional<Tag<?>> OItemList = Util.getAsTagIfExists(map, TagType.TAG_LIST, "Items");
                            if (OItemList.isPresent()) {
                                map.replace("Items", new ListTag<>("Items", TagType.TAG_COMPOUND, recurItemFixerList(((ListTag<CompoundTag>) OItemList.get()).getValue(), 0, "Exception during hopper item content fix")));
                            }
                            break;
                        }
                        case "minecraft:dispenser": {
                            Optional<Tag<?>> OItemList = Util.getAsTagIfExists(map, TagType.TAG_LIST, "Items");
                            if (OItemList.isPresent()) {
                                map.replace("Items", new ListTag<>("Items", TagType.TAG_COMPOUND, recurItemFixerList(((ListTag<CompoundTag>) OItemList.get()).getValue(), 0, "Exception during dispenser item content fix")));
                            }
                            break;
                        }
                        case "minecraft:dropper": {
                            Optional<Tag<?>> OItemList = Util.getAsTagIfExists(map, TagType.TAG_LIST, "Items");
                            if (OItemList.isPresent()) {
                                map.replace("Items", new ListTag<>("Items", TagType.TAG_COMPOUND, recurItemFixerList(((ListTag<CompoundTag>) OItemList.get()).getValue(), 0, "Exception during dropper item content fix")));
                            }
                            break;
                        }
                        case "minecraft:chest": {
                            Optional<Tag<?>> OItemList = Util.getAsTagIfExists(map, TagType.TAG_LIST, "Items");
                            if (OItemList.isPresent()) {
                                map.replace("Items", new ListTag<>("Items", TagType.TAG_COMPOUND, recurItemFixerList(((ListTag<CompoundTag>) OItemList.get()).getValue(), 0, "Exception during chest/trapped chest item content fix")));
                            }
                            Type = TileEntityFixerReturnType.ChestOrTrappedChest;
                            break;
                            //TODO: check for regular or trapped chest as these were split
                        }
                        case "lotr:keg": {
                            //TODO: Look up proper name
                            if (map.containsKey("BrewingTime")) {
                                (map.get("BrewingTime")
                                        .getAsIntTag()).ifPresent(intTag -> map.put(new IntTag("BrewingTimeTotal", (intTag).getValue())));
                            }
                            if (map.containsKey("BarrelMode")) {
                                (map.get("BarrelMode")
                                        .getAsByteTag()).ifPresent(byteTag -> map.replace("BarrelMode", new ByteTag("KegMode", ((byteTag)).getValue())));
                            }
                            Optional<Tag<?>> OItemList = Util.getAsTagIfExists(map, TagType.TAG_LIST, "Items");
                            if (OItemList.isPresent()) {
                                map.replace("Items", new ListTag<>("Items", TagType.TAG_COMPOUND, recurItemFixerList(((ListTag<CompoundTag>) OItemList.get()).getValue(), 0, "Exception during chest/trapped chest item content fix")));
                            }
                            break;
                        }
                        case "lotr:gondor_beacon": {
                            if (map.containsKey("IsLit")) {
                                (map.get("IsLit")
                                        .getAsByteTag()).ifPresent(byteTag -> map.put(new ByteTag("IsBurning", (byteTag).getValue())));
                            } else map.put(new ByteTag("IsBurning", (byte) 0));
                            break;
                        }
                        case "lotr:plate": {
                            if (map.containsKey("FoodItem")) {
                                (map.get("FoodItem")
                                        .getAsCompoundTag()).flatMap(compoundTag -> (recurItemFixer(compoundTag, 0, "Exception during plate item fix")))
                                        .ifPresent(tags -> map.replace("FoodItem", new CompoundTag("FoodItem", tags)));
                            } else map.put(new CompoundTag("FoodItem", new CompoundMap()));
                            map.remove("PlateEmpty");
                            break;
                        }
                        case "minecraft:furnace": {
                            alloyFixer(map);
                            map.put(new CompoundTag("RecipesUsed", new CompoundMap()));
                            break;
                        }
                        case "lotr:dwarven_forge":
                        case "lotr:hobbit_over":
                        case "lotr:elven_forge":
                        case "lotr:alloy_forge":
                        case "lotr:orc_forge": {
                            alloyFixer(map);
                            break;
                        }
                        case "lotr:ent_jar": {
                            Type = TileEntityFixerReturnType.Ent_Jar;
                        }
                        case "minecraft:sign": {
                            map.put(new StringTag("Color", "black"));
                            for (int i = 1; i < 5; i++) {
                                if (map.containsKey("Text" + i)) {
                                    Optional<StringTag> TextI = map.get("Text" + i).getAsStringTag();
                                    if (TextI.isPresent()) {
                                        map.replace("Text" + i, new StringTag("Text" + i, JSONTextFixer(TextI.get()
                                                                                                                .getValue())));
                                    }
                                }
                            }
                            break;
                        }
                        case "minecraft:mob_spawner": {
                            //Format changed to containing a full entity instead of just the id
                            //https://minecraft.fandom.com/wiki/Spawner#Block_data
                            //TODO: generate a full on entity to use
                            return new TileEntityResult(Optional.empty(), TileEntityFixerReturnType.Null);
                        }
                        case "minecraft:command_block": {
                            //Note: this just carries over the old command, command syntax changes are not within the scope of this converter
                            map.put(new ByteTag("auto", (byte) 0));
                            map.put(new ByteTag("ConditionMet", (byte) 1));
                            map.put(new LongTag("LastExecution", 0L));
                            map.put(new ByteTag("powered", (byte) 0));
                            map.put(new StringTag("LastOutput", "Command block converted, please check if command is still valid")); //TODO: check format to see if this is correct
                            map.put(new ByteTag("UpdateLastExecution", (byte) 1));
                            break;
                        }
                        case "lotr:vessel_drink": {
                            if (map.containsKey("MugItem")) {
                                (map.get("MugItem")
                                        .getAsCompoundTag()).flatMap(compoundTag -> (recurItemFixer(compoundTag, 0, "Exception during mug item fix")))
                                        .ifPresent(tags -> map.replace("MugItem", new CompoundTag("DrinkItem", tags)));

                            } else map.put(new CompoundTag("DrinkItem", new CompoundMap()));
                            map.remove("HasMugItem");
                            Optional<Tag<?>> OVessel = Util.getAsTagIfExists(map, TagType.TAG_BYTE, "Vessel");
                            if (OVessel.isPresent()) {
                                switch ((((ByteTag) OVessel.get()).getValue())) {
                                    case 0: {
                                        map.replace("Vessel", new StringTag("Vessel", "wooden_mug"));
                                    }
                                    case 1: {
                                        map.replace("Vessel", new StringTag("Vessel", "ceramic_mug"));
                                    }
                                    case 2: {
                                        map.replace("Vessel", new StringTag("Vessel", "golden_goblet"));
                                    }
                                    case 3: {
                                        map.replace("Vessel", new StringTag("Vessel", "silver_goblet"));
                                    }
                                    case 4: {
                                        map.replace("Vessel", new StringTag("Vessel", "copper_goblet"));
                                    }
                                    case 5: {
                                        map.replace("Vessel", new StringTag("Vessel", "wooden_cup"));
                                    }
                                    case 6: {
                                        map.replace("Vessel", new StringTag("Vessel", "wooden_mug")); //should be skull cup
                                    }
                                    case 7: {
                                        map.replace("Vessel", new StringTag("Vessel", "bottle")); //Should be wine glass
                                    }
                                    case 8: {
                                        map.replace("Vessel", new StringTag("Vessel", "bottle"));
                                    }
                                    case 9: {
                                        map.replace("Vessel", new StringTag("Vessel", "waterskin"));
                                    }
                                    case 10: {
                                        map.replace("Vessel", new StringTag("Vessel", "ale_horn"));
                                    }
                                    case 11: {
                                        map.replace("Vessel", new StringTag("Vessel", "golden_ale_horn"));
                                    }
                                }
                            } else map.put(new StringTag("Vessel", "wooden_mug"));
                            break;
                        }
                    }
                    //Needed for validation apparently
                    map.put(new ByteTag("keepPacked", (byte) 0));
                    RegularResult = Optional.of(map);
                } else if (Objects.equals(id, "Music")) {
                    Type = TileEntityFixerReturnType.Note_Block;
                } else if (Objects.equals(id, "Cauldron")) {
                    Type = TileEntityFixerReturnType.Cauldron;
                } else if (Objects.equals(id, "LOTRArmorStand")) {
                    Type = TileEntityFixerReturnType.Armour_Stand;
                } else if (Objects.equals(id, "FlowerPot") || Objects.equals(id, "LOTRFlowerPot")) {
                    Type = TileEntityFixerReturnType.Flower_Pot;
                } else {
                    //Unhandled items:
                    /*
                    UtumnoPortal
                     */

                    STRINGCACHE.printLine("unknown tile entity found with id: " + id);
                }
            } else {
                STRINGCACHE.printLine("No block entity id found for old id: " + Oid.get(), false);
            }
        }
        return new TileEntityResult(RegularResult, Type);
    }

    /**
     * Fixes the Block/Tile entity of Lotr mod forges and the vanilla furnace (partially)
     *
     * @param map         {@link CompoundMap} to be fixed
     * @throws IOException when something fails
     */
    @SuppressWarnings("unchecked")
    public void alloyFixer(CompoundMap map) throws IOException {
        Optional<Tag<?>> OItemList = Util.getAsTagIfExists(map, TagType.TAG_LIST, "Items");
        if (OItemList.isPresent()) {
            map.replace("Items", new ListTag<>("Items", TagType.TAG_COMPOUND, recurItemFixerList(((ListTag<CompoundTag>) OItemList.get()).getValue(), 0, "Exception during furnace/forge fixing")));
        }
        if (map.containsKey("BurnTime")) {
            (map.get("BurnTime")
                    .getAsShortTag()).ifPresent(shortTag -> map.replace("BurnTime", new IntTag("BurnTime", shortTag.getValue())));
        } else map.put("BurnTime", new IntTag("BurnTime", 0));
        Optional<Tag<?>> OSmeltTime = Util.getAsTagIfExists(map, TagType.TAG_SHORT, "SmeltTime");
        if (OSmeltTime.isPresent()) {
            map.replace("SmeltTime", new IntTag("CookTime", ((Short) OSmeltTime.get().getValue()).intValue()));
            map.put(new IntTag("CookTimeTotal", ((Short) OSmeltTime.get().getValue()).intValue()));
        } else {
            map.put(new IntTag("CookTime", 0));
            map.put(new IntTag("CookTimeTotal", 0));
        }
        map.put(new ShortTag("RecipesUsedSize", (short) 0));
    }

    /**
     * Creates the basic structure for json text
     *
     * @param input {@link String}
     * @return fixed {@link String}
     */
    public String JSONTextFixer(String input) {
        //TODO: check for paragraph symbols to fix colours and such
        return ("{" + '"' + "text" + '"' + ':' + '"' + input + '"' + '}');
    }

    /**
     * Fixes the "Sections" of a chunk by calling the appropriate functions
     *
     * @param Sections    {@link List} of type {@link CompoundTag} containing the sections
     */
    public void sectionMapFixer(List<CompoundTag> Sections, List<TileEntityResult> EdgeCases) {
        /*
I created a flat-world with the 'the void' preset.
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

        /*
        Splits up the EdgeCases into a HashMap, so I won't have to loop through them all for every section
         */
        /*
        HashMap<Byte, List<TileEntityResult>> Sorter = new HashMap<>();
        for (TileEntityResult EdgeCase : EdgeCases) {
            if (EdgeCase.getContent().isPresent()) {
                CompoundMap EdgeCaseContent = EdgeCase.getContent().get();
                if (EdgeCaseContent.containsKey("y")) {
                    Optional<IntTag> OYCoordinate = EdgeCaseContent.get("y").getAsIntTag();
                    if (OYCoordinate.isPresent()) {
                        byte y = Util.sectionHeight(OYCoordinate.get().getValue());
                        if (Sorter.containsKey(y)) {
                            //Sorter.put(Util.sectionHeight(y), Collections.singletonList(EdgeCase));
                        } else {
                            //TODO: fix
                            //Sorter.get(y).add(EdgeCase);
                        }
                    }
                }
            }
        }

         */
        for (int i = 0; i < Sections.size(); i++) {
            CompoundMap SectionCompoundMap = Sections.get(i).getValue();
            List<CompoundTag> PaletteBuilderList = new ArrayList<>();

            //Apparently air is always in the palette, or once it's in it never leaves, I don't know yet
            PaletteBuilderList.add(new CompoundTag("", Util.createCompoundMapWithContents(new StringTag("Name", "minecraft:air"))));

            //used for making sure no identical palette entries exist
            List<String> PaletteCheckerList = new ArrayList<>(Collections.singleton("BlockMapping{name='minecraft:air', properties=null}"));

            Optional<ByteArrayTag> OBlocksByteArray = SectionCompoundMap.get("Blocks").getAsByteArrayTag();
            Optional<ByteArrayTag> ODataByteArray = SectionCompoundMap.get("Data").getAsByteArrayTag();
            Optional<ByteArrayTag> OAddByteArray;
            if (SectionCompoundMap.containsKey("Add")) {
                OAddByteArray =SectionCompoundMap.get("Add").getAsByteArrayTag();
            }
            else {
                OAddByteArray = Optional.empty();
            }

            if (SectionCompoundMap.containsKey("Y")) {
                Optional<ByteTag> OY = SectionCompoundMap.get("Y").getAsByteTag();
                if (OY.isPresent()) {

                    if (OBlocksByteArray.isPresent() && ODataByteArray.isPresent()) {
                        byte[] BlocksByteArray = OBlocksByteArray.get().getValue();
                        byte[] DataByteArray = ODataByteArray.get().getValue();
                        byte[] AddByteArray = OAddByteArray.isPresent() ? OAddByteArray.get()
                                .getValue() : new byte[2048];
                        //initializes with 0 as default value, as air is always the first entry, nothing needs to happen with air
                        int[] BlockPaletteReferences = new int[4096];
                        //this should never fail as far as I know, purely redundancy
                        if (BlocksByteArray.length == 4096 && DataByteArray.length == 2048) {
                            //to loop through both lists at once.
                            /*

                            Optional<List<TileEntityResult>> OPerSectionEdgeCases;
                            if (Sorter.containsKey(OY.get().getValue())) {
                                OPerSectionEdgeCases = Optional.ofNullable(Sorter.get(OY.get().getValue()));
                            } else OPerSectionEdgeCases = Optional.empty();
                             */
                            for (int DataCounter = 0; DataCounter < 4096; DataCounter++) {

                                int blockId = Util.combine(BlocksByteArray[DataCounter], Util.nibble4(AddByteArray,DataCounter));

                                if (DATA.legacyIds.containsKey(blockId)) {
                                    String legacyId = DATA.legacyIds.get(blockId);
                                    //TODO: Put in the extra information of the Edge cases

                                    /*

                                    if (OPerSectionEdgeCases.isPresent()) {

                                        //Coordinate should simply be coordinates % 16, then apply (y * 16 + z) * 16 + x) for the position in the array

                                        List<TileEntityResult> perSectionEdgeCases = OPerSectionEdgeCases.get();
                                        byte[] positions = new byte[perSectionEdgeCases.size()];
                                        TileEntityFixerReturnType[] types = new TileEntityFixerReturnType[perSectionEdgeCases.size()];
                                        List<Optional<CompoundMap>> contentList = new ArrayList<>();
                                        for (int j = 0; j < perSectionEdgeCases.size(); j++) {
                                            //compoundMap annoyance, use Coordinate for easy access
                                            //positions[j] =
                                            Optional<CompoundMap> OContentMap = perSectionEdgeCases.get(j).getContent();
                                            if (OContentMap.isPresent()) {
                                                CompoundMap contentMap = OContentMap.get();
                                                types[j] = perSectionEdgeCases.get(j).getType();
                                                contentList.add(OContentMap);
                                                if (contentMap.containsKey("x") && contentMap.containsKey("y") && contentMap.containsKey("z")) {
                                                    //necessary checks for making a Coordinate
                                                    //TODO: get the block array position, figure out how to replace a block with the proper data
                                                }
                                            }

                                        }

                                    }
                                     */
                                    if (DATA.blockMappings.containsKey(legacyId)) {
                                        byte neededValue = Util.nibble4(DataByteArray,DataCounter);
                                        BlockPaletteReferences[DataCounter] = addPaletteEntryIfNecessary(DATA.blockMappings.get(legacyId), PaletteCheckerList, PaletteBuilderList, neededValue);
                                    }
                                }
                            }
                            //}

                            ListTag<CompoundTag> Palette = new ListTag<>("Palette", TagType.TAG_COMPOUND, PaletteBuilderList);

                            SectionCompoundMap.remove("Blocks");
                            SectionCompoundMap.remove("Data");
                            SectionCompoundMap.remove("Add");
                            SectionCompoundMap.put(Palette);
                            SectionCompoundMap.put(new LongArrayTag("BlockStates", blockStateGenerator(PaletteCheckerList, BlockPaletteReferences)));
                            Sections.set(i, new CompoundTag("", SectionCompoundMap));

                        } else {
                            STRINGCACHE.printLine("Invalid section format!", false);
                        }
                    }
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
    public long[] blockStateGenerator(List<String> PaletteCheckerList, int[] PaletteReferences) {
        long[] BlockStates;
        //Should always be true due to where we call it, just making sure
        if (PaletteReferences.length == 4096) {
            //How many bits do you need to store the data of 1 block, minimum is 4
            int bitsPerIndex = bitsPerIndex(PaletteCheckerList.size());
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
                BlockStates[ExternalLongPosition] = blockStateLongUpdater(BlockStates[ExternalLongPosition], PaletteReferences[i], bitsPerIndex, InternalLongPosition);
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
    public long blockStateLongUpdater(long Base, int Value, int BPI, int InternalBlockPosition) {
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
    public int bitsPerIndex(int PaletteCheckerListLength) {
        return Math.max(4, 32 - Integer.numberOfLeadingZeros(PaletteCheckerListLength - 1));
    }

    /**
     * Adds an entry to the Palette if it's necessary (it doesn't exist yet)
     *
     * @param BlockMapping       {@link Map<String>} Mapping of the main id
     * @param PaletteCheckerList {@link List<String>} containing String versions of the Palette entries, used for faster searching
     * @param PaletteBuilderList {@link List<CompoundTag>} containing the Palette entries
     */
    public int addPaletteEntryIfNecessary(Map<String, BlockMapping> BlockMapping, List<String> PaletteCheckerList, List<CompoundTag> PaletteBuilderList, byte neededValue) {
        int returner = 0;
        if (entryExists(BlockMapping, neededValue)) {
            BlockMapping mapping = BlockMapping.get(String.valueOf((neededValue)));
            if (!PaletteCheckerList.contains(mapping.toString())) {
                PaletteCheckerList.add(mapping.toString());
                CompoundMap map = new CompoundMap();
                map.put(new StringTag("Name", mapping.getName()));
                CompoundMap innerCompoundBuilder = new CompoundMap();
                Map<String, ?> properties = mapping.getProperties();
                for (Map.Entry<String, ?> property : properties.entrySet()) {
                    //probably always a string if I read the wiki correctly, just in case I put in the case of a Boolean
                    if (property.getValue() instanceof String)
                        innerCompoundBuilder.put(new StringTag(property.getKey(), (String) property.getValue()));
                    else if (property.getValue() instanceof Boolean)
                        innerCompoundBuilder.put(new ByteTag(property.getKey(), (Boolean) property.getValue()));
                }
                if (!innerCompoundBuilder.isEmpty()) {
                    map.put(new CompoundTag("Properties", innerCompoundBuilder));
                }
                PaletteBuilderList.add(new CompoundTag("Palette", map));
            }
            returner = PaletteCheckerList.indexOf(mapping.toString());
        }
        return returner;
    }

    public boolean entryExists(Map<String, BlockMapping> Mapping, byte neededValue) {
        return (Mapping.containsKey(String.valueOf(neededValue)));
    }


    /**
     * Fixes Chunk {@link CompoundMap}
     *
     * @param Chunk {@link CompoundMap} of a chunk
     */
    @SuppressWarnings("unchecked")
    public void chunkFixer(CompoundMap Chunk) throws IOException {
        Optional<Tag<?>> OLevel = Util.getAsTagIfExists(Chunk, TagType.TAG_COMPOUND, "Level");
        if (OLevel.isPresent()) {
            CompoundMap level = (CompoundMap) OLevel.get().getValue();
            //Will regenerate the biomes automatically, might cause some weird things with older worlds, but it isn't a priority right now
            level.remove("Biomes");
            //I don't know what this does, it isn't present in newer versions I know that for sure
            level.remove("V");
            //Removed until I know exactly what it does (I don't know the modern format, again, not a priority)
            level.remove("TileTicks");
            level.remove("LightPopulated");
            level.remove("TerrainPopulated");
            //Will hopefully regenerate, easily testable by just removing them from a new world via NBTExplorer, I'm just too lazy to do it.
            level.remove("Heightmap");

            //Used for retaining data that changed format e.i. lotr armour stands (tile-entities) to vanilla armour stands (regular entities) but also flower pots and cauldrons (no longer tile entities, instead stored data in palette)
            List<TileEntityResult> EdgeCases = new ArrayList<>();
        /*
        In time this will also have to communicate with EntityFixer and sectionMapFixer as some things changed namely:
        - Flower pots are no longer block entities, instead everything that can be put into a flower pot has the placed id ...:potted_...
        So, first Tile EntityFixer, then SectionFixer
        - Cauldrons are also no longer tile/block entities
        Same
        - Chests and Trapped chests now have their own tile entity, there should be a way to determine which one needs to be picked. Tile entity coordinates are stored in their in game coordinates, not per chunk so that'll be interesting to fix
        requires blockFixer, then blockEntityFixer, yikes
        - Lotr Armor stands have become vanilla armour stands. Should be an easy fix from tile entity to regular entity, except you need to know the direction so sectionMapFixer will have to get involved as well
        first tileEntityFixer, then SectionFixer, then entityFixer, then special separate check for chests and trapped chests and update blockEntities accordingly
         */
            Optional<Tag<?>> OBlockEntities = Util.getAsTagIfExists(level, TagType.TAG_LIST, "TileEntities");
            if (OBlockEntities.isPresent()) {
                ListTag<CompoundTag> TileEntities = (ListTag<CompoundTag>) OBlockEntities.get();
                List<CompoundTag> TileEntityBuilder = new ArrayList<>();

                for (CompoundTag t : TileEntities.getValue()) {
                    TileEntityResult result = blockEntityFixer(t.getValue());
                    if (result.getContent().isPresent()) {
                        switch (result.getType()) {
                            case Regular: {
                                TileEntityBuilder.add(new CompoundTag("", result.getContent().get()));
                                break;
                            }
                            case Armour_Stand:
                            case Cauldron:
                            case Ent_Jar:
                            case Flower_Pot:
                            case Note_Block: {
                                EdgeCases.add(result);
                                break;
                            }
                            case ChestOrTrappedChest: {
                                TileEntityBuilder.add(new CompoundTag("", result.getContent().get()));
                                CompoundMap ChestOrTrappedChest = result.getContent().get();
                                //Computationally less heavy to do this than to pass the items (I hope)
                                ChestOrTrappedChest.remove("Items");
                                EdgeCases.add(new TileEntityResult(Optional.of(ChestOrTrappedChest), TileEntityFixerReturnType.ChestOrTrappedChest));
                                break;
                            }
                            case Null: {
                                //For unsupported Entities
                                break;
                            }
                        }
                    }
                }
                level.replace("TileEntities", new ListTag<>("TileEntities", TagType.TAG_COMPOUND, TileEntityBuilder));
            }
            Optional<Tag<?>> OSections = Util.getAsTagIfExists(level, TagType.TAG_LIST, "Sections");
            if (OSections.isPresent()) {
                ListTag<CompoundTag> SectionsTag = (ListTag<CompoundTag>) OSections.get();
                List<CompoundTag> Sections = SectionsTag.getValue();
                sectionMapFixer(Sections, EdgeCases);
                level.replace("Sections", new ListTag<>("Sections", TagType.TAG_COMPOUND, Sections));
            }
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
            level.remove("Entities");
            //Needed for the game to try to use the changed data, otherwise it'll regenerate the chunks
            level.put(new StringTag("Status", "full"));
            Chunk.replace("Level", new CompoundTag("Level", level));
            //This value triggers Mojang's own DataFixers so use with caution
            Chunk.put(new IntTag("DataVersion", 2586));
        }
    }

    /**
     * Fixes Regions
     *
     * @param Chunks {@link HashMap} with key Position and Value Chunk
     * @return {@link HashMap} with the fixed chunks
     */
    public HashMap<Integer, Chunk> regionFixer(HashMap<Integer, Chunk> Chunks) throws IOException {
        for (Map.Entry<Integer, Chunk> entry : Chunks.entrySet()) {
            Chunk chunk = entry.getValue();
            CompoundTag tag = chunk.readTag();
            CompoundMap map = tag.getValue();
            chunkFixer(map);
            tag.setValue(map);
            chunk = new Chunk(chunk.x, chunk.z, chunk.timestamp, tag, chunk.getCompression());
            entry.setValue(chunk);
        }
        return Chunks;
    }
}

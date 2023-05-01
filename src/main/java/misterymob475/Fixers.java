package misterymob475;

import de.piegames.nbt.ByteArrayTag;
import de.piegames.nbt.ByteTag;
import de.piegames.nbt.CompoundMap;
import de.piegames.nbt.CompoundTag;
import de.piegames.nbt.DoubleTag;
import de.piegames.nbt.IntArrayTag;
import de.piegames.nbt.IntTag;
import de.piegames.nbt.ListTag;
import de.piegames.nbt.LongArrayTag;
import de.piegames.nbt.LongTag;
import de.piegames.nbt.ShortTag;
import de.piegames.nbt.StringTag;
import de.piegames.nbt.Tag;
import de.piegames.nbt.TagType;
import de.piegames.nbt.regionfile.Chunk;
import misterymob475.data.Conversions;
import misterymob475.data.Conversions.BlockMapping;
import misterymob475.data.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

/**
 * Functionally static class containing all the fixes that are applied*.
 * <p>
 * * (Except for fixing idcount as that requires file interaction which is explicitly not handled in this class)
 */
public class Fixers {
    private static final Data data = Data.getInstance();
    private static final StringCache stringCache = StringCache.getInstance();

    public Fixers() {
    }

    /**
     * Fixes entities
     *
     * @param Entity    Map with key String and value Tag (.getValue() of CompoundTag)
     * @param Important Bool stating if mob should be removed if in Utumno
     * @return Fixed Map
     */
    @SuppressWarnings("unchecked")
    public Optional<CompoundMap> EntityFixer(CompoundMap Entity, Boolean Important) throws IOException {
        //Temporary measure to prevent crashes as this is still unfinished atm
        //Determines the actual mob
        if (Entity.containsKey("id")) {
            if (data.entities.containsKey((String) (Entity.get("id").getValue()))) {
                if (!data.entities.get((String) (Entity.get("id").getValue())).equals("")) {
                    //code for split types here (horses mainly, I'm not gonna bother with zombie villagers)
                    if (data.entities.get((String) (Entity.get("id").getValue())).equals("minecraft:horse")) {
                        if (Entity.get("Type").getValue().equals((byte) 1)) {
                            Entity.replace("id", new StringTag("id", "minecraft:donkey"));
                            Entity.remove("Variant");
                        }
                        else if (Entity.get("Type").getValue().equals((byte) 2)) {
                            Entity.replace("id", new StringTag("id", "minecraft:mule"));
                            Entity.remove("Variant");
                        }
                        else if (Entity.get("Type").getValue().equals((byte) 3)) {
                            Entity.replace("id", new StringTag("id", "minecraft:zombie_horse"));
                            Entity.remove("Variant");
                        }
                        else if (Entity.get("Type").getValue().equals((byte) 1)) {
                            Entity.replace("id", new StringTag("id", "minecraft:skeleton_horse"));
                            Entity.remove("Variant");
                        }
                        else Entity.replace("id", new StringTag("id", "minecraft:horse"));
                    }

                    //code for turning camels into donkeys (to keep the storage)
                    else if (Entity.get("id").getValue().equals("lotr.Camel")) {
                        Entity.remove("Type");
                        Entity.replace("id", new StringTag("id", "minecraft:donkey"));
                    }
                    else if (Entity.get("id").getValue().equals("lotr.Horse")) {
                        //this is temporary, when blocks work I'm gonna finish this function, there's still lotr-related stuff missing
                        Entity.remove("Type");
                        Entity.replace("id", new StringTag("id", "minecraft:donkey"));
                    }
                    else {
                        Entity.replace("id", new StringTag("id", data.entities.get((String) (Entity.get("id")
                                .getValue()))));
                    }
                    Entity.remove("Type");
                }
                else stringCache.printLine("No mapping found for Entity: " + Entity.get("id")
                        .getValue() + " - It probably hasn't been ported yet", false);
            }
            else {
                stringCache.printLine("No mapping found for Entity: " + Entity.get("id").getValue(), false);
                return Optional.empty();
            }
        }
        else return Optional.empty();
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
                }
                else return Optional.empty();
            }
            else newDimension = "minecraft:overworld";
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

        /*
        I've had enough of this for know

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
     * @param Entity {@link CompoundMap} containing entity data
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
        }
        else return Optional.empty();
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
     * @param data {@link Map} with key {@link String} and value {@link Tag} containing the to be fixed data
     * @throws IOException if something fails
     */
    @SuppressWarnings("unchecked")
    public void playerFixer(CompoundMap data) throws IOException {
        boolean inUtumno = false;
        //not needed in renewed
        data.remove("ForgeData");
        //changed too much to bother with, especially as the game will recreate the property
        data.remove("Attributes");

        Optional<Tag<?>> ORiding = Util.getAsTagIfExists(data, TagType.TAG_COMPOUND, "Riding");
        if (ORiding.isPresent()) {
            //call to entity fixer, this means the player is riding on a mount (fixer will temporarily replace said mount with a donkey)
            Optional<CompoundMap> Riding = riderEntityFixer((CompoundMap) ORiding.get().getValue());
            if (Riding.isPresent()) {
                CompoundTag RootVehicle = new CompoundTag("RootVehicle", Riding.get());
                data.replace("Riding", RootVehicle);
            }
            else data.remove("Riding");
        }

        if (Fixers.data.settings.isCreativeSpawn()) {
            data.replace("playerGameType", new IntTag("playerGameType", 1));
        }

        if (data.containsKey("EnderItems")) {
            data.replace("EnderItems", new ListTag<>("EnderItems", TagType.TAG_COMPOUND, recurItemFixerList((((ListTag<CompoundTag>) data.get("EnderItems")).getValue()), 0, "Exception during Ender chest conversion")));
        }
        if (data.containsKey("Inventory")) {
            data.replace("Inventory", new ListTag<>("Inventory", TagType.TAG_COMPOUND, recurItemFixerList((((ListTag<CompoundTag>) data.get("Inventory")).getValue()), 0, "Exception during inventory conversion")));
        }

        data.remove("Attack Time");
        if (!data.containsKey("DataVersion")) {
            data.put("DataVersion", new IntTag("DataVersion", 2586));
        }

        Optional<Tag<?>> ODimension = Util.getAsTagIfExists(data, TagType.TAG_INT, "Dimension");
        if (ODimension.isPresent()) {
            int dimension = ((IntTag) ODimension.get()).getValue();
            String newDimension;
            switch (dimension) {
                case 1: {
                    newDimension = "Minecraft:the_nether";
                    break;
                }
                case -1: {
                    newDimension = "Minecraft:the_end";
                    break;
                }
                case 100: {
                    newDimension = "lotr:middle_earth";
                    break;
                }
                case 101: {
                    //utumno doesn't exist yet
                    newDimension = "lotr:middle_earth";
                    inUtumno = true;
                    break;
                }
                default: {
                    newDimension = "minecraft:overworld";
                    break;
                }
            }
            data.replace("Dimension", new StringTag("Dimension", newDimension));
        }

        if (inUtumno) {
            //sets the player coordinates at the coordinates of the pit if they're currently in Utumno (roughly, they'll be moved in renewed I've heard)
            List<DoubleTag> pos = new ArrayList<DoubleTag>(1) {
            };
            pos.add(new DoubleTag("", 46158.0));
            pos.add(new DoubleTag("", 80.0));
            pos.add(new DoubleTag("", -40274.0));
            data.replace("Pos", new ListTag<>("Pos", TagType.TAG_DOUBLE, pos));

        }
        data.remove("HealF");
        data.remove("Sleeping");
        if (data.containsKey("UUIDLeast")) {
            Optional<LongTag> OUUIDMost = data.get("UUIDMost").getAsLongTag();
            Optional<LongTag> OUUIDLeast = data.get("UUIDLeast").getAsLongTag();
            if (OUUIDMost.isPresent() && OUUIDLeast.isPresent()) {
                data.put("UUID", uuidFixer(OUUIDMost.get(), OUUIDLeast.get()));
            }
            data.remove("UUIDLeast");
            data.remove("UUIDMost");
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
                if (data.colours.containsKey(name.substring(0, 2))) {
                    colour = "," + '"' + "color" + '"' + ':' + '"' + data.colours.get(name.substring(0, 2)) + '"';
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
            if (depth++ < data.settings.getItemRecursionDepth()) {
                for (CompoundTag itemCompoundTag : itemList) {
                    (recurItemFixer(itemCompoundTag, depth, exceptionMessage)).ifPresent(tags -> itemListBuilder.add(new CompoundTag("", tags)));
                }
            }
            else {
                //if this actually gets triggered someone has been annoying on purpose, and you're dealing with an old worlds as triggering this is only possible in older versions of the lotr mod
                stringCache.printLine("Maximum set recursion depth reached (default = 7, defined in JSON)", false);
            }
            return itemListBuilder;
        }
        catch (final ClassCastException | NullPointerException ex) {
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
            if (depth++ < data.settings.getItemRecursionDepth()) {
                CompoundMap itemCompoundMap = itemCompoundTag.getValue();
                if (!(itemCompoundMap).isEmpty()) {
                    Optional<ShortTag> OShortIDTag = itemCompoundMap.get("id").getAsShortTag();
                    Optional<StringTag> OStringIDTag = itemCompoundMap.get("id").getAsStringTag();
                    Optional<String> OStringID = Optional.empty();
                    if (OShortIDTag.isPresent()) {
                        int idValue = OShortIDTag.get().getValue();
                        if (data.legacyIds.containsKey(idValue)) {
                            OStringID = Optional.of(data.legacyIds.get(idValue));
                        }
                        else {
                            //this should never happen as I gather these ids dynamically
                            stringCache.printLine("No string id found for id: " + idValue);
                        }
                    }
                    else if (OStringIDTag.isPresent()) {
                        //String id found instead of short id (apparently possible as I found my old world had this)
                        OStringID = Optional.of(OStringIDTag.get().getValue());
                    }

                    if (OStringID.isPresent()) {
                        String StringID = OStringID.get();
                        boolean save = true;

                        if (data.itemNames.containsKey(StringID)) {
                            List<String> item = data.itemNames.get(StringID);
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
                                    }
                                    else {
                                        OPouchColor = Optional.empty();
                                    }

                                    if (filler.containsKey("LOTRPouchData") || OPouchColor.isPresent()) {
                                        CompoundMap LOTRPouchData;
                                        if (filler.containsKey("LOTRPouchData")) {
                                            Optional<CompoundTag> OLOTRPouchData = filler.get("LOTRPouchData")
                                                    .getAsCompoundTag();
                                            if (OLOTRPouchData.isPresent()) {
                                                LOTRPouchData = OLOTRPouchData.get().getValue();
                                            }
                                            else LOTRPouchData = new CompoundMap();
                                        }
                                        else {
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
                            }
                            else if (item.size() == 1) {

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
                                                    if (data.authorBlacklist.contains((String) filler.get("author")
                                                            .getValue())) {
                                                        save = false;
                                                    }
                                                }
                                                if (filler.containsKey("title")) {
                                                    if (data.titleBlacklist.contains((String) filler.get("title")
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
                                                        ench.replace("id", new StringTag("id", data.enchantments.get(OID.get()
                                                                                                                             .getValue()
                                                                                                                             .toString())));
                                                        ench_filler.add(new CompoundTag("", ench));
                                                    }
                                                }
                                                filler.replace("ench", new ListTag<>("Enchantments", TagType.TAG_COMPOUND, ench_filler));
                                                Optional<Tag<?>> ODamage = Util.getAsTagIfExists(itemCompoundMap, TagType.TAG_SHORT, "Damage");
                                                if (ODamage.isPresent()) {
                                                    filler.put("Damage", (new IntTag("Damage", ((ShortTag) ODamage.get()).getValue())));
                                                }
                                                else
                                                    stringCache.printLine("No damage tag found or damage is not a short");
                                            }
                                            else {
                                                //enchanted books
                                                for (CompoundTag ench_t : ((ListTag<CompoundTag>) filler.get("StoredEnchantments")).getValue()) {
                                                    CompoundMap ench = new CompoundMap((ench_t.getValue()));
                                                    Optional<Tag<?>> OID = Util.getAsTagIfExists(ench, TagType.TAG_SHORT, "id");
                                                    if (OID.isPresent()) {
                                                        ench.replace("id", new StringTag("id", data.enchantments.get(OID.get()
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
                                        if (data.potions.containsKey(itemCompoundMap.get("Damage").getValue()
                                                                             .toString())) {
                                            Conversions.Potion potion = data.potions.get(itemCompoundMap.get("Damage")
                                                                                                 .getValue()
                                                                                                 .toString());
                                            filler.put("Potion", new StringTag("Potion", potion.getName()));
                                            if (potion.isSplash())
                                                itemCompoundMap.replace("id", new StringTag("id", "minecraft:splash_potion"));
                                            else itemCompoundMap.replace("id", new StringTag("id", "minecraft:potion"));
                                        }
                                        else itemCompoundMap.replace("id", new StringTag("id", "minecraft:potion"));
                                    }
                                    //map fixer (very simple thankfully)
                                    else if (item.get(0).equals("minecraft:filled_map")) {
                                        if (itemCompoundMap.containsKey("Damage")) {
                                            filler.put("map", new IntTag("map", (int) ((Short) itemCompoundMap.get("Damage")
                                                    .getValue())));
                                        }
                                    }
                                    else {
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
                                    if (data.vanillaMobIds.containsKey(((Short) itemCompoundMap.get("Damage")
                                            .getValue()).toString())) {
                                        itemCompoundMap.replace("id", new StringTag("id", data.vanillaMobIds.get(((Short) itemCompoundMap.get("Damage")
                                                .getValue()).toString())));
                                        itemCompoundMap.remove("Damage");
                                        return Optional.of(itemCompoundMap);
                                    }
                                    else
                                        stringCache.printLine("No vanilla spawn Egg found for Damage value : " + itemCompoundMap.get("Damage")
                                                .getValue(), false);
                                }
                                //lotr spawn egg handler
                                else if (StringID.equals("lotr:item.spawnEgg")) {
                                    //itemFixer
                                    if (itemCompoundMap.containsKey("tag")) {
                                        (itemCompoundMap.get("tag")
                                                .getAsCompoundTag()).ifPresent(compoundTag -> itemCompoundMap.replace("tag", new CompoundTag("tag", baseTagItemFixer((compoundTag.getValue())))));
                                    }
                                    if (data.modMobIds.containsKey(((Short) itemCompoundMap.get("Damage")
                                            .getValue()).toString())) {
                                        itemCompoundMap.replace("id", new StringTag("id", data.modMobIds.get(((Short) itemCompoundMap.get("Damage")
                                                .getValue()).toString())));
                                        itemCompoundMap.remove("Damage");
                                        return Optional.of(itemCompoundMap);
                                    }
                                    else
                                        stringCache.printLine("No lotr mod spawn Egg found for Damage value : " + itemCompoundMap.get("Damage")
                                                .getValue(), false);
                                }
                                else {
                                    stringCache.printLine("No mapping found for legacy id: " + StringID, false);
                                }
                            }
                            else {
                                Optional<Tag<?>> ODamage = Util.getAsTagIfExists(itemCompoundMap, TagType.TAG_SHORT, "Damage");
                                if (ODamage.isPresent()) {
                                    //code for blocks/some items here
                                    Short Damage = (Short) ODamage.get().getValue();
                                    //Check if block is actually in the list and not just a placeholder
                                    if (!data.itemNames.get(StringID).get(Damage).equals("")) {
                                        if (itemCompoundMap.containsKey("tag")) {
                                            (itemCompoundMap.get("tag")
                                                    .getAsCompoundTag()).ifPresent(compoundTag -> itemCompoundMap.replace("tag", new CompoundTag("tag", baseTagItemFixer((compoundTag.getValue())))));
                                        }
                                        itemCompoundMap.remove("Damage");
                                        itemCompoundMap.replace("id", new StringTag("id", item.get(Damage)));
                                        return Optional.of(itemCompoundMap);
                                    }
                                    else {
                                        stringCache.printLine("No mapping found for " + StringID + ":" + Damage, false);
                                    }
                                }
                            }
                        }
                        else {
                            stringCache.printLine("No mapping found for id: " + StringID, false);
                        }
                    }

                }
                else {
                    stringCache.printLine("Empty tag found, skipping", false);
                }
            }
        }
        catch (Exception e) {
            System.out.println(exceptionMessage);
        }
        return Optional.empty();
    }

    /**
     * Function which returns a new IntArrayTag based off the given LongTags and name
     *
     * @param uuidLeast {@link LongTag}
     * @param uuidMost  {@link LongTag}
     * @param name      {@link String} name
     * @return {@link IntArrayTag} with given name and param inputs
     */
    public IntArrayTag uuidFixer(LongTag uuidMost, LongTag uuidLeast, String name) {
        //Creates the UUID in the new format based with name being the name of the intArrayTag
        long v1 = uuidMost.getValue();
        long v2 = uuidLeast.getValue();
        return new IntArrayTag(name, new int[]{(int) (v1 >> 32), (int) v1, (int) (v2 >> 32), (int) v2});
    }

    /**
     * Overload for when name is "UUID"
     *
     * @param uuidLeast {@link LongTag}
     * @param uuidMost  {@link LongTag}
     * @return {@link IntArrayTag} with name "UUID" and param inputs
     */
    public IntArrayTag uuidFixer(LongTag uuidMost, LongTag uuidLeast) {
        return uuidFixer(uuidMost, uuidLeast, "UUID");
    }

    /**
     * Overload for StringTags
     *
     * @param uuidTag {@link StringTag}
     * @param name    String
     * @return {@link IntArrayTag} with name as name and param inputs
     */
    public IntArrayTag uuidFixer(StringTag uuidTag, String name) {
        if (!uuidTag.getValue().equals("")) {
            UUID uuid = UUID.fromString(uuidTag.getValue());
            return uuidFixer(new LongTag("", uuid.getMostSignificantBits()), new LongTag("", uuid.getLeastSignificantBits()), name);
        }
        else
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
                    .getAsCompoundTag())
                    .ifPresent(compoundTag -> filler.replace("display", nameFixer(compoundTag)));
        }

        Util.getAsTagIfExists(filler, TagType.TAG_LIST, "LOTRPreviousOwnerList").ifPresent(tag -> {
            List<StringTag> ownerList = (List<StringTag>) tag.getValue();
            ownerList.replaceAll(stringTag -> new StringTag(stringTag.getName(), JSONTextFixer(stringTag.getValue())));
            filler.put(new CompoundTag("LOTROwnership", Util.createCompoundMapWithContents(new ListTag<>("PreviousOwners", TagType.TAG_STRING, ownerList))));
        });
        return filler;
    }

    /**
     * Creates a {@link CompoundTag} containing the special data for drink items
     *
     * @param damage short storing the damage value determining the type & potency
     * @return {@link CompoundTag} containing the special data for drinks
     */
    public CompoundTag vesselMapItemCreator(short damage) {
        CompoundMap vesselMap = new CompoundMap();

        //Code for determining the strength of the drink
        if ((damage % 10) == 0) vesselMap.put("potency", new StringTag("potency", "weak"));
        else if ((damage % 10) == 1) vesselMap.put("potency", new StringTag("potency", "light"));
        else if ((damage % 10) == 2) vesselMap.put("potency", new StringTag("potency", "moderate"));
        else if ((damage % 10) == 3) vesselMap.put("potency", new StringTag("potency", "string"));
        else if ((damage % 10) == 4) vesselMap.put("potency", new StringTag("potency", "potent"));
        //Code for determining the vessel (wooden mug, goblet etc.)
        if (damage < 100) vesselMap.put("type", new StringTag("type", "wooden_mug"));
        else if (damage < 200) vesselMap.put("type", new StringTag("type", "ceramic_mug"));
        else if (damage < 300) vesselMap.put("type", new StringTag("type", "golden_goblet"));
        else if (damage < 400) vesselMap.put("type", new StringTag("type", "silver_goblet"));
        else if (damage < 500) vesselMap.put("type", new StringTag("type", "copper_goblet"));
        else if (damage < 600) vesselMap.put("type", new StringTag("type", "wooden_cup"));
        else if (damage < 700) vesselMap.put("type", new StringTag("type", "wooden_mug")); //skull cups not in yet
        else if (damage < 800) vesselMap.put("type", new StringTag("type", "bottle")); //wine glasses not in yet
        else if (damage < 900) vesselMap.put("type", new StringTag("type", "bottle"));
        else if (damage < 1000) vesselMap.put("type", new StringTag("type", "waterskin"));
        else if (damage < 1100) vesselMap.put("type", new StringTag("type", "ale_horn"));
        else if (damage < 1200) vesselMap.put("type", new StringTag("type", "golden_ale_horn"));

        return new CompoundTag("vessel", vesselMap);
    }

    /**
     * Fixes maps
     *
     * @param map {@link CompoundMap} with map data
     */
    public void mapFixer(CompoundMap map) {
        Optional<Tag<?>> oDimension = Util.getAsTagIfExists(map, TagType.TAG_INT, "Dimension");
        if (oDimension.isPresent()) {
            Integer Dimension = (Integer) oDimension.get().getValue();
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
     * @param newData         {@link CompoundMap} of the level.dat file
     * @param renewedLevelDat {@link CompoundTag} of a renewed level.dat file
     * @throws IOException when something goes wrong
     */
    public void levelDatFixer(CompoundMap newData, CompoundTag renewedLevelDat) throws IOException {
        Optional<Tag<?>> oData = Util.getAsTagIfExists(newData, TagType.TAG_COMPOUND, "Data");
        Optional<Tag<?>> oRenewedData = Util.getAsTagIfExists(renewedLevelDat.getValue(), TagType.TAG_COMPOUND, "Data");

        if (oData.isPresent() && oRenewedData.isPresent()) {
            CompoundMap data = (CompoundMap) oData.get().getValue();
            CompoundMap renewedData = (CompoundMap) oRenewedData.get().getValue();

            //GameRules fix (only 9 added in 1.7.10, keeping rest of the selected Renewed World)
            Optional<Tag<?>> oGameRules1Tag = Util.getAsTagIfExists(data, TagType.TAG_COMPOUND, "GameRules");
            Optional<Tag<?>> oGameRules = Util.getAsTagIfExists(renewedData, TagType.TAG_COMPOUND, "GameRules");
            if (oGameRules1Tag.isPresent() && oGameRules.isPresent()) {
                CompoundMap gameRules1 = (CompoundMap) oGameRules1Tag.get().getValue();
                CompoundMap gameRules = (CompoundMap) oGameRules.get().getValue();
                if (gameRules.containsKey("commandBlockOutput") && gameRules1.containsKey("commandBlockOutput")) {
                    gameRules.replace("commandBlockOutput", gameRules1.get("commandBlockOutput"));
                }
                if (gameRules.containsKey("doDaylightCycle") && gameRules1.containsKey("doDaylightCycle")) {
                    gameRules.replace("doDaylightCycle", gameRules1.get("doDaylightCycle"));
                }
                if (gameRules.containsKey("doFireTick") && gameRules1.containsKey("doFireTick")) {
                    gameRules.replace("doFireTick", gameRules1.get("doFireTick"));
                }
                if (gameRules.containsKey("doMobLoot") && gameRules1.containsKey("doMobLoot")) {
                    gameRules.replace("doMobLoot", gameRules1.get("doMobLoot"));
                }
                if (gameRules.containsKey("doMobSpawning") && gameRules1.containsKey("doMobSpawning")) {
                    gameRules.replace("doMobSpawning", gameRules1.get("doMobSpawning"));
                }
                if (gameRules.containsKey("doTileDrops") && gameRules1.containsKey("doTileDrops")) {
                    gameRules.replace("doTileDrops", gameRules1.get("doTileDrops"));
                }
                if (gameRules.containsKey("keepInventory") && gameRules1.containsKey("keepInventory")) {
                    gameRules.replace("keepInventory", gameRules1.get("keepInventory"));
                }
                if (gameRules.containsKey("mobGriefing") && gameRules1.containsKey("mobGriefing")) {
                    gameRules.replace("mobGriefing", gameRules1.get("mobGriefing"));
                }
                if (gameRules.containsKey("naturalRegeneration") && gameRules1.containsKey("naturalRegeneration")) {
                    gameRules.replace("naturalRegeneration", gameRules1.get("naturalRegeneration"));
                }
                newData.replace("GameRules", new CompoundTag("GameRules", gameRules));
            }

            Optional<Tag<?>> oWorldGenSettings = Util.getAsTagIfExists(data, TagType.TAG_COMPOUND, "WorldGenSettings");
            if (oWorldGenSettings.isPresent()) {
                CompoundMap worldGenSettings = (CompoundMap) oWorldGenSettings.get().getValue();
                if (renewedData.containsKey("MapFeatures")) {
                    worldGenSettings.replace("generate_features", renewedData.get("MapFeatures"));
                }
                if (renewedData.containsKey("RandomSeed")) {
                    worldGenSettings.replace("seed", new LongTag("seed", (Long) renewedData.get("RandomSeed").getValue()));
                }

                Optional<Tag<?>> oDimensions = Util.getAsTagIfExists(worldGenSettings, TagType.TAG_COMPOUND, "dimension");
                if (oDimensions.isPresent()) {
                    CompoundMap dimensions = (CompoundMap) (oDimensions.get()).getValue();

                    //should have made this a loop in hindsight, oh well...

                    CompoundMap meDimension = ((CompoundTag) dimensions.get("lotr:middle_earth")).getValue();
                    CompoundMap generatorMap1 = ((CompoundTag) meDimension.get("generator")).getValue();
                    //lotr:middle_earth
                    //generatorMap1.replace("seed",renewedData.get("RandomSeed"));
                    generatorMap1.replace("seed", new LongTag("seed", (Long) renewedData.get("RandomSeed").getValue()));

                    CompoundMap biome_source1 = new CompoundMap();
                    Optional<Tag<?>> oBiomeSource1 = Util.getAsTagIfExists(generatorMap1, TagType.TAG_COMPOUND, "biome_source");
                    if (oBiomeSource1.isPresent()) {
                        biome_source1 = (CompoundMap) oBiomeSource1.get().getValue();
                    }


                    biome_source1.replace("seed", new LongTag("seed", (Long) renewedData.get("RandomSeed").getValue()));
                    //sets instant_middle_earth right in lotr:middle_earth
                    //meClassic apparently doesn't use this tag, even though you definitely spawn directly into middle-earth
                    //renewedData.get("generatorName").getValue().equals("meClassic") ||
                    if (renewedData.get("generatorName").getValue().equals("middleEarth")) {
                        generatorMap1.replace("instant_middle_earth", new ByteTag("instant_middle_earth", (byte) 1));
                        if (renewedData.get("generatorName").getValue().equals("meClassic"))
                            biome_source1.replace("classic_biomes", new ByteTag("classic_biomes", (byte) 1));
                        else biome_source1.replace("classic_biomes", new ByteTag("classic_biomes", (byte) 0));
                    }
                    else generatorMap1.replace("instant_middle_earth", new ByteTag("instant_middle_earth", (byte) 0));

                    generatorMap1.replace("biome_source", new CompoundTag("biome_source", biome_source1));
                    meDimension.replace("generator", new CompoundTag("generator", generatorMap1));
                    dimensions.replace("lotr:middle_earth", new CompoundTag("lotr:middle_earth", meDimension));

                    CompoundMap overworldDimension = ((CompoundTag) dimensions.get("minecraft:overworld")).getValue();
                    CompoundMap generatorMap2 = ((CompoundTag) overworldDimension.get("generator")).getValue();
                    //minecraft:overworld
                    if ((renewedData.get("generatorName").getValue().equals("flat"))) {
                        //handles flat-worlds, hardcodes the default values as transcribing them is beyond the scope of the convertor, salt might be the seed and not actually this odd value
                        generatorMap2.replace("type", new StringTag("type", "minecraft:flat"));
                        generatorMap2.remove("biome_source");
                        generatorMap2.remove("seed");
                        generatorMap2.remove("settings");
                        CompoundMap settingsMap = Util.createCompoundMapWithContents(new StringTag("biome", "minecraft:plains"), new ByteTag("features", (byte) 0), new ByteTag("lakes", (byte) 0));

                        CompoundMap strongholdMap = Util.createCompoundMapWithContents(new IntTag("count", 128), new IntTag("distance", 32), new IntTag("spread", 3));
                        CompoundMap structures1Map = Util.createCompoundMapWithContents(new CompoundTag("stronghold", strongholdMap));

                        //TODO: Fix salt to use seed
                        CompoundMap villageMap = Util.createCompoundMapWithContents(new IntTag("salt", ((new Random()).nextInt(1000000000))), new IntTag("separation", 8), new IntTag("spacing", 32));

                        structures1Map.put("structures", new CompoundTag("structures", Util.createCompoundMapWithContents(new CompoundTag("minecraft:village", villageMap))));

                        settingsMap.put("structures", new CompoundTag("structures", structures1Map));

                        //Adds the entries for flatworld generation
                        List<CompoundTag> layersList = new ArrayList<>();
                        layersList.add(new CompoundTag("", Util.createCompoundMapWithContents(new StringTag("block", "minecraft:bedrock"), new IntTag("height", 1))));
                        layersList.add(new CompoundTag("", Util.createCompoundMapWithContents(new StringTag("block", "minecraft:dirt"), new IntTag("height", 2))));
                        layersList.add(new CompoundTag("", Util.createCompoundMapWithContents(new StringTag("block", "minecraft:grass_block"), new IntTag("height", 1))));

                        settingsMap.put("layers", new ListTag<>("layers", TagType.TAG_COMPOUND, layersList));

                        generatorMap2.put("settings", new CompoundTag("settings", settingsMap));
                    }
                    else {
                        generatorMap2.replace("seed", new LongTag("seed", (Long) renewedData.get("RandomSeed").getValue()));
                        CompoundMap biomeSource2;
                        Optional<Tag<?>> oBiomeSource2 = Util.getAsTagIfExists(generatorMap2, TagType.TAG_COMPOUND, "biome_source");
                        if (oBiomeSource2.isPresent()) {
                            biomeSource2 = (CompoundMap) oBiomeSource2.get().getValue();
                            biomeSource2.replace("seed", new LongTag("seed", (Long) renewedData.get("RandomSeed")
                                    .getValue()));
                            generatorMap2.replace("biome_source", new CompoundTag("biome_source", biomeSource2));
                            if (renewedData.get("generatorName").getValue().equals("largeBiomes"))
                                generatorMap2.replace("large_biomes", new ByteTag("large_biomes", (byte) 1));
                            else generatorMap2.replace("large_biomes", new ByteTag("large_biomes", (byte) 0));
                        }
                    }
                    overworldDimension.replace("generator", new CompoundTag("generator", generatorMap2));
                    dimensions.replace("minecraft:overworld", new CompoundTag("minecraft:overworld", overworldDimension));

                    //minecraft:the_end
                    Optional<Tag<?>> oEndDimension = Util.getAsTagIfExists(dimensions, TagType.TAG_COMPOUND, "minecraft:the_end");
                    if (oEndDimension.isPresent()) {
                        CompoundMap endDimension = (CompoundMap) oEndDimension.get().getValue();
                        Optional<Tag<?>> oGeneratorMap3 = Util.getAsTagIfExists(endDimension, TagType.TAG_COMPOUND, "generator");
                        if (oGeneratorMap3.isPresent()) {
                            CompoundMap generatorMap3 = (CompoundMap) oGeneratorMap3.get().getValue();
                            generatorMap3.replace("seed", new LongTag("seed", (Long) renewedData.get("RandomSeed")
                                    .getValue()));
                            Optional<Tag<?>> oBiomeSource3 = Util.getAsTagIfExists(generatorMap3, TagType.TAG_COMPOUND, "biome_source");
                            if (oBiomeSource3.isPresent()) {
                                CompoundMap biomeSource3 = (CompoundMap) oBiomeSource3.get().getValue();
                                biomeSource3.replace("seed", new LongTag("seed", (Long) renewedData.get("RandomSeed")
                                        .getValue()));
                                generatorMap3.replace("biome_source", new CompoundTag("biome_source", biomeSource3));
                                endDimension.replace("generator", new CompoundTag("generator", generatorMap3));
                                dimensions.replace("minecraft:the_end", new CompoundTag("minecraft:the_end", endDimension));
                            }
                        }
                    }

                    //minecraft:the_nether
                    Optional<Tag<?>> oNetherDimension = Util.getAsTagIfExists(dimensions, TagType.TAG_COMPOUND, "minecraft:the_nether");
                    if (oNetherDimension.isPresent()) {
                        CompoundMap netherDimension = (CompoundMap) oNetherDimension.get().getValue();
                        Optional<Tag<?>> oGeneratorMap4 = Util.getAsTagIfExists(netherDimension, TagType.TAG_COMPOUND, "generator");
                        if (oGeneratorMap4.isPresent()) {
                            CompoundMap generatorMap4 = (CompoundMap) oGeneratorMap4.get().getValue();
                            generatorMap4.replace("seed", new LongTag("seed", (Long) renewedData.get("RandomSeed")
                                    .getValue()));
                            Optional<Tag<?>> oBiomeSource4 = Util.getAsTagIfExists(generatorMap4, TagType.TAG_COMPOUND, "biome_source");
                            if (oBiomeSource4.isPresent()) {
                                CompoundMap biomeSource4 = (CompoundMap) oBiomeSource4.get().getValue();
                                biomeSource4.replace("seed", new LongTag("seed", (Long) renewedData.get("RandomSeed")
                                        .getValue()));
                                generatorMap4.replace("biome_source", new CompoundTag("biome_source", biomeSource4));
                                netherDimension.replace("generator", new CompoundTag("generator", generatorMap4));
                                dimensions.replace("minecraft:the_nether", new CompoundTag("minecraft:the_nether", netherDimension));
                            }
                        }
                    }
                    worldGenSettings.replace("dimensions", new CompoundTag("dimensions", dimensions));
                    data.replace("WorldGenSettings", new CompoundTag("WorldGenSettings", worldGenSettings));
                }
            }

            //rest of 'Data' fix
            data.replace("DayTime", renewedData.get("DayTime"));
            data.replace("GameType", renewedData.get("GameType"));
            data.replace("hardcore", renewedData.get("hardcore"));
            data.replace("initialized", renewedData.get("initialized"));
            data.replace("LastPlayed", renewedData.get("LastPlayed"));
            data.replace("LevelName", renewedData.get("LevelName"));
            data.replace("raining", renewedData.get("raining"));
            data.replace("rainTime", renewedData.get("rainTime"));
            data.replace("SpawnX", renewedData.get("SpawnX"));
            data.replace("SpawnY", renewedData.get("SpawnY"));
            data.replace("SpawnZ", renewedData.get("SpawnZ"));
            data.replace("thundering", renewedData.get("thundering"));
            data.replace("thunderTime", renewedData.get("thunderTime"));
            data.replace("Time", renewedData.get("Time"));
            data.replace("version", renewedData.get("version"));
            if (data.containsKey("Player") && renewedData.containsKey("Player")) {
                Optional<CompoundTag> oPlayer = renewedData.get("Player").getAsCompoundTag();
                if (oPlayer.isPresent()) {
                    CompoundMap player = oPlayer.get().getValue();
                    playerFixer(player);
                    data.replace("Player", new CompoundTag("Player", player));
                }
            }
            newData.replace("Data", new CompoundTag("Data", data));
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

        Optional<Tag<?>> oDates = Util.getAsTagIfExists(originalData, TagType.TAG_COMPOUND, "Dates");
        if (oDates.isPresent()) {
            CompoundMap dates = (CompoundMap) oDates.get().getValue();
            if (dates.containsKey("ShireDate")) {
                (dates.get("ShireDate")
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
        Optional<Tag<?>> oAlignmentMap = Util.getAsTagIfExists(originalData, TagType.TAG_LIST, "AlignmentMap");
        List<CompoundTag> alignmentMapBuilder = new ArrayList<CompoundTag>(1) {
        };
        if (oAlignmentMap.isPresent()) {
            ListTag<CompoundTag> alignmentMapOld = (ListTag<CompoundTag>) oAlignmentMap.get();
            for (CompoundTag tag : alignmentMapOld.getValue()) {
                CompoundMap map = tag.getValue();
                Optional<Tag<?>> oFaction = Util.getAsTagIfExists(map, TagType.TAG_STRING, "Faction");
                if (oFaction.isPresent()) {
                    String faction = (String) (oFaction.get()).getValue();
                    if (data.facNames.containsKey(faction)) {
                        alignmentMapBuilder.add(new CompoundTag("", Util.createCompoundMapWithContents(map.get("AlignF"), new StringTag("Faction", data.facNames.get(faction)))));
                    }
                }
            }
        }

        //ListTag AlignmentMap = new ListTag("AlignmentMap",CompoundTag.class, alignmentMapBuilder);

        ListTag<CompoundTag> factionStatsOld = (ListTag<CompoundTag>) originalData.get("FactionData");
        List<CompoundTag> factionStatsBuilder = new ArrayList<CompoundTag>(1) {
        };
        for (CompoundTag tag : factionStatsOld.getValue()) {
            CompoundMap map = tag.getValue();
            Optional<Tag<?>> oFaction = Util.getAsTagIfExists(map, TagType.TAG_STRING, "Faction");
            if (oFaction.isPresent()) {
                String factionAL = (String) (oFaction.get()).getValue();
                if (data.facNames.containsKey(factionAL)) {
                    final CompoundMap newDataAL = Util.createCompoundMapWithContents(map.get("ConquestHorn"), map.get("EnemyKill"), new StringTag("Faction", data.facNames.get(factionAL)), map.get("Hired"), map.get("MiniQuests"), map.get("Trades"));

                    //Couldn't think of a way to do renaming implicitly
                    newDataAL.put("MemberKill", map.get("NPCKill"));
                    CompoundTag AM_AL_Builder = new CompoundTag("", newDataAL);
                    factionStatsBuilder.add(AM_AL_Builder);
                }
            }
        }
        ListTag<CompoundTag> prevRegionFactionsOld = (ListTag<CompoundTag>) originalData.get("PrevRegionFactions");
        List<CompoundTag> prevRegionFactionsBuilder = new ArrayList<CompoundTag>(1) {
        };
        for (CompoundTag tag : prevRegionFactionsOld.getValue()) {
            CompoundMap map = tag.getValue();
            Optional<Tag<?>> oRegion = Util.getAsTagIfExists(map, TagType.TAG_STRING, "Region");
            Optional<Tag<?>> oFaction = Util.getAsTagIfExists(map, TagType.TAG_STRING, "Faction");
            if (oRegion.isPresent() && oFaction.isPresent()) {
                String regionPRF = (String) oRegion.get().getValue();
                String factionPRF = (String) oFaction.get().getValue();
                if (data.facNames.containsKey(factionPRF)) {
                    final CompoundMap newDataPRF = Util.createCompoundMapWithContents(new StringTag("Faction", data.facNames.get(factionPRF)));
                    switch (regionPRF) {
                        case "west":
                            newDataPRF.put("Region", new StringTag("Region", "lotr:westlands"));
                            break;
                        case "east":
                            newDataPRF.put("Region", new StringTag("Region", "lotr:rhun"));
                            break;
                        case "south":
                            newDataPRF.put("Region", new StringTag("Region", "lotr:harad"));
                            break;
                    }
                    prevRegionFactionsBuilder.add(new CompoundTag("", newDataPRF));
                }
            }
        }
        //ListTag PrevRegionFactions = new ListTag("PrevRegionFactions",CompoundTag.class, prevRegionFactionsBuilder);

        //SentMessageTypes
        List<StringTag> unlockedFTRegionsBuilder = new ArrayList<>(0);
        Optional<Tag<?>> oUnlockedFTRegions = Util.getAsTagIfExists(originalData, TagType.TAG_LIST, "UnlockedFTRegions");
        if (oUnlockedFTRegions.isPresent()) {
            ListTag<CompoundTag> unlockedFTRegions = (ListTag<CompoundTag>) oUnlockedFTRegions.get();
            for (CompoundTag tag : unlockedFTRegions.getValue()) {
                CompoundMap map = tag.getValue();
                Optional<Tag<?>> oRegionName = Util.getAsTagIfExists(map, TagType.TAG_STRING, "Name");
                if (oRegionName.isPresent()) {
                    String regionName = (String) oRegionName.get().getValue();
                    if (data.regions.containsKey(regionName)) {
                        String nameValue = data.regions.get(regionName);
                        if (!Objects.equals(nameValue, "")) {
                            StringTag name = new StringTag("", nameValue);
                            unlockedFTRegionsBuilder.add(name);
                        }
                    }
                }
            }
        }

        List<CompoundTag> wayPointUsesBuilder = new ArrayList<>(1);
        Optional<Tag<?>> oWaypointUses = Util.getAsTagIfExists(originalData, TagType.TAG_LIST, "WPUses");
        if (oWaypointUses.isPresent()) {
            ListTag<CompoundTag> oldWaypointUses = (ListTag<CompoundTag>) oWaypointUses.get();
            for (CompoundTag tag : oldWaypointUses.getValue()) {
                CompoundMap map = tag.getValue();
                Optional<Tag<?>> oWaypointName = Util.getAsTagIfExists(map, TagType.TAG_STRING, "WPName");
                if (oWaypointName.isPresent()) {
                    if (data.waypoints.containsKey(((StringTag) oWaypointName.get()).getValue())) {
                        //add the CompoundTag to the List
                        //CompoundMap Info:
                        //Var1: the amount of waypoint usage (cooldown depends on it)
                        //Var2: the new name
                        wayPointUsesBuilder.add(new CompoundTag("", Util.createCompoundMapWithContents(map.get("Count"), new StringTag("WPName", data.waypoints.get(((StringTag) oWaypointName.get()).getValue())))));
                    }
                }
            }
        }
        //get the old WPUses
        //ListTag<CompoundTag> WPUses_old = (ListTag<CompoundTag>) originalData.get("WPUses");
        //create a new empty array put the new WPUses in
        //List<CompoundTag> wayPointUsesBuilder = new ArrayList<CompoundTag>(1) {
        //};
        //loop though the entries in the list

        //create the ListTag from the List
        //ListTag WPUses = new ListTag("WPUses",CompoundTag.class, wayPointUsesBuilder);


        //the game will add missing items itself, hence the commented out fields
        //ByteTag ShowMapMarkers = new ByteTag("ShowMapMarkers", (byte) 1);

        //removes redundant data (for now, at least)
        Util.compoundMapVarArgRemover(originalData, "QuestData", "Achievements", "SentMessageTypes", "BountiesPlaced", "CustomWayPoints", "CWPSharedHidden", "CWPSharedUnlocked", "CWPSharedUses", "CWPUses", "FellowshipInvites", "Fellowships", "MiniQuests", "MiniQuestsCompleted", "TakenAlignmentRewards", "AdminHideMap", "Chosen35Align", "ConquestKills", "HideAlignment", "HideOnMap", "HiredDeathMessages", "LastBiome", "MiniQuestTrack", "MQCompleteCount", "MQCompletedBounties", "Pre35Align", "ShowHiddenSWP", "StructuresBanned", "ChatBoundFellowship", "DeathDim");
        originalData.replace("AlignmentMap", new ListTag<>("AlignmentMap", TagType.TAG_COMPOUND, alignmentMapBuilder));
        originalData.replace("FactionStats", new ListTag<>("FactionStats", TagType.TAG_COMPOUND, factionStatsBuilder));
        originalData.replace("PrevRegionFactions", new ListTag<>("PrevRegionFactions", TagType.TAG_COMPOUND, prevRegionFactionsBuilder));
        originalData.replace("UnlockedFTRegions", new ListTag<>("UnlockedFTRegions", TagType.TAG_COMPOUND, unlockedFTRegionsBuilder));
        originalData.replace("WPUses", new ListTag<>("WPUses", TagType.TAG_COMPOUND, wayPointUsesBuilder));
        originalData.replace("CurrentFaction", new StringTag("CurrentFaction", data.facNames.getOrDefault(originalData.get("CurrentFaction")
                                                                                                                  .getValue()
                                                                                                                  .toString(), "lotr:hobbit")));

        if (Objects.equals(originalData.get("TeleportedME").getValue(), (byte) 1)) {
            originalData.replace("TeleportedME", (new ByteTag("InitialSpawnedIntoME", (byte) 0)));
        }
        else {
            originalData.replace("TeleportedME", (new ByteTag("InitialSpawnedIntoME", (byte) 1)));
        }

        //Byte in legacy, string in renewed, because of this you can replace it in the stream
        if (Objects.equals(originalData.get("FemRankOverride").getValue(), (byte) 0)) {
            originalData.put("RankGender", (new StringTag("RankGender", "M")));

        }
        else {
            originalData.put("RankGender", (new StringTag("RankGender", "F")));
            // "FLOPPA_CAT" Mevans, really?
        }

        originalData.remove("FemRankOverride");
        if (originalData.containsKey("HideOnMap")) {
            if (Objects.equals(originalData.get("HideOnMap").getValue(), (byte) 1)) {
                originalData.replace("HideOnMap", new ByteTag("ShowMapLocation", (byte) 0));
            }
            else {
                originalData.replace("HideOnMap", new ByteTag("ShowMapLocation", (byte) 1));
            }
        }
    }

    /**
     * Fixes tile/block entities
     *
     * @param map {@link CompoundMap} containing the old TileEntity tag data
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
        Optional<CompoundMap> regularResult = Optional.empty();
        TileEntityFixerReturnType type = TileEntityFixerReturnType.Regular;
        Optional<Tag<?>> oId = Util.getAsTagIfExists(map, TagType.TAG_STRING, "id");
        if (oId.isPresent()) {
            String id = (String) oId.get().getValue();
            if (data.blockEntityMappings.containsKey(id)) {
                String value = data.blockEntityMappings.get(id);
                if (!Objects.equals(value, "")) {
                    map.replace("id", new StringTag("id", value));
                    //case switch for ids here
                    switch (value) {
                        case "minecraft:hopper": {
                            Optional<Tag<?>> oItemList = Util.getAsTagIfExists(map, TagType.TAG_LIST, "Items");
                            if (oItemList.isPresent()) {
                                map.replace("Items", new ListTag<>("Items", TagType.TAG_COMPOUND, recurItemFixerList(((ListTag<CompoundTag>) oItemList.get()).getValue(), 0, "Exception during hopper item content fix")));
                            }
                            break;
                        }
                        case "minecraft:dispenser": {
                            Optional<Tag<?>> oItemList = Util.getAsTagIfExists(map, TagType.TAG_LIST, "Items");
                            if (oItemList.isPresent()) {
                                map.replace("Items", new ListTag<>("Items", TagType.TAG_COMPOUND, recurItemFixerList(((ListTag<CompoundTag>) oItemList.get()).getValue(), 0, "Exception during dispenser item content fix")));
                            }
                            break;
                        }
                        case "minecraft:dropper": {
                            Optional<Tag<?>> oItemList = Util.getAsTagIfExists(map, TagType.TAG_LIST, "Items");
                            if (oItemList.isPresent()) {
                                map.replace("Items", new ListTag<>("Items", TagType.TAG_COMPOUND, recurItemFixerList(((ListTag<CompoundTag>) oItemList.get()).getValue(), 0, "Exception during dropper item content fix")));
                            }
                            break;
                        }
                        case "minecraft:chest": {
                            Optional<Tag<?>> oItemList = Util.getAsTagIfExists(map, TagType.TAG_LIST, "Items");
                            if (oItemList.isPresent()) {
                                map.replace("Items", new ListTag<>("Items", TagType.TAG_COMPOUND, recurItemFixerList(((ListTag<CompoundTag>) oItemList.get()).getValue(), 0, "Exception during chest/trapped chest item content fix")));
                            }
                            type = TileEntityFixerReturnType.ChestOrTrappedChest;
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
                            }
                            else map.put(new ByteTag("IsBurning", (byte) 0));
                            break;
                        }
                        case "lotr:plate": {
                            if (map.containsKey("FoodItem")) {
                                (map.get("FoodItem")
                                        .getAsCompoundTag()).flatMap(compoundTag -> (recurItemFixer(compoundTag, 0, "Exception during plate item fix")))
                                        .ifPresent(tags -> map.replace("FoodItem", new CompoundTag("FoodItem", tags)));
                            }
                            else map.put(new CompoundTag("FoodItem", new CompoundMap()));
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
                            type = TileEntityFixerReturnType.Ent_Jar;
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

                            }
                            else map.put(new CompoundTag("DrinkItem", new CompoundMap()));
                            map.remove("HasMugItem");
                            Optional<Tag<?>> oVessel = Util.getAsTagIfExists(map, TagType.TAG_BYTE, "Vessel");
                            if (oVessel.isPresent()) {
                                switch ((((ByteTag) oVessel.get()).getValue())) {
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
                            }
                            else map.put(new StringTag("Vessel", "wooden_mug"));
                            break;
                        }
                    }
                    //Needed for validation apparently
                    map.put(new ByteTag("keepPacked", (byte) 0));
                    regularResult = Optional.of(map);
                }
                else if (Objects.equals(id, "Music")) {
                    type = TileEntityFixerReturnType.Note_Block;
                }
                else if (Objects.equals(id, "Cauldron")) {
                    type = TileEntityFixerReturnType.Cauldron;
                }
                else if (Objects.equals(id, "LOTRArmorStand")) {
                    type = TileEntityFixerReturnType.Armour_Stand;
                }
                else if (Objects.equals(id, "FlowerPot") || Objects.equals(id, "LOTRFlowerPot")) {
                    type = TileEntityFixerReturnType.Flower_Pot;
                }
                else {
                    //Unhandled items:
                    /*
                    UtumnoPortal
                     */

                    stringCache.printLine("unknown tile entity found with id: " + id);
                }
            }
            else {
                stringCache.printLine("No block entity id found for old id: " + oId.get(), false);
            }
        }
        return new TileEntityResult(regularResult, type);
    }

    /**
     * Fixes the Block/Tile entity of Lotr mod forges and the vanilla furnace (partially)
     *
     * @param map {@link CompoundMap} to be fixed
     * @throws IOException when something fails
     */
    @SuppressWarnings("unchecked")
    public void alloyFixer(CompoundMap map) throws IOException {
        Optional<Tag<?>> oItemList = Util.getAsTagIfExists(map, TagType.TAG_LIST, "Items");
        if (oItemList.isPresent()) {
            map.replace("Items", new ListTag<>("Items", TagType.TAG_COMPOUND, recurItemFixerList(((ListTag<CompoundTag>) oItemList.get()).getValue(), 0, "Exception during furnace/forge fixing")));
        }
        if (map.containsKey("BurnTime")) {
            (map.get("BurnTime")
                    .getAsShortTag()).ifPresent(shortTag -> map.replace("BurnTime", new IntTag("BurnTime", shortTag.getValue())));
        }
        else map.put("BurnTime", new IntTag("BurnTime", 0));
        Optional<Tag<?>> oSmeltTime = Util.getAsTagIfExists(map, TagType.TAG_SHORT, "SmeltTime");
        if (oSmeltTime.isPresent()) {
            map.replace("SmeltTime", new IntTag("CookTime", ((Short) oSmeltTime.get().getValue()).intValue()));
            map.put(new IntTag("CookTimeTotal", ((Short) oSmeltTime.get().getValue()).intValue()));
        }
        else {
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
     * @param sections {@link List} of type {@link CompoundTag} containing the sections
     */
    public void sectionMapFixer(List<CompoundTag> sections, List<TileEntityResult> edgeCases) {
        for (int i = 0; i < sections.size(); i++) {
            CompoundMap sectionCompoundMap = sections.get(i).getValue();
            List<CompoundTag> paletteBuilderList = new ArrayList<>();

            //Apparently air is always in the palette, or once it's in it never leaves, I don't know yet
            paletteBuilderList.add(new CompoundTag("", Util.createCompoundMapWithContents(new StringTag("Name", "minecraft:air"))));

            //used for making sure no identical palette entries exist
            List<String> paletteCheckerList = new ArrayList<>(Collections.singleton("BlockMapping{name='minecraft:air', properties=null}"));

            Optional<ByteArrayTag> oBlocksByteArray = sectionCompoundMap.get("Blocks").getAsByteArrayTag();
            Optional<ByteArrayTag> oDataByteArray = sectionCompoundMap.get("Data").getAsByteArrayTag();
            Optional<ByteArrayTag> oAddByteArray;
            if (sectionCompoundMap.containsKey("Add")) {
                oAddByteArray = sectionCompoundMap.get("Add").getAsByteArrayTag();
            }
            else {
                oAddByteArray = Optional.empty();
            }

            if (sectionCompoundMap.containsKey("Y")) {
                Optional<ByteTag> oY = sectionCompoundMap.get("Y").getAsByteTag();
                if (oY.isPresent()) {

                    if (oBlocksByteArray.isPresent() && oDataByteArray.isPresent()) {
                        byte[] blocksByteArray = oBlocksByteArray.get().getValue();
                        byte[] dataByteArray = oDataByteArray.get().getValue();
                        byte[] addByteArray = oAddByteArray.isPresent() ? oAddByteArray.get()
                                .getValue() : new byte[2048];
                        //initializes with 0 as default value, as air is always the first entry, nothing needs to happen with air
                        int[] blockPaletteReferences = new int[4096];
                        //this should never fail as far as I know, purely redundancy
                        if (blocksByteArray.length == 4096 && dataByteArray.length == 2048) {
                            //to loop through both lists at once.
                            /*

                            Optional<List<TileEntityResult>> OPerSectionEdgeCases;
                            if (Sorter.containsKey(oY.get().getValue())) {
                                OPerSectionEdgeCases = Optional.ofNullable(Sorter.get(oY.get().getValue()));
                            } else OPerSectionEdgeCases = Optional.empty();
                             */
                            for (int dataCounter = 0; dataCounter < 4096; dataCounter++) {
                                int blockId = Util.combine(blocksByteArray[dataCounter], Util.nibble4(addByteArray, dataCounter));

                                /*
                                if (firstByte == (byte) 0B11010101 || firstByte == 0b0011101) {
                                    System.out.println("breakpoint");
                                }
                                 */

                                if (data.legacyIds.containsKey(blockId)) {
                                    String legacyId = data.legacyIds.get(blockId);
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
                                    if (data.blockMappings.containsKey(legacyId)) {
                                        Map<String, Conversions.BlockMapping> blockMapping = data.blockMappings.get(legacyId);
                                        if (blockMapping != null) {
                                            byte neededValue = Util.nibble4(dataByteArray, dataCounter);
                                            blockPaletteReferences[dataCounter] = addPaletteEntryIfNecessary(blockMapping, paletteCheckerList, paletteBuilderList, neededValue);
                                        }
                                    }
                                }
                            }
                            //}

                            ListTag<CompoundTag> Palette = new ListTag<>("Palette", TagType.TAG_COMPOUND, paletteBuilderList);

                            sectionCompoundMap.remove("Blocks");
                            sectionCompoundMap.remove("Data");
                            sectionCompoundMap.remove("Add");
                            sectionCompoundMap.put(Palette);
                            sectionCompoundMap.put(new LongArrayTag("BlockStates", blockStateGenerator(paletteCheckerList, blockPaletteReferences)));
                            sections.set(i, new CompoundTag("", sectionCompoundMap));

                        }
                        else {
                            stringCache.printLine("Invalid section format!", false);
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates the long[] for the BlockStates Tag
     *
     * @param paletteCheckerList {@link List<String>} with the palette entries stringified
     * @param paletteReferences  int[] with the block values
     * @return long[] containing the encoded BlockStates
     */
    public long[] blockStateGenerator(List<String> paletteCheckerList, int[] paletteReferences) {
        long[] blockStates;
        //Should always be true due to where we call it, just making sure
        if (paletteReferences.length == 4096) {
            //How many bits do you need to store the data of 1 block, minimum is 4
            int bitsPerIndex = bitsPerIndex(paletteCheckerList.size());
            //How many blocks can you fit in 1 long, a long stores the data of 64 bits, so you simply divide and floor
            int blocksPerLong = Math.floorDiv(64, bitsPerIndex);
            //How many longs do you need to store 4096 blocks with BlockPerLong
            int longsNeeded = (int) Math.ceil((double) 4096 / blocksPerLong);
            blockStates = new long[longsNeeded];

            //Which long should we write to, ranges from 0 up to longsNeeded
            int externalLongPosition;
            //Which position within the long are we gonna use, value ranges from 0 to 16
            int internalLongPosition;
            for (int i = 0; i < 4096; i++) {
                //progression for switching to the next long
                externalLongPosition = Math.floorDiv(i, blocksPerLong);
                //progression within the long
                internalLongPosition = i % blocksPerLong;
                //Updates the long accordingly to the Palette reference, the BPI (Bits Per Index) & the internal position
                blockStates[externalLongPosition] = blockStateLongUpdater(blockStates[externalLongPosition], paletteReferences[i], bitsPerIndex, internalLongPosition);
            }

        }
        else blockStates = new long[256]; //returns an empty section with bpi of 4
        return blockStates;
    }

    /**
     * Updates the value of a long with its new value depending on the bits per index (bpi), blocks per long (BPL) and the internal position
     *
     * @param base                  the long to be updated
     * @param value                 the value the long should be updated with
     * @param bpi                   Bits per Index
     * @param internalBlockPosition Position of the Long that should be updated
     */
    public long blockStateLongUpdater(long base, int value, int bpi, int internalBlockPosition) {
        //if value is 0 there is no need to update the value of the long
        if (value != 0) {
            base = base | ((long) value << (bpi * internalBlockPosition));
        }
        return base;
    }

    /**
     * @param paletteCheckerListLength Length of the Palette
     * @return the bits per Index of the Palette
     * @author PieGames
     * Gets the Bits per Index, used in the extractFromLong1_16 method in {@link Chunk}, unfortunately it's not a seperate method, so I added it here
     */
    public int bitsPerIndex(int paletteCheckerListLength) {
        return Math.max(4, 32 - Integer.numberOfLeadingZeros(paletteCheckerListLength - 1));
    }

    /**
     * Adds an entry to the Palette if it's necessary (it doesn't exist yet)
     *
     * @param blockMapping       {@link Map<String>} Mapping of the main id
     * @param paletteCheckerList {@link List<String>} containing String versions of the Palette entries, used for faster searching
     * @param paletteBuilderList {@link List<CompoundTag>} containing the Palette entries
     */
    public int addPaletteEntryIfNecessary(Map<String, BlockMapping> blockMapping, List<String> paletteCheckerList, List<CompoundTag> paletteBuilderList, byte neededValue) {
        int returner = 0;
        if (entryExists(blockMapping, neededValue)) {
            BlockMapping mapping = blockMapping.get(String.valueOf((neededValue)));
            String mapString = mapping.toString();
            if (!paletteCheckerList.contains(mapString)) {
                paletteCheckerList.add(mapString);
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
                paletteBuilderList.add(new CompoundTag("Palette", map));
            }
            returner = paletteCheckerList.indexOf(mapString);
        }
        return returner;
    }

    public boolean entryExists(Map<String, BlockMapping> mapping, byte neededValue) {
        return mapping.containsKey(String.valueOf(neededValue));
    }


    /**
     * Fixes chunk {@link CompoundMap}
     *
     * @param chunk {@link CompoundMap} of a chunk
     */
    @SuppressWarnings("unchecked")
    public void chunkFixer(CompoundMap chunk) throws IOException {
        Optional<Tag<?>> oLevel = Util.getAsTagIfExists(chunk, TagType.TAG_COMPOUND, "Level");
        if (oLevel.isPresent()) {
            CompoundMap level = (CompoundMap) oLevel.get().getValue();
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
            List<TileEntityResult> edgeCases = new ArrayList<>();
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
            Optional<Tag<?>> oBlockEntities = Util.getAsTagIfExists(level, TagType.TAG_LIST, "TileEntities");
            if (oBlockEntities.isPresent()) {
                ListTag<CompoundTag> tileEntities = (ListTag<CompoundTag>) oBlockEntities.get();
                List<CompoundTag> tileEntityBuilder = new ArrayList<>();

                for (CompoundTag t : tileEntities.getValue()) {
                    TileEntityResult result = blockEntityFixer(t.getValue());
                    if (result.getContent().isPresent()) {
                        switch (result.getType()) {
                            case Regular: {
                                tileEntityBuilder.add(new CompoundTag("", result.getContent().get()));
                                break;
                            }
                            case Armour_Stand:
                            case Cauldron:
                            case Ent_Jar:
                            case Flower_Pot:
                            case Note_Block: {
                                edgeCases.add(result);
                                break;
                            }
                            case ChestOrTrappedChest: {
                                tileEntityBuilder.add(new CompoundTag("", result.getContent().get()));
                                CompoundMap ChestOrTrappedChest = result.getContent().get();
                                //Computationally less heavy to do this than to pass the items (I hope)
                                ChestOrTrappedChest.remove("Items");
                                edgeCases.add(new TileEntityResult(Optional.of(ChestOrTrappedChest), TileEntityFixerReturnType.ChestOrTrappedChest));
                                break;
                            }
                            case Null: {
                                //For unsupported Entities
                                break;
                            }
                        }
                    }
                }
                level.replace("TileEntities", new ListTag<>("TileEntities", TagType.TAG_COMPOUND, tileEntityBuilder));
            }
            Optional<Tag<?>> oSections = Util.getAsTagIfExists(level, TagType.TAG_LIST, "Sections");
            if (oSections.isPresent()) {
                ListTag<CompoundTag> sectionsTag = (ListTag<CompoundTag>) oSections.get();
                List<CompoundTag> sections = sectionsTag.getValue();
                sectionMapFixer(sections, edgeCases);
                level.replace("Sections", new ListTag<>("Sections", TagType.TAG_COMPOUND, sections));
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
            chunk.replace("Level", new CompoundTag("Level", level));
            //This value triggers Mojang's own DataFixers so use with caution
            chunk.put(new IntTag("DataVersion", 2586));
        }
    }

    /**
     * Fixes Regions
     *
     * @param chunks {@link HashMap} with key Position and Value Chunk
     * @return {@link HashMap} with the fixed chunks
     */
    public HashMap<Integer, Chunk> regionFixer(HashMap<Integer, Chunk> chunks) throws IOException {
        for (Map.Entry<Integer, Chunk> entry : chunks.entrySet()) {
            Chunk chunk = entry.getValue();
            CompoundTag tag = chunk.readTag();
            CompoundMap map = tag.getValue();
            chunkFixer(map);
            tag.setValue(map);
            chunk = new Chunk(chunk.x, chunk.z, chunk.timestamp, tag, chunk.getCompression());
            entry.setValue(chunk);
        }
        return chunks;
    }
}

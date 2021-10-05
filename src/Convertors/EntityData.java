package Convertors;

import org.jnbt.*;
import misterymob475.Data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Convertors.PlayerData.RecurItemFixer;
import static misterymob475.Main.PrintLine;

//atm only here because players can log out whilst on entities, this way they can carry more items into renewed if implemented properly
public class EntityData implements Convertor{
    final private String pathName;
    final private Map<Integer,String> LegacyIds;
    final private Data Data;

    public EntityData(Data data, String pathname, Map<Integer,String> legacyIds) {
        this.Data = data;
        pathName = pathname;
        LegacyIds = legacyIds;
    }




    /**
     * Copies the files to their new location
     *
     * @param p        path of the folder where files are copied
     * @param FileName name of the to be copied file
     * @throws IOException if something fails
     */
    @Override
    public void copier(Path p, String FileName) throws IOException {
        //does nothing for the moment, this is here for when I get started on actual world conversion.
    }

    /**
     * Modifies the files to work in Renewed
     *
     * @param p        path of the folder where files are copied
     * @param FileName name of the to be modified files
     * @throws IOException if something fails
     */
    @Override
    public void modifier(Path p, String FileName) throws IOException {

    }

    public static Map<String, Tag> EntityFixer(Map<String, Tag> Entity, Map<Integer,String> LegacyIds, Data Data) throws IOException {
        //has something to do with the lotrmod
        Entity.remove("ForgeData");
        /*
                Map<String,Tag> brain = new HashMap<>();
        brain.put("memories",new CompoundTag("memories",new HashMap<>()));
        Entity.put("Brain",new CompoundTag("Brain",brain));
         */


        if (Entity.containsKey("SaddleItem")) {
            Map<String,Tag> newSaddleItem = new HashMap<>();
            newSaddleItem.put("Count",new ByteTag("Count", (byte) 1));
            newSaddleItem.put("id",new StringTag("id", "minecraft:saddle"));
            Entity.replace("SaddleItem",new CompoundTag("SaddleItem",newSaddleItem));
        }
        /*
                Entity.remove("ArmorDropChances");
        Entity.remove("HandDropChances");
        Entity.remove("HandItems");

        Entity.remove("CanUpdate");
        Entity.remove("ForcedAge");

         */

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
        List<Tag> Attributes_old = ((ListTag) Entity.get("Attributes")).getValue();
        List<Tag> Attributes_new = new ArrayList<>();
        for (Tag t : Attributes_old) {
            switch (((StringTag) ((CompoundTag) t).getValue().get("Name")).getValue()) {

                case "generic.attackDamage":
                    Map<String,Tag> attackDamage = new HashMap<>();
                    attackDamage.put("Modifiers",modifierFixer(((ListTag) ((CompoundTag) t).getValue().get("Modifiers"))));
                    attackDamage.put("Name",new StringTag("Name","generic.attack_damage"));
                    Attributes_new.add(new CompoundTag("",attackDamage));
                    break;

                //modifiers present here
                case "zombie.spawnReinforcements":
                    Map<String,Tag> spawnReinf = new HashMap<>(((CompoundTag) t).getValue());
                    spawnReinf.replace("Name",new StringTag("Name","zombie.spawn_reinforcements"));
                    Attributes_new.add(new CompoundTag("",spawnReinf));
                    break;

                case "generic.movementSpeed":
                    Map<String,Tag> movSpeed = new HashMap<>();
                    movSpeed.put("Base", ((CompoundTag) t).getValue().get("Base"));
                    movSpeed.put("Name",new StringTag("Name","minecraft:generic.movement_speed"));
                    Attributes_new.add(new CompoundTag("",movSpeed));
                    break;

                case "generic.followRange":
                    Map<String,Tag> followRange = new HashMap<>();
                    //yet to be tested
                    followRange.put("Modifiers",modifierFixer(((ListTag) ((CompoundTag) t).getValue().get("Modifiers"))));
                    followRange.put("Base", ((CompoundTag) t).getValue().get("Base"));
                    followRange.put("Name",new StringTag("Name","minecraft:generic.follow_range"));
                    Attributes_new.add(new CompoundTag("",followRange));
                    break;

                case "generic.maxHealth":
                    Map<String,Tag> maxHealth = new HashMap<>();
                    maxHealth.put("Base", ((CompoundTag) t).getValue().get("Base"));
                    maxHealth.put("Name",new StringTag("Name","minecraft:generic.max_health"));
                    Attributes_new.add(new CompoundTag("",maxHealth));
                    break;
                case "generic.knockbackResistance":
                    Map<String,Tag> knockbackResistance = new HashMap<>();
                    knockbackResistance.put("Base", ((CompoundTag) t).getValue().get("Base"));
                    knockbackResistance.put("Name",new StringTag("Name","generic.knockback_resistance"));
                    Attributes_new.add(new CompoundTag("",knockbackResistance));
                    break;
                case "horse.jumpStrength":
                    Map<String,Tag> jumpStrength = new HashMap<>();
                    jumpStrength.put("Base", ((CompoundTag) t).getValue().get("Base"));
                    jumpStrength.put("Name",new StringTag("Name","horse.jump_strength"));
                    Attributes_new.add(new CompoundTag("",jumpStrength));
                    break;
                default:
                    //this is possible because unknown tags will get discarded by the game engine
                    Attributes_new.add(t);
                    break;
            }
        }
        ListTag Attributes = new ListTag("Attributes",CompoundTag.class,Attributes_new);
        Entity.replace("Attributes",Attributes);
        //will easily regenerate I hope
        Entity.remove("DropChances");
        if (Entity.containsKey("Equipment")) {
            Entity.replace("Equipment",new ListTag("Equipment",CompoundTag.class,RecurItemFixer((((ListTag) Entity.get("Items")).getValue()),LegacyIds,Data.ItemNames(),(double) 0,"Exception during Entity Equipment Fix", Data)));
        }
        //The sole reason I implemented this before I started working on fixing the world
        if (Entity.containsKey("Items")) {
            Entity.replace("Items",new ListTag("Items",CompoundTag.class,RecurItemFixer((((ListTag) Entity.get("Items")).getValue()),LegacyIds,Data.ItemNames(),(double) 0,"Exception during Entity Inventory Fix", Data)));
        }
        Entity.remove("AttackTime");
        //LOTR mod related
        Entity.remove("BelongsNPC");

        if (Entity.containsKey("Dimension")) {
            //fixer here int --> string
            Integer Dimension = ((IntTag) Entity.get("Dimension")).getValue();
            String newDimension;
            if (Dimension == 0) newDimension = "minecraft:overworld";
            else if (Dimension == 1) newDimension = "Minecraft:the_nether";
            else if (Dimension == 2) newDimension = "Minecraft:the_end";
            else if (Dimension == 100) newDimension = "lotr:middle_earth";
            else if (Dimension == 101) {
                newDimension = "lotr:middle_earth"; //utumno doesn't exist yet
            }
            else newDimension = "minecraft:overworld";
            Entity.replace("Dimension",new StringTag("Dimension",newDimension));
        }

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


        //Determines the actual mob
        if (Entity.containsKey("id")) {
            if (Data.Entities().containsKey((String) (Entity.get("id").getValue()))) {
                if (! Data.Entities().get( (String)(Entity.get("id").getValue())).equals("")) {
                    //code for split types here (horses mainly, I'm not gonna bother with zombie villagers)
                    if (Data.Entities().get( (String)(Entity.get("id").getValue())).equals("minecraft:horse")) {
                        if (Entity.get("Type").getValue().equals((byte)1)) {
                            Entity.replace("id",new StringTag("id","minecraft:donkey"));
                            Entity.remove("Variant");
                        }
                        else if (Entity.get("Type").getValue().equals((byte)2)) {
                            Entity.replace("id",new StringTag("id","minecraft:mule"));
                            Entity.remove("Variant");
                        }
                        else if (Entity.get("Type").getValue().equals((byte)3)) {
                            Entity.replace("id",new StringTag("id","minecraft:zombie_horse"));
                            Entity.remove("Variant");
                        }
                        else if (Entity.get("Type").getValue().equals((byte)1)) {
                            Entity.replace("id",new StringTag("id","minecraft:skeleton_horse"));
                            Entity.remove("Variant");
                        }
                        else
                            Entity.replace("id",new StringTag("id","minecraft:horse"));
                    }

                    //I think these are actually bypassed but oh well
                    //code for turning camels into donkeys (to keep the storage)
                    else if (Entity.get("id").getValue().equals("lotr.Camel")) {
                        Entity.remove("Type");
                        Entity.replace("id",new StringTag("id","minecraft:donkey"));
                    }
                    else if (Entity.get("id").getValue().equals("lotr.Horse")) {
                        //this is temporary, when blocks work I'm gonna finish this function, there's still lotr-related stuff missing
                        Entity.remove("Type");
                        Entity.replace("id",new StringTag("id","minecraft:donkey"));
                    }
                    else {
                        Entity.replace("id",new StringTag("id",Data.Entities().get((String)(Entity.get("id").getValue()))));
                    }
                    Entity.remove("Type");
                }
                else PrintLine("No mapping found for Entity: " + Entity.get("id").getValue() + " - It probably hasn't been ported yet",Data,false);
            }
            else {
                PrintLine("No mapping found for Entity: " + Entity.get("id").getValue(),Data,false);
            }
        }


        Entity.remove("Leashed");
        Entity.remove("Mountable");

        Entity.put("LeftHanded",new ByteTag("LeftHanded",(byte)0));
        if (Entity.containsKey("OwnerUUID")) {
            String OwnerUUID = (String) Entity.get("OwnerUUID").getValue();
            Entity.put("Owner",new IntArrayTag("Owner",new int[]{Long.valueOf(OwnerUUID.substring(0,8),16).intValue(),Long.valueOf((OwnerUUID.substring(9,12) + OwnerUUID.substring(14,18)),16).intValue(),Long.valueOf((OwnerUUID.substring(19,23) + OwnerUUID.substring(24,28)),16).intValue(),Long.valueOf(OwnerUUID.substring(28),16).intValue()}));
        }

        Entity.remove("OwnerUUID");
        Entity.remove("TicksSinceFeed");

        Entity.remove("Type");
        //has to do with splitting horses into donkeys and such
        Entity.remove("Variant");

        if (Entity.containsKey("UUIDLeast")) {
            Entity.put("UUID", misterymob475.Data.UUIDFixer((LongTag) Entity.get("UUIDLeast"),(LongTag) Entity.get("UUIDMost")));
            Entity.remove("UUIDLeast");
            Entity.remove("UUIDMost");
        }
        return Entity;
    }
    public static Map<String, Tag> RiderEntityFixer(Map<String, Tag> Entity, Map<Integer,String> LegacyIds, Data Data) throws IOException {
        Map<String,Tag> RootVehicle = new HashMap<>();
        if (Entity.containsKey("UUIDLeast")) {
            RootVehicle.put("Attach", misterymob475.Data.UUIDFixer((LongTag) Entity.get("UUIDLeast"),(LongTag) Entity.get("UUIDMost"),"Attach"));
        }

        //
        Map<String,Tag> Entity_map = EntityFixer(Entity,LegacyIds,Data);
        RootVehicle.put("Entity",new CompoundTag("Entity",Entity_map));
        //

        return RootVehicle;
    }

    public static ListTag modifierFixer(ListTag t) {
        List<Tag> list = new ArrayList<>(t.getValue());
        List<Tag> newList = new ArrayList<>();
        for (Tag c : list) {
            Map<String,Tag> Modifier = new HashMap<>(((CompoundTag) c).getValue());
            if (Modifier.containsKey("UUIDLeast")) {
                Modifier.put("UUID", misterymob475.Data.UUIDFixer((LongTag) Modifier.get("UUIDLeast"),(LongTag) Modifier.get("UUIDMost"),"UUID"));
                Modifier.remove("UUIDLeast");
                Modifier.remove("UUIDMost");
            }
            newList.add(new CompoundTag("",Modifier));
        }
        return new ListTag("Modifiers",CompoundTag.class,newList);
    }
}

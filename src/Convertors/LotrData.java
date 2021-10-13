package Convertors;

import misterymob475.Data;
import org.jnbt.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static misterymob475.Main.PrintLine;

//this class fixes the data in the LOTR folder, this also means it renames the folder to lotr (so to lower case)

/**
 * Copies and fixes the contents of the lotr/ folder
 */
public class LotrData implements Convertor {
	private final Data Data;

	/**
	 * @param data instance of {@link Data}
	 */

	public LotrData(Data data) {
		this.Data = data;
	}

	/**
	 *
	 * @param p path of the folder where files are copied
	 * @param FileName name of the to be modified files
	 * @throws IOException if something fails
	 */
	@Override
	public void modifier(Path p, String FileName) throws IOException {
		Map<String,String> Waypoints = Data.Waypoints();
		Map<String,String> Regions = Data.Regions();
		Map<String,String> FacNames = Data.FacNames();

		Files.createDirectory(Paths.get(p +"/"+FileName+"_Converted/lotr/"));
		Files.createDirectory(Paths.get(p +"/"+FileName+"_Converted/lotr/players"));
		//current working folder
		File currentFolder = new File(Paths.get(p +"/"+FileName+"/LOTR").toString());
		//File currentFolder = new File(Paths.get(p +"/"+FileName+"_Converted/lotr").toString());
		if (currentFolder.exists()) {
			if (new File(Paths.get(currentFolder+"/LOTRTime.dat").toString()).exists()) {
				Files.copy(Paths.get(p +"/"+FileName+"/LOTR/LOTRTime.dat"),Paths.get(p +"/"+FileName+"_Converted/lotr/LOTRTime.dat"));
			}
		}

		/*
		try {
			//technically I could simply not run this, I did this to learn the handling of the tags as JNBT doesn't have proper documentation

		    //Explanation of the tags:
			//LOTRTime.dat fix
			//discards the TotalWorldTime IntTag as it isn't in renewed yet

			//opens the file as a stream and saves the result as a CompoundTag
			//final NBTInputStream input = new NBTInputStream(new FileInputStream(currentFolder+"/LOTRTime.dat"));
			//final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
			//input.close();
			//saves the input as a map, this is important for saving the file, for reading it is redundant
			//Map<String, Tag> originalData = originalTopLevelTag.getValue();
			//gets the value we want
			//LongTag LotrWorldTime = (LongTag) originalData.get("LOTRWorldTime");

			//creates a new map as the old one was immutable, the 1 is the amount of values stored (in this case 1)
			//final Map<String, Tag> newData = new HashMap<>(1);
			//puts the data in
			//newData.put("LOTRWorldTime",LotrWorldTime);
			//creates the new top level tag, otherwise it won't work
			//final CompoundTag newTopLevelTag = new CompoundTag("", newData);

			//creates an output stream, this overwrites the file so deleting it is not necessary
			//final NBTOutputStream output = new NBTOutputStream(new FileOutputStream(currentFolder+"/LOTRTime.dat"));
			//output.writeTag(newTopLevelTag);
			//output.close();

            final NBTInputStream input = new NBTInputStream(new FileInputStream(currentFolder+"/LOTRTime.dat"));
            final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
            input.close();

			Map<String, Tag> newData = new HashMap<>(originalTopLevelTag.getValue());
			newData.remove("LOTRTotalTime");
            //newData.put("LOTRWorldTime",(originalTopLevelTag.getValue()).get("LOTRWorldTime"));
            final CompoundTag newTopLevelTag = new CompoundTag("", newData);

            final NBTOutputStream output = new NBTOutputStream(new FileOutputStream(currentFolder+"/LOTRTime.dat"));
            output.writeTag(newTopLevelTag);
            output.close();

			System.out.println("converted LOTRTime.dat");
		}
		 */

		try {
			//LOTR.dat fix

			//opens the file as a stream and saves the result as a CompoundTag
			final NBTInputStream input = new NBTInputStream(new FileInputStream(currentFolder+"/LOTR.dat"));
			final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
			input.close();
			//saves the input as a map, this is important for saving the file, for reading it is redundant
			Map<String, Tag> originalData = new HashMap<>(originalTopLevelTag.getValue());
			//discards: as they aren't in renewed yet or are now datapackable, if something gets ported to renewed in the exact same way as legacy I can simply uncomment these lines
			originalData.remove("TravellingTraders");
			originalData.remove("GreyWanderers");
			originalData.remove("AlignmentZones");
			originalData.remove("ConqRate");
			originalData.remove("DifficultyLock");
			originalData.remove("GollumSpawned");
			originalData.remove("GWSpawnTick");
			originalData.remove("StructuresBanned");

			IntTag CurrentDay = new IntTag("CurrentDay", ((IntTag) ((CompoundTag) originalData.get("Dates")).getValue().get("ShireDate")).getValue());
			Map<String,Tag> Dates_map = new HashMap<>();
			Dates_map.put("CurrentDay",CurrentDay);
			CompoundTag Dates = new CompoundTag("Dates",Dates_map);
			originalData.replace("Dates",Dates);
			originalData.replace("MadeMiddlePortal",new ByteTag("MadeMiddlePortal",(byte)(int) originalData.get("MadeMiddlePortal").getValue()));
			originalData.replace("MadePortal",new ByteTag("MadePortal",(byte)(int) originalData.get("MadePortal").getValue()));

			//creates the new top level tag, otherwise it won't work
			final CompoundTag newTopLevelTag = new CompoundTag("", originalData);

			//creates an output stream, this overwrites the file so deleting it is not necessary
			final NBTOutputStream output = new NBTOutputStream(new FileOutputStream(Paths.get(p +"/"+FileName+"_Converted/lotr/LOTR.dat").toString()));
			output.writeTag(newTopLevelTag);
			output.close();
			System.out.println("converted LOTR.dat");
		}
		//took this out of an example I found, changed it as my ide wanted me to
		catch (final ClassCastException | NullPointerException ex) {
			throw new IOException("Error during LOTR.dat fix");
		}

		//players folder fix
		//disregards anything that isn't in renewed yet (obviously)
		File PlayerDir = new File(currentFolder+"/players");
		int i = 1;
		for (File playerFile : Objects.requireNonNull(PlayerDir.listFiles())) {
			i++;
			try {
				//opens the file as a stream and saves the result as a CompoundTag
				final NBTInputStream input = new NBTInputStream(new FileInputStream(playerFile));
				final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
				input.close();
				//saves the input as a map, this is important for saving the file, for reading it is redundant
				Map<String, Tag> originalData = new HashMap<>(originalTopLevelTag.getValue());
				//gets the values we want, note, = I'm doing the easy ones first (lists last) I'm keeping the order though as I've read somewhere that that matters

			ListTag AlignmentMap_old = (ListTag) originalData.get("AlignmentMap");
			List<Tag> AlignmentMap_builder = new ArrayList<Tag>(1) {};
			for (Tag tag : AlignmentMap_old.getValue()) {
				StringTag Faction_tag = (StringTag) ((CompoundTag)tag).getValue().get("Faction");
				String Faction = Faction_tag.getValue();
				if (FacNames.containsKey(Faction)) {
					final Map<String, Tag> newData_CF = new HashMap<>(1);
					newData_CF.put("AlignF", ((CompoundTag)tag).getValue().get("AlignF"));
					newData_CF.put("Faction",new StringTag("Faction",FacNames.get(Faction)));
					CompoundTag AM_CT_Builder = new CompoundTag("",newData_CF);
					AlignmentMap_builder.add(AM_CT_Builder);
				}
			}
			//ListTag AlignmentMap = new ListTag("AlignmentMap",CompoundTag.class, AlignmentMap_builder);

			ListTag FactionStats_old = (ListTag) originalData.get("FactionData");
			List<Tag> FactionStats_builder = new ArrayList<Tag>(1) {};
			for (Tag tag : FactionStats_old.getValue()) {
				StringTag Faction_tag_AL = (StringTag) ((CompoundTag)tag).getValue().get("Faction");
				String Faction_AL = Faction_tag_AL.getValue();
				if (FacNames.containsKey(Faction_AL)) {
					final Map<String, Tag> newData_AL = new HashMap<>(1);
					newData_AL.put("ConquestHorn", ((CompoundTag)tag).getValue().get("ConquestHorn"));
					newData_AL.put("EnemyKill", ((CompoundTag)tag).getValue().get("EnemyKill"));
					newData_AL.put("Faction",new StringTag("Faction",FacNames.get(Faction_AL)));
					newData_AL.put("Hired", ((CompoundTag)tag).getValue().get("Hired"));
					newData_AL.put("MemberKill", ((CompoundTag)tag).getValue().get("NPCKill"));
					newData_AL.put("MiniQuests", ((CompoundTag)tag).getValue().get("MiniQuests"));
					newData_AL.put("Trades", ((CompoundTag)tag).getValue().get("Trades"));
					CompoundTag AM_AL_Builder = new CompoundTag("",newData_AL);
					FactionStats_builder.add(AM_AL_Builder);
				}
			}
			//ListTag FactionStats = new ListTag("FactionStats",CompoundTag.class, FactionStats_builder);

                ListTag PrevRegionFactions_Old = (ListTag) originalData.get("PrevRegionFactions");
                List<Tag> PrevRegionFactions_builder = new ArrayList<Tag>(1) {};
                for (Tag tag : PrevRegionFactions_Old.getValue()) {
                    StringTag Faction_tag_PRF = (StringTag) ((CompoundTag)tag).getValue().get("Faction");
                    String Region_PRF = ((StringTag) ((CompoundTag)tag).getValue().get("Region")).getValue();
                    String Faction_PRF = Faction_tag_PRF.getValue();
					if (FacNames.containsKey(Faction_PRF)) {
                        final Map<String, Tag> newData_PRF = new HashMap<>(1);
                        newData_PRF.put("Faction",new StringTag("Faction",FacNames.get(Faction_PRF)));
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
                        CompoundTag PRF_AL_Builder = new CompoundTag("",newData_PRF);
                        PrevRegionFactions_builder.add(PRF_AL_Builder);
                    }
                }
                //ListTag PrevRegionFactions = new ListTag("PrevRegionFactions",CompoundTag.class, PrevRegionFactions_builder);

				//SentMessageTypes

				ListTag UnlockedFTRegions_Old = (ListTag) originalData.get("UnlockedFTRegions");
				List<Tag> UnlockedFTRegions_Builder = new ArrayList<Tag>(0) {};
				for (Tag tag : UnlockedFTRegions_Old.getValue()) {
					StringTag RegionName_Tag = (StringTag) ((CompoundTag)tag).getValue().get("Name");
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
					if (Regions.containsKey(RegionName)) {
						StringTag Name = new StringTag("",Regions.get(RegionName));
						UnlockedFTRegions_Builder.add(Name);

					}
				}
				//ListTag UnlockedFTRegions = new ListTag("UnlockedFTRegions",StringTag.class, UnlockedFTRegions_Builder);

				//get the old WPUses
				ListTag WPUses_old = (ListTag) originalData.get("WPUses");
				//create a new empty array put the new WPUses in
				List<Tag> WPUses_builder = new ArrayList<Tag>(1) {};
				//loop though the entries in the list
				for (Tag tag : WPUses_old.getValue()) {
					//get the StringTag of the waypoint
					StringTag WPName_tag = (StringTag) ((CompoundTag)tag).getValue().get("WPName");
					//convert to string
					String WPName = WPName_tag.getValue();
					//if the waypoint exists in renewed (not everything has been ported yet)
					if (Waypoints.containsKey(WPName)) {
						//create empty map for the CompoundTag
						final Map<String, Tag> newData_WP = new HashMap<>(1);
						//put in the amount of waypoint usage (cooldown depends on it)
						newData_WP.put("Count", ((CompoundTag)tag).getValue().get("Count"));
						//put in the new name
						newData_WP.put("WPName",new StringTag("WPName",Waypoints.get(WPName)));
						//create the CompoundTag
						CompoundTag WPUses_CT_Builder = new CompoundTag("",newData_WP);
						//add the CompoundTag to the List
						WPUses_builder.add(WPUses_CT_Builder);
					}
				}
				//create the ListTag from the List
				//ListTag WPUses = new ListTag("WPUses",CompoundTag.class, WPUses_builder);




				//the game will add missing items itself, hence the commented out fields
				//ByteTag ShowMapMarkers = new ByteTag("ShowMapMarkers", (byte) 1);

				//removes redundant data (when said info gets ported I can simply uncomment it)
				originalData.remove("QuestData");
				originalData.remove("Achievements");
				originalData.remove("SentMessageTypes"); //not sure what this does
				originalData.remove("BountiesPlaced");
				originalData.remove("CustomWayPoints"); //additional requirements in renewed, might port these later as a thing you can only use once
				originalData.remove("CWPSharedHidden");
				originalData.remove("CWPSharedUnlocked");
				originalData.remove("CWPSharedUses");
				originalData.remove("CWPUses");
				originalData.remove("FellowshipInvites");
				originalData.remove("Fellowships");
				originalData.remove("MiniQuests");
				originalData.remove("MiniQuestsCompleted");
				originalData.remove("TakenAlignmentRewards");
				originalData.remove("AdminHideMap");
				originalData.remove("Chosen35Align");
				originalData.remove("ConquestKills");
				originalData.remove("HideAlignment");
				originalData.remove("HideOnMap");
				originalData.remove("HiredDeathMessages");
				originalData.remove("LastBiome");
				originalData.remove("MiniQuestTrack");
				originalData.remove("MQCompleteCount");
				originalData.remove("MQCompletedBounties");
				originalData.remove("Pre35Align");
				originalData.remove("ShowHiddenSWP");
				originalData.remove("StructuresBanned");
				originalData.remove("ChatBoundFellowship");
				originalData.remove("DeathDim");

				originalData.replace("AlignmentMap",new ListTag("AlignmentMap",CompoundTag.class, AlignmentMap_builder));
				originalData.replace("FactionStats",new ListTag("FactionStats",CompoundTag.class, FactionStats_builder));
				originalData.replace("PrevRegionFactions",new ListTag("PrevRegionFactions",CompoundTag.class, PrevRegionFactions_builder));
				originalData.replace("UnlockedFTRegions",new ListTag("UnlockedFTRegions",StringTag.class, UnlockedFTRegions_Builder));
				originalData.replace("WPUses",new ListTag("WPUses",CompoundTag.class, WPUses_builder));
				originalData.replace("CurrentFaction",new StringTag("CurrentFaction",FacNames.getOrDefault(originalData.get("CurrentFaction").getValue().toString(),"lotr:hobbit")));

				if (Objects.equals(originalData.get("TeleportedME").getValue(), (byte) 1)) {
					originalData.replace("TeleportedME",(new ByteTag("InitialSpawnedIntoME", (byte) 0)));
				}
				else {
					originalData.replace("TeleportedME",(new ByteTag("InitialSpawnedIntoME", (byte) 1)));
				}

				//Byte in legacy, string in renewed, because of this you can replace it in the stream
				if (Objects.equals(originalData.get("FemRankOverride").getValue(), (byte) 0)) {
					originalData.put("RankGender",(new StringTag("RankGender","M")));

				}
				else {
					originalData.put("RankGender",(new StringTag("RankGender","F")));
					// "FLOPPA_CAT" Mevans, really?
				}

				originalData.remove("FemRankOverride");
				if (originalData.containsKey("HideOnMap")) {
					if (Objects.equals(originalData.get("HideOnMap").getValue(), (byte) 1)) {
						originalData.replace("HideOnMap",new ByteTag("ShowMapLocation", (byte) 0));
					}
					else {
						originalData.replace("HideOnMap",new ByteTag("ShowMapLocation", (byte) 1));
					}
				}


				//creates the new top level tag, otherwise it won't work
				final CompoundTag newTopLevelTag = new CompoundTag("", originalData);
				//creates an output stream, this overwrites the file so deleting it is not necessary
				//final NBTOutputStream output = new NBTOutputStream(new FileOutputStream((new File(Paths.get(p +"/"+FileName+"_Converted/data/" + mapFile.getName()).toString())).getAbsolutePath()));
				//playerFile
				//final NBTOutputStream output = new NBTOutputStream(new FileOutputStream((new File(Paths.get(p +"/"+FileName+"_Converted/lotr/" + playerFile.getName()).toString())).getAbsolutePath()));
				final NBTOutputStream output = new NBTOutputStream(new FileOutputStream((new File(Paths.get(p +"/"+FileName+"_Converted/lotr/players/" + playerFile.getName()).toString())).getAbsolutePath()));
				output.writeTag(newTopLevelTag);

				output.close();
			}
			//took this out of an example I found, changed it as my ide wanted me to
			catch (final ClassCastException | NullPointerException ex) {
				throw new IOException("Error during playerData conversion fix");
			}
			PrintLine("Converted " + (i-1) + "/" + Objects.requireNonNull(PlayerDir.listFiles()).length + " Playerfiles",Data,true);
		}
		System.out.println("Converted all the player files in the /lotr folder");
	}
}

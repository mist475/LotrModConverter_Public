package Convertors;

import misterymob475.Data;
import lib.jnbt.jnbt.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

//this class fixes the data in the LOTR folder, this also means it renames the folder to lotr (so to lower case)
public class LotrData implements Convertor {
	//directory copying without using commons-io (sigh)
	public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation)
			throws IOException {
		Files.walk(Paths.get(sourceDirectoryLocation))
				.forEach(source -> {
					Path destination = Paths.get(destinationDirectoryLocation, source.toString()
							.substring(sourceDirectoryLocation.length()));
					try {
						Files.copy(source, destination);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
	}


	@Override
	public void copier(Path p, String FileName) throws IOException {
		//copies over all the files in the LOTR folder to the lotr folder
		File src = new File(Paths.get(p.toString()+"/"+FileName+"/LOTR").toString());
		File out = new File(Paths.get(p +"/"+FileName+"_Converted/lotr").toString());
		copyDirectory(src.getAbsolutePath(), out.getAbsolutePath());
		//remove the unnecessary files (at least for now)
		Files.deleteIfExists(Paths.get(src+"/conquest_zones"));
		Files.deleteIfExists(Paths.get(src+"/factionbounties"));
		Files.deleteIfExists(Paths.get(src+"/faction_relations.dat"));
		Files.deleteIfExists(Paths.get(src+"/spawn_damping.dat"));
	}

	@Override
	public void modifier(Path p, String FileName) throws IOException {
		HashMap<String,String> Waypoints = Data.Waypoints();
		HashMap<String,String> Regions = Data.Regions();
		HashMap<String,String> FacNames = Data.FacNames();

		//current working folder
		File currentFolder = new File(Paths.get(p +"/"+FileName+"_Converted/lotr").toString());
		try {
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

            final Map<String, Tag> newData = new HashMap<>(1);
            newData.put("LOTRWorldTime",(originalTopLevelTag.getValue()).get("LOTRWorldTime"));
            final CompoundTag newTopLevelTag = new CompoundTag("", newData);

            final NBTOutputStream output = new NBTOutputStream(new FileOutputStream(currentFolder+"/LOTRTime.dat"));
            output.writeTag(newTopLevelTag);
            output.close();

			System.out.println("converted LOTRTime.dat");
		}
		//took this out of an example I found, changed it as my ide wanted me to
		catch (final ClassCastException | NullPointerException ex) {
			throw new IOException("Error during LOTRTime.dat fix");
		}

		try {
			//LOTR.dat fix
			//discards: as they aren't in renewed yet or are now datapackable
			// (CompoundTag) Travelling Traders
			// GreyWanderers (list of some sort, probably int)
			// (ByteTag) AlignmentZones
			// (FloatTag) ConqRate
			// (ByteTag) DifficultyLock
			// (ByteTag) GollumSpawned
			// (IntTag) GWSpawnTick
			// (IntTag) StructuresBanned


			//opens the file as a stream and saves the result as a CompoundTag
			final NBTInputStream input = new NBTInputStream(new FileInputStream(currentFolder+"/LOTR.dat"));
			final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
			input.close();
			//saves the input as a map, this is important for saving the file, for reading it is redundant
			Map<String, Tag> originalData = originalTopLevelTag.getValue();
			//gets the values we want

			Map<String, Tag> Dates = new HashMap<>(1);
			Dates.put("CurrentDay",((CompoundTag) originalData.get("Dates")).getValue().get("ShireDate"));

			//creates a new map as the old one was immutable
			final Map<String, Tag> newData = new HashMap<>(1);
			//puts the data in
			newData.put("Dates",new CompoundTag("Dates",Dates));
			newData.put("MadeMiddlePortal",new ByteTag("MadeMiddlePortal",(byte)(int) originalData.get("MadeMiddlePortal").getValue()));
			newData.put("MadePortal",new ByteTag("MadePortal",(byte)(int) originalData.get("MadePortal").getValue()));
			newData.put("MiddleEarthX",originalData.get("MiddleEarthX"));
			newData.put("MiddleEarthY",originalData.get("MiddleEarthY"));
			newData.put("MiddleEarthZ",originalData.get("MiddleEarthZ"));
			newData.put("OverWorldX",originalData.get("OverworldX"));
			newData.put("OverWorldY",originalData.get("OverworldY"));
			newData.put("OverWorldZ",originalData.get("OverworldZ"));
			newData.put("WpCdMax",originalData.get("WpCdMax"));
			newData.put("WpCdMin",originalData.get("WpCdMin"));
			//creates the new top level tag, otherwise it won't work
			final CompoundTag newTopLevelTag = new CompoundTag("", newData);

			//creates an output stream, this overwrites the file so deleting it is not necessary
			final NBTOutputStream output = new NBTOutputStream(new FileOutputStream(currentFolder+"/LOTR.dat"));
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
				Map<String, Tag> originalData = originalTopLevelTag.getValue();
				//gets the values we want, note, = I'm doing the easy ones first (lists last) I'm keeping the order though as I've read somewhere that that matters

			ListTag AlignmentMap_old = (ListTag) originalData.get("AlignmentMap");
			List<Tag> AlignmentMap_builder = new ArrayList(1) {};
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
			List<Tag> FactionStats_builder = new ArrayList(1) {};
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
                List<Tag> PrevRegionFactions_builder = new ArrayList(1) {};
                for (Tag tag : PrevRegionFactions_Old.getValue()) {
                    StringTag Faction_tag_PRF = (StringTag) ((CompoundTag)tag).getValue().get("Faction");
                    String Region_PRF = ((StringTag) ((CompoundTag)tag).getValue().get("Region")).getValue();
                    String Faction_PRF = Faction_tag_PRF.getValue();
					assert FacNames != null;
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
				List<Tag> UnlockedFTRegions_Builder = new ArrayList(0) {};
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
				List<Tag> WPUses_builder = new ArrayList(1) {};
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

				Byte InitialSpawnedIntoME_old = (Byte) originalData.get("TeleportedME").getValue();
				ByteTag InitialSpawnedIntoME;
				if (Objects.equals(InitialSpawnedIntoME_old, (byte) 1)) {
					InitialSpawnedIntoME = new ByteTag("ShowMapLocation", (byte) 0);
				}
				else {
					InitialSpawnedIntoME = new ByteTag("ShowMapLocation", (byte) 1);
				}

				//Byte in legacy, string in renewed
				Byte RankGender_old = (Byte) originalData.get("FemRankOverride").getValue();
				StringTag RankGender;
				if (Objects.equals(RankGender_old, (byte) 0)) {
					RankGender = new StringTag("RankGender","M");
				}
				else {
					RankGender = new StringTag("RankGender","F");
					// "FLOPPA_CAT" Mevans, really?
				}

				//there must be an easier way for this but oh well
				Byte ShowMapLocation_old = (Byte) originalData.get("HideOnMap").getValue();
				ByteTag ShowMapLocation;
				if (Objects.equals(ShowMapLocation_old, (byte) 1)) {
					ShowMapLocation = new ByteTag("ShowMapLocation", (byte) 0);
				}
				else {
					ShowMapLocation = new ByteTag("ShowMapLocation", (byte) 1);
				}
				//the game will add missing items itself, hence the commented out fields
				//ByteTag ShowMapMarkers = new ByteTag("ShowMapMarkers", (byte) 1);

				//creates a new map as the old one was immutable, the 1 is the amount of values stored (in this case 1)
				final Map<String, Tag> newData = new HashMap<>(1);
				//puts the data in
				newData.put("AlignmentMap",new ListTag("AlignmentMap",CompoundTag.class, AlignmentMap_builder));
				newData.put("FactionStats",new ListTag("FactionStats",CompoundTag.class, FactionStats_builder));
				newData.put("PrevRegionFactions",new ListTag("PrevRegionFactions",CompoundTag.class, PrevRegionFactions_builder));
				//newData.put("SentMessageTypes",SentMessageTypes); not gonna do unless someone really bugs me about it, not sure what it does
				newData.put("UnlockedFTRegions",new ListTag("UnlockedFTRegions",StringTag.class, UnlockedFTRegions_Builder));
				newData.put("WPUses",new ListTag("WPUses",CompoundTag.class, WPUses_builder));
				newData.put("Alcohol",originalData.get("Alcohol"));
				newData.put("CurrentFaction",new StringTag("CurrentFaction",Data.getOrDefault(FacNames,originalData.get("CurrentFaction").getValue().toString(),"lotr:hobbit")));
				newData.put("FriendlyFire",originalData.get("FriendlyFire"));
				newData.put("FTSince",originalData.get("FTSince"));
				newData.put("InitialSpawnedIntoME",InitialSpawnedIntoME);
				newData.put("LastOnlineTime",originalData.get("LastOnlineTime"));
				newData.put("MountUUIDTime",originalData.get("MountUUIDTime"));
				newData.put("NextCWPID",originalData.get("NextCWPID"));
				//newData.put("NextMapMarkerId",NextMapMarkerID);
				newData.put("PledgeBreakCD",originalData.get("PledgeBreakCD"));
				newData.put("PledgeBreakCDStart",originalData.get("PledgeBreakCDStart"));
				newData.put("PledgeKillCD",originalData.get("PledgeKillCD"));
				newData.put("RankGender",RankGender);
				newData.put("ShowCWP",originalData.get("ShowCWP"));
				newData.put("ShowMapLocation",ShowMapLocation);
				//newData.put("ShowMapMarkers",ShowMapMarkers);
				newData.put("ShowWP",originalData.get("ShowWP"));

				//creates the new top level tag, otherwise it won't work
				final CompoundTag newTopLevelTag = new CompoundTag("", newData);
				//creates an output stream, this overwrites the file so deleting it is not necessary
				final NBTOutputStream output = new NBTOutputStream(new FileOutputStream(playerFile.getAbsolutePath()));
				output.writeTag(newTopLevelTag);

				output.close();
			}
			//took this out of an example I found, changed it as my ide wanted me to
			catch (final ClassCastException | NullPointerException ex) {
				throw new IOException("Error during playerData conversion fix");
			}
			System.out.println("Converted " + (i-1) + "/" + Objects.requireNonNull(PlayerDir.listFiles()).length + " Playerfiles");
		}
		System.out.println("Converted all the player files in the /lotr folder");
	}
}

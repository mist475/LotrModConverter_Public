package Convertors;

import de.piegames.nbt.CompoundMap;
import de.piegames.nbt.CompoundTag;
import de.piegames.nbt.stream.NBTInputStream;
import de.piegames.nbt.stream.NBTOutputStream;
import misterymob475.Data;
import misterymob475.Fixers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

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
     * @param p        path of the folder where files are copied
     * @param FileName name of the to be modified files
     * @throws IOException if something fails
     */
    @Override
    public void modifier(Path p, String FileName) throws IOException {


        Files.createDirectory(Paths.get(p + "/" + FileName + "_Converted/lotr/"));
        Files.createDirectory(Paths.get(p + "/" + FileName + "_Converted/lotr/players"));
        //current working folder
        File currentFolder = new File(Paths.get(p + "/" + FileName + "/LOTR").toString());
        //File currentFolder = new File(Paths.get(p +"/"+FileName+"_Converted/lotr").toString());
        if (currentFolder.exists()) {
            if (new File(Paths.get(currentFolder + "/LOTRTime.dat").toString()).exists()) {
                Files.copy(Paths.get(p + "/" + FileName + "/LOTR/LOTRTime.dat"), Paths.get(p + "/" + FileName + "_Converted/lotr/LOTRTime.dat"));
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
            final NBTInputStream input = new NBTInputStream(new FileInputStream(currentFolder + "/LOTR.dat"));
            final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
            input.close();
            //saves the input as a map, this is important for saving the file, for reading it is redundant
            CompoundMap originalData = new CompoundMap(originalTopLevelTag.getValue());

            Fixers.LOTRDatFixer(originalData);

            //creates the new top level tag, otherwise it won't work
            final CompoundTag newTopLevelTag = new CompoundTag("", originalData);

            //creates an output stream, this overwrites the file so deleting it is not necessary
            final NBTOutputStream output = new NBTOutputStream(new FileOutputStream(Paths.get(p + "/" + FileName + "_Converted/lotr/LOTR.dat").toString()));
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
        File PlayerDir = new File(currentFolder + "/players");
        int i = 1;
        for (File playerFile : Objects.requireNonNull(PlayerDir.listFiles())) {
            i++;
            try {
                //opens the file as a stream and saves the result as a CompoundTag
                final NBTInputStream input = new NBTInputStream(new FileInputStream(playerFile));
                final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
                input.close();
                //saves the input as a map, this is important for saving the file, for reading it is redundant
                CompoundMap originalData = new CompoundMap(originalTopLevelTag.getValue());
                Fixers.LOTRPlayerDataFixer(originalData, Data);


                //creates the new top level tag, otherwise it won't work
                final CompoundTag newTopLevelTag = new CompoundTag("", originalData);
                //creates an output stream, this overwrites the file so deleting it is not necessary
                //final NBTOutputStream output = new NBTOutputStream(new FileOutputStream((new File(Paths.get(p +"/"+FileName+"_Converted/data/" + mapFile.getName()).toString())).getAbsolutePath()));
                //playerFile
                //final NBTOutputStream output = new NBTOutputStream(new FileOutputStream((new File(Paths.get(p +"/"+FileName+"_Converted/lotr/" + playerFile.getName()).toString())).getAbsolutePath()));
                final NBTOutputStream output = new NBTOutputStream(new FileOutputStream((new File(Paths.get(p + "/" + FileName + "_Converted/lotr/players/" + playerFile.getName()).toString())).getAbsolutePath()));
                output.writeTag(newTopLevelTag);

                output.close();
            }
            //took this out of an example I found, changed it as my ide wanted me to
            catch (final ClassCastException | NullPointerException ex) {
                throw new IOException("Error during playerData conversion fix");
            }
            PrintLine("Converted " + (i - 1) + "/" + Objects.requireNonNull(PlayerDir.listFiles()).length + " Playerfiles", Data, true);
        }
        System.out.println("Converted all the player files in the /lotr folder");
    }
}

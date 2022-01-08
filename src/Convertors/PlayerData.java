package Convertors;

import de.piegames.nbt.CompoundMap;
import de.piegames.nbt.CompoundTag;
import de.piegames.nbt.stream.NBTInputStream;
import de.piegames.nbt.stream.NBTOutputStream;
import misterymob475.Data;
import misterymob475.Fixers;
import misterymob475.StringCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//this class fixes the regular player data (the files in the playerdata folder) and the level.dat file (mainly because playerdata is also stored in there)

/**
 * Copies and fixes the regular player data
 */
public class PlayerData implements Convertor {
    private final Data Data;
    private final StringCache stringCache;

    /**
     * Creates an instance of PlayerData
     *
     * @param data        instance of {@link Data}
     * @param stringCache instance of {@link StringCache}
     */
    public PlayerData(Data data, StringCache stringCache) {
        this.Data = data;
        this.stringCache = stringCache;
    }

    /**
     * @param p        path of the folder where files are copied
     * @param FileName name of the to be modified files
     * @throws IOException if something fails
     */
    @Override
    public void modifier(Path p, String FileName) throws IOException {
        //Map<Integer,String> LegacyIds = misterymob475.Data.LegacyIds(Paths.get(p + "/" + FileName+ "/level.dat").toAbsolutePath().toString());
        Files.createDirectory(Paths.get(p + "/" + FileName + "_Converted/playerdata"));
        //level.dat fixer/modifier
        //File renewedWorld = new File(p+"/"+this.pathName+"/level.dat");

        //try {
        //heavier filter on here to only use the current .dat's and not the old ones
        File currentFolder = new File(Paths.get(p + "/" + FileName + "/playerdata").toString());
        File[] curDirList = currentFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".dat"));
        int i = 1;
        if (curDirList != null) {
            for (File f : curDirList) {
                i++;
                final NBTInputStream input = new NBTInputStream(new FileInputStream(f));
                final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
                input.close();

                //because of the way playerdata is stored in the level.dat I have moved the fixer to a slightly different function lmao
                CompoundMap originalData = originalTopLevelTag.getValue();
                //this way I can modify the map directly, instead of regenerating it every time
                CompoundMap newData = new CompoundMap(originalData);
//
                Fixers.playerFixer(newData, stringCache, Data);
//

                final CompoundTag newTopLevelTag = new CompoundTag("", newData);
                //(new File(Paths.get(p +"/"+FileName+"_Converted/playerdata/" + f.getName()).toString())).getAbsolutePath()
                //final NBTOutputStream output = new NBTOutputStream(new FileOutputStream(f));
                final NBTOutputStream output = new NBTOutputStream(new FileOutputStream((new File(Paths.get(p + "/" + FileName + "_Converted/playerdata/" + f.getName()).toString())).getAbsolutePath()));
                output.writeTag(newTopLevelTag);
                output.close();
                //stringCache.PrintLine("Converted " + (i - 1) + "/ " + Objects.requireNonNull(currentFolder.listFiles()).length + " player data files", true);
            }
            System.out.println("converted all the playerdata");
        }

            /*


        }
        //took this out of an example I found, changed it as my ide wanted me to
        catch (final ClassCastException | NullPointerException ex) {
            throw new IOException("Error during playerdata fixing");
        }
             */
        //planning for playerdata:
        //note: for blocks the "Damage" ShortTag is actually the second id: regular dwarven brick = (Usually) 186 with 'Damage' 6, 'Damage' is always the same, id is not though
        //items are present in "EnderItems" & "Inventory" (+ tile entities and such ofc, but those will only get looked at once regular blocks have been converted which will take a very long time)

        // 'fixer' will have to be called recursively due to pouches/ cracker with a max conversion depth (probably 7 or something like that), might put in a pieced of paper saying max depth reached or something similar
        // will have to look up 'special tags', so I have to modify the existing stream (i.e. make a map of it and replace it)
    }


}

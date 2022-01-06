package Convertors;

import de.piegames.nbt.CompoundMap;
import de.piegames.nbt.CompoundTag;
import de.piegames.nbt.IntTag;
import de.piegames.nbt.stream.NBTInputStream;
import de.piegames.nbt.stream.NBTOutputStream;
import misterymob475.Fixers;
import misterymob475.StringCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DataFolder implements Convertor {
    private final StringCache stringCache;

    /**
     * Creates an instance of HandMapData
     *
     * @param stringCache instance of {@link StringCache}
     */
    public DataFolder(StringCache stringCache) {
        this.stringCache = stringCache;
    }


    /**
     * Modifies the files to work in Renewed
     *
     * @param p        path of the folder where files are copied
     * @param FileName name of the to be modified files
     */
    @Override
    public void modifier(Path p, String FileName) throws IOException {
        //in this file: fixers for idcounts.dat and map_%.dat files
        //TODO: Rewrite using the proper way (previously impossible due to library restrictions)
        File currentFolder = new File(Paths.get(p + "/" + FileName + "/data").toString());
        Files.createDirectory(Paths.get(p + "/" + FileName + "_Converted/data"));
        if (currentFolder.exists()) {
            //idcounts fixer
            //map fixer (should be a quickie)
            File[] curDirList = currentFolder.listFiles((dir, name) -> name.toLowerCase().startsWith("map_"));
            if (curDirList != null && curDirList.length > 0) {
                int i = 1;
                for (File mapFile : curDirList) {

                    i++;
                    try {
                        //opens the file as a stream and saves the result as a CompoundTag
                        final NBTInputStream input = new NBTInputStream(new FileInputStream(mapFile));
                        final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
                        input.close();
                        //DataVersion = 2586
                        //saves the input as a map, this is important for saving the file, for reading it is redundant
                        CompoundMap originalData = new CompoundMap(originalTopLevelTag.getValue());
                        CompoundMap data = new CompoundMap(((CompoundTag) originalData.get("data")).getValue());

                        Fixers.MapFixer(data);

                        originalData.replace("data", new CompoundTag("data", data));
                        final CompoundTag newTopLevelTag = new CompoundTag("", originalData);
                        final NBTOutputStream output = new NBTOutputStream(new FileOutputStream((new File(Paths.get(p + "/" + FileName + "_Converted/data/" + mapFile.getName()).toString())).getAbsolutePath()));
                        output.writeTag(newTopLevelTag);
                        output.close();
                    }
                    //took this out of an example I found, changed it as my ide wanted me to
                    catch (Exception e) {
                        throw new IOException("Error during map conversion fix");
                    }
                    //stringCache.PrintLine("Converted " + (i - 1) + "/" + Objects.requireNonNull(curDirList).length + " maps", true);
                }

                stringCache.PrintLine("Converted all the maps", false);
                try {
                    if (new File(currentFolder + "/idcounts.dat").exists()) {

                        CompoundMap newData = new CompoundMap();
                        CompoundMap tMap = new CompoundMap();
                        tMap.put("map", new IntTag("map", curDirList.length - 1));
                        newData.put("map", new CompoundTag("data", tMap));

                        final CompoundTag newTopLevelTag = new CompoundTag("", newData);

                        final NBTOutputStream output = new NBTOutputStream(new FileOutputStream((new File(Paths.get(p + "/" + FileName + "_Converted/data/idcounts.dat").toString())).getAbsolutePath()));
                        output.writeTag(newTopLevelTag);
                        output.close();

                        stringCache.PrintLine("converted idcount.dat", false);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

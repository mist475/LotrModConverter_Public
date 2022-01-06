package Convertors;

import de.piegames.nbt.regionfile.Chunk;
import de.piegames.nbt.regionfile.RegionFile;
import misterymob475.Data;
import misterymob475.Fixers;
import misterymob475.StringCache;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class End implements Convertor {
    private final misterymob475.Data Data;
    private final StringCache stringCache;

    /**
     * Creates an instance of HandMapData
     *
     * @param data        instance of {@link misterymob475.Data}
     * @param stringCache instance of {@link StringCache}
     */
    public End(Data data, StringCache stringCache) {
        this.Data = data;
        this.stringCache = stringCache;
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
        File currentFolder = new File(Paths.get(p + "/" + FileName + "/DIM-1/region").toString());
        Files.createDirectory(Paths.get(p + "/" + FileName + "_Converted/DIM-1"));
        Files.createDirectory(Paths.get(p + "/" + FileName + "_Converted/DIM-1/region"));
        if (currentFolder.exists()) {
            File[] curDirList = currentFolder.listFiles();
            if (curDirList != null) {
                int i = 1;
                for (File mapFile : curDirList) {
                    i++;
                    try {
                        //opens the file as a stream and saves the result as a CompoundTag
                        RegionFile regionFile = RegionFile.openReadOnly(Paths.get(mapFile.getPath()));
                        HashMap<Integer, Chunk> chunks = new HashMap<>();
                        List<Integer> list = regionFile.listChunks();
                        for (int j : list) {
                            chunks.put(j, regionFile.loadChunk(j));
                        }
                        regionFile.close();
                        RegionFile new_Region = RegionFile.createNew(Paths.get(p + "/" + FileName + "_Converted/DIM-1/region/" + mapFile.getName()));

                        new_Region.writeChunks(Fixers.regionFixer(chunks, Data, stringCache));
                    } catch (Exception e) {
                        throw new IOException("Error during end dimension fix");
                    }
                    //stringCache.PrintLine("Converted " + (i - 1) + "/" + Objects.requireNonNull(curDirList).length + " region files of end dimension", true);
                }

                stringCache.PrintLine("Converted the end dimension", false);
            }
        }
    }
}

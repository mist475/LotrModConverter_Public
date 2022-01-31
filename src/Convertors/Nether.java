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
import java.util.Objects;

public class Nether implements Convertor {
    private final misterymob475.Data Data;
    private final StringCache stringCache;

    /**
     * Creates an instance of Nether
     *
     * @param data        instance of {@link misterymob475.Data}
     * @param stringCache instance of {@link StringCache}
     */
    public Nether(Data data, StringCache stringCache) {
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
        File currentFolder = new File(Paths.get(p + "/" + FileName + "/DIM1/region").toString());
        Files.createDirectory(Paths.get(p + "/" + FileName + "_Converted/DIM1"));
        Files.createDirectory(Paths.get(p + "/" + FileName + "_Converted/DIM1/region"));
        if (currentFolder.exists()) {
            File[] curDirList = currentFolder.listFiles();
            if (curDirList != null) {
                int i = 1;
                for (File mapFile : curDirList) {
                    i++;
                    try {
                        RegionFile regionFile = RegionFile.openReadOnly(Paths.get(mapFile.getPath()));
                        HashMap<Integer, Chunk> chunks = new HashMap<>();
                        List<Integer> list = regionFile.listChunks();
                        for (int j : list) {
                            chunks.put(j, regionFile.loadChunk(j));
                        }
                        regionFile.close();
                        RegionFile new_Region = RegionFile.createNew(Paths.get(p + "/" + FileName + "_Converted/DIM1/region/" + mapFile.getName()));

                        new_Region.writeChunks(Fixers.regionFixer(chunks, Data, stringCache));
                    } catch (Exception e) {
                        throw new IOException("Error during nether dimension fix");
                    }
                    stringCache.PrintLine("Converted " + (i - 1) + "/" + Objects.requireNonNull(curDirList).length + " nether dimension region files", true);
                }

                stringCache.PrintLine("Converted the nether dimension", false);
            }
        }
    }
}

package Convertors;

import de.piegames.nbt.regionfile.Chunk;
import de.piegames.nbt.regionfile.RegionFile;
import misterymob475.Data;
import misterymob475.Fixers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static misterymob475.Main.PrintLine;

public class Nether implements Convertor {
    private final misterymob475.Data Data;

    /**
     * Creates an instance of HandMapData
     *
     * @param data instance of {@link misterymob475.Data}
     */
    public Nether(Data data) {
        this.Data = data;
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

                        new_Region.writeChunks(Fixers.regionFixer(chunks, Data));
                    } catch (Exception e) {
                        throw new IOException("Error during nether dimension fix");
                    }
                    PrintLine("Converted " + (i - 1) + "/" + Objects.requireNonNull(curDirList).length + " nether dimension region files", Data, true);
                }

                PrintLine("Converted the nether dimension", Data, false);
            }
        }
    }
}

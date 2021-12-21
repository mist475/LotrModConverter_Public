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

public class MiddleEarth implements Convertor {
    private final misterymob475.Data Data;

    public MiddleEarth(Data Data) {
        this.Data = Data;
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
        File currentFolder = new File(Paths.get(p + "/" + FileName + "/MiddleEarth/region").toString());
        Files.createDirectory(Paths.get(p + "/" + FileName + "_Converted/dimensions"));
        Files.createDirectory(Paths.get(p + "/" + FileName + "_Converted/dimensions/lotr"));
        Files.createDirectory(Paths.get(p + "/" + FileName + "_Converted/dimensions/lotr/middle_earth"));
        Files.createDirectory(Paths.get(p + "/" + FileName + "_Converted/dimensions/lotr/middle_earth/region"));
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
                        RegionFile new_Region = RegionFile.createNew(Paths.get(p + "/" + FileName + "_Converted/dimensions/lotr/middle_earth/region/" + mapFile.getName()));

                        new_Region.writeChunks(Fixers.regionFixer(chunks, Data));
                    }
                    catch (Exception e) {
                        throw new IOException("Error during middle earth dimension fix");
                    }
                    PrintLine("Converted " + (i - 1) + "/" + Objects.requireNonNull(curDirList).length + " region files of middle earth dimension", Data, true);
                }

                PrintLine("Converted the middle earth dimension", Data, false);
            }
        }
    }
}

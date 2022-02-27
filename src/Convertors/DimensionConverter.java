package Convertors;

import de.piegames.nbt.regionfile.Chunk;
import de.piegames.nbt.regionfile.RegionFile;
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

public class DimensionConverter implements Convertor {
    private final misterymob475.Data Data;
    private final StringCache stringCache;
    private final String CurrentFolder;
    private final String[] CDStrings;
    private final String ExceptionMessage;
    private final String SingleFileMessage;
    private final String DoneMessage;


    /**
     * Constructor, implemented with parameters for standard conversion
     *
     * @param data              instance of {@link misterymob475.Data}
     * @param stringCache       instance of {@link StringCache}
     * @param currentFolder     {@link String} name or path of the current folder
     * @param cdStrings         {@link String}[] with pathnames for which a new directory must be created
     * @param exceptionMessage  {@link String} with exception message
     * @param singleFileMessage {@link String} containing what should be printed per file
     * @param doneMessage       {@link String} what should be printed when done
     */
    public DimensionConverter(misterymob475.Data data, StringCache stringCache, String currentFolder, String[] cdStrings, String exceptionMessage, String singleFileMessage, String doneMessage) {
        this.Data = data;
        this.stringCache = stringCache;
        this.CurrentFolder = currentFolder;
        this.CDStrings = cdStrings;
        this.ExceptionMessage = exceptionMessage;
        this.SingleFileMessage = singleFileMessage;
        this.DoneMessage = doneMessage;
    }

    /**
     * @param p        {@link Path} path to use
     * @param FileName {@link String} filename to use
     * @throws IOException When something fails
     */
    //modifies the information to be compatible
    public void modifier(Path p, String FileName) throws IOException {
        File currentFolder = new File(Paths.get(p + "/" + FileName + this.CurrentFolder).toString());

        for (String s : this.CDStrings) {
            Files.createDirectory(Paths.get(p + "/" + FileName + s));
        }
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
                        String PathToUse = p + "/" + FileName + this.CDStrings[this.CDStrings.length - 1] + '/' + mapFile.getName();
                        //System.out.println(Paths.get(PathToUse));
                        RegionFile new_Region = RegionFile.createNew(Paths.get(PathToUse));

                        //TODO: Start using multithreading here
                        HashMap<Integer,Chunk> result = Fixers.regionFixer(chunks, Data, stringCache);
                        new_Region.writeChunks(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new IOException(this.ExceptionMessage);
                    }
                    stringCache.PrintLine("Converted " + (i - 1) + "/" + Objects.requireNonNull(curDirList).length + this.SingleFileMessage, true);
                    //System.out.println("Converted " + (i - 1) + "/" + Objects.requireNonNull(curDirList).length + this.SingleFileMessage);
                }

                stringCache.PrintLine(this.DoneMessage, false);
            }
        }
    }
}


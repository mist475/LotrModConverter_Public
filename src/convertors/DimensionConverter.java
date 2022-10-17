package convertors;

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
    private final StringCache stringCache;
    private final String currentFolder;
    private final String[] cdStrings;
    private final String exceptionMessage;
    private final String singleFileMessage;
    private final String doneMessage;


    /**
     * Constructor, implemented with parameters for standard conversion
     *
     * @param currentFolder     {@link String} name or path of the current folder
     * @param cdStrings         {@link String}[] with pathnames for which a new directory must be created
     * @param exceptionMessage  {@link String} with exception message
     * @param singleFileMessage {@link String} containing what should be printed per file
     * @param doneMessage       {@link String} what should be printed when done
     */
    public DimensionConverter(String currentFolder, String[] cdStrings, String exceptionMessage, String singleFileMessage, String doneMessage) {
        this.stringCache = StringCache.getInstance();
        this.currentFolder = currentFolder;
        this.cdStrings = cdStrings;
        this.exceptionMessage = exceptionMessage;
        this.singleFileMessage = singleFileMessage;
        this.doneMessage = doneMessage;
    }

    /**
     * @param p        {@link Path} path to use
     * @param FileName {@link String} filename to use
     * @throws IOException When something fails
     */
    //modifies the information to be compatible
    public void modifier(Path p, String FileName) throws IOException {
        File currentFolder = new File(Paths.get(p + "/" + FileName + this.currentFolder).toString());

        for (String s : this.cdStrings) {
            Files.createDirectory(Paths.get(p + "/" + FileName + s));
        }
        if (currentFolder.exists()) {
            File[] curDirList = currentFolder.listFiles();
            Fixers fixers = new Fixers();
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
                        String PathToUse = p + "/" + FileName + this.cdStrings[this.cdStrings.length - 1] + '/' + mapFile.getName();
                        //System.out.println(Paths.get(PathToUse));
                        RegionFile new_Region = RegionFile.createNew(Paths.get(PathToUse));

                        //TODO: Start using multithreading here
                        HashMap<Integer, Chunk> result = fixers.regionFixer(chunks);
                        new_Region.writeChunks(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new IOException(this.exceptionMessage);
                    }
                    stringCache.printLine("Converted " + (i - 1) + "/" + Objects.requireNonNull(curDirList).length + this.singleFileMessage, true);
                    //System.out.println("Converted " + (i - 1) + "/" + Objects.requireNonNull(curDirList).length + this.SingleFileMessage);
                }

                stringCache.printLine(this.doneMessage, false);
            }
        }
    }
}


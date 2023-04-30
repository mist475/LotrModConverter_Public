package convertors;

import de.piegames.nbt.CompoundMap;
import de.piegames.nbt.CompoundTag;
import de.piegames.nbt.IntTag;
import de.piegames.nbt.ShortTag;
import de.piegames.nbt.stream.NBTInputStream;
import de.piegames.nbt.stream.NBTOutputStream;
import misterymob475.Fixers;
import misterymob475.StringCache;
import misterymob475.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

public class DataFolder implements Convertor {
    private final StringCache stringCache;

    /**
     * Creates an instance of HandMapData
     */
    public DataFolder() {
        this.stringCache = StringCache.getInstance();
    }


    /**
     * Modifies the files to work in Renewed
     *
     * @param p        path of the folder where files are copied
     * @param fileName name of the to be modified files
     */
    @Override
    public void modifier(Path p, String fileName) throws IOException {
        File currentFolder = new File(Paths.get(p + "/" + fileName + "/data").toString());
        Files.createDirectory(Paths.get(p + "/" + fileName + "_Converted/data"));
        if (currentFolder.exists()) {
            File[] curDirList = currentFolder.listFiles((dir, name) -> name.toLowerCase().startsWith("map_"));
            if (curDirList != null && curDirList.length > 0) {
                int i = 1;
                Fixers fixers = new Fixers();
                for (File mapFile : curDirList) {

                    i++;
                    try {
                        //opens the file as a stream and saves the result as a CompoundTag
                        final NBTInputStream input = new NBTInputStream(Files.newInputStream(mapFile.toPath()));
                        final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
                        input.close();
                        //DataVersion = 2586
                        //saves the input as a map, this is important for saving the file, for reading it is redundant
                        CompoundMap originalData = new CompoundMap(originalTopLevelTag.getValue());
                        CompoundMap data = new CompoundMap(((CompoundTag) originalData.get("data")).getValue());

                        fixers.mapFixer(data);

                        originalData.replace("data", new CompoundTag("data", data));
                        final CompoundTag newTopLevelTag = new CompoundTag("", originalData);
                        String pathToUse = p + "/" + fileName + "_Converted/data/" + mapFile.getName();
                        //System.out.println(Paths.get(pathToUse));
                        final NBTOutputStream output = new NBTOutputStream(Files.newOutputStream(Paths.get((new File(Paths.get(pathToUse)
                                                                                                                             .toString())).getAbsolutePath())));
                        output.writeTag(newTopLevelTag);
                        output.close();
                    }
                    //took this out of an example I found, changed it as my ide wanted me to
                    catch (Exception e) {
                        throw new IOException("Error during map conversion fix");
                    }
                    stringCache.printLine("Converted " + (i - 1) + "/" + Objects.requireNonNull(curDirList).length + " maps", true);
                }

                stringCache.printLine("Converted all the maps", false);
                try {
                    if (new File(currentFolder + "/idcounts.dat").exists()) {
                        final NBTInputStream input = new NBTInputStream(Files.newInputStream(Paths.get(currentFolder + "/idcounts.dat")), NBTInputStream.NO_COMPRESSION);
                        final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
                        input.close();

                        if (originalTopLevelTag.getValue().containsKey("map")) {
                            Optional<ShortTag> tag = originalTopLevelTag.getValue().get("map").getAsShortTag();
                            Optional<IntTag> newMap = tag.map(shortTag -> new IntTag("map", shortTag.getValue()));
                            if (newMap.isPresent()) {
                                final CompoundTag newTopLevelTag = new CompoundTag("", Util.createCompoundMapWithContents(new CompoundTag("data", Util.createCompoundMapWithContents(newMap.get())), new IntTag("DataVersion", 2586)));
                                String pathToUse = p + "/" + fileName + "_Converted/data/idcounts.dat";
                                final NBTOutputStream output = new NBTOutputStream(Files.newOutputStream(Paths.get((new File(Paths.get(pathToUse)
                                                                                                                                     .toString())).getAbsolutePath())), NBTInputStream.NO_COMPRESSION);
                                output.writeTag(newTopLevelTag);
                                output.close();
                                stringCache.printLine("converted idcounts.dat", false);
                            }
                        }
                    }

                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

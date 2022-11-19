package convertors;

import de.piegames.nbt.CompoundMap;
import de.piegames.nbt.CompoundTag;
import de.piegames.nbt.stream.NBTInputStream;
import de.piegames.nbt.stream.NBTOutputStream;
import misterymob475.Fixers;
import misterymob475.StringCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Copies {@literal &} Fixes the level.dat file
 */
public class LevelDat implements Convertor {
    final private String pathName;
    private final StringCache stringCache;

    /**
     * Creates an instance of LevelDat using the provided parameters
     *
     * @param pathname String of the pathname, see {@link Convertor}
     */
    public LevelDat(String pathname) {
        pathName = pathname;
        this.stringCache = misterymob475.StringCache.getInstance();
    }

    /**
     * @param p        path of the folder where files are copied
     * @param fileName name of the to be modified files
     * @throws IOException if something fails
     */
    @Override
    public void modifier(Path p, String fileName) throws IOException {
        try {
            final NBTInputStream legacyLevelDatStream = new NBTInputStream(Files.newInputStream(Paths.get(Paths.get(p + "/" + this.pathName + "/level.dat")
                                                                                                                  .toString())));
            final CompoundTag originalTopLevelTag = (CompoundTag) legacyLevelDatStream.readTag();
            legacyLevelDatStream.close();

            final NBTInputStream renewedLevelDatStream = new NBTInputStream(Files.newInputStream(Paths.get(Paths.get(p + "/" + fileName + "/level.dat")
                                                                                                                   .toString())));
            final CompoundTag renewedLevelDat = (CompoundTag) renewedLevelDatStream.readTag();
            renewedLevelDatStream.close();

            Fixers fixers = new Fixers();
            CompoundMap map = new CompoundMap(originalTopLevelTag.getValue());
            fixers.levelDatFixer(map, renewedLevelDat);

            final CompoundTag newTopLevelTag = new CompoundTag("", map);
            String pathToUse = p + "/" + fileName + "_Converted/level.dat";

            final NBTOutputStream output = new NBTOutputStream(Files.newOutputStream(Paths.get(Paths.get(pathToUse)
                                                                                                       .toString())));
            output.writeTag(newTopLevelTag);
            output.close();

            stringCache.printLine("Converted the level.dat file");
        } catch (final ClassCastException | NullPointerException ex) {
            throw new IOException("Error during level.dat fixing");
        }
    }

}

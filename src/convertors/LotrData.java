package convertors;

import de.piegames.nbt.CompoundMap;
import de.piegames.nbt.CompoundTag;
import de.piegames.nbt.stream.NBTInputStream;
import de.piegames.nbt.stream.NBTOutputStream;
import misterymob475.Fixers;
import misterymob475.StringCache;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

//this class fixes the data in the LOTR folder, this also means it renames the folder to lotr (so to lower case)

/**
 * Copies and fixes the contents of the lotr/ folder
 */
public class LotrData implements Convertor {
    private final StringCache stringCache;

    public LotrData() {
        this.stringCache = misterymob475.StringCache.getInstance();
    }

    /**
     * @param p        path of the folder where files are copied
     * @param fileName name of the to be modified files
     * @throws IOException if something fails
     */
    @Override
    public void modifier(Path p, String fileName) throws IOException {


        Files.createDirectory(Paths.get(p + "/" + fileName + "_Converted/lotr/"));
        Files.createDirectory(Paths.get(p + "/" + fileName + "_Converted/lotr/players"));
        File currentFolder = new File(Paths.get(p + "/" + fileName + "/LOTR").toString());

        Fixers fixers = new Fixers();
        try {
            if (new File(Paths.get(p + "/" + fileName + "/LOTR/LOTR.dat").toString()).exists()) {
                final NBTInputStream input = new NBTInputStream(Files.newInputStream(Paths.get(currentFolder + "/LOTR.dat")));
                final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
                input.close();

                //creates the new top level tag, otherwise it won't work
                CompoundMap map = new CompoundMap(originalTopLevelTag.getValue());
                fixers.LOTRDatFixer(map);
                final CompoundTag newTopLevelTag = new CompoundTag("", map);

                String pathToUse = p + "/" + fileName + "_Converted/lotr/LOTR.dat";
                final NBTOutputStream output = new NBTOutputStream(Files.newOutputStream(Paths.get(Paths.get(pathToUse)
                                                                                                           .toString())));
                output.writeTag(newTopLevelTag);
                output.close();
                stringCache.printLine("converted LOTR.dat");
            }
        }
        catch (final ClassCastException | NullPointerException ex) {
            throw new IOException("Error during LOTR.dat fix");
        }

        File playerDir = new File(currentFolder + "/players");
        int i = 1;
        for (File playerFile : Objects.requireNonNull(playerDir.listFiles())) {
            i++;
            try {
                final NBTInputStream input = new NBTInputStream(Files.newInputStream(playerFile.toPath()));
                final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
                input.close();

                CompoundMap originalData = new CompoundMap(originalTopLevelTag.getValue());
                fixers.LOTRPlayerDataFixer(originalData);

                final CompoundTag newTopLevelTag = new CompoundTag("", originalData);

                String pathToUse = p + "/" + fileName + "_Converted/lotr/players/" + playerFile.getName();
                final NBTOutputStream output = new NBTOutputStream(Files.newOutputStream(Paths.get((new File(Paths.get(pathToUse)
                                                                                                                     .toString())).getAbsolutePath())));
                output.writeTag(newTopLevelTag);

                output.close();
            }
            //took this out of an example I found, changed it as my ide wanted me to
            catch (final ClassCastException | NullPointerException ex) {
                throw new IOException("Error during playerData conversion fix");
            }
            stringCache.printLine("Converted " + (i - 1) + "/" + Objects.requireNonNull(playerDir.listFiles()).length + " Playerfiles", true);
        }
        stringCache.printLine("Converted all the player files in the /lotr folder");
    }
}

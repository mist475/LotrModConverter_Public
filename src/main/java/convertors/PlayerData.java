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

/**
 * Copies and fixes the regular player data
 */
public class PlayerData implements Convertor {
    private final StringCache stringCache;

    /**
     * Creates an instance of PlayerData
     */
    public PlayerData() {
        this.stringCache = misterymob475.StringCache.getInstance();
    }

    /**
     * @param p        path of the folder where files are copied
     * @param fileName name of the to be modified files
     * @throws IOException if something fails
     */
    @Override
    public void modifier(Path p, String fileName) throws IOException {
        Files.createDirectory(Paths.get(p + "/" + fileName + "_Converted/playerdata"));
        //level.dat fixer/modifier

        File currentFolder = new File(Paths.get(p + "/" + fileName + "/playerdata").toString());
        File[] curDirList = currentFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".dat"));
        int i = 1;
        if (curDirList != null) {
            for (File f : curDirList) {
                i++;
                try {
                    final NBTInputStream input = new NBTInputStream(Files.newInputStream(f.toPath()));
                    final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
                    input.close();

                    Fixers fixers = new Fixers();
                    CompoundMap map = new CompoundMap(originalTopLevelTag.getValue());
                    fixers.playerFixer(map);

                    final CompoundTag topLevelTag = new CompoundTag("", map);

                    String path = p + "/" + fileName + "_Converted/playerdata/" + f.getName();
                    final NBTOutputStream output = new NBTOutputStream(Files.newOutputStream(Paths.get((new File(Paths.get(path)
                                                                                                                         .toString())).getAbsolutePath())));
                    output.writeTag(topLevelTag);
                    output.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    stringCache.printLine("Something went wrong during playerdata conversion");
                }
                stringCache.printLine("Converted " + (i - 1) + "/ " + Objects.requireNonNull(currentFolder.listFiles()).length + " player data files", true);
            }
            stringCache.printLine("converted all the playerdata");
        }
    }
}

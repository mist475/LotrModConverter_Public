package Convertors;

import de.piegames.nbt.CompoundMap;
import de.piegames.nbt.CompoundTag;
import de.piegames.nbt.stream.NBTInputStream;
import de.piegames.nbt.stream.NBTOutputStream;
import misterymob475.Data;
import misterymob475.Fixers;
import misterymob475.StringCache;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Copies {@literal &} Fixes the level.dat file
 */
public class LevelDat implements Convertor {
    final private String pathName;
    final private Data Data;
    private final StringCache stringCache;

    /**
     * Creates an instance of LevelDat using the provided parameters
     *
     * @param data        Instance of {@link Data}
     * @param pathname    String of the pathname, see {@link Convertor}
     * @param stringCache instance of {@link StringCache}
     */
    public LevelDat(Data data, String pathname, StringCache stringCache) {
        this.Data = data;
        pathName = pathname;
        this.stringCache = stringCache;
    }

    /**
     * @param p        path of the folder where files are copied
     * @param FileName name of the to be modified files
     * @throws IOException if something fails
     */
    @Override
    public void modifier(Path p, String FileName) throws IOException {
        try {

            //new level.dat
            final NBTInputStream input = new NBTInputStream(new FileInputStream(Paths.get(p + "/" + this.pathName + "/level.dat").toString()));
            //final NBTInputStream input = new NBTInputStream(new FileInputStream(Paths.get(p +"/"+FileName+"/level.dat").toString()));
            final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
            input.close();

            //old level.dat
            //final NBTInputStream input1 = new NBTInputStream(new FileInputStream(Paths.get(p +"/"+FileName+"/level.dat").toString()));
            final NBTInputStream input1 = new NBTInputStream(new FileInputStream(Paths.get(p + "/" + FileName + "/level.dat").toString()));
            final CompoundTag originalTopLevelTag1 = (CompoundTag) input1.readTag();
            input1.close();

            //because of the way playerdata is stored in the level.dat I have moved the fixer to a slightly different function
            CompoundMap originalData = originalTopLevelTag.getValue();

            //this way I can modify the map directly, instead of regenerating it every time
            CompoundMap newData = new CompoundMap(originalData);

            Fixers.LevelDatFixer(newData, Data, stringCache, originalTopLevelTag1);


            final CompoundTag newTopLevelTag = new CompoundTag("", newData);
            final NBTOutputStream output = new NBTOutputStream(new FileOutputStream(Paths.get(p + "/" + FileName + "_Converted/level.dat").toString()));
            output.writeTag(newTopLevelTag);
            output.close();
            System.out.println("Converted the level.dat file");
        } catch (final ClassCastException | NullPointerException ex) {
            throw new IOException("Error during level.dat fixing");
        }
    }

}

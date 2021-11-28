package Convertors;

import de.piegames.nbt.CompoundMap;
import de.piegames.nbt.CompoundTag;
import de.piegames.nbt.stream.NBTInputStream;
import de.piegames.nbt.stream.NBTOutputStream;
import misterymob475.Data;
import misterymob475.Fixers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static misterymob475.Main.PrintLine;


public class Overworld implements Convertor {
    private final misterymob475.Data Data;

    /**
     * Creates an instance of HandMapData
     *
     * @param data instance of {@link Data}
     */
    public Overworld(Data data) {
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
        File currentFolder = new File(Paths.get(p + "/" + FileName + "/region").toString());
        Files.createDirectory(Paths.get(p + "/" + FileName + "_Converted/region"));
        if (currentFolder.exists()) {
            File[] curDirList = currentFolder.listFiles();
            if (curDirList != null) {
                int i = 1;
                for (File mapFile : curDirList) {
                    i++;
                    try {
                        final NBTInputStream input = new NBTInputStream(new FileInputStream(mapFile));
                        final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
                        input.close();
                        //DataVersion = 2586
                        //saves the input as a map, this is important for saving the file, for reading it is redundant
                        CompoundMap originalData = new CompoundMap(originalTopLevelTag.getValue());
                        //
                        CompoundMap chunk = Fixers.ChunkFixer(new CompoundMap(((CompoundTag) originalData.get("Chunk")).getValue()), Data);
                        //
                        originalData.replace("data", new CompoundTag("data", chunk));
                        final CompoundTag newTopLevelTag = new CompoundTag("", originalData);
                        final NBTOutputStream output = new NBTOutputStream(new FileOutputStream((new File(Paths.get(p + "/" + FileName + "_Converted/region/" + mapFile.getName()).toString())).getAbsolutePath()));
                        output.writeTag(newTopLevelTag);
                        output.close();
                    }
                    //took this out of an example I found, changed it as my ide wanted me to
                    catch (Exception e) {
                        e.printStackTrace();
                        throw new IOException("Error during overworld dimension fix");
                    }
                    PrintLine("Converted " + (i - 1) + "/" + Objects.requireNonNull(curDirList).length + " overworld dimension region files", Data, true);
                }

                PrintLine("Converted the overworld dimension", Data, false);
            }
        }
    }
}

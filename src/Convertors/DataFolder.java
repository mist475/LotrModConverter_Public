package Convertors;

import misterymob475.Data;
import misterymob475.Main;
import org.jnbt.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static misterymob475.Main.PrintLine;

public class DataFolder implements Convertor{
    private final Data Data;

    /**
     * Creates an instance of HandMapData
     * @param data instance of {@link Data}
     */
    public DataFolder(Data data) {
        this.Data = data;
    }



    /**
     * Modifies the files to work in Renewed
     *
     * @param p        path of the folder where files are copied
     * @param FileName name of the to be modified files
     */
    @Override
    public void modifier(Path p, String FileName) throws IOException {
        //in this file: fixers for idcounts.dat and map_%.dat files

        File currentFolder = new File(Paths.get(p +"/"+FileName+"/data").toString());
        Files.createDirectory(Paths.get(p +"/"+FileName+"_Converted/data"));
        if (currentFolder.exists()) {
            //idcounts fixer
            //map fixer (should be a quickie)
            File[] curDirList = currentFolder.listFiles((dir, name) -> name.toLowerCase().startsWith("map_"));
            if (curDirList != null && curDirList.length > 0) {
                int i = 1;
                for (File mapFile : curDirList) {

                    i++;
                    try {
                        //opens the file as a stream and saves the result as a CompoundTag
                        final NBTInputStream input = new NBTInputStream(new FileInputStream(mapFile));
                        final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
                        input.close();
                        //DataVersion = 2586
                        //saves the input as a map, this is important for saving the file, for reading it is redundant
                        Map<String, Tag> originalData = new HashMap<>(originalTopLevelTag.getValue());
                        Map<String,Tag> data = new HashMap<>(((CompoundTag) originalData.get("data")).getValue());
                        //gets the values we want, note, = I'm doing the easy ones first (lists last) I'm keeping the order though as I've read somewhere that that matters
                        if (data.containsKey("dimension") ) {
                            //fixer here int --> string
                            Integer Dimension = ((IntTag) data.get("dimension")).getValue();
                            String newDimension;
                            if (Dimension == 0) newDimension = "minecraft:overworld";
                            else if (Dimension == 1) newDimension = "Minecraft:the_nether";
                            else if (Dimension == -1) newDimension = "Minecraft:the_end";
                            else if (Dimension == 100) newDimension = "lotr:middle_earth";
                            //not sure if this is gonna work, we'll see
                            else if (Dimension == 101) newDimension = "lotr:utumno";
                            else newDimension = "minecraft:overworld";
                            data.replace("dimension",new StringTag("dimension",newDimension));
                        }
                        //hmm?
                        data.remove("width");
                        data.remove("height");
                        originalData.replace("data",new CompoundTag("data",data));
                        final CompoundTag newTopLevelTag = new CompoundTag("", originalData);
                        final NBTOutputStream output = new NBTOutputStream(new FileOutputStream((new File(Paths.get(p +"/"+FileName+"_Converted/data/" + mapFile.getName()).toString())).getAbsolutePath()));
                        output.writeTag(newTopLevelTag);
                        output.close();
                    }
                    //took this out of an example I found, changed it as my ide wanted me to
                    catch (Exception e) {
                        throw new IOException("Error during map conversion fix");
                    }
                    PrintLine("Converted " + (i-1) + "/" + Objects.requireNonNull(curDirList).length + " maps",Data,true);
                }

                PrintLine("Converted all the maps",Data,false);
                try {
                    if (new File(currentFolder+"/idcounts.dat").exists()) {

                        Map<String, Tag> newData = new HashMap<>();
                        Map<String,Tag> tMap = new HashMap<>();
                        tMap.put("map",new IntTag("map",curDirList.length-1));
                        newData.put("map",new CompoundTag("data",tMap));

                        final CompoundTag newTopLevelTag = new CompoundTag("", newData);

                        final NBTOutputStream output = new NBTOutputStream(new FileOutputStream((new File(Paths.get(p +"/"+FileName+"_Converted/data/idcounts.dat").toString())).getAbsolutePath()));
                        output.writeTag(newTopLevelTag);
                        output.close();

                        Main.PrintLine("converted idcount.dat",Data,false);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
}
}

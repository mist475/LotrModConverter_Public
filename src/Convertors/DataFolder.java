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
     * Copies directories
     * @param sourceDirectoryLocation Path of source
     * @param destinationDirectoryLocation Path of destination
     * @throws IOException if something fails
     */
    private static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation)
            throws IOException {
        Files.walk(Paths.get(sourceDirectoryLocation))
                .forEach(source -> {
                    Path destination = Paths.get(destinationDirectoryLocation, source.toString()
                            .substring(sourceDirectoryLocation.length()));
                    try {
                        Files.copy(source, destination);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }


    /**
     * Copies the files to their new location
     *
     * @param p        path of the folder where files are copied
     * @param FileName name of the to be copied file
     * @throws IOException if something fails
     */
    @Override
    public void copier(Path p, String FileName) throws IOException {
        File src = new File(Paths.get(p.toString()+"/"+FileName+"/data").toString());
        File out = new File(Paths.get(p +"/"+FileName+"_Converted/data").toString());
        if (src.exists()) {
            copyDirectory(src.getAbsolutePath(), out.getAbsolutePath());
        }
        Files.deleteIfExists(Paths.get(out+"/villages.dat"));
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

        File currentFolder = new File(Paths.get(p +"/"+FileName+"_Converted/data").toString());
        if (currentFolder.exists()) {
            //idcounts fixer
            //map fixer (should be a quickie)
            File[] curDirList = currentFolder.listFiles((dir, name) -> name.toLowerCase().startsWith("map_"));
            if (currentFolder.exists()) {
                int i = 1;
                assert curDirList != null;
                for (File mapFile : curDirList) {
                    i++;
                    try {
                        //opens the file as a stream and saves the result as a CompoundTag
                        final NBTInputStream input = new NBTInputStream(new FileInputStream(mapFile));
                        final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
                        input.close();
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
                            else if (Dimension == 2) newDimension = "Minecraft:the_end";
                            else if (Dimension == 100) newDimension = "lotr:middle_earth";
                            //not sure if this is gonna work, we'll see
                            else if (Dimension == 101) newDimension = "lotr:utumno";
                            else newDimension = "minecraft:overworld";
                            data.replace("dimension",new StringTag("dimension",newDimension));
                        }
                        //hmm?
                        data.remove("width");
                        originalData.replace("data",new CompoundTag("data",data));
                        //creates the new top level tag, otherwise it won't work
                        final CompoundTag newTopLevelTag = new CompoundTag("", originalData);
                        //creates an output stream, this overwrites the file so deleting it is not necessary
                        final NBTOutputStream output = new NBTOutputStream(new FileOutputStream(mapFile.getAbsolutePath()));
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

            }

            try {
                if (new File(currentFolder+"/idcounts.dat").exists()) {

                    Map<String, Tag> newData = new HashMap<>();
                    Map<String,Tag> tMap = new HashMap<>();
                    assert curDirList != null;
                    tMap.put("map",new IntTag("map",curDirList.length-1));
                    newData.put("map",new CompoundTag("data",tMap));

                    final CompoundTag newTopLevelTag = new CompoundTag("", newData);

                    final NBTOutputStream output = new NBTOutputStream(new FileOutputStream(currentFolder+"/idcounts.dat"));
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

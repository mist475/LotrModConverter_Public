package misterymob475;

import Convertors.*;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import static misterymob475.Util.*;

/**
 * Main class
 */
public class Main {
    /**
     * Main class
     *
     * @param args currently unused
     * @throws IOException if something fails
     */
    public static void main(String[] args) throws IOException, InterruptedException {

        Gson gson = new Gson();
        ArrayList<Thread> arrThreads = new ArrayList<>();

        if (new File(Paths.get("Conversions.json").toString()).exists()) {
            Reader reader = Files.newBufferedReader(Paths.get("Conversions.json"));
            Map<?, ?> map = gson.fromJson(reader, Map.class);
            reader.close();
            System.out.println("Welcome to the Legacy to Renewed world convertor for Mevans's LOTR mod by Misterymob475\nHow to use: unzip the zip file and place the created folder in your saves folder (or a different folder where you put your world).\nCreate a new world in the most recent version of renewed (1.16.5 as of now),\nCopy this world to the same folder as the world you want to upgrade.\nRun the .bat(windows) file or run via the command line.\nOpen the generated output in the same version of renewed as the new world you just created\nIf something doesn't work as planned please check if said feature is actually supported.\nOtherwise mention it on the #issues channel on my discord:rppMgSHaTe");

            //now create a new folder with the name of $worldName_converted
            //release should be ready now
            Data data = new Data(map);
            StringCache stringCache = new StringCache(data);
            //used for copying data over
            Optional<String> legacyWorld = LegacyWorldSelection();
            //basis for the new level.dat (modifying data is easier in this case then generating from scratch)
            Optional<String> renewedWorld = RenewedWorldSelection();
            if (legacyWorld.isPresent() && renewedWorld.isPresent()) {
                File selectedWorld = new File(legacyWorld.get());
                if (new File("../" + selectedWorld.getName() + "_Converted").exists()) {
                    deleteDir(new File("../" + selectedWorld.getName() + "_Converted"));
                }
                Files.createDirectories(Paths.get("../" + selectedWorld.getName() + "_Converted"));
                Path launchDir = Paths.get(".").toAbsolutePath().normalize().getParent();
                data.LegacyIds(Paths.get(launchDir + "/" + legacyWorld.get() + "/level.dat").toAbsolutePath().toString());
                //HashMap<String, List<String>> ItemNames = Data.ItemNames();
                //fancy way of looping through the implementations of the Convertor interface, this way I only have to change this line instead of adding an init, and the calling of the 2 functions per implementation
                //new LotrData(data), new PlayerData(data, stringCache), new LevelDat(data, renewedWorld.get(), stringCache), new DataFolder(stringCache), new Overworld(data, stringCache), new MiddleEarth(data, stringCache), new Nether(data, stringCache), new End(data, stringCache)
                for (Convertor c : new Convertor[]{new LotrData(data), new PlayerData(data, stringCache), new LevelDat(data, renewedWorld.get(), stringCache), new DataFolder(stringCache), new Overworld(data, stringCache), new MiddleEarth(data, stringCache), new Nether(data, stringCache), new End(data, stringCache)}) {
                    Thread t = new Thread(() -> {
                        try {
                            c.modifier(launchDir, selectedWorld.getName());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    t.start();
                    arrThreads.add(t);
                }
                //waits for all threads to finish
                for (Thread arrThread : arrThreads) {
                    arrThread.join();
                }
                System.out.println("Done!");
            }
        } else System.out.println("Conversions.json wasn't found, have you unzipped the zip-file correctly?");
    }
}

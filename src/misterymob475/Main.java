package misterymob475;

import Convertors.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {


        System.out.println("Welcome to the Legacy to Renewed world convertor for the LOTR mod by Mevans\nHow to use: unzip the zip file and place the created folder in your saves folder (or a different folder where you put you world).\n Create a new world in the most recent version of renewed (1.16.5 as of now), copy this world as the same folder as the world you want to upgrade. Doubleclick the .bat(windows) or .sh(macOS/Linux) file.\nOPen the generated output in the same version opf renewed as the new world you just created\nIf something doesn't work as planned please check if said feature is actually supported.\nOtherwise mention it on the #issues channel on my discord:rppMgSHaTe");

        //now create a new folder with the name of $worldName_converted
        //try {
            //used for copying data over
            String legacyWorld = legacyWorldSelection();
            //used to get ids and provides the basis for the new level.dat (old settings get superimposed on it)
            String renewedWorld = renewedWorldSelection();
            if (! legacyWorld.equals("") && ! renewedWorld.equals("")) {
                File selectedWorld = new File(legacyWorld);
                if (new File("../"+selectedWorld.getName()+"_Converted").exists()) {
                    deleteDir(new File("../"+selectedWorld.getName()+"_Converted"));
                }
                Files.createDirectories(Paths.get("../"+selectedWorld.getName()+"_Converted"));
                Path launchDir = Paths.get(".").toAbsolutePath().normalize().getParent();
                HashMap<Integer,String> LegacyIds = Data.LegacyIds(Paths.get(launchDir + "/" + legacyWorld+ "/level.dat").toAbsolutePath().toString());
                HashMap<String, List<String>> ItemNames = Data.ItemNames();
                //fancy way of looping through the implementations of the Convertor interface, this way I only have to change this line instead of adding an init, and the calling of the 2 functions per implementation
                for (Convertor c : new Convertor[]{new LotrData(),new PlayerData(LegacyIds,ItemNames),new LevelDat(renewedWorld,LegacyIds,ItemNames)}) {
                    c.copier(launchDir,selectedWorld.getName());
                    c.modifier(launchDir,selectedWorld.getName());
                }
            }
        //}
        //catch (Exception e) {
            //System.out.println("Something went wrong");
        //}
    }

    public static String renewedWorldSelection() {
        String[] pathnames;
        File f = new File("../");
        int i = 1;
        String selectOption = "Please select the new world you wan to use as the basis for your converted world:";
        FilenameFilter filter = (f1, name) -> {
            if (new File(f1, name).isDirectory()) {
                return new File(f1, name + "/datapacks").exists();
            }
            else {
                return false;
            }
        };
        pathnames = f.list(filter);
        if (!(pathnames == null)) {
            if (pathnames.length == 1) return pathnames[0];
            else if (pathnames.length > 1) {
                for (String pathname : pathnames) {
                    // Print the names of files and directories
                    System.out.println(i + " " + pathname);
                    i += 1;
                }

                System.out.println(selectOption);
                Scanner myScanner = new Scanner(System.in);
                int input = myScanner.nextInt();
                return pathnames[input-1];
            }
            else {
                System.out.println("no worlds found, are you sure you placed this file in the right directory?");
                return "";
            }
        }
        else {
            System.out.println("no worlds found, are you sure you placed this file in the right directory?");
            return "";
        }
    }

    private static String legacyWorldSelection() {
        String[] pathnames;

        File f = new File("../");

        int i = 1;
        String selectOption = "Please select the world you want to convert,\nonce selected a copy of the world will be generated and the necessary fixes will be applied:";

        FilenameFilter filter = (f1, name) -> {
            if (new File(f1, name).isDirectory()) {
                return new File(f1, name + "/MiddleEarth").exists();
            }
            else {
                return false;
            }
        };

        pathnames = f.list(filter);
        if (!(pathnames == null)) {
            if (pathnames.length == 1) return pathnames[0];
            else if (pathnames.length > 1) {
                for (String pathname : pathnames) {
                    // Print the names of files and directories
                    System.out.println(i + " " + pathname);
                    i += 1;
                }

                System.out.println(selectOption);
                Scanner myScanner = new Scanner(System.in);
                int input = myScanner.nextInt();
                return pathnames[input-1];
            }
            else {
                System.out.println("no worlds found, are you sure you placed this file in the right directory?");
                return "";
            }
        }
        else {
            System.out.println("no worlds found, are you sure you placed this file in the right directory?");
            return "";
        }
    }
    static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (! Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f);
                }
            }
        }
        if (! file.delete())
        System.out.println("Failed to delete file");
    }



}

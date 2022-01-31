package misterymob475;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.util.Scanner;

public class Util {
    /**
     * Deletes directory
     *
     * @param file directory to be deleted
     */
    public static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (!Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f);
                }
            }
        }
        if (!file.delete()) System.out.println("Failed to delete file");
    }

    /**
     * Gives an option prompt asking for a renewed world (if only one world is found no questions are asked), returns "" if no worlds are found
     *
     * @return Path of the selected renewed world
     */
    public static String renewedWorldSelection() {
        String[] pathnames;
        File f = new File("../");
        int i = 1;
        String selectOption = "Please select the new world you wan to use as the basis for your converted world:";

        FilenameFilter filter = (f1, name) -> {
            if (new File(f1, name).isDirectory()) {
                return new File(f1, name + "/datapacks").exists();
            } else {
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

                try {
                    int input = myScanner.nextInt();
                    return pathnames[input - 1];
                } catch (Exception e) {
                    System.out.println("Invalid selection");
                    return renewedWorldSelection();

                }
            } else {
                System.out.println("no legacy worlds found, are you sure you placed this file in the right directory?");
                return "";
            }
        } else {
            System.out.println("no legacy worlds found, are you sure you placed this file in the right directory?");
            return "";
        }
    }

    /**
     * Gives an option prompt asking for a legacy world (if only one world is found no questions are asked), returns "" if no worlds are found
     *
     * @return Path of the selected old world
     */
    public static String legacyWorldSelection() {
        String[] pathnames;

        File f = new File("../");

        int i = 1;
        String selectOption = "Please select the world you want to convert,\nonce selected a copy of the world will be generated and the necessary fixes will be applied:";

        FilenameFilter filter = (f1, name) -> {
            if (new File(f1, name).isDirectory()) {
                return new File(f1, name + "/MiddleEarth").exists();
            } else {
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
                try {
                    int input = myScanner.nextInt();
                    return pathnames[input - 1];
                } catch (Exception e) {
                    System.out.println("Invalid selection");
                    return legacyWorldSelection();
                }

            } else {
                System.out.println("no renewed worlds found, are you sure you placed this file in the right directory?");
                return "";
            }
        } else {
            System.out.println("no renewed worlds found, are you sure you placed this file in the right directory?");
            return "";
        }
    }
}

package misterymob475;

import de.piegames.nbt.CompoundMap;
import de.piegames.nbt.Tag;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.util.Optional;
import java.util.Scanner;

public class Util {
    /**
     * Deletes directory
     *
     * @param file {@link File} directory to be deleted
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
     * @return {@link Optional<String>} containing the path of the selected renewed world if found
     */
    public static Optional<String> RenewedWorldSelection() {
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
            if (pathnames.length == 1) return Optional.of(pathnames[0]);
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
                    return Optional.of(pathnames[input - 1]);
                } catch (Exception e) {
                    System.out.println("Invalid selection");
                    return RenewedWorldSelection();

                }
            } else {
                System.out.println("no legacy worlds found, are you sure you placed this file in the right directory?");
                return Optional.empty();
            }
        } else {
            System.out.println("no legacy worlds found, are you sure you placed this file in the right directory?");
            return Optional.empty();
        }
    }

    /**
     * Gives an option prompt asking for a legacy world (if only one world is found no questions are asked), returns "" if no worlds are found
     *
     * @return {@link Optional<String>} containing the path of the selected old world if found
     */
    public static Optional<String> LegacyWorldSelection() {
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
            if (pathnames.length == 1) return Optional.of(pathnames[0]);
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
                    return Optional.of(pathnames[input - 1]);
                } catch (Exception e) {
                    System.out.println("Invalid selection");
                    return LegacyWorldSelection();
                }

            } else {
                System.out.println("no renewed worlds found, are you sure you placed this file in the right directory?");
                return Optional.empty();
            }
        } else {
            System.out.println("no renewed worlds found, are you sure you placed this file in the right directory?");
            return Optional.empty();
        }
    }

    /**
     * Creates a CompoundMap with contents pre-added
     *
     * @param Tags {@link Tag}s to be added
     * @return {@link CompoundMap} with the given tags as content
     */
    public static CompoundMap CreateCompoundMapWithContents(Tag<?>... Tags) {
        CompoundMap m = new CompoundMap();
        for (Tag<?> t : Tags) {
            if (t != null) m.put(t);
        }
        return m;
    }

    /**
     * Removes entries from a given {@link CompoundMap}
     *
     * @param m          {@link CompoundMap}
     * @param conditions {@link String} varargs
     */
    public static void CMRemoveVarArgs(CompoundMap m, String... conditions) {
        for (String t : conditions) {
            m.remove(t);
        }
    }

}

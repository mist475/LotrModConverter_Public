package misterymob475;

import de.piegames.nbt.CompoundMap;
import de.piegames.nbt.Tag;
import de.piegames.nbt.TagType;

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
    public static Optional<String> renewedWorldSelector() {
        String[] pathNames;
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
        pathNames = f.list(filter);
        if (!(pathNames == null)) {
            if (pathNames.length == 1) return Optional.of(pathNames[0]);
            else if (pathNames.length > 1) {
                for (String pathname : pathNames) {
                    // Print the names of files and directories
                    System.out.println(i + " " + pathname);
                    i += 1;
                }

                System.out.println(selectOption);
                Scanner myScanner = new Scanner(System.in);

                try {
                    int input = myScanner.nextInt();
                    return Optional.of(pathNames[input - 1]);
                } catch (Exception e) {
                    System.out.println("Invalid selection");
                    return renewedWorldSelector();
                }
            }
        }
        System.out.println("no legacy worlds found, are you sure you placed this file in the right directory?");
        return Optional.empty();
    }

    /**
     * Gives an option prompt asking for a legacy world (if only one world is found no questions are asked), returns "" if no worlds are found
     *
     * @return {@link Optional<String>} containing the path of the selected old world if found
     */
    public static Optional<String> legacyWorldSelector() {
        String[] pathNames;

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

        pathNames = f.list(filter);
        if (!(pathNames == null)) {
            if (pathNames.length == 1) return Optional.of(pathNames[0]);
            else if (pathNames.length > 1) {
                for (String pathname : pathNames) {
                    // Print the names of files and directories
                    System.out.println(i + " " + pathname);
                    i += 1;
                }

                System.out.println(selectOption);
                Scanner myScanner = new Scanner(System.in);
                try {
                    int input = myScanner.nextInt();
                    return Optional.of(pathNames[input - 1]);
                } catch (Exception e) {
                    System.out.println("Invalid selection");
                    return legacyWorldSelector();
                }

            }
            else {
                System.out.println("no renewed worlds found, are you sure you placed this file in the right directory?");
                return Optional.empty();
            }
        }
        else {
            System.out.println("no renewed worlds found, are you sure you placed this file in the right directory?");
            return Optional.empty();
        }
    }

    /**
     * Creates a CompoundMap with contents pre-added
     *
     * @param tags {@link Tag}s to be added
     * @return {@link CompoundMap} with the given tags as content
     */
    public static CompoundMap createCompoundMapWithContents(Tag<?>... tags) {
        CompoundMap m = new CompoundMap();
        for (Tag<?> t : tags) {
            if (t != null) m.put(t);
        }
        return m;
    }

    /**
     * Removes entries from a given {@link CompoundMap}
     *
     * @param map        {@link CompoundMap}
     * @param conditions {@link String} varargs
     */
    public static void compoundMapVarArgRemover(CompoundMap map, String... conditions) {
        for (String t : conditions) {
            map.remove(t);
        }
    }

    /**
     * Gets the section a block is in (only height, not width)
     *
     * @param y y position of a block
     * @return section of a block
     */
    public static byte sectionHeight(int y) {
        return (byte) (y >> 4);
    }

    /**
     * Gets a Tag if it Exists, and it's the right Type
     *
     * @param map  {@link CompoundMap} that is checked
     * @param key  {@link String} key
     * @param type {@link TagType} denoting the requested type
     * @return {@link Optional} containing the requested value if it exists, and it's the right type. Otherwise Empty
     */
    public static Optional<Tag<?>> getAsTagIfExists(CompoundMap map, TagType type, String key) {
        if (map.containsKey(key)) {
            Tag<?> t = map.get(key);
            if (t.getType() == type) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

    /**
     * Get the blockId of a block
     * A blockId uses 12 bits. 8 Being in the first id and 4 in the second
     */
    public static int combine(byte id1, byte id2) {
        return Byte.toUnsignedInt(id1) | (Byte.toUnsignedInt(id2) << 8);
    }

    /**
     * Gets the first or last 4 bytes from the provided byte array
     */
    public static byte nibble4(byte[] arr, int index) {
        return (byte) (index % 2 == 0 ? arr[index / 2] & 0x0F : (arr[index / 2] >> 4) & 0x0F);
    }
}

package misterymob475;

import de.piegames.nbt.*;

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

    /**
     * If an entry for key exists as a {@link ByteArrayTag} it will be returned in an {@link Optional}, otherwise it will return an empty {@link Optional}
     *
     * @param map {@link CompoundMap} that might contain key
     * @param key {@link String} the key to be searched with
     * @return {@link Optional} of type {@link ByteArrayTag}
     */
    public static Optional<ByteArrayTag> GetAsByteArrayTagIfExistsFromCompoundMap(CompoundMap map, String key) {
        if (map.containsKey(key)) {
            Tag<?> t = map.get(key);
            if (t instanceof ByteArrayTag) return Optional.of((ByteArrayTag)t);
            else return Optional.empty();
        } else return Optional.empty();
    }

    /**
     * If an entry for key exists as a {@link ByteTag} it will be returned in an {@link Optional}, otherwise it will return an empty {@link Optional}
     *
     * @param map {@link CompoundMap} that might contain key
     * @param key {@link String} the key to be searched with
     * @return {@link Optional} of type {@link ByteArrayTag}
     */
    public static Optional<ByteTag> GetAsByteTagIfExistsFromCompoundMap(CompoundMap map, String key) {
        if (map.containsKey(key)) {
            Tag<?> t = map.get(key);
            if (t instanceof ByteTag) return Optional.of((ByteTag)t);
            else return Optional.empty();
        } else return Optional.empty();
    }

    /**
     * If an entry for key exists as a {@link de.piegames.nbt.CompoundTag} it will be returned in an {@link Optional}, otherwise it will return an empty {@link Optional}
     *
     * @param map {@link CompoundMap} that might contain key
     * @param key {@link String} the key to be searched with
     * @return {@link Optional} of type {@link ByteArrayTag}
     */
    public static Optional<CompoundTag> GetAsCompoundTagIfExistsFromCompoundMap(CompoundMap map, String key) {
        if (map.containsKey(key)) {
            Tag<?> t = map.get(key);
            if (t instanceof CompoundTag) return Optional.of((CompoundTag) t);
            else return Optional.empty();
        } else return Optional.empty();
    }

/*
    **
     * If a value exists as the given type in the given map it will get returned, otherwise an empty {@link Optional} gets returned
     * @param map {@link CompoundMap} map
     * @param key {@link String} key
     * @param type int representation of enum
     * @return {@link Optional} of given tag if exists, empty otherwise
     *
public static <T extends Tag> Optional<T> GetAsTagTypeIfExists(CompoundMap map, String key, TagType type, T TagTemp) {
    if (map.containsKey(key)) {
        Tag<?> t = map.get(key);
        switch(type) {
            case TAG_END : if (t instanceof EndTag) {
                return (Optional<T>) Optional.of((EndTag)t);
            } else return Optional.empty();
            case TAG_BYTE : if (t instanceof ByteTag) {
                return Optional.of(t);
            } else return Optional.empty();
            case TAG_SHORT : if (t instanceof ShortTag) {
                return Optional.of(t);
            } else return Optional.empty();
            case TAG_INT : if (t instanceof IntTag) {
                return Optional.of(t);
            } else return Optional.empty();
            case TAG_LONG : if (t instanceof LongTag) {
                return Optional.of(t);
            } else return Optional.empty();
            case TAG_FLOAT : if (t instanceof FloatTag) {
                return Optional.of(t);
            } else return Optional.empty();
            case TAG_DOUBLE : if (t instanceof DoubleTag) {
                return Optional.of(t);
            } else return Optional.empty();
            case TAG_BYTE_ARRAY : if (t instanceof ByteArrayTag) {
                return Optional.of(t);
            } else return Optional.empty();
            case TAG_STRING : if (t instanceof StringTag) {
                return Optional.of(t);
            } else return Optional.empty();
            case TAG_LIST : if (t instanceof ListTag) {
                return Optional.of(t);
            } else return Optional.empty();
            case TAG_COMPOUND : if (t instanceof CompoundTag) {
                return Optional.of(t);
            } else return Optional.empty();
            case TAG_INT_ARRAY : if (t instanceof IntArrayTag) {
                return Optional.of(t);
            } else return Optional.empty();
            case TAG_LONG_ARRAY : if (t instanceof LongArrayTag) {
                return Optional.of(t);
            } else return Optional.empty();
            case TAG_SHORT_ARRAY : if (t instanceof ShortArrayTag) {
                return Optional.of(t);
            } else return Optional.empty();
            default:return Optional.empty();
        }
    }
    else return Optional.empty();
}
 */
}

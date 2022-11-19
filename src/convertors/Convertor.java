package convertors;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface to make looping over these easier
 */
public interface Convertor {

    /**
     * Modifies the world such that the files become readable in renewed
     *
     * @param p        {@link Path} path to use
     * @param fileName {@link String} filename to use
     * @throws IOException if something fails
     */
    void modifier(Path p, String fileName) throws IOException;

}

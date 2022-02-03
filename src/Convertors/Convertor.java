package Convertors;

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
     * @param FileName {@link String} filename to use
     * @throws IOException if something fails
     */
    //modifies the information to be compatible
    void modifier(Path p, String FileName) throws IOException;

}

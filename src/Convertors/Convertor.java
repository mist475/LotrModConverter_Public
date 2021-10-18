package Convertors;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface to make looping over these easier
 */
public interface Convertor {
    /**
     * Modifies the files to work in Renewed
     * @param p path of the folder where files are copied
     * @param FileName name of the to be modified files
     * @throws IOException if something fails
     */
    //modifies the information to be compatible
    void modifier(Path p, String FileName) throws IOException;
}

package Convertors;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface to make looping over these easier
 */
public interface Convertor {
    /**
     * Copies the files to their new location
     * @param p path of the folder where files are copied
     * @param FileName name of the to be copied file
     * @throws IOException if something fails
     */
    //copies the information to the new folder
    void copier(Path p, String FileName) throws IOException;

    /**
     * Modifies the files to work in Renewed
     * @param p path of the folder where files are copied
     * @param FileName name of the to be modified files
     * @throws IOException if something fails
     */
    //modifies the information to be compatible
    void modifier(Path p, String FileName) throws IOException;
}

package Convertors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface Convertor {
    //copies the information to the new folder
    void copier(Path p, String FileName) throws IOException;
    //modifies the information to be compatible
    void modifier(Path p, String FileName) throws IOException;
}

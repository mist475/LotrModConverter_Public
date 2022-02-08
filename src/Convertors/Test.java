package Convertors;

import misterymob475.Data;

import java.io.IOException;
import java.nio.file.Path;

public class Test implements Convertor {
    public Data Data;

    public Test(Data Data) {
        this.Data = Data;
    }


    /**
     * Tests functions to make sure they work properly
     *
     * @param p        path of the folder where files are copied
     * @param FileName name of the to be modified files
     * @throws IOException if something fails
     */
    @Override
    public void modifier(Path p, String FileName) throws IOException {

        test1();
    }


    //Creates a test scenario
    public void test1() {

    }

}

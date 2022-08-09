package Convertors;

import de.piegames.nbt.*;
import misterymob475.Data;
import misterymob475.Util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

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
        CompoundMap m = new CompoundMap();
        m.put(new StringTag("key", "value"));
        m.put(new ByteTag("key2", (byte) 0));
        Optional<Tag<?>> tag1 = Util.getAsTagIfExists(m, TagType.TAG_INT, "key");
        Optional<Tag<?>> tag2 = Util.getAsTagIfExists(m, TagType.TAG_STRING, "key");
        Optional<Tag<?>> tag3 = Util.getAsTagIfExists(m, TagType.TAG_BYTE, "value");
    }

}

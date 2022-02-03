package Convertors;

import java.io.IOException;
import java.nio.file.Path;

public class Test implements Convertor {
    /**
     * Tests functions to make sure they work properly
     *
     * @param p        path of the folder where files are copied
     * @param FileName name of the to be modified files
     * @throws IOException if something fails
     */
    @Override
    public void modifier(Path p, String FileName) throws IOException {
        /*
        byte[] BlockStateDataValueGetterTester = new byte[]{0, 14, 15, 16, 32, 48, 64, 80, 96, 112, -128, -112, -96, -80, -64, -48, 0};
        byte[] BSDVGT_Result1 = new byte[BlockStateDataValueGetterTester.length];
        byte[] BSDVGT_Result2 = new byte[BlockStateDataValueGetterTester.length];
        for (int i = 0; i < BlockStateDataValueGetterTester.length; i++) {
            BSDVGT_Result1[i] = Fixers.BlockDataSelector(BlockStateDataValueGetterTester[i], false);
            BSDVGT_Result2[i] = Fixers.BlockDataSelector(BlockStateDataValueGetterTester[i], true);
        }
        System.out.println();
        */
    }
}

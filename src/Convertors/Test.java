package Convertors;

import de.piegames.nbt.CompoundTag;
import de.piegames.nbt.StringTag;
import misterymob475.Data;
import misterymob475.Util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static misterymob475.Fixers.AddPaletteEntryIfNecessary;
import static misterymob475.Fixers.BlockStatesGenerator;

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


    //Shows the current issue very well, even when all entries are 1 only one actually ends up in the BlockStates
    public void test1() {
        byte[] blockArr = new byte[4096];
        for (int i = 0; i < blockArr.length - 1; i++) {
            blockArr[i] = 1;
        }
        byte[] dataArr = new byte[2048];

        //CompoundMap SectionCompoundMap = list.get(i).getValue();
        List<CompoundTag> PaletteBuilderList = new ArrayList<>();

        //Apparently air is always in the palette, or once it's in it never leaves, I don't know yet
        PaletteBuilderList.add(new CompoundTag("", Util.CreateCompoundMapWithContents(new StringTag("Name", "minecraft:air"))));

        //used for making sure no identical palette entries exist
        List<String> PaletteCheckerList = new ArrayList<>();
        PaletteCheckerList.add("{name=minecraft:air}");

        int[] BlockPaletteReferences = new int[4096];

        for (int DataCounter = 0; DataCounter < 4096; DataCounter++) {
            int dataValue = Math.floorDiv(DataCounter, 2);
            //I might've reversed this one accidentally, time will tell...
            boolean SecondEntry = DataCounter % 2 == 1;
            if (Data.BlockIdToName.containsKey(String.valueOf(blockArr[DataCounter]))) {
                String LegacyId = Data.BlockIdToName.get(String.valueOf(blockArr[DataCounter]));

                //Only print for debugging purposes, this is extremely slow (1 region file with this on takes 15 min, with this off it takes 15 seconds)
                //stringCache.PrintLine(LegacyId, false);

                if (Data.BlockMappings.containsKey(LegacyId)) {
                    int temp = AddPaletteEntryIfNecessary(Data.BlockMappings.get(LegacyId), dataArr[dataValue], SecondEntry, PaletteCheckerList, PaletteBuilderList);
                    BlockPaletteReferences[DataCounter] = temp;
                }
            }
            DataCounter++;
        }

        //ListTag<CompoundTag> Palette = new ListTag<>("Palette", TagType.TAG_COMPOUND, PaletteBuilderList);

        //BlockStatesGenerator works fine, so it must be AddPaletteEntryIfNecessary
        long[] endResult = BlockStatesGenerator(PaletteCheckerList, BlockPaletteReferences);
        for (int i = 0; i < endResult.length - 1; i++) {
            System.out.println(endResult[i]);
        }


    }
}

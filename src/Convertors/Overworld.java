package Convertors;

import misterymob475.Data;
import misterymob475.StringCache;


public class Overworld extends DimensionConverter {


    /**
     * Creates an instance of HandMapData
     * Because of the super call, there is no need to implement modifier here
     *
     * @param data        instance of {@link Data}
     * @param stringCache instance of {@link StringCache}
     */
    public Overworld(Data data, StringCache stringCache) {
        super(data, stringCache, "/region", new String[]{"_Converted/region"}, "Error during overworld dimension fix", " overworld dimension region files", "Converted the overworld dimension");
    }
}

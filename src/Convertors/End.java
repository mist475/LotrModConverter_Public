package Convertors;

import misterymob475.Data;
import misterymob475.StringCache;

public class End extends DimensionConverter {
    /**
     * Creates an instance of HandMapData
     * Because of the super call, there is no need to implement modifier here
     *
     * @param data        instance of {@link misterymob475.Data}
     * @param stringCache instance of {@link StringCache}
     */
    public End(Data data, StringCache stringCache) {
        super(data, stringCache, "/DIM-1/region", new String[]{"_Converted/DIM-1", "_Converted/DIM-1/region"}, "Error during end dimension fix", " region files of end dimension", "Converted the end dimension");
    }
}

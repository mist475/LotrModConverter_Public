package Convertors;

import misterymob475.StringCache;

public class MiddleEarth extends DimensionConverter {

    /**
     * Creates an instance on MiddleEarth
     *
     * @param stringCache instance of {@link StringCache}
     */
    public MiddleEarth(StringCache stringCache) {
        super(stringCache, "/MiddleEarth/region", new String[]{"_Converted/dimensions", "_Converted/dimensions/lotr", "_Converted/dimensions/lotr/middle_earth", "_Converted/dimensions/lotr/middle_earth/region"}, "Error during middle earth dimension fix", " region files of middle earth dimension", "Converted the middle earth dimension");
    }


}

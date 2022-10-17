package Convertors;

public class End extends DimensionConverter {
    /**
     * Creates an instance of HandMapData
     * Because of the super call, there is no need to implement modifier here
     */
    public End() {
        super("/DIM-1/region", new String[]{"_Converted/DIM-1", "_Converted/DIM-1/region"}, "Error during end dimension fix", " region files of end dimension", "Converted the end dimension");
    }
}

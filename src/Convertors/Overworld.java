package Convertors;


public class Overworld extends DimensionConverter {


    /**
     * Creates an instance of HandMapData
     * Because of the super call, there is no need to implement modifier here
     */
    public Overworld() {
        super("/region", new String[]{"_Converted/region"}, "Error during overworld dimension fix", " overworld dimension region files", "Converted the overworld dimension");
    }
}

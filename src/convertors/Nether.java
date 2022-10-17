package convertors;

public class Nether extends DimensionConverter {

    /**
     * Creates an instance of Nether
     * Because of the super call, there is no need to implement modifier here
     */
    public Nether() {
        super("/DIM1/region", new String[]{"_Converted/DIM1", "_Converted/DIM1/region"}, "Error during nether dimension fix", " nether dimension region files", "Converted the nether dimension");
    }


}

package misterymob475.settings;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class BlockMapping {
    @SerializedName("name")
    private final String name;
    @SerializedName("properties")
    private final Map<String,Object> properties;

    public BlockMapping(String name, Map<String, Object> properties) {
        this.name = name;
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}

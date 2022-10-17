package misterymob475.data;

import com.google.gson.annotations.SerializedName;

public class Settings {
    @SerializedName("Debug Messages")
    private final int debugMessages;
    @SerializedName("Cache debug messages")
    private final boolean cacheDebugMessages;
    @SerializedName("Recursion Depth")
    private final int itemRecursionDepth;
    @SerializedName("Creative Mode spawn")
    private final boolean creativeSpawn;
    @SerializedName("Comments")
    private final String comments;

    public Settings(int debug_messages, boolean cache_debug_messages, int recursion_depth, boolean creative_spawn, String comments) {
        this.debugMessages = debug_messages;
        this.cacheDebugMessages = cache_debug_messages;
        this.itemRecursionDepth = recursion_depth;
        this.creativeSpawn = creative_spawn;
        this.comments = comments;
    }

    public boolean isCacheDebugMessages() {
        return cacheDebugMessages;
    }

    public boolean isCreativeSpawn() {
        return creativeSpawn;
    }

    public int getDebugMessages() {
        return debugMessages;
    }

    public int getItemRecursionDepth() {
        return itemRecursionDepth;
    }

    public String getComments() {
        return comments;
    }
}

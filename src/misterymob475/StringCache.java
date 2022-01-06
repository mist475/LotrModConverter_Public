package misterymob475;

import java.util.ArrayList;
import java.util.List;

public class StringCache {
    private final List<String> stringCache;
    private final double debugLevel;
    private final Boolean cacheDebug;

    /**
     * Creates an instance
     *
     * @param data instance of {@link Data}
     */
    public StringCache(Data data) {
        this.stringCache = new ArrayList<>();
        this.debugLevel = (Double) data.Settings.get("Debug Messages");
        this.cacheDebug = (Boolean) data.Settings.get("Cache debug messages");
    }

    /**
     * Starts a thread which checks if msg is in the stringCache, if not, it will be added and printed
     *
     * @param msg      {@link String} the message that might be printed
     * @param extended {@link Boolean}, determines if the message should only be printed during if all messages should be displayed (particularly spammy messages)
     */
    public void PrintLine(String msg, Boolean extended) {
        Thread t = new Thread(() -> {
            if (debugLevel == 2.0) {
                if (cacheDebug) {
                    actualPrint(msg);
                } else System.out.println(msg);

            } else if (debugLevel == 1.0) {
                if (!extended) {
                    if (cacheDebug) {
                        actualPrint(msg);
                    }
                } else System.out.println(msg);
            }
        });
        t.start();
    }

    /**
     * Synchronized method that checks if msg is in the cache. if it is, nothing will happen, if isn't, it will get printed and added to the list.
     *
     * @param msg the message that might be printed
     */
    private synchronized void actualPrint(String msg) {
        if (!stringCache.contains(msg)) {
            System.out.println(msg);
            stringCache.add(msg);
        }
    }
}

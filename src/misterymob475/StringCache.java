package misterymob475;

import misterymob475.data.Data;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class StringCache {
    private static StringCache INSTANCE;
    private final List<String> stringCache;
    private final double debugLevel;
    private final Boolean cacheDebug;
    private final BufferedWriter fasterPrint;


    private StringCache() {
        this.stringCache = new ArrayList<>();
        Data data = Data.getInstance();
        this.debugLevel = data.settings.getDebugMessages();
        this.cacheDebug = data.settings.isCacheDebugMessages();
        this.fasterPrint = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(java.io.FileDescriptor.out), StandardCharsets.US_ASCII), 512);
    }

    public static synchronized StringCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StringCache();
        }
        return INSTANCE;
    }

    /**
     * Starts a thread which checks if msg is in the stringCache, if not, it will be added and printed
     *
     * @param msg      {@link String} the message that might be printed
     * @param extended {@link Boolean}, determines if the message should always be printed or only if it hasn't been printed yet
     */
    public void printLine(String msg, Boolean extended) {
        Thread t = new Thread(() -> {
            if (debugLevel == 2.0) {
                if (cacheDebug) {
                    try {
                        actualPrint(msg);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        fasterPrint(msg);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            else if (debugLevel == 1.0) {
                if (!extended) {
                    if (cacheDebug) {
                        try {
                            actualPrint(msg);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    try {
                        fasterPrint(msg);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

    /**
     * Overload for basic version
     *
     * @param msg {@link String} to be printed
     */
    public void printLine(String msg) {
        printLine(msg, false);
    }

    /**
     * Synchronized method that checks if msg is in the cache. if it is, nothing will happen, if isn't, it will get printed and added to the list.
     *
     * @param msg the message that might be printed
     */
    private synchronized void actualPrint(String msg) throws IOException {
        if (!stringCache.contains(msg)) {
            fasterPrint(msg);
            stringCache.add(msg);
        }
    }

    /**
     * Prints a message in a way that should be faster
     *
     * @param msg {@link String} to be printed
     * @throws IOException if printing fails
     * @author <a href="https://www.rgagnon.com/javadetails/java-0603.html">Réal Gagnon</a>
     */
    private void fasterPrint(String msg) throws IOException {
        fasterPrint.write(msg + '\n');
        fasterPrint.flush();
    }
}

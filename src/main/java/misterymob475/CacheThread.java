package misterymob475;

import misterymob475.data.Data;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * Thread for logging
 */
public class CacheThread extends Thread {
    private final List<String> stringCache;
    private final Boolean cacheDebug;
    private final BufferedWriter fasterPrint;
    private final Queue<Pair<String, Boolean>> messages = new ConcurrentLinkedQueue<>();
    private final Consumer<Pair<String, Boolean>> printFunction;
    private volatile boolean done = false;

    public CacheThread() {
        this.stringCache = new ArrayList<>();
        Data data = Data.getInstance();
        double debugLevel = data.settings.getDebugMessages();
        this.cacheDebug = data.settings.isCacheDebugMessages();
        this.fasterPrint = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(java.io.FileDescriptor.out), StandardCharsets.US_ASCII), 512);

        //Sets consumers instead of checking values every call
        if (debugLevel == 2.0) {
            this.printFunction = message -> {
                if (cacheDebug) {
                    try {
                        actualPrint(message.left);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        fasterPrint(message.left);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        else if (debugLevel == 1.0) {
            this.printFunction = message -> {
                if (!message.right) {
                    if (cacheDebug) {
                        try {
                            actualPrint(message.left);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    try {
                        fasterPrint(message.left);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        else {
            this.printFunction = stringBooleanPair -> {
            };
        }
    }

    /**
     * Thread should run as daemon so this will remain in background until closed
     */
    @Override
    public void run() {
        while (!done) {
            if (!messages.isEmpty()) {
                messages.forEach(this::print);
            }
        }
    }

    private void print(Pair<String, Boolean> message) {
        this.printFunction.accept(message);
        this.messages.remove(message);

    }

    public void setDone(boolean done) throws InterruptedException {
        this.done = done;
        this.join(0);
    }

    /**
     * Prints the message if the message is not in the cache or if debugging is enabled
     *
     * @param msg the message that might be printed
     */
    private void actualPrint(String msg) throws IOException {
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

    public void addLogMessage(String message, boolean extended) {
        this.messages.add(new Pair<>(message, extended));
    }

    public static class Pair<T1, T2> {
        public T1 left;
        public T2 right;

        public Pair(T1 left, T2 right) {
            this.left = left;
            this.right = right;
        }
    }
}

package misterymob475;

/**
 * Wrapper for {@link CacheThread}
 */
public class StringCache {
    private static StringCache INSTANCE;

    private final CacheThread logThread;


    private StringCache() {
        this.logThread = new CacheThread();
        logThread.start();
    }

    public static synchronized StringCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StringCache();
        }
        return INSTANCE;
    }

    /**
     * Adds log message to {@link CacheThread} queue
     *
     * @param msg      {@link String} the message that might be printed
     * @param extended {@link Boolean}, determines if the message should always be printed or only if it hasn't been printed yet
     */
    public void printLine(String msg, Boolean extended) {
        this.logThread.addLogMessage(msg, extended);
    }

    /**
     * Overload for basic version
     *
     * @param msg {@link String} to be printed
     */
    public void printLine(String msg) {
        printLine(msg, false);
    }

    public void stopLogging() throws InterruptedException {
        this.logThread.setDone(true);
    }
}

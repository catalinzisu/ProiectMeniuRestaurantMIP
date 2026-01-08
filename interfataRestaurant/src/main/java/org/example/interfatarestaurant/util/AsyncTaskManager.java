package org.example.interfatarestaurant.util;

import javafx.concurrent.Task;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manager pentru rularea task-urilor în thread-uri separate
 * Previne blocarea UI-ului JavaFX
 */
public class AsyncTaskManager {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(3);

    /**
     * Execută o Task în background și permite update-uri pe UI Thread
     */
    public static <T> void executeTask(Task<T> task) {
        executorService.execute(task);
    }

    /**
     * Shutdown graceful al executor-ului (apelat la closing app)
     */
    public static void shutdown() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}


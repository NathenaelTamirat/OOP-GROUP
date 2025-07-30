
// Utility for logging events/errors to logs/app_log.txt.

package util;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class Logger {
    public static void log(String message) {
        try (FileWriter fw = new FileWriter("logs/app_log.txt", true)) {
            fw.write(LocalDateTime.now() + ": " + message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package cc.cassian.raspberry.util;

import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class RaspberryProfiler {
    private static final Logger LOGGER = LoggerFactory.getLogger("RaspberryProfiler");
    public static final ThreadLocal<Long> TAB_START_TIME = ThreadLocal.withInitial(() -> 0L);

    private static final Map<String, Long> creativeTabTimes = new LinkedHashMap<>();
    private static final Map<String, Long> emiPluginTimes = new LinkedHashMap<>();
    private static final Map<String, Long> searchIndexTimes = new TreeMap<>();

    public static void recordCreativeTab(String tabName, long nanos) {
        if (ModConfig.get().enableProfiler) {
            creativeTabTimes.put(tabName, nanos);
        }
    }

    public static void recordEmiPlugin(String pluginName, long nanos) {
        if (ModConfig.get().enableProfiler) {
            emiPluginTimes.put(pluginName, nanos);
        }
    }

    public static void recordSearchIndex(String modId, long nanos) {
        if (ModConfig.get().enableProfiler) {
            searchIndexTimes.merge(modId, nanos, Long::sum);
        }
    }

    public static void dumpLogs() {
        if (!ModConfig.get().enableProfiler) return;

        File logDir = new File(Minecraft.getInstance().gameDirectory, "logs");
        if (!logDir.exists()) logDir.mkdirs();
        File logFile = new File(logDir, "raspberry_profiler.log");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile))) {
            writer.write("Raspberry Core Profiler Log - " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            writer.newLine();
            writer.write("==================================================");
            writer.newLine();

            writer.write("--- Creative Mode Tabs Rebuild ---");
            writer.newLine();
            long totalTabTime = 0;
            creativeTabTimes.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEach(entry -> writeEntry(writer, entry.getKey(), entry.getValue()));

            for (Long val : creativeTabTimes.values()) totalTabTime += val;
            writer.write(String.format("TOTAL TAB TIME: %.2f ms", totalTabTime / 1_000_000.0));
            writer.newLine();
            writer.newLine();

            writer.write("--- Search Tree Indexing (Total) ---");
            writer.newLine();
            searchIndexTimes.forEach((key1, value1) -> writeEntry(writer, key1, value1));
            writer.newLine();

            writer.write("--- EMI Plugin Loading (Total) ---");
            writer.newLine();
            emiPluginTimes.forEach((key, value) -> writeEntry(writer, key, value));
            writer.newLine();

            LOGGER.info("Dumped profiling info to {}", logFile.getAbsolutePath());

        } catch (IOException e) {
            LOGGER.error("Failed to dump Raspberry Profiler logs", e);
        }
    }

    private static void writeEntry(BufferedWriter writer, String name, long nanos) {
        try {
            double ms = nanos / 1_000_000.0;
            writer.write(String.format("[%s]: %.2f ms", name, ms));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
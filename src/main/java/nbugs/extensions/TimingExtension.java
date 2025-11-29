package nbugs.extensions;

import nbugs.annotations.AroundTestExtension;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback, AroundTestExtension {
    private static final Map<String, Long> startTimes = new ConcurrentHashMap<>();
    private static final Map<String, Long> testDurations = new ConcurrentHashMap<>();
    private static final AtomicLong totalDuration = new AtomicLong(0);
    private static String threadCount = "1";
    private static boolean parallelismEnabled = false;

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
        String testName = getTestName(extensionContext);
        startTimes.put(testName, System.currentTimeMillis());
        System.out.println("Thread " + Thread.currentThread().getName() + ": Test started " + testName);
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        String testName = getTestName(extensionContext);
        Long startTime = startTimes.get(testName);
        if (startTime != null) {
            long testDuration = System.currentTimeMillis() - startTime;
            testDurations.put(testName, testDuration);
            totalDuration.addAndGet(testDuration);

            System.out.println("Thread " + Thread.currentThread().getName() + ": Test finished " + testName + ", test duration " + testDuration + " ms");
        }
    }

    @Override
    public void onStart(ExtensionContext context) {
        readConfigurationParameters(context);
    }

    @Override
    public void onEnd() {
        writeStatisticsToFile();
    }

    private String getTestName(ExtensionContext extensionContext) {
        return extensionContext.getRequiredTestClass().getSimpleName() + "." + extensionContext.getDisplayName();
    }

    private void writeStatisticsToFile() {
        try {
            var filePath = Paths.get("stat.md");
            var content = new StringBuilder();
            content.append("# Test Execution Statistics\n\n");

            if (parallelismEnabled && threadCount != null) {
                content.append("**Parallel execution enabled**\n");
                content.append("**Number of threads:** ").append(threadCount).append("\n\n");
            } else {
                content.append("**Parallel execution disabled**\n\n");
            }

            // Таблица с результатами тестов
            if (!testDurations.isEmpty()) {
                content.append("## Test Results\n\n");
                content.append("| Test Name | Duration (ms) |\n");
                content.append("|-----------|---------------|\n");

                testDurations.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .forEach(entry -> {
                            content.append("| ").append(entry.getKey())
                                    .append(" | ").append(entry.getValue())
                                    .append(" |\n");
                        });

                content.append("\n");
            }

            content.append("## Summary\n\n");
            content.append("**Total execution time:** ").append(totalDuration.get()).append(" ms\n");

            Files.writeString(filePath, content.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Statistics written to stat.md");

        } catch (IOException e) {
            System.err.println("Failed to write statistics to file: " + e.getMessage());
        }
    }

    private void readConfigurationParameters(ExtensionContext context) {
        try {
            parallelismEnabled = Boolean.parseBoolean(context
                    .getConfigurationParameter("junit.jupiter.execution.parallel.enabled").orElse("false"));
            threadCount = context.getConfigurationParameter("junit.jupiter.execution.parallel.config.fixed.parallelism")
                    .orElse("1");
            return; // Успешно получили настройки
        } catch (Exception ignored) {
        }

        // Пробуем системные свойства
        String parallelismProp = System.getProperty("junit.jupiter.execution.parallel.enabled");
        String threadCountProp = System.getProperty("junit.jupiter.execution.parallel.config.fixed.parallelism");

        if (parallelismProp != null || threadCountProp != null) {
            parallelismEnabled = "true".equals(parallelismProp);
            threadCount = threadCountProp != null ? threadCountProp : "1";
            return;
        }
    }

    private void readFromPropertiesFile() {
        try {
            var propsFile = Paths.get("junit-platform.properties");
            if (Files.exists(propsFile)) {
                String content = Files.readString(propsFile);
                String[] lines = content.split("\n");

                for (String line : lines) {
                    line = line.trim();
                    if (line.startsWith("junit.jupiter.execution.parallel.enabled")) {
                        parallelismEnabled = line.contains("true");
                    } else if (line.startsWith("junit.jupiter.execution.parallel.config.fixed.parallelism")) {
                        String[] parts = line.split("=");
                        if (parts.length > 1) {
                            threadCount = parts[1].trim();
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read junit-platform.properties: " + e.getMessage());
        }
    }

}


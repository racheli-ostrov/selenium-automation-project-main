package tests;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import utils.ExcelUtils;

public class TestResultLogger implements ITestListener {
    private BufferedWriter writer;
    private final AtomicInteger passed = new AtomicInteger();
    private final AtomicInteger failed = new AtomicInteger();
    private final AtomicInteger skipped = new AtomicInteger();
    private long suiteStart;

    private void initIfNeeded() {
        if (writer == null) {
            try {
                Files.createDirectories(Path.of("output"));
                File file = new File("output/test-results.log");
                writer = new BufferedWriter(new FileWriter(file, true)); // append
                writer.write("================ Test Run @ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " ================\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart(ITestContext context) {
        suiteStart = System.currentTimeMillis();
        // Clear Excel file at start of each suite run
        try {
            File excelFile = new File("output/test-results.xlsx");
            if(excelFile.exists()) {
                excelFile.delete();
                System.out.println("=== Cleared previous test-results.xlsx");
            }
        } catch (Exception e) {
            System.out.println("=== Failed to clear Excel: " + e.getMessage());
        }
        initIfNeeded();
        writeLine("Suite start: " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        long duration = System.currentTimeMillis() - suiteStart;
        writeLine("Suite finish: " + context.getName() + " (" + duration + " ms)\n");
        writeLine("Summary: passed=" + passed.get() + ", failed=" + failed.get() + ", skipped=" + skipped.get());
        writeLine("Screenshots present: " + listScreenshotNames());
        writeLine("================ End Run ================\n\n");
        close();
    }

    @Override
    public void onTestStart(ITestResult result) {
        initIfNeeded();
        writeLine("START | " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        passed.incrementAndGet();
        writeLine("PASS  | " + result.getMethod().getMethodName() + timing(result));
        try { 
            System.out.println("=== Writing PASS to Excel for: " + result.getMethod().getMethodName());
            ExcelUtils.appendTestResult("output/test-results.xlsx", result.getMethod().getMethodName(), "PASS"); 
            System.out.println("=== Excel write completed");
        } catch (Exception e) { 
            System.out.println("=== Excel write failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        failed.incrementAndGet();
        writeLine("FAIL  | " + result.getMethod().getMethodName() + timing(result));
        if (result.getThrowable() != null) {
            writeLine("       Reason: " + result.getThrowable().getMessage());
        }
        try { ExcelUtils.appendTestResult("output/test-results.xlsx", result.getMethod().getMethodName(), "FAIL"); } catch (Exception ignored) {}
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        skipped.incrementAndGet();
        writeLine("SKIP  | " + result.getMethod().getMethodName() + timing(result));
        try { ExcelUtils.appendTestResult("output/test-results.xlsx", result.getMethod().getMethodName(), "SKIP"); } catch (Exception ignored) {}
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) { /* unused */ }
    @Override
    public void onTestFailedWithTimeout(ITestResult result) { onTestFailure(result); }

    private String timing(ITestResult result) {
        long dur = result.getEndMillis() - result.getStartMillis();
        return " (" + dur + " ms)";
    }

    private void writeLine(String s) {
        try {
            initIfNeeded();
            writer.write(s + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String listScreenshotNames() {
        File dir = new File("output/screenshots");
        if (!dir.exists()) return "none";
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".png"));
        if (files == null || files.length == 0) return "none";
        StringBuilder sb = new StringBuilder();
        for (File f : files) sb.append(f.getName()).append(", ");
        if (sb.length() >= 2) sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    private void close() {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException ignored) {}
        writer = null;
    }
}

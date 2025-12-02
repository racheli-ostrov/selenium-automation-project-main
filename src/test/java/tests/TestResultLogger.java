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
        // התוצאות יישמרו ב-all_test_results.xlsx דרך ConsolidatedTestResultsManager
        initIfNeeded();
        writeLine("Suite start: " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        long duration = System.currentTimeMillis() - suiteStart;
        writeLine("Suite finish: " + context.getName() + " (" + duration + " ms)\n");
        writeLine("Summary: passed=" + passed.get() + ", failed=" + failed.get() + ", skipped=" + skipped.get());
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
        // התוצאות יישמרו ב-all_test_results.xlsx דרך ConsolidatedTestResultsManager
    }

    @Override
    public void onTestFailure(ITestResult result) {
        failed.incrementAndGet();
        writeLine("FAIL  | " + result.getMethod().getMethodName() + timing(result));
        if (result.getThrowable() != null) {
            writeLine("       Reason: " + result.getThrowable().getMessage());
        }
        // התוצאות יישמרו ב-all_test_results.xlsx דרך ConsolidatedTestResultsManager
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        skipped.incrementAndGet();
        writeLine("SKIP  | " + result.getMethod().getMethodName() + timing(result));
        // התוצאות יישמרו ב-all_test_results.xlsx דרך ConsolidatedTestResultsManager
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

    private void close() {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException ignored) {}
        writer = null;
    }
}

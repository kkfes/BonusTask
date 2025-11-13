package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Runner that reads tests from `sample_input.txt`, executes KMP on each test,
 * performs warm-up and repeated measurements, prints results and writes
 * `sample_output.txt`.
 *
 * Input file format (UTF-8): each non-empty non-comment line has three fields
 * separated by '|': Label|Text|Pattern
 * - If Text starts with `repeat:COUNT:SUBSTRING`, the real text is
 *   SUBSTRING repeated COUNT times (COUNT is integer).
 */
public class Main {
    public static void main(String[] args) {
        Path in = Path.of("sample_input.txt");
        Path outFile = Path.of("sample_output.txt");
        StringBuilder out = new StringBuilder();
        List<TestCase> cases = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(in);
            for (String line : lines) {
                String t = line.trim();
                if (t.isEmpty() || t.startsWith("#")) continue;
                String[] parts = t.split("\\|", 3);
                if (parts.length != 3) {
                    System.err.println("Ignored invalid line: " + line);
                    continue;
                }
                String label = parts[0].trim();
                String textField = parts[1];
                String pattern = parts[2];
                String text = expandTextField(textField);
                cases.add(new TestCase(label, text, pattern));
            }
        } catch (IOException e) {
            System.err.println("Failed to read sample_input.txt: " + e.getMessage());
            return;
        }

        for (TestCase tc : cases) {
            out.append(runTest(tc.label, tc.text, tc.pattern));
        }

        System.out.print(out.toString());
        try {
            Files.writeString(outFile, out.toString());
            System.out.println("Saved sample output to " + outFile.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to write sample output: " + e.getMessage());
        }
    }

    private static String expandTextField(String field) {
        String f = field.trim();
        if (f.startsWith("repeat:")) {
            // syntax: repeat:COUNT:SUBSTRING
            String[] p = f.split(":", 3);
            if (p.length != 3) return "";
            int count;
            try {
                count = Integer.parseInt(p[1]);
            } catch (NumberFormatException ex) {
                return "";
            }
            String sub = p[2];
            StringBuilder sb = new StringBuilder(sub.length() * Math.max(1, count));
            for (int i = 0; i < count; i++) sb.append(sub);
            return sb.toString();
        }
        return f;
    }

    private static String runTest(String label, String text, String pattern) {
        final int WARMUP = 3;
        final int MEASURE = 5;

        // warm-up runs
        for (int w = 0; w < WARMUP; w++) {
            KMP.search(text, pattern);
        }

        long totalTimeNs = 0L;
        List<Integer> lastMatches = null;
        for (int r = 0; r < MEASURE; r++) {
            long start = System.nanoTime();
            lastMatches = KMP.search(text, pattern);
            long elapsed = System.nanoTime() - start;
            totalTimeNs += elapsed;
        }
        double avgMs = (totalTimeNs / (double) MEASURE) / 1_000_000.0;

        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(label).append(" test ===\n");
        sb.append("Text length: ").append(text.length()).append(", Pattern length: ").append(pattern.length()).append("\n");
        sb.append("Matches found: ").append(lastMatches == null ? 0 : lastMatches.size()).append("\n");
        if (lastMatches != null) {
            if (lastMatches.size() <= 20) sb.append("Indices: ").append(lastMatches).append("\n");
            else sb.append("First 10 indices: ").append(lastMatches.subList(0, 10)).append("\n");
        }
        sb.append(String.format("Avg Time (over %d runs): %.6f ms\n\n", MEASURE, avgMs));
        return sb.toString();
    }

    private static class TestCase {
        final String label;
        final String text;
        final String pattern;

        TestCase(String label, String text, String pattern) {
            this.label = label;
            this.text = text;
            this.pattern = pattern;
        }
    }
}

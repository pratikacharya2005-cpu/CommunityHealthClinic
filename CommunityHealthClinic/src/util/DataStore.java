package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/** Low-level flat-file helper. Single Responsibility: only file I/O. */
public final class DataStore {

    private DataStore() { }

    public static void writeLines(String filePath, List<String> lines) throws IOException {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Unable to create directory: " + parent.getAbsolutePath());
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line == null ? "" : line);
                writer.newLine();
            }
        }
    }

    public static List<String> readLines(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return lines;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) lines.add(line);
        }
        return lines;
    }

    public static boolean fileExists(String filePath) { return new File(filePath).exists(); }
}
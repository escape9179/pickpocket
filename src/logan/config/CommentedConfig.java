package logan.config;

import logan.pickpocket.main.PickpocketPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

public class CommentedConfig {

    public static String COMMENT_PREFIX = "# ";
    public static String KEY_VALUE_SEPARATOR = ":";
    private static final String lineSeparator = System.lineSeparator();

    private final Map<String, String> keyCommentMap = new HashMap<>();

    private YamlConfiguration configuration;
    private Path filePath;

    public CommentedConfig(Stream<String> stringStream, String destPath) {
        this(stringStream.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString(), destPath);
    }

    /**
     * Creates a commented config from a string.
     */
    public CommentedConfig(String contents, String destPath) {
        filePath = Paths.get(destPath);
        Scanner scanner = new Scanner(contents);
        String currentLine;
        StringBuilder currentComment = new StringBuilder();
        while (scanner.hasNext()) {
            currentLine = scanner.nextLine();
            // Stores the current comment getting built.
            // Make sure there is nothing in the buffer first by deleting all chars.
            currentComment.delete(0, currentComment.length());
            // This line is a comment. Append the line to the comment builder.
            if (currentLine.startsWith(COMMENT_PREFIX)) {
                // Get rid of the comment prefix.
                currentLine = currentLine.replaceFirst(COMMENT_PREFIX, "");
                // If necessary, append the system dependent line separator at the end of the string.
                currentComment.append(currentLine.endsWith(lineSeparator) ? currentLine : currentLine + lineSeparator);
                continue;
            }
            // Check of the line is a blank (Doesn't contain any important characters).
            if (currentLine.isEmpty() || currentLine.equalsIgnoreCase(lineSeparator)) {
                continue;
            }
            // This line is probably a key value pair (arranged as 'key: value')
            String[] keyValueArray = currentLine.split(KEY_VALUE_SEPARATOR);
            // If the length is 1, then we'll assume there's no value, therefore assign a default value of null.
            if (keyValueArray.length == 1) {
                keyCommentMap.put(keyValueArray[0], null);
                continue;
            }
            // Assume the length is greater than 1, therefore the key probably has a value
            // associated with it.
            keyCommentMap.put(keyValueArray[0], currentComment.toString());
        }
    }

    public void createKeyValuePair(String key, Object value) {
        if (key == null || key.isEmpty()) {
            PickpocketPlugin.log("Failed to create key: The key is null.");
            return;
        }
        if (!configuration.contains(key) || !configuration.isSet(key)) {
            configuration.set(key, value);
        }
    }

    /**
     * Add a comment to a configuration key.
     */
    public void addComment(String key, String... comment) {
        this.addComment(key, String.join(lineSeparator, comment));
    }

    public void addComment(String key, String comment) {
        if (!configuration.contains(key)) {
            PickpocketPlugin.log("No key with name " + key + " found. Skipping comment.");
        }
        keyCommentMap.put(key, comment);
    }

    public void save() {
        try (BufferedWriter buffWriter = Files.newBufferedWriter(filePath, StandardOpenOption.APPEND)) {
            for (String key : configuration.getKeys(true)) {
                String comment = keyCommentMap.get(key);
                // Write the comment if one exists.
                if (comment != null) {
                    buffWriter.append(comment);
                }
                // Write the key-value-pair to the file.
                buffWriter.append(key)
                        .append(": ")
                        .append(String.valueOf(configuration.get(key)))
                        .append(lineSeparator)
                        .append(lineSeparator);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public void loadConfiguration() {
        try {
            configuration = YamlConfiguration.loadConfiguration(Files.newBufferedReader(filePath));
        } catch (IOException e) {
            PickpocketPlugin.log("Error loading configuration: " + filePath + ".");
        }
    }
}

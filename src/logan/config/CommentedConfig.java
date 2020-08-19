package logan.config;

import logan.pickpocket.main.PickpocketPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CommentedConfig {

    public static String COMMENT_PREFIX = "# ";
    public static String KEY_VALUE_SEPARATOR = ":";
    private static final String lineSeparator = System.lineSeparator();

    private Map<String, String> keyCommentMap;

    private YamlConfiguration configuration = new YamlConfiguration();
    private Path filePath;

    public CommentedConfig(InputStream inputStream, String destPath) {
        filePath = Paths.get(destPath);
        loadConfiguration();
        StringBuilder stringBuilder = new StringBuilder();
        int currByte;
        try {
            while ((currByte = inputStream.read()) != -1) {
                stringBuilder.append((char) currByte);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        keyCommentMap = getKeyCommentMapFromString(stringBuilder.toString());
    }

    /**
     * Creates a commented config from a string.
     */
    public CommentedConfig(String contents, String destPath) {
        filePath = Paths.get(destPath);
        loadConfiguration();
        keyCommentMap = getKeyCommentMapFromString(contents);
    }

    private Map<String, String> getKeyCommentMapFromString(String contents) {
        Map<String, String> keyCommentMap = new HashMap<>();
        Scanner scanner = new Scanner(contents);
        String currentLine;
        StringBuilder currentComment = new StringBuilder();
        while (scanner.hasNext()) {
            currentLine = scanner.nextLine();
            // This line is a comment. Append the line to the comment builder.
            if (currentLine.startsWith(COMMENT_PREFIX)) {
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
                createKeyValuePair(keyValueArray[0], null);
            } else {
                // Assume the length is greater than 1, therefore the key probably has a value
                // associated with it.
                createKeyValuePair(keyValueArray[0], keyValueArray[1]);
            }
            keyCommentMap.put(keyValueArray[0], currentComment.toString());
            // Stores the current comment getting built.
            // Make sure there is nothing in the buffer first by deleting all chars.
            currentComment.delete(0, currentComment.length());
        }
        return keyCommentMap;
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

    public void save() {
        try (BufferedWriter buffWriter = Files.newBufferedWriter(filePath, Files.exists(filePath) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE)) {
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
        // We'll just use an empty config if there's not an existing file.
        if (!Files.exists(filePath))
            return;
        try {
            configuration = YamlConfiguration.loadConfiguration(Files.newBufferedReader(filePath));
        } catch (IOException e) {
            PickpocketPlugin.log("Error loading configuration: " + filePath + ".");
        }
    }
}

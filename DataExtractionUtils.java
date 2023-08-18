import java.nio.file.Files;
import java.nio.file.Paths;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class DataExtractionUtils {
    public static void extractColorData(BufferedImage image, String outputPath) throws IOException {
        Map<Integer, Integer> colorFrequency = new HashMap<>();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getRGB(x, y);
                colorFrequency.put(pixel, colorFrequency.getOrDefault(pixel, 0) + 1);
            }
        }

        String jsonOutput = createJsonFromColorData(colorFrequency);
        Files.write(Paths.get(outputPath), jsonOutput.getBytes());
    }

    public static String createJsonFromColorData(Map<Integer, Integer> colorData) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n");

        List<String> jsonEntries = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : colorData.entrySet()) {
            String key = "\"" + Integer.toHexString(entry.getKey() & 0xFFFFFF) + "\""; // Convert RGB int to hex
            int value = entry.getValue();
            jsonEntries.add("    " + key + ": " + value);
        }

        jsonBuilder.append(String.join(",\n", jsonEntries));
        jsonBuilder.append("\n}");

        return jsonBuilder.toString();
    }

}

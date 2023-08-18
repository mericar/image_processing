import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

interface ImageObserver {
    void onImageReady(BufferedImage image, String path) throws IOException;
}

class ImageSubject {
    private final List<ImageObserver> observers = new ArrayList<>();

    public void addObserver(ImageObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers(BufferedImage image, String path) throws IOException {
        for (ImageObserver observer : observers) {
            observer.onImageReady(image, path);
        }
    }
}

interface TransformationStrategy {
    void transform(int[] pixels);
}

class SortPixelsStrategy implements TransformationStrategy {
    public void transform(int[] pixels) {
        Integer[] pixelObjects = new Integer[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            pixelObjects[i] = pixels[i];
        }

        Arrays.sort(pixelObjects, Comparator.comparingInt(pixel -> (pixel & 0xFFFFFF))); // Sort by RGB value

        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = pixelObjects[i];
        }
    }
}

class RandomizePixelsStrategy implements TransformationStrategy {
    private Random random = new Random();
    public void transform(int[] pixels) {
        for (int i = 0; i < pixels.length; i++) {
            int randomIndex = random.nextInt(pixels.length);

            // Swap pixels
            int temp = pixels[i];
            pixels[i] = pixels[randomIndex];
            pixels[randomIndex] = temp;
        }
    }
}

class ImageProcessor implements ImageObserver {
    private final TransformationStrategy strategy;

    public ImageProcessor(TransformationStrategy strategy) {
        this.strategy = strategy;
    }

    public void onImageReady(BufferedImage image, String outputPath) throws IOException {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = new int[width * height];

        // Extract the pixels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y * width + x] = image.getRGB(x, y);
            }
        }

        // Apply the transformation
        strategy.transform(pixels);

        // Set the pixels back to the image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, pixels[y * width + x]);
            }
        }

        // Save the transformed image
        File transformedImageFile = new File(outputPath);
        ImageIO.write(image, "png", transformedImageFile);
    }
}

class Logger {
    private static Logger instance;

    private Logger() {}

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void log(String message) {
        System.out.println(message);
    }
}


public class ImageProcessingDemo {
    public static void main(String[] args) throws IOException {
        Logger.getInstance().log("Starting Image Transformation...");

        File imageFile = new File("/Users/mec/Desktop/img_fun/image.jpeg"); // Update to the path of your image
        String inputImagePath = imageFile.getAbsolutePath();
        String fileExtension = inputImagePath.substring(inputImagePath.lastIndexOf('.') + 1);

        if (!Arrays.asList(ImageIO.getReaderFileSuffixes()).contains(fileExtension)) {
            Logger.getInstance().log("Unsupported file type: " + fileExtension);
            return;
        }

        BufferedImage image = ImageIO.read(imageFile);
        String outputPath = inputImagePath.substring(0, inputImagePath.lastIndexOf('.')) + "_transformed." + fileExtension;

        ImageSubject imageSubject = new ImageSubject();
        //ImageProcessor processor = new ImageProcessor(new SortPixelsStrategy());
        ImageProcessor processor_r = new ImageProcessor(new RandomizePixelsStrategy());
       // imageSubject.addObserver(processor);
        imageSubject.addObserver(processor_r);
        imageSubject.notifyObservers(image, outputPath);

        DataExtractionUtils.extractColorData(image, "/path/to/file.jpeg");
        Logger.getInstance().log("Extracting image data.");

        Logger.getInstance().log("Image Transformation Complete. Transformed image saved to " + outputPath);
    }
}

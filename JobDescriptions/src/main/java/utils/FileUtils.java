package utils;

import com.aspose.words.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {
    public static List<File> getFilesFromFolder(String pathFolder) {
        List<File> tempFiles = null;
        try {
            tempFiles = Files.walk(Paths.get(pathFolder))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFiles;
    }

    public static void copyFile(String originalFileName, String copiedFileName) {
        try {
            Files.copy(Path.of(originalFileName), Path.of(copiedFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void convertFile(String originalFileName, String convertedFileName){
        try {
            new Document(originalFileName).save(convertedFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(String fileName) {
        new File(fileName).delete();
    }
}

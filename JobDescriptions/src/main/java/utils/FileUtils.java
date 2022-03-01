package utils;

import com.aspose.words.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtils {
    /**
     * Получает все файлы из папки.
     *
     * @param folder Папка в которой нужно найти файлы.
     * @return Список файлов.
     */
    public static List<File> getFilesFromFolder(File folder) {
        if (folder.listFiles() != null) {
            return new ArrayList<File>(Arrays.asList(folder.listFiles()));
        } else {
            return null;
        }
    }

    /**
     * Копирует файл.
     *
     * @param originalFileName Файл который необходимо скопировать.
     * @param copiedFileName   Файл копия.
     */
    public static void copyFile(String originalFileName, String copiedFileName) {
        try {
            Files.copy(Path.of(originalFileName), Path.of(copiedFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Конвертирует файл.
     *
     * @param originalFileName  Файл который необходимо конвертировать.
     * @param convertedFileName Конвертированный файл.
     */
    public static void convertFile(String originalFileName, String convertedFileName) {
        try {
            new Document(originalFileName).save(convertedFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Удаляет файл.
     *
     * @param fileName Файл который необходимо удалить.
     */
    public static void deleteFile(String fileName) {
        new File(fileName).delete();
    }
}

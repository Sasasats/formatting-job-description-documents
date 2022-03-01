import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import utils.DocxUtils;
import utils.FileUtils;
import utils.TxtUtils;
import utils.enums.DocNames;
import utils.enums.Folders;

import java.io.*;
import java.util.List;

public class Main {
    //Названия для статичных файлов, которые не будут меняться по ходу выполнения программы
    private static final String TEMPLATE_FILE = Folders.FOLDER_FOR_TEMPLATES_FILES.getValue()
            + DocNames.TEMPLATE_WORD_DOC_NAME.getValue();
    private static final String COPIED_TEMPLATE_FILE = Folders.FOLDER_FOR_TEMPLATES_FILES.getValue()
            + DocNames.COPIED_TEMPLATE_WORD_DOC_NAME.getValue();

    /**
     * Главная функция программы.
     */
    public static void main(String[] args) {
        //Получить все исходные файлы
        List<File> originalFiles = FileUtils.getFilesFromFolder(new File(Folders.FOLDER_FOR_ORIGINAL_FILES.getValue()));

        assert originalFiles != null;
        for (File file : originalFiles) {
            //Названия файлов с относительным путем для доступа к ним
            String originalDocxFile = Folders.FOLDER_FOR_ORIGINAL_FILES.getValue() + file.getName();
            String formattedDocxFile = Folders.FOLDER_FOR_FORMATTED_FILES.getValue() + file.getName();
            String convertedTextFile = Folders.FOLDER_FOR_CONVERTED_TEXT_FILES.getValue()
                    + file.getName().substring(0, file.getName().length() - 5) + ".txt";
            String formattedTextFile = Folders.FOLDER_FOR_FORMATTED_TEXT_FILES.getValue()
                    + file.getName().substring(0, file.getName().length() - 5) + ".txt";

            //Копирование шаблонного файла
            FileUtils.copyFile(TEMPLATE_FILE, COPIED_TEMPLATE_FILE);

            //Конвертирование .docx в .txt
            FileUtils.convertFile(originalDocxFile, convertedTextFile);

            //Форматирование текстового файла
            TxtUtils.readFile(convertedTextFile, formattedTextFile);

            try {
                //Открытие скопированного файла
                XWPFDocument document = new XWPFDocument(OPCPackage.open(COPIED_TEMPLATE_FILE));

                //Получение списка текста из .txt файла
                List<String> text = TxtUtils.getTextFromFileTxt(formattedTextFile);

                //Получение номера строки для вставки текста
                List<IBodyElement> elements = document.getBodyElements();
                int i = 0;
                for (; i < elements.size(); i++) {
                    IBodyElement element = elements.get(i);
                    if (element instanceof XWPFParagraph paragraph) {
                        List<XWPFRun> runList = paragraph.getRuns();
                        StringBuilder sb = new StringBuilder();
                        for (XWPFRun run : runList) {
                            sb.append(run.getText(0));
                        }
                        if (sb.toString().contains("12345")) {
                            document.removeBodyElement(i);
                            break;
                        }
                    }
                }

                //Вставка текста
                assert text != null;
                DocxUtils.setText(document, text, i);

                //Создание выходного файла
                FileOutputStream out = new FileOutputStream(formattedDocxFile);

                document.write(out);
                document.close();

                //Удаление скопированного файла
                FileUtils.deleteFile(COPIED_TEMPLATE_FILE);
            } catch (IOException | InvalidFormatException e) {
                e.printStackTrace();
            }
        }
    }
}

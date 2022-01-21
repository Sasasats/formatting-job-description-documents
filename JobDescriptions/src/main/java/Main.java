import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import utils.DOCXUtils;
import utils.FileUtils;
import utils.TXTUtils;
import utils.enums.DocNames;
import utils.enums.Folders;

import java.io.*;
import java.util.List;

public class Main {
    //Названия для статичных файлов, которые не будут меняться по ходу выполнения программы
    private static final String TEMPLATE_FILE = Folders.FOLDER_FOR_TEMPLATES_FILES.getValue() +
            DocNames.TEMPLATE_WORD_DOC_NAME.getValue();
    private static final String COPIED_TEMPLATE_FILE = Folders.FOLDER_FOR_TEMPLATES_FILES.getValue() +
            DocNames.COPIED_TEMPLATE_WORD_DOC_NAME.getValue();

    public static void main(String[] args) {
        //Получить все исходные файлы
        List<File> originalFiles = FileUtils.getFilesFromFolder(Folders.FOLDER_FOR_ORIGINAL_FILES.getValue());

        for (File file : originalFiles) {
            System.out.println(file.getName());

            //Названия файлов с относительным путем для доступа к ним
            String ORIGINAL_DOCX_FILE = Folders.FOLDER_FOR_ORIGINAL_FILES.getValue() + file.getName();
            String FORMATTED_DOCX_FILE = Folders.FOLDER_FOR_FORMATTED_FILES.getValue() + file.getName();
            String CONVERTED_TEXT_FILE = Folders.FOLDER_FOR_CONVERTED_TEXT_FILES.getValue() +
                    file.getName().substring(0, file.getName().length() - 5) + ".txt";
            String FORMATTED_TEXT_FILE = Folders.FOLDER_FOR_FORMATTED_TEXT_FILES.getValue() +
                    file.getName().substring(0, file.getName().length() - 5) + ".txt";

            //Копирование шаблонного файла
            FileUtils.copyFile(TEMPLATE_FILE, COPIED_TEMPLATE_FILE);

            //Конвертирование .docx в .txt
            FileUtils.convertFile(ORIGINAL_DOCX_FILE, CONVERTED_TEXT_FILE);

            //Форматирование текстового файла
            TXTUtils.readFile(CONVERTED_TEXT_FILE, FORMATTED_TEXT_FILE);

            try {
                //Открытие скопированного файла
                XWPFDocument document = new XWPFDocument(OPCPackage.open(COPIED_TEMPLATE_FILE));

                //Получение списка текста из .txt файла
                List<String> text = TXTUtils.getTextFromFileTxt(FORMATTED_TEXT_FILE);

                //Получение номера строки для вставки текста
                int counter = 0;
                List<IBodyElement> elements = document.getBodyElements();
                for (; counter < elements.size(); counter++) {
                    IBodyElement element = elements.get(counter);
                    if (element instanceof XWPFParagraph) {
                        XWPFParagraph p1 = (XWPFParagraph) element;
                        List<XWPFRun> runList = p1.getRuns();
                        StringBuilder sb = new StringBuilder();
                        for (XWPFRun run : runList)
                            sb.append(run.getText(0));
                        if (sb.toString().contains("12345")) {
                            document.removeBodyElement(counter);
                            counter--;
                            break;
                        }
                    }
                }

                //Вставка текста
                DOCXUtils.addNumList(document, text, counter, 1, "%1.1", 1);

                //Создание выходного файла
                FileOutputStream out = new FileOutputStream(FORMATTED_DOCX_FILE);

                document.write(out);
                document.close();

                //Удаление скопированного файла
                FileUtils.deleteFile(COPIED_TEMPLATE_FILE);
            } catch (IOException |
                    InvalidFormatException e) {
                e.printStackTrace();
            }
        }
    }
}

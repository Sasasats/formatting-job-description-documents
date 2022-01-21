import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat;
import utils.TXTUtils;
import utils.enums.DocNames;
import utils.enums.Folders;
import com.aspose.words.Document;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    //Названия для статичных файлов, которые не будут меняться по ходу выполнения программы
    private static final String TEMPLATE_FILE = Folders.FOLDER_FOR_TEMPLATES_FILES.getValue() +
            DocNames.TEMPLATE_WORD_DOC_NAME.getValue();
    private static final String COPIED_TEMPLATE_FILE = Folders.FOLDER_FOR_TEMPLATES_FILES.getValue() +
            DocNames.COPIED_TEMPLATE_WORD_DOC_NAME.getValue();

    public static void main(String[] args) {
        List<File> originalFiles = null;
        try {
            originalFiles = Files.walk(Paths.get(Folders.FOLDER_FOR_ORIGINAL_FILES.getValue()))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (File file : originalFiles) {
            System.out.println(file.getName());

            //Названия файлов с относительным путем для доступа к ним
            String ORIGINAL_DOCX_FILE = Folders.FOLDER_FOR_ORIGINAL_FILES.getValue() + file.getName();
            String FORMATTED_DOCX_FILE = Folders.FOLDER_FOR_FORMATTED_FILES.getValue() + file.getName();
            String CONVERTED_TEXT_FILE = Folders.FOLDER_FOR_CONVERTED_TEXT_FILES.getValue() +
                    file.getName().substring(0, file.getName().length() - 5) + ".txt";
            String FORMATTED_TEXT_FILE = Folders.FOLDER_FOR_FORMATTED_TEXT_FILES.getValue() +
                    file.getName().substring(0, file.getName().length() - 5) + ".txt";

            //Конвертирование .docx в .txt
            try {
                Document doc = new Document(ORIGINAL_DOCX_FILE);
                doc.save(CONVERTED_TEXT_FILE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Форматирование текстового файла
            TXTUtils.readFile(CONVERTED_TEXT_FILE, FORMATTED_TEXT_FILE);

            //Копирование шаблонного файла
            try {
                Files.copy(Path.of(TEMPLATE_FILE), Path.of(COPIED_TEMPLATE_FILE));
            } catch (IOException e) {
                e.printStackTrace();
            }

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
                addNumList(document, text, counter, 1, "%1.1", 1);

                //Создание выходного файла
                FileOutputStream out = new FileOutputStream(FORMATTED_DOCX_FILE);

                document.write(out);
                document.close();

                //Удаление скопированного файла
                deleteFile(COPIED_TEMPLATE_FILE);
            } catch (IOException |
                    InvalidFormatException e) {
                e.printStackTrace();
            }
        }
    }


    public static void deleteFile(String fileName) {
        new File(fileName).delete();
    }

    public static void addText() {

    }

    public static void addNumList(XWPFDocument document, List<String> text, int counter, int absNum, String lvlText, int startNum) {
        CTAbstractNum cTAbstractNum = CTAbstractNum.Factory.newInstance();
        cTAbstractNum.setAbstractNumId(BigInteger.valueOf(absNum));
        CTLvl cTLvl = cTAbstractNum.addNewLvl();
        cTLvl.addNewNumFmt().setVal(STNumberFormat.DECIMAL);
        cTLvl.addNewLvlText().setVal(lvlText);
        cTLvl.addNewStart().setVal(BigInteger.valueOf(startNum));

        XWPFAbstractNum abstractNum = new XWPFAbstractNum(cTAbstractNum);
        XWPFNumbering numbering = document.createNumbering();
        BigInteger abstractNumID = numbering.addAbstractNum(abstractNum);
        BigInteger numID = numbering.addNum(abstractNumID);

        for (String line : text) {
            XWPFParagraph paragraph = document.getParagraphs().get(counter);
            XmlCursor cursor = paragraph.getCTP().newCursor();
            XWPFParagraph new_par = document.insertNewParagraph(cursor);
            XWPFRun run = new_par.createRun();

            new_par.setAlignment(ParagraphAlignment.BOTH);
            new_par.setSpacingAfter(0);
            new_par.setSpacingBetween(1);
            new_par.setIndentationFirstLine(851);

            if (!line.isEmpty()) {

                line = line.substring(getFirstLetterIndex(line) - 1);

                new_par.setNumID(numID);
            }
            run.setText(line);
            run.setFontSize(14);
            run.setFontFamily("Times New Roman");
            counter++;
        }
    }

    private static Integer getFirstLetterIndex(String textLine) {
        int counter = 0;
        for (char character : textLine.toCharArray()) {
            counter++;
            if (character >= 'А' && character <= 'я') {
                break;
            }
        }
        return counter;
    }
}

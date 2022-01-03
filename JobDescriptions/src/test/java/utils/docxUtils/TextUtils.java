package utils.docxUtils;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextUtils {
    public static List<XWPFParagraph> getDIText(File file) {
        List<XWPFParagraph> neededParagraphList = new ArrayList<>();

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file.getAbsolutePath());
            XWPFDocument docx = new XWPFDocument(fis);
            List<XWPFParagraph> paragraphList = docx.getParagraphs();

            int indexStart = 0;
            int indexEnd = 0;

            for (int i = 0; i < paragraphList.size(); i++) {
                //Получаем индекс первой строки должностных инструкций
                if (paragraphList.get(i).getText().toLowerCase().contains("общие положения")) {
                    indexStart = i;
                }

                //Получаем индекс последней строки должностных инструкций
                if (paragraphList.get(i).getText().startsWith("5") && paragraphList.get(i + 1).getText().isEmpty()) {
                    indexEnd = i;
                }
            }

            //Записываем нужные строки с первой по последнюю
            for (int i = indexStart; i <= indexEnd; i++) {
                neededParagraphList.add(paragraphList.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return neededParagraphList;
    }

    public static void getParagraphsText(List<XWPFParagraph> paragraphList) {
        for (XWPFParagraph paragraph : paragraphList) {
            System.out.println(paragraph.getText());
        }
    }

    public static void setTextInFile(List<XWPFParagraph> paragraphList, String fileName) {
        try {
            XWPFDocument document = new XWPFDocument();
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);

            XWPFParagraph xwpfParagraph = document.createParagraph();
            XWPFRun run = xwpfParagraph.createRun();
            run.setFontSize(14);
            run.setFontFamily("Times New Roman");
            for (XWPFParagraph paragraph : paragraphList) {
                run.setText(paragraph.getText());
                run.addBreak();
            }

            document.write(fileOutputStream);
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String formatText(XWPFParagraph paragraph) {
        String text = paragraph.getText();
        text = text.trim();
        StringBuilder stringBuffer = new StringBuilder(text);
        if (Character.isDigit(stringBuffer.charAt(0))) {
            stringBuffer.delete(0, 0);
        }
        text = stringBuffer.toString();

        return text;
    }

    /*public void docxToTxt(File file) {
        InputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        POITextExtractor extractor = null;
        // if docx
        if (fileName.toLowerCase().endsWith(".docx")) {
            XWPFDocument doc = null;
            try {
                doc = new XWPFDocument(fis);
            } catch (IOException e) {
                e.printStackTrace();
            }
            extractor = new XWPFWordExtractor(doc);
        } else {
            // if doc
            POIFSFileSystem fileSystem = null;
            try {
                fileSystem = new POIFSFileSystem(fis);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                extractor = ExtractorFactory.createExtractor(fileSystem);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String extractedText = extractor.getText();
    }*/
}

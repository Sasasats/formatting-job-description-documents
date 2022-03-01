package utils;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumbering;

import java.math.BigInteger;
import java.util.List;

public class DocxUtils {
    /**
     * Метод с помощью которого осуществляется вставка текста в документ Word.
     *
     * @param document  Экземпляр документа.
     * @param textArray Текстовый массив (текст который будет вставлен).
     * @param counter   Счетчик (используется для нахождения места для вставки и дальнейшего её продолжения).
     */
    public static void setText(XWPFDocument document, List<String> textArray, int counter) {
        int absNum = 100;
        for (String textLine : textArray) {

            XWPFParagraph newPar = document.getParagraphs().get(counter);
            XmlCursor cursor = newPar.getCTP().newCursor();

            XWPFParagraph paragraph = setDefaultParagraphStyle(document, cursor);
            XWPFRun run = setDefaultRunStyle(paragraph, "Times New Roman", 14);

            if (TxtUtils.getFirstNumberIndex(textLine) == 0 && !textLine.isEmpty()) { // Нумерованный список
                try {
                    String numbers = textLine.substring(0, TxtUtils.getFirstLetterIndex(textLine));
                    numbers = numbers.trim();
                    int replacedNumber = TxtUtils.getLastNumber(numbers);
                    numbers = replaceLastNumber(numbers, 1);
                    numbers = insertIndexLvlTextInTextLine(numbers);

                    String text = textLine.substring(TxtUtils.getFirstLetterIndex(textLine));

                    CTNumbering ctNumbering = CTNumbering.Factory.parse(setDefaultNumberingStyle(
                            "decimal",
                            "Times New Roman",
                            14));
                    CTAbstractNum ctAbstractNum = ctNumbering.getAbstractNumArray(0);
                    ctAbstractNum.setAbstractNumId(BigInteger.valueOf(absNum));
                    //ctAbstractNum.setAbstractNumId(BigInteger.valueOf(getNumberRepeatedCharacters(numbers, ".")));

                    CTLvl ctLvl = ctAbstractNum.addNewLvl();
                    ctLvl.addNewLvlText().setVal(numbers);
                    ctLvl.addNewStart().setVal(BigInteger.valueOf(replacedNumber));

                    XWPFAbstractNum abstractNum = new XWPFAbstractNum(ctAbstractNum);
                    XWPFNumbering numbering = document.createNumbering();
                    BigInteger abstractNumID = numbering.addAbstractNum(abstractNum);
                    BigInteger numID = numbering.addNum(abstractNumID);

                    paragraph.setNumID(numID);
                    run.setText(text);
                } catch (XmlException e) {
                    e.printStackTrace();
                }
            } else if (!textLine.isEmpty()) { // Маркированный список
                try {
                    String numbers = textLine.substring(0, TxtUtils.getFirstLetterIndex(textLine));
                    numbers = numbers.trim();
                    numbers = insertIndexLvlTextInTextLine(numbers);

                    String text = textLine.substring(TxtUtils.getFirstLetterIndex(textLine));

                    CTNumbering ctNumbering = CTNumbering.Factory.parse(setDefaultNumberingStyle(
                            "bullet",
                            "Times New Roman",
                            14));
                    CTAbstractNum ctAbstractNum = ctNumbering.getAbstractNumArray(0);
                    ctAbstractNum.setAbstractNumId(BigInteger.valueOf(7));

                    CTLvl ctLvl = ctAbstractNum.addNewLvl();
                    ctLvl.addNewLvlText().setVal(numbers);

                    XWPFAbstractNum abstractNum = new XWPFAbstractNum(ctAbstractNum);
                    XWPFNumbering numbering = document.createNumbering();
                    BigInteger abstractNumID = numbering.addAbstractNum(abstractNum);
                    BigInteger numID = numbering.addNum(abstractNumID);

                    paragraph.setNumID(numID);
                    run.setText(text);
                } catch (XmlException e) {
                    e.printStackTrace();
                }
            } else if (TxtUtils.getFirstLetterIndex(textLine) == 0 && !textLine.isEmpty()) { // Обычный текст
                run.setText(textLine);
            }
            absNum++;
            counter++;
        }
    }

    /**
     * Метод для настройки по умолчанию нумерации в тексте.
     *
     * @param numberingType Тип нумерации. Decimal - числовой, bullet - маркированный.
     * @param fontStyle     Стиль шрифта.
     * @param fontSize      Размер шрифта.
     * @return Заполненная строка для настройки нумерации.
     */
    private static String setDefaultNumberingStyle(String numberingType, String fontStyle, int fontSize) {
        return "<w:abstractNum xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\""
                + " w:abstractNumId=\"0\">"
                + "<w:multiLevelType w:val=\"hybridMultilevel\"/>"
                + "<w:lvl w:ilvl=\"0\">"
                + "<w:start w:val=\"1\"/>"
                + "<w:numFmt w:val=\"" + numberingType + "\"/>"
                + "<w:suff w:val=\"space\"/>"
                + "<w:lvlText w:val=\"%1.\"/>"
                + "<w:lvlJc w:val=\"left\"/>"
                + "<w:pPr>"
                + "<w:ind w:left=\"0\" w:hanging=\"0\"/>"
                + "</w:pPr>"
                + "<w:rPr>"
                + "<w:rFonts w:ascii=\"" + fontStyle + "\" w:hAnsi=\"" + fontStyle + "\" w:cs=\"" + fontStyle + "\" w:hint=\"default\"/>"
                + "<w:sz w:val=\"" + fontSize * 2 + "\"/>"
                + "<w:szCs w:val=\"" + fontSize * 2 + "\"/>"
                + "</w:rPr>"
                + "</w:lvl>"
                + "</w:abstractNum>";
    }

    /**
     * Метод настройки по умолчанию параграфов в тексте.
     *
     * @param document Исходный документ.
     * @param cursor   Курсор для нахождения конкретного параграфа.
     * @return Настроенный экземпляр параграфа.
     */
    private static XWPFParagraph setDefaultParagraphStyle(XWPFDocument document, XmlCursor cursor) {
        XWPFParagraph paragraph = document.insertNewParagraph(cursor);
        paragraph.setAlignment(ParagraphAlignment.BOTH); // Выравнивание
        paragraph.setSpacingAfter(0); // Отступ после абзаца
        paragraph.setSpacingBetween(1); // Междустрочный множитель
        paragraph.setIndentationFirstLine(851); // Отступ красной строки
        return paragraph;
    }

    /**
     * Метод настройки по умолчанию Run.
     *
     * @param paragraph Параграф для применения стиля.
     * @return Настроенный run.
     */
    private static XWPFRun setDefaultRunStyle(XWPFParagraph paragraph, String fontStyle, int fontSize) {
        XWPFRun run = paragraph.createRun();
        run.setFontFamily(fontStyle); // Стиль шрифта
        run.setFontSize(fontSize); // Размер шрифта
        return run;
    }

    /**
     * Чтобы нумерация корректно отображалась перед последней цифрой/числом необходимо поставить знак '%'. Он служит
     * последующего продолжения данной нумерации.
     *
     * @param textLine Текстовая строка, в частности строка нумерации по примеру 1.1.1.
     * @return Строку нумерации с данным символом, например 1.1.%1. или 1.1.%22.
     */
    private static String insertIndexLvlTextInTextLine(String textLine) {
        if (TxtUtils.getFirstNumberIndex(textLine) == 0) {
            StringBuilder sb = new StringBuilder(textLine);
            int counter = 0;

            switch (getNumberRepeatedCharacters(textLine, ".")) {
                case 1:
                    sb.insert(0, "%");
                    break;
                case 2:
                    counter += sb.indexOf(".") + 1;
                    sb.insert(counter, "%");
                    break;
                case 3:
                    //String copyTextLine = null;
                    for (int i = 0; i < 2; i++) {
                        counter += sb.indexOf(".");
                        //copyTextLine = sb.substring(counter);
                        counter += 1;
                    }
                    sb.insert(counter, "%");
                    break;
                default:
                    break;
            }
            return sb.toString();
        }
        return textLine;
    }

    /**
     * Данный метод заменяет последнюю цифру или даже число на необходимое.
     *
     * @param textLine     Текстовая строка.
     * @param neededNumber Цифра/число на которую происходит замена.
     * @return Форматированная строка с нужной цифрой.
     */
    private static String replaceLastNumber(String textLine, int neededNumber) {
        if (TxtUtils.getFirstNumberIndex(textLine) == 0) {
            StringBuilder sb = new StringBuilder(textLine);

            int number = TxtUtils.getLastNumber(textLine);
            int lastIndex = textLine.lastIndexOf(String.valueOf(number));
            String copyTextLine = sb.substring(0, lastIndex);
            copyTextLine += neededNumber + ".";
            return copyTextLine;
        }
        return textLine;
    }

    /**
     * Метод подсчитывает сколько раз в строке повторяется нужный символ. Используется для нахождения места куда
     * вставлять индекс уровня списка '%'.
     *
     * @param textLine  Текстовая строка.
     * @param character Символ, чье количество нужно подсчитать.
     * @return Количество повторов символа.
     */
    private static int getNumberRepeatedCharacters(String textLine, String character) {
        int index = textLine.indexOf(character);
        int count = 0;
        while (index != -1) {
            textLine = textLine.substring(index + 1);
            index = textLine.indexOf(character);
            count++;
        }
        return count;
    }
}

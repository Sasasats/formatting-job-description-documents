package utils;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat;

import java.math.BigInteger;
import java.util.List;

public class DOCXUtils {
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

                line = line.substring(TXTUtils.getFirstLetterIndex(line) - 1);

                new_par.setNumID(numID);
            }
            run.setText(line);
            run.setFontSize(14);
            run.setFontFamily("Times New Roman");
            counter++;
        }
    }
}

import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import enums.DocNames;
import org.testng.annotations.Test;
import utils.txtUtils.TXTUtils;

public class WordProj {

    @Test
    public void main() {
        // load DOCX with an instance of Document
        Document wordDocument = null;
        try {
            wordDocument = new Document(DocNames.IMPORT_WORD_DOC_NAME.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // call Save method while passing SaveFormat.TXT
        try {
            wordDocument.save(DocNames.CONVERTED_WORD_TO_TXT_DOC_NAME.getValue(), SaveFormat.TEXT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TXTUtils.readFile(DocNames.CONVERTED_WORD_TO_TXT_DOC_NAME.getValue(), DocNames.EXPORT_TXT_DOC_NAME.getValue());
    }
}
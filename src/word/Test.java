package word;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Wen Ke on 2016/12/28.
 */
public class Test {
    public static void main(String[] args) throws IOException {
//        System.out.println(wordCount("data/input.docx"));
//        System.out.println(paraCount("data/input.docx"));
//        printTitleHeirarchy("data/input.docx");
        replace("data/input.doc", "天使", "安娜");
    }
    
    public static int wordCount(String path) throws IOException {
        XWPFDocument docx = new XWPFDocument(new FileInputStream(path));
        XWPFWordExtractor ex = new XWPFWordExtractor(docx);
        char[] raw = ex.getText().toCharArray();
        // word 2016 的字数统计是把所有有形的ASCII子串算作一个字，每个宽字符算作一个字，回车、换行不计字数，回车用\n表示，软回车和硬回车都是\n
        int count = 0;
        boolean word = false;
        for (char c: raw) {
            if (c > 32 && c < 127) { // 中文双引号算作英文标点(用word2016试出来的，什么鬼规则！！！）
                if (!word)
                    count++;
                word = true;
            }
            // 扩展ascii码及宽字符，对于中英文混排的文本不会算错，如果有俄文、法文等就会算错
            else if (c > 127) {
                word = false;
                count++;
            }
        }
        return count;
    }
    
    public static int paraCount(String path) throws IOException {
        XWPFDocument docx = new XWPFDocument(new FileInputStream(path));
        List<XWPFParagraph> ls = docx.getParagraphs();
        int count = 0;
        for (XWPFParagraph p: ls) {
            if (p.getText().trim().length()!=0) {
                ++count;
            }
        }
        return count;
    }
    
    public static void printTitleHeirarchy(String path) throws IOException {
        XWPFDocument docx = new XWPFDocument(new FileInputStream(path));
        List<XWPFParagraph> ls = docx.getParagraphs();
        for (XWPFParagraph p: ls) {
            if (p.getStyle()!=null) {
                int level = Integer.parseInt(p.getStyleID());
                for (int i=0; i<level; ++i)
                    System.out.print('#');
                System.out.print(' ');
                System.out.println(p.getText());
            }
        }
    }
    
    /**
     * .docx文件用的是OOXML格式，paragraph内部是由多个run连缀起来的，经常出现被替换文本被相邻run分割的情况，比较麻烦，因此采用doc格式
     */
    public static void replace(String path, String pat, String dest) throws IOException {
        HWPFDocument doc = new HWPFDocument(new FileInputStream(path));
        Range range = doc.getRange();
        range.replaceText(pat, dest);
        doc.write(new FileOutputStream(path));
    }
}

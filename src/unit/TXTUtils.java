//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package unit;

import common.java.nlogger.nlogger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class TXTUtils {
    public TXTUtils() {
    }

    public static void main(String[] args) {
        String pathname = "C:\\aaa.txt";

        try {
            String code = FileUtils.get_File_charset(pathname);
            String readTxt = Txt2String(pathname);
            string2Txt("C:\\bbb.txt", readTxt, "UTF-8");
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public static void string2Txt(String pathname, String content, String charset) {
        OutputStreamWriter out = null;

        try {
            File writename = new File(pathname);
            writename.createNewFile();
            out = new OutputStreamWriter(new FileOutputStream(writename), charset);
            out.write(content);
            out.flush();
        } catch (IOException var13) {
            nlogger.logout(var13);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException var12) {
                var12.printStackTrace();
            }

        }

    }

    public static String Txt2String(String filePath) {
        StringBuffer stringBuffer = new StringBuffer();
        String code = FileUtils.get_File_charset(filePath);
        BufferedReader br = null;
        InputStreamReader isr = null;
        FileInputStream fis = null;

        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                fis = new FileInputStream(file);
                isr = new InputStreamReader(fis, code);
                br = new BufferedReader(isr);
                String lineTxt = "";

                while((lineTxt = br.readLine()) != null) {
                    stringBuffer.append(lineTxt).append("\r\n");
                }
            }
        } catch (Exception var24) {
            nlogger.logout(var24);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException var23) {
                var23.printStackTrace();
            }

            try {
                if (isr != null) {
                    isr.close();
                }
            } catch (IOException var22) {
                var22.printStackTrace();
            }

            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException var21) {
                var21.printStackTrace();
            }

        }

        return stringBuffer.toString();
    }
}

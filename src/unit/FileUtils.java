//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package unit;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    public FileUtils() {
    }

    public static String get_File_charset(String fileName) {
        BufferedInputStream bin = null;
        String code = null;

        try {
            bin = new BufferedInputStream(new FileInputStream(fileName));
            int p = (bin.read() << 8) + bin.read();
            code = null;
            switch(p) {
            case 61371:
                code = "UTF-8";
                break;
            case 65279:
                code = "UTF-16BE";
                break;
            case 65534:
                code = "Unicode";
                break;
            default:
                code = "GBK";
            }
        } catch (IOException var12) {
            var12.printStackTrace();
        } finally {
            try {
                if (bin != null) {
                    bin.close();
                }
            } catch (IOException var11) {
                var11.printStackTrace();
            }

        }

        return code;
    }

    public static List<File> getFileList(String strPath) {
        File dir = new File(strPath);
        File[] files = dir.listFiles();
        List<File> filelist = new ArrayList();
        if (files != null) {
            for(int i = 0; i < files.length; ++i) {
                String fileName = files[i].getName();
                if (files[i].isDirectory()) {
                    getFileList(files[i].getAbsolutePath());
                } else if (fileName.endsWith("zip")) {
                    String strFileName = files[i].getAbsolutePath();
                    System.out.println("---" + strFileName);
                    filelist.add(files[i]);
                }
            }
        }

        return filelist;
    }

    public static void deleteAllFilesOfDir(File path) {
        if (path.exists()) {
            if (path.isFile()) {
                path.delete();
            } else {
                File[] files = path.listFiles();

                for(int i = 0; i < files.length; ++i) {
                    deleteAllFilesOfDir(files[i]);
                }

                path.delete();
            }
        }

    }

    public static void main(String[] args) {
        deleteAllFilesOfDir(new File("C:\\ckd\\CrawlerContent\\Content\\e6707d5ba2134f9da95ba4d8530b0caf1515592343464.pdf"));
    }
}

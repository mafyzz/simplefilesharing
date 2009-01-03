/*
 *  =============================================================================
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <eirikb@google.com> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return Eirik Brandtzæg
 *  =============================================================================
 */
package no.eirikb.utils.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 *
 * @author Eirik Brandtzæg <eirikdb@gmail.com>
 */
public class MD5File {

    public static String MD5File(File file) {
        try {
            if (file.isFile()) {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                InputStream is = new FileInputStream(file);
                byte[] buffer = new byte[8192];
                int read = 0;
                try {
                    while ((read = is.read(buffer)) > 0) {
                        digest.update(buffer, 0, read);
                    }
                    byte[] md5sum = digest.digest();
                    BigInteger bigInt = new BigInteger(1, md5sum);
                    return bigInt.toString(16);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Note; is recursive - will fail at large files.
     * @param file
     * @return
     */
    public static String MD5Directory(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            Arrays.sort(files);
            String total = "";
            for (File f : files) {
                total += MD5Directory(f);
            }
            return total;
        } else if (file.isFile()) {
            return file.getName() + ":" + MD5File(file);
        }
        return null;
    }
}

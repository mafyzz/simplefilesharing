/*
 * =============================================================================
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <eirikb@google.com> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return Eirik Brandtzæg
 * =============================================================================
 */
package no.eirikb.sfs.share;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class ShareFileReader extends ShareFileHandler {

    private RandomAccessFile currentStream;
    private int read;

    public ShareFileReader(ShareFolder share, File path) {
        super(share, path);
        resetStream();
    }

    public void read(byte[] b, int start) {
        try {
            if (start > b.length) {
                System.out.println("FUCK");
            }
            // byte array smaller then file size
            if (read + b.length < currentStream.length()) {
                //First time run
                if (start == 0) {
                    currentStream.readFully(b);
                    read += b.length;
                // Byte array is not empty!
                } else {
                    byte[] b2 = new byte[b.length - start];
                    currentStream.readFully(b2);
                    System.arraycopy(b2, 0, b, start, b2.length);
                    read += b2.length;
                }
            // Byte array is longer then file length
            } else {
                byte[] b2 = new byte[(int) (currentStream.length() - read)];
                currentStream.readFully(b2);
                System.arraycopy(b2, 0, b, start, b2.length);
                resetStream();
                read(b, b2.length + start);
            }
        } catch (IOException ex) {
            Logger.getLogger(ShareFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void resetStream() {
        selectNextFile();
        try {
            currentStream = new RandomAccessFile(getPath() + currentFile.getPath() + currentFile.getName(), "r");
            currentStream.seek((int) currentFile.getStart());
            read = (int) currentFile.getStart();
        } catch (IOException ex) {
            Logger.getLogger(ShareFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

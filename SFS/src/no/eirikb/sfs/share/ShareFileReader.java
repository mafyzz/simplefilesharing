/*
 * =============================================================================
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <eirikb@google.com> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return Eirik Brandtzæg
 * =============================================================================
 */
package no.eirikb.sfs.share;

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

    public ShareFileReader(ShareFolder share, File path) {
        super(share, path);
        resetStream();
    }

    public byte[] read(int length) {
        try {
            byte[] b = new byte[length];
            if (length >= currentFile.getStop() - (currentFile.getSize() - currentStream.length())) {
                byte[] b2 = new byte[(int) (currentFile.getStop() - (currentFile.getSize() - currentStream.length()))];
                currentStream.read(b2);
                resetStream();
                byte[] b3 = read(length - b2.length);
                System.arraycopy(b2, 0, b, 0, b2.length);
                System.arraycopy(b3, 0, b, b2.length, b3.length);
                return b;
            } else {
                currentStream.read(b);
                return b;
            }
        } catch (IOException ex) {
            Logger.getLogger(ShareFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void resetStream() {
        selectNextFile();
        try {
            currentStream = new RandomAccessFile(getPath() + currentFile.getPath() + currentFile.getName(), "r");
            currentStream.seek((int) currentFile.getStart());
        } catch (IOException ex) {
            Logger.getLogger(ShareFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

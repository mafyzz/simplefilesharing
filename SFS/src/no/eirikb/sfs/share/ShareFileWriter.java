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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class ShareFileWriter extends ShareFileHandler {

    private FileOutputStream currentStream;
    private int written;

    public ShareFileWriter(ShareFolder share, File path) {
        super(share, path);
        resetStream();
    }

    public void write(byte[] b) {
        try {
            if (b.length + written <= currentFile.getStop()) {
                currentStream.write(b);
                written += b.length;
            } else {
                byte[] b2 = new byte[(int) (currentFile.getStop() - written)];
                System.arraycopy(b, 0, b2, 0, b2.length);
                write(b2);
                resetStream();
                byte[] b3 = new byte[b.length - b2.length];
                System.arraycopy(b, b2.length, b3, 0, b3.length);
                write(b3);
            }
        } catch (IOException ex) {
            Logger.getLogger(ShareFileWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void resetStream() {
        selectNextFile();
        try {
            new File(getPath() + currentFile.getPath()).mkdirs();
            currentStream = new FileOutputStream(getPath() + currentFile.getPath() + currentFile.getName());
            written = 0;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ShareFileWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

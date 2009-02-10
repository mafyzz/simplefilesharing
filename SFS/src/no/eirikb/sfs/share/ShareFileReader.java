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

    public void read(byte[] b) {
        read(b, 0);
    }

    private void read(byte[] b, int start) {
        try {
            //Total length of file, cropped
            long fileSize = currentFile.getStop() - currentFile.getStart();
            //Bytes to write
            int length = b.length - start;

            //Number of bytes to write is not longer than file end
            if (read + length < fileSize) {
                currentStream.readFully(b, start, length);
                read += length;
            //Number of bytes exceed end of file
            } else {
                //Rest of bytes to write
                int rest = (int) (fileSize - read);
                currentStream.readFully(b, start, rest);
                //Send the rest to next file
                if (resetStream()) {
                    read(b, start + rest);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ShareFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean resetStream() {
        selectNextFile();
        if (currentFile == null) {
            return false;
        }
        try {
            if (currentStream != null) {
                currentStream.close();
            }
            currentStream = new RandomAccessFile(getPath() + currentFile.getPath() + currentFile.getName(), "r");
            currentStream.seek((int) currentFile.getStart());
            read = 0;
            return true;
        } catch (Exception ex) {
            Logger.getLogger(ShareFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}

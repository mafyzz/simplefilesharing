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
public class ShareFileWriter extends ShareFileHandler {

    private RandomAccessFile currentStream;
    private int written;
    private int blength;

    public ShareFileWriter(ShareFolder share, File path) {
        super(share, path);
        resetStream();
    }

    public void writeTest(byte[] b, int start) {
        try {
            //Total length of file, cropped
            long fileSize = currentFile.getStop() - currentFile.getStart();
            //Bytes to write
            int length = b.length - start;

            //Number of bytes to write is not longer than file end
            if (written + length < fileSize) {
                currentStream.write(b, start, length);
                written += length;
            //Number of bytes exceed end of file
            } else {
                //Rest of bytes to write
                int rest = (int) (fileSize - written);
                currentStream.write(b, start, rest);
                //Send the rest to next file
                if (resetStream()) {
                    writeTest(b, start + rest);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ShareFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void write(byte[] b, int blength) {
        this.blength = blength;
        write2(b, 0);
    }

    private void write2(byte[] b, int start) {
        try {
            //Total length of file, cropped
            long fileSize = currentFile.getStop() - currentFile.getStart();
            //Bytes to write
            int length = blength - start;

            //Number of bytes to write is not longer than file end
            if (written + length < fileSize) {
                currentStream.write(b, start, length);
                written += length;
            //Number of bytes exceed end of file
            } else {
                //Rest of bytes to write
                int rest = (int) (fileSize - written);
                currentStream.write(b, start, rest);
                //Send the rest to next file
                if (resetStream()) {
                    write2(b, start + rest);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ShareFileWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean resetStream() {
        selectNextFile();
        if (currentFile == null) {
            return false;
        }
        try {
            new File(getPath() + currentFile.getPath()).mkdirs();
            if (currentStream != null) {
                currentStream.close();
            }
            currentStream = new RandomAccessFile(getPath() + currentFile.getPath() + currentFile.getName(), "rw");
            currentStream.seek((int) currentFile.getStart());
            written = 0;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

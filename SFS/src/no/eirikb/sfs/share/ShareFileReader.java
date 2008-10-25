/*
 * =============================================================================
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <eirikb@google.com> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return Eirik Brandtzæg
 * =============================================================================
 */
package no.eirikb.sfs.share;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class ShareFileReader {

    private ShareFolder share;
    private String path;
    private ShareFolder currentShare;
    private ShareFile currentFile;
    private FileInputStream currentStream;

    public ShareFileReader(ShareFolder share, String path) {
        this.share = share;
        currentShare = share;
        selectNextFile();
    }

    public byte[] read(int length) {
        byte[] b = readFromFile(length);
        if (b == null) {
            return null;
        }
        if (b.length == length) {
            return b;
        } else {
            selectNextFile();
            return readFromFile(length - b.length);

        }
    }

    private byte[] readFromFile(int length) {
        try {
            if (currentFile.getSize() - currentStream.available() > currentFile.getStart()) {
                if (currentStream.available() < currentFile.getStop()) {
                    if (currentFile.getStop() - currentStream.available() > length) {
                        length = (int) (currentFile.getStop() - currentStream.available());
                    }
                    byte[] b = new byte[length];
                    currentStream.read(b);
                    return b;
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(ShareFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void selectNextFile() {
        try {
            if (currentShare.getFiles().size() > 0) {
                currentFile = currentShare.getFiles().get(0);
                currentShare.getFiles().remove(0);
                currentStream.close();
                currentStream = new FileInputStream(path + currentFile.getPath());
            } else {
                currentShare = getNotEmptyFolder(share);
                selectNextFile();
            }
        } catch (IOException ex) {
            Logger.getLogger(ShareFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ShareFolder getNotEmptyFolder(ShareFolder folder) {
        if (folder.getFiles().size() > 0) {
            return folder;
        } else {
            ShareFolder[] folders = folder.getFolders().toArray(new ShareFolder[0]);
            for (ShareFolder sh : folders) {
                ShareFolder sh2 = getNotEmptyFolder(sh);
                if (sh2 != null) {
                    return sh2;
                } else {
                    folder.getFolders().remove(sh2);
                }
            }
        }
        return null;
    }
}

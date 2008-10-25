/*
 * =============================================================================
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <eirikb@google.com> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return Eirik Brandtzæg
 * =============================================================================
 */
package no.eirikb.sfs.share;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class ShareFileWriter {

    private ShareFolder share;
    private String path;
    private ShareFolder currentShare;
    private ShareFile currentFile;
    private FileOutputStream currentStream;
    private int written;

    public ShareFileWriter(ShareFolder share, String path) {
        this.share = share;
        currentShare = new ShareFolder(share.getName());
        written = 0;
        selectNextFile();
    }

    public void write(byte[] b) {
    }

    private void writeToFile(byte[] b) {

        try {
            if (b.length + written <= currentFile.getStop()) {
                currentStream.write(b);
                written += b.length;
            } else {
                byte[] b2 = new byte[(int) (currentFile.getStop() - written)];
                System.arraycopy(b, 0, b2, 0, b2.length);
                writeToFile(b2);
                //SELECT FILE!
                byte[] b3 = new byte[(int) (currentFile.getSize() - currentFile.getStop())];
                System.arraycopy(b, (int) currentFile.getStop(), b3, 0, b3.length);
                writeToFile(b3);
            }
        } catch (IOException ex) {
            Logger.getLogger(ShareFileWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void selectNextFile() {
        try {
            if (currentShare.getFiles().size() > 0) {
                currentFile = currentShare.getFiles().get(0);
                currentShare.getFiles().remove(0);
                currentStream.close();
                currentStream = new FileOutputStream(path + currentFile.getPath());
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

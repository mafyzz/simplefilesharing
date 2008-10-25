/*
 * =============================================================================
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <eirikb@google.com> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return Eirik Brandtzæg
 * =============================================================================
 */
package no.eirikb.sfs.share;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public abstract class ShareFileHandler {

    public ShareFolder share;
    public String path;
    public ShareFolder currentShare;
    public ShareFile currentFile;

    public ShareFileHandler(ShareFolder share, String path) {
        this.share = share;
        this.path = path;
        currentShare = share;
    }

    public void selectNextFile() {
        if (currentFile != null) {
            currentShare.getFiles().remove(currentFile);
        }
        if (currentShare.getFiles().size() > 0) {
            currentFile = currentShare.getFiles().get(0);
            currentShare.getFiles().remove(0);
        } else {
            currentShare = getNotEmptyFolder(share);
            if (currentShare != null) {
                selectNextFile();
            }
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

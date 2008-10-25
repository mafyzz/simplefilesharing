package no.eirikb.sfs.test;

import no.eirikb.sfs.share.ShareFileHandler;
import no.eirikb.sfs.share.ShareFileReader;
import no.eirikb.sfs.share.ShareFolder;
import no.eirikb.sfs.share.ShareUtility;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class ShareFileHandlerTest extends ShareFileHandler {

    public ShareFileHandlerTest(ShareFolder share, String path) {
        super(share, path);
        for (int i = 0; i < 9; i++) {
            selectNextFile();
            System.out.println(currentFile.getPath());
        }
    }

    public static void main(String[] args) {
        String filePath = "/home/eirikb/test";
        ShareFolder share = ShareUtility.createShare(filePath).getShare();
        ShareFileHandlerTest test = new ShareFileHandlerTest(share, filePath);
    }
}

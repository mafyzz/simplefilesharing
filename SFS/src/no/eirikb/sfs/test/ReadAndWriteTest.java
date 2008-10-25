package no.eirikb.sfs.test;

import no.eirikb.sfs.share.ShareFileReader;
import no.eirikb.sfs.share.ShareFileWriter;
import no.eirikb.sfs.share.ShareFolder;
import no.eirikb.sfs.share.ShareUtility;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class ReadAndWriteTest {

    public static void main(String[] args) {
        String filePath = "/home/eirikb/test";
        ShareFolder writeShare = ShareUtility.createShare(filePath).getShare();
        ShareFolder readShare = ShareUtility.createShare(filePath).getShare();

        ShareFileReader reader = new ShareFileReader(writeShare, "/home/eirikb/test");

        ShareFileWriter writer = new ShareFileWriter(readShare, "downloads/" + readShare.getName());

        long end = readShare.getSize() - 1;
        int buffer = 10000;
        long tot = 0;
        while (tot < end) {
            buffer = buffer < readShare.getSize() - tot ? buffer : (int) (end - tot);
            writer.write(reader.read(buffer));
            tot += buffer;
        }
    }
}

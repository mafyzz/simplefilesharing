package no.eirikb.sfs.test;

import java.io.File;
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
        File file = new File("/home/eirikb/Desktop/[DB]_Bleach_191_[B10B96E2].avi");
        ShareFolder writeShare = ShareUtility.createShare(file).getShare();
        ShareFolder readShare = ShareUtility.createShare(file).getShare();

        ShareFileReader reader = new ShareFileReader(writeShare, file);

        ShareFileWriter writer = new ShareFileWriter(readShare, new File("downloads/" + readShare.getName()));

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

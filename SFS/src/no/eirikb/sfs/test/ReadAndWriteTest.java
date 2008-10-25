package no.eirikb.sfs.test;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import no.eirikb.sfs.share.Share;
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
        Share readShare = ShareUtility.createShare(file);
        long split = readShare.getShare().getSize() / 2;

        ShareFileReader r1 = new ShareFileReader(ShareUtility.cropShare(readShare, 0, split), file);
        ShareFileReader r2 = new ShareFileReader(ShareUtility.cropShare(readShare, split, split * 2), file);

        Share writeShare = ShareUtility.createShare(file);

        File writeFile = new File("downloads/" + readShare.getName());

        ShareFileWriter w1 = new ShareFileWriter(ShareUtility.cropShare(writeShare, 0, split), writeFile);
        ShareFileWriter w2 = new ShareFileWriter(ShareUtility.cropShare(writeShare, split, split * 2), writeFile);


        long end = split - 1;
        int buffer = 10000;
        long tot = 0;
        while (tot < end) {
            buffer = buffer < end - tot ? buffer : (int) (end - tot);
            w1.write(r1.read(buffer));
            w2.write(r2.read(buffer));
            tot += buffer;
        }
    }
}

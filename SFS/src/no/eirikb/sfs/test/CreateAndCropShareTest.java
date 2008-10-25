package no.eirikb.sfs.test;

import no.eirikb.sfs.share.Share;
import no.eirikb.sfs.share.ShareFolder;
import no.eirikb.sfs.share.ShareUtility;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class CreateAndCropShareTest {

    public static void main(String[] args) {
        String filePath = "/home/eirikb/Desktop";
        Share result = ShareUtility.createShare(filePath);

        long split = result.getShare().getSize() / 2;
        ShareFolder crop1 = ShareUtility.cropShare(result, 0, split);
        ShareFolder crop2 = ShareUtility.cropShare(result, split, split * 2);
        System.out.println(crop1);
        System.out.println("-------------------------------------------------");
        System.out.println(crop2);
    }
}

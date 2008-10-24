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

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class ShareCreator {

    private static long tot;

    public static Share createShare(String filePath) {
        File file = new File(filePath);
        Share share = new Share(file.getName());
        ShareFolder shareFolder = new ShareFolder(file.getName());
        share.setShare(shareFolder);
        insert(shareFolder, file);
        share.setHash(new String(share.getName() + shareFolder.getSize() + shareFolder.getTotal()).hashCode());
        return share;
    }

    private static void insert(ShareFolder share, File file) {
        if (file.isDirectory()) {
            ShareFolder sh = new ShareFolder(file.getName());
            share.getFolders().add(sh);
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    insert(sh, f);
                }
                share.setSize(share.getSize() + sh.getSize());
                share.setTotal(share.getTotal() + sh.getTotal());
            }
        } else if (file.isFile()) {
            ShareFile sf = new ShareFile(file.getName(), file.length());
            share.getFiles().add(sf);
            share.setSize(share.getSize() + file.length());
            share.setTotal(share.getTotal() + 1);
        }
    }

    public static ShareFolder cropShare(Share share, long start, long stop) {
        ShareFolder startShare = share.getShare();
        ShareFolder newShare = new ShareFolder(share.getName());
        tot = 0;
        cropShareFolder(startShare, newShare, start, stop);
        return newShare;
    }

    private synchronized static void cropShareFolder(ShareFolder share, ShareFolder newShare,
            long start, long stop) {
        if (tot >= 0) {
            for (ShareFile fl : share.getFiles()) {
                ShareFile f = new ShareFile(fl.getName(), fl.getSize());
                tot += f.getSize();
                if (tot >= start) {
                    if (tot - f.getSize() < start) {
                        f.setStart(f.getSize() - (tot - start));
                    }
                    newShare.getFiles().add(f);
                    newShare.setSize(newShare.getSize() + f.getSize());
                    newShare.setTotal(newShare.getTotal() + 1);
                    if (tot >= stop) {
                        if (tot > stop) {
                            System.out.println(f.getSize() + " " + tot + " " + stop + " " + f.getName());
                            f.setStop(f.getSize() - (tot - stop));
                            newShare.setSize(newShare.getSize() - (tot - stop));
                            tot = -1;
                            return;
                        }
                    }
                }
            }
            for (ShareFolder sh : share.getFolders()) {
                ShareFolder newShare2 = new ShareFolder(sh.getName());
                newShare.getFolders().add(newShare2);
                cropShareFolder(sh, newShare2, start, stop);
            }
        }
    }
}

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
public class ShareUtility {

    private static long tot;
    private static int pathLength;

    public static Share createShare(File[] files, String name) {
        File file = files[0];
        pathLength = file.getAbsolutePath().length();
        if (file.isFile()) {
            pathLength = file.getAbsolutePath().substring(0,
                    file.getAbsolutePath().length() - file.getName().length() - 1).length();
        }
        Share share = new Share(name);
        ShareFolder shareFolder = new ShareFolder(name);
        share.setShare(shareFolder);
        for (File f : files) {
            insert(shareFolder, file);
        }
        deleteEmptyFolders(shareFolder);
        share.setHash(new String(share.getName() + shareFolder.getSize() + shareFolder.getTotal()).hashCode());
        return share;
    }

    public static Share createShare(File file) {
        pathLength = file.getAbsolutePath().length();
        if (file.isFile()) {
            pathLength = file.getAbsolutePath().substring(0,
                    file.getAbsolutePath().length() - file.getName().length() - 1).length();
        }
        Share share = new Share(file.getName());
        ShareFolder shareFolder = new ShareFolder(file.getName());
        share.setShare(shareFolder);
        insert(shareFolder, file);
        deleteEmptyFolders(shareFolder);
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
            String path = file.getPath();
            path = path.substring(pathLength, path.length() - file.getName().length());
            ShareFile sf = new ShareFile(file.getName(), file.length(), path);
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
        deleteEmptyFolders(newShare);
        newShare.setSize(stop - start);
        return newShare;
    }

    public static ShareFolder[] cropShareToParts(Share share, int parts) {
        ShareFolder[] shares = new ShareFolder[parts];
        long size = (share.getShare().getSize() / parts);
        for (int i = 0; i < parts - 1; i++) {
            shares[i] = ShareUtility.cropShare(share, i * size, (i + 1) * size);
        }
        int i = parts - 1;
        shares[i] = ShareUtility.cropShare(share, i * size, share.getShare().getSize());
        return shares;
    }

    private static void cropShareFolder(ShareFolder share, ShareFolder newShare,
            long start, long stop) {
        if (tot >= 0) {
            for (ShareFile fl : share.getFiles()) {
                ShareFile f = new ShareFile(fl.getName(), fl.getSize(), fl.getPath());
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

    private static ShareFolder deleteEmptyFolders(ShareFolder share) {
        ShareFolder[] shares = share.getFolders().toArray(new ShareFolder[0]);
        for (ShareFolder sh : shares) {
            if (sh.getFolders().size() == 0 && sh.getFiles().size() == 0) {
                share.getFolders().remove(sh);
            } else {
                ShareFolder sh2 = deleteEmptyFolders(sh);
                if (sh2 != null) {
                    share.getFolders().remove(sh2);
                }
            }
        }
        if (share.getFolders().size() > 0 || share.getFiles().size() > 0) {
            return null;
        } else {
            return share;
        }
    }
}

/*
 * =============================================================================
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <eirikb@google.com> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return Eirik Brandtzæg
 * =============================================================================
 */
package no.eirikb.sfs.share;

import java.io.Serializable;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class Share implements Serializable {

    private int hash;
    private String name;
    private ShareFolder share;

    public Share(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ShareFolder getShare() {
        return share;
    }

    public void setShare(ShareFolder share) {
        this.share = share;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public int getHash() {
        return hash;
    }

    @Override
    public String toString() {
        long bytes = share.getSize();
        String size = bytes + " bytes";
        if (bytes >= Math.pow(1024, 1) && bytes < Math.pow(1024, 2)) {
            size = (bytes / Math.pow(1024, 1)) + " KiB";
        } else if (bytes >= Math.pow(1024, 2) && bytes < Math.pow(1024, 3)) {
            size = (bytes / Math.pow(1024, 2)) + " MiB";
        } else if (bytes >= Math.pow(1024, 3)) {
            size = (bytes / Math.pow(1024, 3)) + " GiB";
        }
        return name + " (" + share.getTotal() + " files, " + size + ") [" + hash + "]";

    }
}

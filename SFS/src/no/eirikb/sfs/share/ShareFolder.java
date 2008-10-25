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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class ShareFolder implements Serializable {

    private String name;
    private List<ShareFolder> folders;
    private List<ShareFile> files;
    private long size;
    private int total;

    public ShareFolder(String name) {
        this.name = name;
        folders = new ArrayList<ShareFolder>();
        files = new ArrayList<ShareFile>();
    }

    public List<ShareFile> getFiles() {
        return files;
    }

    public List<ShareFolder> getFolders() {
        return folders;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String toString() {
        return toString(0);
    }

    public String toString(int tabLength) {
        String tab = "";
        for (int i = 0; i < tabLength; i++) {
            tab += " ";
        }
        String s = tab + "[FOLDER] " + total + " - " + size + " - " + name;
        for (ShareFile f : files) {
            s += "\n" + tab + "[FILE] " + f.getSize() + "(" + f.getStart() + ", " + f.getStop() + ") - " + f.getName() + " " + f.getPath();
        }
        for (ShareFolder f : folders) {
            s += tab + "\n" + f.toString(tabLength + 1);
        }
        return s;
    }
}

/*
 * =============================================================================
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <eirikb@google.com> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return Eirik Brandtzæg
 * =============================================================================
 */
package no.eirikb.sfs.sfsserver;

import java.util.ArrayList;
import java.util.List;
import no.eirikb.sfs.share.Share;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class ShareHolder {

    private Share share;
    private List<User> users;

    public ShareHolder(Share share) {
        this.share = share;
        users = new ArrayList<User>();
    }

    public Share getShare() {
        return share;
    }

    public List<User> getUsers() {
        return users;
    }
}

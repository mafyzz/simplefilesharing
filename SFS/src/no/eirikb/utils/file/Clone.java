/*
 *  =============================================================================
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <eirikb@google.com> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return Eirik Brandtzæg
 *  =============================================================================
 */
package no.eirikb.utils.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eirik Brandtzæg <eirikdb@gmail.com>
 */
public class Clone {

    public static Object clone(Object o) {
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            out = new ObjectOutputStream(b);
            out.writeObject(o);
            out.close();
            ByteArrayInputStream bi = new ByteArrayInputStream(b.toByteArray());
            in = new ObjectInputStream(bi);
            Object no = in.readObject();
            return no;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Clone.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Clone.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(Clone.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
}

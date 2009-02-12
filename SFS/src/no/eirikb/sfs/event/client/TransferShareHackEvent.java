/*
 * =============================================================================
 * Copyright (c) 2008 Exaid. All rights reserved.
 * =============================================================================
 */
package no.eirikb.sfs.event.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.eirikb.sfs.client.Client;
import no.eirikb.sfs.client.LocalShare;
import no.eirikb.sfs.client.SFSClient;
import no.eirikb.sfs.client.SFSClientListener;
import no.eirikb.sfs.event.Event;
import no.eirikb.sfs.event.server.DownloadCompleteEvent;
import no.eirikb.sfs.server.Server;
import no.eirikb.sfs.sfsserver.SFSServer;
import no.eirikb.sfs.sfsserver.SFSServerListener;
import no.eirikb.sfs.share.ShareFileReader;
import no.eirikb.sfs.share.ShareFileWriter;
import no.eirikb.sfs.share.ShareFolder;
import no.eirikb.sfs.share.ShareUtility;

/**
 *
 * @author eirikb
 */
public class TransferShareHackEvent extends Event {

    private Integer hash;
    private int totalParts;
    private int partNumber;
    private Socket socket;
    private ShareFolder part;

    public TransferShareHackEvent(Integer hash, int totalParts, int partNumber, Socket socket, ShareFolder part) {
        this.hash = hash;
        this.totalParts = totalParts;
        this.partNumber = partNumber;
        this.socket = socket;
        this.part = part;
    }

    public TransferShareHackEvent(Socket socket) {
        this.socket = socket;
    }

    public void executeServer(SFSClientListener listener, SFSClient client) {
        try {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            hash = in.read();
            System.out.println("Hash: " + hash);
            totalParts = in.read();
            System.out.println("Total parts: " + totalParts);
            partNumber = in.read();
            System.out.println("Part number: " + partNumber);

            LocalShare ls = client.getLocalShares().get(hash);
            System.out.println("LocalShare: " + ls);
            System.out.println("LocalShares size: " + client.getLocalShares().size());
            System.out.println("LocalShare hashes:");
            for (LocalShare l : client.getLocalShares().values()) {
                System.out.println(l.getShare().getHash());
            }

            part = ShareUtility.cropShareToParts(ls.getShare(), totalParts)[partNumber];
            ShareFileReader reader = new ShareFileReader(part, ls.getFile());
            byte[] buf = new byte[socket.getSendBufferSize()];
            long tot = 0;
            while (tot < part.getSize()) {
                reader.read(buf);
                out.write(buf);
                out.flush();
                tot += buf.length;
                listener.sendStatus(ls, part, partNumber, tot);
            }
            listener.sendDone(ls);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void execute(SFSServerListener listener, Server client, SFSServer server) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void execute(SFSClientListener listener, SFSClient sfsClient) {
        try {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            out.write(hash);
            out.write(totalParts);
            out.write(partNumber);

            ShareFileWriter writer = new ShareFileWriter(part,
                    new File(sfsClient.getShareFolder() + part.getName()));
            LocalShare ls = sfsClient.getLocalShares().get(hash);
            byte[] buf = new byte[socket.getReceiveBufferSize()];
            int read;
            long tot = 0;
            while (tot < part.getSize()) {
                read = in.read(buf);
                writer.write(buf, read);
                tot += read;
                listener.receiveStatus(ls, part, partNumber, read);
            }
            ls.incShares();
            if (ls.getShares() == ls.getTotalShares()) {
                listener.receiveDone(ls);
                sfsClient.getClient().sendObject(new DownloadCompleteEvent(hash));
            }
        } catch (IOException ex) {
            Logger.getLogger(TransferShareEvent.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(TransferShareEvent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void execute(SFSClientListener listener, SFSClient client, Server server) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void execute(SFSClientListener listener, SFSClient sfsClient, Client client) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

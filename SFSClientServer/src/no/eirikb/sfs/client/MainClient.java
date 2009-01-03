/*
 * MainClient.java
 *
 * Created on November 2, 2008, 2:49 PM
 */
package no.eirikb.sfs.client;

import java.awt.CardLayout;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import no.eirikb.sfs.event.server.CreateShareEvent;
import no.eirikb.sfs.event.server.GetShareOwnersEvent;
import no.eirikb.sfs.share.Share;
import no.eirikb.sfs.share.ShareFile;
import no.eirikb.sfs.share.ShareFolder;
import no.eirikb.sfs.share.ShareUtility;

/**
 *
 * @author  eirikb
 */
public class MainClient extends javax.swing.JFrame implements SFSClientListener {

    private SFSClient client;
    private JProgressBar[] bars;

    /** Creates new form MainClient */
    public MainClient() {
        initComponents();

        final MainClient mainClient = this;
        new Thread() {

            public void run() {
                loginTextArea.append("Welcome to SFS (Simple File Sharing)!");
                loginTextArea.append("\nConnecting and setting up local server...");
                try {
                    int listenPort = (int) (Math.random() * (65500 - 1024) + 1024);
                    loginTextArea.append("\nListening on port " + listenPort);
                    client = new SFSClient(mainClient, listenPort);
                    loginTextArea.append("\nDone");
                    show("main");
                } catch (IOException ex) {
                    loginTextArea.append("\nUnable to connect! Error: " + ex.toString());
                    Logger.getLogger(MainClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();
    }

    public void addShare(Share share) {
        System.out.println("add share");
        DefaultTreeModel model = (DefaultTreeModel) availableSharesTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        model.insertNodeInto(createShareNode(share), root, root.getChildCount());
        availableSharesTree.expandRow(0);
    }

    private void show(String panelName) {
        CardLayout layout = (CardLayout) containerPanel.getLayout();
        layout.show(containerPanel, panelName);
    }

    private DefaultMutableTreeNode createShareNode(Share share) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(share);
        addNodesToShareNode(node, share.getShare());
        return node;
    }

    private void addNodesToShareNode(DefaultMutableTreeNode node, ShareFolder folder) {
        for (ShareFile file : folder.getFiles()) {
            node.add(new DefaultMutableTreeNode(file));
        }
        for (ShareFolder f : folder.getFolders()) {
            DefaultMutableTreeNode folderNode = new DefaultMutableTreeNode(f);
            node.add(folderNode);
            addNodesToShareNode(folderNode, f);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        AvailableSharesPopup = new javax.swing.JPopupMenu();
        downloadShareMenuItem = new javax.swing.JMenuItem();
        containerPanel = new javax.swing.JPanel();
        loginPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        loginTextArea = new javax.swing.JTextArea();
        mainPanel = new javax.swing.JPanel();
        verticalSplitPane = new javax.swing.JSplitPane();
        horizontalSplitPane = new javax.swing.JSplitPane();
        mySharesPanel = new javax.swing.JPanel();
        mySharesLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mySharesList = new javax.swing.JList();
        availableSharesPanel = new javax.swing.JPanel();
        availableSharesLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        availableSharesTree = new javax.swing.JTree();
        transferPanel = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        addShareMenuItem = new javax.swing.JMenuItem();

        downloadShareMenuItem.setText("Download");
        downloadShareMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadShareMenuItemActionPerformed(evt);
            }
        });
        AvailableSharesPopup.add(downloadShareMenuItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SFS (Simple File Sharing)");
        setBounds(new java.awt.Rectangle(0, 0, 640, 480));
        setMinimumSize(new java.awt.Dimension(640, 480));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.CardLayout());

        containerPanel.setLayout(new java.awt.CardLayout());

        loginPanel.setLayout(new java.awt.BorderLayout());

        loginTextArea.setColumns(20);
        loginTextArea.setEditable(false);
        loginTextArea.setRows(5);
        jScrollPane4.setViewportView(loginTextArea);

        loginPanel.add(jScrollPane4, java.awt.BorderLayout.CENTER);

        containerPanel.add(loginPanel, "login");

        mainPanel.setLayout(new java.awt.BorderLayout());

        verticalSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        verticalSplitPane.setResizeWeight(0.8);

        horizontalSplitPane.setResizeWeight(0.8);

        mySharesPanel.setLayout(new java.awt.BorderLayout());

        mySharesLabel.setText("My shares");
        mySharesPanel.add(mySharesLabel, java.awt.BorderLayout.NORTH);

        mySharesList.setModel(new DefaultListModel());
        jScrollPane1.setViewportView(mySharesList);

        mySharesPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        horizontalSplitPane.setRightComponent(mySharesPanel);

        availableSharesPanel.setLayout(new java.awt.BorderLayout());

        availableSharesLabel.setText("Available shares");
        availableSharesPanel.add(availableSharesLabel, java.awt.BorderLayout.NORTH);

        availableSharesTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Shares")));
        availableSharesTree.setComponentPopupMenu(AvailableSharesPopup);
        jScrollPane2.setViewportView(availableSharesTree);

        availableSharesPanel.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        horizontalSplitPane.setLeftComponent(availableSharesPanel);

        verticalSplitPane.setTopComponent(horizontalSplitPane);

        transferPanel.setMinimumSize(new java.awt.Dimension(0, 100));
        transferPanel.setLayout(new java.awt.GridLayout());
        verticalSplitPane.setRightComponent(transferPanel);

        mainPanel.add(verticalSplitPane, java.awt.BorderLayout.CENTER);

        containerPanel.add(mainPanel, "main");

        getContentPane().add(containerPanel, "card4");

        fileMenu.setText("File");

        addShareMenuItem.setText("Add share");
        addShareMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addShareMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(addShareMenuItem);

        menuBar.add(fileMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    System.out.println("Closing...");
    if (client != null) {
        client.closeServerListener();
    }
}//GEN-LAST:event_formWindowClosing

private void addShareMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addShareMenuItemActionPerformed
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    fileChooser.setMultiSelectionEnabled(true);
    fileChooser.showOpenDialog(this);
    if (fileChooser.getSelectedFiles().length > 0) {
        String shareName = "";
        if (fileChooser.getSelectedFiles().length == 1) {
            shareName = JOptionPane.showInputDialog(this, "Name of share:", fileChooser.getSelectedFiles()[0].getName());
        } else {
            shareName = JOptionPane.showInputDialog(this, "Name of share:");
        }
        if (shareName != null && shareName.length() > 0) {
            System.out.println(shareName);
            Share share = ShareUtility.createShare(fileChooser.getSelectedFiles(), shareName);
            System.out.println(share.getShare());
            DefaultListModel model = (DefaultListModel) mySharesList.getModel();
            model.addElement(share);
            client.getClient().sendObject(new CreateShareEvent(share));
            client.getLocalShares().put(share.getHash(), new LocalShare(fileChooser.getSelectedFiles()[0], share));
        }
    }
}//GEN-LAST:event_addShareMenuItemActionPerformed

private void downloadShareMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadShareMenuItemActionPerformed
    if (availableSharesTree.getSelectionPath() != null) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) availableSharesTree.getSelectionPath().getLastPathComponent();
        Share share = (Share) node.getUserObject();
        client.getClient().sendObject(new GetShareOwnersEvent(share));
    }
}//GEN-LAST:event_downloadShareMenuItemActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new MainClient().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPopupMenu AvailableSharesPopup;
    private javax.swing.JMenuItem addShareMenuItem;
    private javax.swing.JLabel availableSharesLabel;
    private javax.swing.JPanel availableSharesPanel;
    private javax.swing.JTree availableSharesTree;
    private javax.swing.JPanel containerPanel;
    private javax.swing.JMenuItem downloadShareMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JSplitPane horizontalSplitPane;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel loginPanel;
    private javax.swing.JTextArea loginTextArea;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JLabel mySharesLabel;
    private javax.swing.JList mySharesList;
    private javax.swing.JPanel mySharesPanel;
    private javax.swing.JPanel transferPanel;
    private javax.swing.JSplitPane verticalSplitPane;
    // End of variables declaration//GEN-END:variables

    public void removeShare(Share share) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void receiveStatus(LocalShare ls, ShareFolder share, int partNumber, long bytes) {
        bars[partNumber].setValue(bars[partNumber].getValue() + (int) bytes);
    }

    public void reveiveDone(LocalShare ls) {
        JOptionPane.showMessageDialog(null, "Receive done!");
    }

    public void sendStatus(LocalShare ls, ShareFolder share, int partNumber, long bytes) {
    }

    public void sendDone(LocalShare ls) {
        JOptionPane.showMessageDialog(null, "Send done!");
    }

    public void shareStartInfo(ShareFolder[] parts) {
        bars = new JProgressBar[parts.length];
        for (int i = 0; i < parts.length; i++) {
            bars[i] = new JProgressBar();
            bars[i].setMaximum((int) (parts[i].getSize()));
            bars[i].setVisible(true);
            transferPanel.add(bars[i]);
        }
    }
}

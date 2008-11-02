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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import no.eirikb.sfs.share.Share;
import no.eirikb.sfs.share.ShareUtility;

/**
 *
 * @author  eirikb
 */
public class MainClient extends javax.swing.JFrame implements SFSClientListener {

    private SFSClient client;

    /** Creates new form MainClient */
    public MainClient() {
        initComponents();

        final MainClient mainClient = this;
        new Thread() {

            public void run() {
                loginTextArea.append("Welcome to SFS (Simple File Sharing)!");
                loginTextArea.append("\nSearching for server...");
                String server = findServer();
                loginTextArea.append("\nServer found: " + server);
                loginTextArea.append("\nConnecting and setting up local server...");
                try {
                    client = new SFSClient(mainClient, server, 31338, 31340);
                    loginTextArea.append("\nDone");
                    show("main");
                } catch (IOException ex) {
                    loginTextArea.append("\nUnable to connect! Error: " + ex.toString());
                    Logger.getLogger(MainClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();
    }

    private String findServer() {
        return "localhost";
    }

    public void addShare(Share share) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void show(String panelName) {
        CardLayout layout = (CardLayout) containerPanel.getLayout();
        layout.show(containerPanel, panelName);
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
        jScrollPane3 = new javax.swing.JScrollPane();
        transferList = new javax.swing.JList();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        addFileShareMenuItem = new javax.swing.JMenuItem();

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

        availableSharesTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
        jScrollPane2.setViewportView(availableSharesTree);

        availableSharesPanel.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        horizontalSplitPane.setLeftComponent(availableSharesPanel);

        verticalSplitPane.setTopComponent(horizontalSplitPane);

        transferPanel.setLayout(new java.awt.BorderLayout());

        transferList.setModel(new DefaultListModel());
        jScrollPane3.setViewportView(transferList);

        transferPanel.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        verticalSplitPane.setRightComponent(transferPanel);

        mainPanel.add(verticalSplitPane, java.awt.BorderLayout.CENTER);

        containerPanel.add(mainPanel, "main");

        getContentPane().add(containerPanel, "card4");

        fileMenu.setText("File");

        addFileShareMenuItem.setText("Add share (Single file)");
        addFileShareMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFileShareMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(addFileShareMenuItem);

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

private void addFileShareMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFileShareMenuItemActionPerformed
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    fileChooser.setMultiSelectionEnabled(true);
    fileChooser.showOpenDialog(this);
    String shareName = JOptionPane.showInputDialog("Name of share:");
    Share share = ShareUtility.createShare(fileChooser.getSelectedFiles(), shareName);
    System.out.println(share.getShare());
}//GEN-LAST:event_addFileShareMenuItemActionPerformed

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
    private javax.swing.JMenuItem addFileShareMenuItem;
    private javax.swing.JLabel availableSharesLabel;
    private javax.swing.JPanel availableSharesPanel;
    private javax.swing.JTree availableSharesTree;
    private javax.swing.JPanel containerPanel;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JSplitPane horizontalSplitPane;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel loginPanel;
    private javax.swing.JTextArea loginTextArea;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JLabel mySharesLabel;
    private javax.swing.JList mySharesList;
    private javax.swing.JPanel mySharesPanel;
    private javax.swing.JList transferList;
    private javax.swing.JPanel transferPanel;
    private javax.swing.JSplitPane verticalSplitPane;
    // End of variables declaration//GEN-END:variables

}

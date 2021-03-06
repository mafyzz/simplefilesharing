/*
 * SFSUIView.java
 */
package sfsui;

import java.io.IOException;
import java.util.EventObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.eirikb.sfs.client.LocalShare;
import no.eirikb.sfs.share.Share;
import no.eirikb.sfs.share.ShareFolder;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.util.Hashtable;
import java.util.Map;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import no.eirikb.sfs.client.SFSClient;
import no.eirikb.sfs.client.SFSClientListener;
import no.eirikb.sfs.event.server.CreateShareEvent;
import no.eirikb.sfs.event.server.GetShareOwnersEvent;
import no.eirikb.sfs.share.ShareFile;
import no.eirikb.sfs.share.ShareUtility;

/**
 * The application's main frame.
 */
public class SFSUIView extends FrameView {

    public SFSUIView(SingleFrameApplication app) {
        super(app);

        initComponents();

        JFrame mainFrame = SFSUIApp.getApplication().getMainFrame();
        errorBox = new SFSUIErrorBox(mainFrame);
        errorBox.setLocationRelativeTo(mainFrame);

        tps = new Hashtable<Integer, TransferPanel>();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        resourceMap = getResourceMap();
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });

        new Thread() {

            public void run() {
                connect(null);
            }
        }.start();
    }

    private void connect(Integer port) {
        try {
            statusAnimationLabel.setIcon(busyIcons[0]);
            busyIconIndex = 0;
            busyIconTimer.start();
            statusMessageLabel.setText("Connecting to server...");
            int randomPort = (int) ((Math.random() * 60000) + 1024);
            if (port != null) {
                randomPort = port;
            }
            sfs = new SFSClient(new SFSClientListener() {

                public void addShare(Share share) {
                    System.out.println("add share");
                    DefaultTreeModel model = (DefaultTreeModel) availableSharesTree.getModel();
                    DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
                    model.insertNodeInto(createShareNode(share), root, root.getChildCount());
                    availableSharesTree.expandRow(0);
                }

                public void removeShare(Share share) {
                    System.out.println("remove share");
                    DefaultTreeModel model = (DefaultTreeModel) availableSharesTree.getModel();
                    DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
                    for (int i = 0; i < root.getChildCount(); i++) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(i);
                        Share share2 = (Share) node.getUserObject();
                        if (share.getHash() == share2.getHash()) {
                            model.removeNodeFromParent(node);
                            break;
                        }
                    }
                }

                public void receiveStatus(LocalShare ls, ShareFolder share, int partNumber, long bytes) {
                    tps.get(ls.getShare().getHash()).receiveStatus(partNumber, bytes);
                    progressPanel.updateUI();
                }

                public void receiveDone(LocalShare ls) {
                    tps.get(ls.getShare().getHash()).done();
                }

                public void sendStatus(LocalShare ls, ShareFolder share, int partNumber, long bytes) {
                    System.out.println("Send status...");
                }

                public void sendDone(LocalShare ls) {
                    System.out.println("Send done");
                }

                public void shareStartInfo(LocalShare ls, ShareFolder[] parts) {
                    TransferPanel tp = new TransferPanel(ls, parts);
                    progressPanel.add(tp);
                    tps.put(ls.getShare().getHash(), tp);
                    progressPanel.updateUI();
                }
            }, randomPort);

            sfs.setShareFolder("Downloads/" + randomPort + "/");

            busyIconTimer.stop();
            statusAnimationLabel.setIcon(idleIcon);
            statusMessageLabel.setText("Connected to " +
                    sfs.getClient().getSocket().getInetAddress().getHostAddress());

        } catch (BindException ex) {
            statusMessageLabel.setText("Port already in use");
            boolean gotPort = false;
            while (!gotPort) {
                try {
                    String newPortString = JOptionPane.showInputDialog(
                            "Enter a new port, leave blank for random");
                    if (newPortString.length() == 0) {
                        connect(null);
                        gotPort = true;
                    } else {
                        gotPort = true;
                        int newPort = Integer.parseInt(newPortString);
                        connect(newPort);
                    }
                } catch (NumberFormatException nex) {
                    statusMessageLabel.setText("Not a number");
                }
            }
            Logger.getLogger(SFSUIView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SFSUIView.class.getName()).log(Level.SEVERE, null, ex);
            busyIconTimer.stop();
            statusMessageLabel.setText("Unable to connect to server");
        }
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

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = SFSUIApp.getApplication().getMainFrame();
            aboutBox = new SFSUIAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        SFSUIApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        progressSplitPane = new javax.swing.JSplitPane();
        listSplitPane = new javax.swing.JSplitPane();
        availableShareListPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        availableSharesTree = new javax.swing.JTree();
        privateShareListPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        infoArea = new javax.swing.JTextArea();
        progressPanel = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        addShareMenuItem = new javax.swing.JMenuItem();
        saveSharesMenuItem = new javax.swing.JMenuItem();
        loadSharesMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        errorMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusIconPanel = new javax.swing.JPanel();
        statusAnimationLabel = new javax.swing.JLabel();
        infoIconLabel = new javax.swing.JLabel();
        availableSharePopup = new javax.swing.JPopupMenu();
        availableShareDownloadMenuItem = new javax.swing.JMenuItem();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new java.awt.BorderLayout());

        progressSplitPane.setDividerLocation(400);
        progressSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        progressSplitPane.setName("progressSplitPane"); // NOI18N

        listSplitPane.setDividerLocation(300);
        listSplitPane.setName("listSplitPane"); // NOI18N

        availableShareListPanel.setName("availableShareListPanel"); // NOI18N
        availableShareListPanel.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Shares");
        availableSharesTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        availableSharesTree.setComponentPopupMenu(availableSharePopup);
        availableSharesTree.setName("availableSharesTree"); // NOI18N
        jScrollPane1.setViewportView(availableSharesTree);

        availableShareListPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        listSplitPane.setLeftComponent(availableShareListPanel);

        privateShareListPanel.setName("privateShareListPanel"); // NOI18N
        privateShareListPanel.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        infoArea.setColumns(20);
        infoArea.setRows(5);
        infoArea.setName("infoArea"); // NOI18N
        jScrollPane2.setViewportView(infoArea);

        privateShareListPanel.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        listSplitPane.setRightComponent(privateShareListPanel);

        progressSplitPane.setTopComponent(listSplitPane);

        progressPanel.setName("progressPanel"); // NOI18N
        progressPanel.setLayout(new java.awt.GridLayout(1, 0));
        progressSplitPane.setRightComponent(progressPanel);

        mainPanel.add(progressSplitPane, java.awt.BorderLayout.CENTER);

        menuBar.setName("menuBar"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(sfsui.SFSUIApp.class).getContext().getResourceMap(SFSUIView.class);
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(sfsui.SFSUIApp.class).getContext().getActionMap(SFSUIView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        addShareMenuItem.setAction(actionMap.get("createShare")); // NOI18N
        addShareMenuItem.setText(resourceMap.getString("addShareMenuItem.text")); // NOI18N
        addShareMenuItem.setName("addShareMenuItem"); // NOI18N
        fileMenu.add(addShareMenuItem);

        saveSharesMenuItem.setAction(actionMap.get("saveLoadShares")); // NOI18N
        saveSharesMenuItem.setText(resourceMap.getString("saveSharesMenuItem.text")); // NOI18N
        saveSharesMenuItem.setName("saveSharesMenuItem"); // NOI18N
        fileMenu.add(saveSharesMenuItem);

        loadSharesMenuItem.setAction(actionMap.get("loadLocalShares")); // NOI18N
        loadSharesMenuItem.setText(resourceMap.getString("loadSharesMenuItem.text")); // NOI18N
        loadSharesMenuItem.setName("loadSharesMenuItem"); // NOI18N
        fileMenu.add(loadSharesMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        errorMenuItem.setAction(actionMap.get("showErrorBox")); // NOI18N
        errorMenuItem.setText(resourceMap.getString("errorMenuItem.text")); // NOI18N
        errorMenuItem.setName("errorMenuItem"); // NOI18N
        helpMenu.add(errorMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N
        statusPanel.setPreferredSize(new java.awt.Dimension(814, 24));
        statusPanel.setLayout(new java.awt.BorderLayout());

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N
        statusPanel.add(statusPanelSeparator, java.awt.BorderLayout.NORTH);

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N
        statusPanel.add(statusMessageLabel, java.awt.BorderLayout.WEST);

        statusIconPanel.setName("statusIconPanel"); // NOI18N
        statusIconPanel.setLayout(new java.awt.BorderLayout());

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N
        statusIconPanel.add(statusAnimationLabel, java.awt.BorderLayout.WEST);

        infoIconLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        infoIconLabel.setName("infoIconLabel"); // NOI18N
        infoIconLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                infoIconLabelMouseClicked(evt);
            }
        });
        statusIconPanel.add(infoIconLabel, java.awt.BorderLayout.EAST);

        statusPanel.add(statusIconPanel, java.awt.BorderLayout.EAST);

        availableSharePopup.setName("availableSharePopup"); // NOI18N

        availableShareDownloadMenuItem.setAction(actionMap.get("downloadShare")); // NOI18N
        availableShareDownloadMenuItem.setText(resourceMap.getString("availableShareDownloadMenuItem.text")); // NOI18N
        availableShareDownloadMenuItem.setName("availableShareDownloadMenuItem"); // NOI18N
        availableSharePopup.add(availableShareDownloadMenuItem);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void infoIconLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_infoIconLabelMouseClicked
        showErrorBox();
        infoIconLabel.setIcon(null);
    }//GEN-LAST:event_infoIconLabelMouseClicked

    @Action
    public void createShare() {
        busyIconTimer.start();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.showOpenDialog(SFSUIApp.getApplication().getMainFrame());
        if (fileChooser.getSelectedFiles().length > 0) {
            String shareName = "";
            if (fileChooser.getSelectedFiles().length == 1) {
                shareName = JOptionPane.showInputDialog(null, "Name of share:", fileChooser.getSelectedFiles()[0].getName());
            } else {
                shareName = JOptionPane.showInputDialog(null, "Name of share:");
            }
            if (shareName != null && shareName.length() > 0) {
                System.out.println(shareName);
                Share share = ShareUtility.createShare(fileChooser.getSelectedFiles(), shareName);
                System.out.println(share.getShare());
                //  DefaultListModel model = (DefaultListModel) mySharesList.getModel();
                //model.addElement(share);
                sfs.getClient().sendObject(new CreateShareEvent(share));
                sfs.getLocalShares().put(share.getHash(), new LocalShare(fileChooser.getSelectedFiles()[0], share));
            }
        }
        busyIconTimer.start();
    }

    @Action
    public void downloadShare() {
        if (availableSharesTree.getSelectionPath() != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) availableSharesTree.getSelectionPath().getLastPathComponent();
            try {
                Share share = (Share) node.getUserObject();
                sfs.getClient().sendObject(new GetShareOwnersEvent(share));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(SFSUIApp.getApplication().getMainFrame(), "You did not select a share-node");
            }
        }
    }

    @Action
    public void showErrorBox() {
        SFSUIApp.getApplication().show(errorBox);
    }

    public SFSUIErrorBox getErrorBox() {
        return errorBox;
    }

    public void setInfoIcon(String icon) {
        infoIconLabel.setIcon(resourceMap.getIcon(icon));
    }

    @Action
    public void saveLoadShares() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.showOpenDialog(SFSUIApp.getApplication().getMainFrame());
        if (fileChooser.getSelectedFile() != null) {
            ObjectOutputStream out = null;
            try {
                out = new ObjectOutputStream(new FileOutputStream(fileChooser.getSelectedFile()));
                out.writeObject(sfs.getLocalShares());
            } catch (IOException ex) {
                Logger.getLogger(SFSUIView.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(SFSUIView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Action
    public void loadLocalShares() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.showOpenDialog(SFSUIApp.getApplication().getMainFrame());
        if (fileChooser.getSelectedFile() != null) {
            ObjectInputStream in = null;
            try {
                in = new ObjectInputStream(new FileInputStream(fileChooser.getSelectedFile()));
                sfs.setLocalShares((Map<Integer, LocalShare>) in.readObject());
                for (LocalShare ls : sfs.getLocalShares().values()) {
                    if (!sfs.getShares().contains(ls.getShare())) {
                        sfs.getClient().sendObject(new CreateShareEvent(ls.getShare()));
                    }
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SFSUIView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SFSUIView.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(SFSUIView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem addShareMenuItem;
    private javax.swing.JMenuItem availableShareDownloadMenuItem;
    private javax.swing.JPanel availableShareListPanel;
    private javax.swing.JPopupMenu availableSharePopup;
    private javax.swing.JTree availableSharesTree;
    private javax.swing.JMenuItem errorMenuItem;
    private javax.swing.JTextArea infoArea;
    private javax.swing.JLabel infoIconLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane listSplitPane;
    private javax.swing.JMenuItem loadSharesMenuItem;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JPanel privateShareListPanel;
    private javax.swing.JPanel progressPanel;
    private javax.swing.JSplitPane progressSplitPane;
    private javax.swing.JMenuItem saveSharesMenuItem;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JPanel statusIconPanel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
    private final Timer busyIconTimer;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
    ResourceMap resourceMap;
    private SFSClient sfs;
    private final Icon idleIcon;
    private Map<Integer, TransferPanel> tps;
    private SFSUIErrorBox errorBox;
}

/*
 * SFSUIApp.java
 */
package sfsui;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class SFSUIApp extends SingleFrameApplication {

    private SFSUIView view;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        view = new SFSUIView(this);
        addLoggers();
        show(view);
    }

    private void addLoggers() {
        try {
            Handler handler = new Handler() {

                @Override
                public void publish(LogRecord record) {
                    view.getErrorBox().log(record);
                    view.setInfoIcon("infoIconLabel.icon.application_error");
                }

                @Override
                public void flush() {
                }

                @Override
                public void close() throws SecurityException {
                }
            };
            FileHandler fileHandler = new FileHandler("SFS.log");
            Enumeration<String> logs = LogManager.getLogManager().getLoggerNames();
            while (logs.hasMoreElements()) {
                String log = logs.nextElement();
                System.out.println("Add logger: " + log);
                Logger.getLogger(log).addHandler(handler);
                Logger.getLogger(log).addHandler(fileHandler);
            }
        } catch (IOException ex) {
            Logger.getLogger(SFSUIApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(SFSUIApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of SFSUIApp
     */
    public static SFSUIApp getApplication() {
        return Application.getInstance(SFSUIApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            launch(SFSUIApp.class, args);
        } else {
            System.out.println("Starting server...");
        }
    }
}

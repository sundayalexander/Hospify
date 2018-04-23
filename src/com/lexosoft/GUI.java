/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lexosoft;

//Import Required classes
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;



/**
 *
 * @author Amowe Sunday Alexander
 */
public class GUI extends JFrame {
    //Variables/Properties Declaration
    private BufferedImage logo;
    private final Dimension size;
    private final JFrame notifier;
    private BufferedImage splashLoader;
    private BufferedImage frameLoader;
    private Container contentPane;
    private JLabel labelPane;
    private JLabel connectionStatus;
    private GridBagConstraints constriant;
    private EventHandler actionListener;
    private JPanel servicePanel;
    private JPanel footerPanel;
    private JPanel userPanel;
    private JTabbedPane tabbedPane;
    private Canvas canvas;

    //Method Declarations
    //Constructor
    public GUI(){
        //Main content of this frame.
        this.servicePanel = new JPanel();
        this.userPanel = new JPanel();
        this.connectionStatus = new JLabel("Hotspot not Started...", JLabel.LEFT);
        try {
            this.actionListener = new EventHandler();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.tabbedPane = new JTabbedPane();

        //Change The default feel and look.
        MediaTracker tracker = new MediaTracker(this);

        try {
            this.logo = ImageIO.read(GUI.class.getResource("/images/logo.png"));
            this.splashLoader = ImageIO.read(GUI.class.getResource("/images/ripple_loader.gif"));
            this.frameLoader = ImageIO.read(GUI.class.getResource("/images/radio_loader.gif"));
            tracker.addImage(logo, 0);
            tracker.addImage(splashLoader,1);

            //Set the look and feel while waiting for the tracker
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            tracker.waitForAll();
        } catch (IOException | InstantiationException | InterruptedException |
                IllegalAccessException | UnsupportedLookAndFeelException |
                ClassNotFoundException e) {
            e.printStackTrace();
        }

        this.size = Toolkit.getDefaultToolkit().getScreenSize();



        //This is the mini status pane
        //which displays when the close
        //button on the frame is clicked
        this.notifier = new JFrame();
        this.notifier.setSize(300,60);
        this.notifier.setLocation(this.getScreenSize().width-(this.notifier.getSize().width - 10),35);
        this.notifier.setUndecorated(true);
        this.notifier.setAlwaysOnTop(true);
        this.notifier.setIconImage(logo);
        //this.notifier.setOpacity(Float.valueOf(0.5f));
        this.notifier.setVisible(false);
        this.notifier.setState(3);
        this.canvas = new Notifier();
        this.canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GUI.this.hideFrame();
            }
        });
        this.notifier.add(this.canvas);

        //this.notifier.getContentPane().addActionListener(this.showFrame());

        //exit operation
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }

    //Initialize component
    public void splashScreen(){
        this.contentPane = this.getContentPane();
        this.setTitle("Hospify");

        //Initalizing menubar components
        this.setIconImage(this.logo);
        this.setSize(380, 600);
        this.setLocation((this.getScreenSize().width - this.getSize().width), (this.getScreenSize().height - this.getSize().height) / 2);
        this.setResizable(false);
        this.contentPane.setBackground(new Color(41,178,255));
        this.setVisible(true);

        //Set flash pane layout
        this.constriant = new GridBagConstraints();
        JLabel logo = new JLabel("Hospify");
        logo.setIconTextGap(10);
        this.contentPane.setLayout(new GridBagLayout());
        constriant.gridx = GridBagConstraints.REMAINDER;
        constriant.weighty = 2;
        constriant.gridy = 0;
        this.contentPane.add(logo, constriant);
        this.labelPane = new JLabel();
        constriant.gridy = 0;
        constriant.weighty = 0;

        this.contentPane.add(this.labelPane, constriant);
        logo.setFont(new Font("Arial Rounded MT Bold",Font.PLAIN,25));
        logo.setIcon(new ImageIcon(this.logo.getScaledInstance(50, 50, 0)));
        logo.setForeground(Color.WHITE);

        //Display loader image
        JLabel loader = new JLabel();
        constriant.gridy = 2;
        constriant.weighty = 1;
        this.contentPane.add(loader, constriant);
        loader.setIcon(new ImageIcon(this.splashLoader.getScaledInstance(100, 100, 0)));

        //Copyright
        JLabel copyright = new JLabel("Powered by: Lexosoft");
        constriant.gridy = GridBagConstraints.LAST_LINE_START;
        constriant.gridheight = 2;
        constriant.weighty = 0;
        copyright.setForeground(Color.WHITE);
        this.contentPane.add(copyright, constriant);



        //Sleep for some time
        try {
            Thread.sleep(900);
            this.contentPane.removeAll();
            this.showTabbedPane();
        } catch (InterruptedException ex) {
            this.labelPane.setText(ex.getMessage());
        }

    }

    //Show sub pannel and all its component
    private void showTabbedPane(){
        this.contentPane.setLayout(new GridBagLayout());
        this.footerPanel = new JPanel();

        userPanel.setSize(380, 300);
        JLabel title = new JLabel("Hospify", JLabel.LEFT);
        title.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 25));
        this.constriant.gridy = 0;
        this.constriant.insets = new Insets(8,13,13,10);
        this.constriant.fill = GridBagConstraints.HORIZONTAL;
        this.constriant.weightx = 0.5;
        this.constriant.ipady = 10;
        this.constriant.ipadx = 15;
        this.constriant.gridwidth = 3;
        this.constriant.gridheight = 2;
        title.setForeground(Color.WHITE);
        this.connectionStatus.setForeground(Color.WHITE);
        this.contentPane.add(title, this.constriant);
        this.constriant.insets = new Insets(2,0,2,0);
        this.constriant.gridwidth = GridBagConstraints.REMAINDER;

        //hide Button
        JButton hideBtn = new JButton("Hide");
        this.constriant.gridwidth = GridBagConstraints.REMAINDER;
        this.constriant.gridheight = 1;
        this.constriant.fill = GridBagConstraints.HORIZONTAL;
        this.constriant.ipady = 0;
        this.constriant.ipadx = 0;
        hideBtn.addActionListener(this.hideFrame());
        hideBtn.setForeground(new Color(41,178,255));
        hideBtn.setFont(new Font("Arial",Font.PLAIN,12));
        this.constriant.gridx = GridBagConstraints.LINE_END;
        this.constriant.gridy = 0;
        this.contentPane.add(hideBtn, this.constriant);

        //Content for service panel
        this.constriant.gridwidth = GridBagConstraints.REMAINDER;
        this.constriant.gridy = 2;
        this.constriant.gridx = 0;
        this.constriant.weighty = 0.9;
        this.constriant.fill = GridBagConstraints.BOTH;
        this.constriant.gridheight = 4;
        this.contentPane.add(tabbedPane, this.constriant);
        tabbedPane.addTab("Services", this.servicePanel);
        tabbedPane.addTab("Users", this.userPanel);
        tabbedPane.setFont(new Font("Arial", Font.BOLD,11));


        //Footer Panel
        this.constriant.insets = new Insets(2,30,2,30);
        this.constriant.fill = GridBagConstraints.BOTH;
        this.constriant.gridx = 0;
        this.constriant.ipady = 2;
        this.constriant.gridy = 6;
        this.constriant.weighty = 0.03;
        this.constriant.gridwidth = GridBagConstraints.REMAINDER;
        this.constriant.gridheight = 1;
        this.contentPane.add(this.connectionStatus, this.constriant);

    }


    //Get Screen Size
    public Dimension getScreenSize(){
        return this.size;
    }

    //Hide Application frame and show status pane
    private ActionListener hideFrame(){
        return e -> {
            GUI.this.setVisible(false);
            GUI.this.notifier.setVisible(true);

            //set frame location.
            GUI.this.setLocation((GUI.this.getScreenSize().width - GUI.this.getSize().width),
                    (GUI.this.getScreenSize().height - GUI.this.getSize().height) / 2);
        };
    }

    //Open or show main application frame and or status pane fram
    private ActionListener showFrame(){
        return e -> {
            GUI.this.setVisible(true);
            GUI.this.notifier.setVisible(false);

            //set frame location.
            GUI.this.setLocation((GUI.this.getScreenSize().width - GUI.this.getSize().width),
                    (GUI.this.getScreenSize().height - GUI.this.getSize().height) / 2);
        };

    }

    //Get Window Container or content pane
    public JPanel getServicePanel(){
        return this.servicePanel;
    }

    //Get notifier pane
    public JFrame getNotifier(){
        return this.notifier;
    }

    //Get Window Container or content pane
    public JPanel getUserPanel(){
        return this.userPanel;
    }

    //Get Connection status label
    public JLabel getStatusLabel(){
        return this.connectionStatus;
    }

    //This method returns the Tabbed Pane
    public JTabbedPane getTappedPane(){
        return this.tabbedPane;
    }

    /**
     * This method returns the splash fram
     */

}


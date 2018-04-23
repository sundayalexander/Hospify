package com.lexosoft;

import java.awt.*;

/**
 * This is a Notifier class
 *
 * @author Amowe Sunday Alexander
 * @version
 * @date 4/19/2018 @1:03 PM
 */
public class Notifier extends Canvas {
    //Class properties goes here


    /**
     * Constructor
     */
    public Notifier() {
        //Constructor logic goes here
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setFont(new Font("Arial Rounded MT Bold",Font.PLAIN,45));
        g.setColor(Color.RED);
        g.drawString("Notification",0,0);
    }
}

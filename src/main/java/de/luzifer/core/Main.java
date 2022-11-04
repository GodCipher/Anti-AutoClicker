package de.luzifer.core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Anti-AutoClicker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(null);

        JLabel label = new JLabel("hey, you opened me wrongly :( here a picture of a cat instead!");
        label.setBounds(0, 0, 500, 50);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        frame.add(label);

        JLabel image = new JLabel();
        image.setIcon(getIcon());
        image.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URL("https://www.youtube.com/watch?v=dQw4w9WgXcQ").toURI());
                } catch (IOException | URISyntaxException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        image.setBounds(0, 50, 500, 450);
        image.setHorizontalAlignment(SwingConstants.CENTER);
        image.setVerticalAlignment(SwingConstants.CENTER);
        frame.add(image);

        frame.setVisible(true);
    }

    private static Icon getIcon() {

        URL url = Main.class.getResource("/images/cat" + ThreadLocalRandom.current().nextInt(1, 6) + ".jpg");
        if (url == null)
            throw new IllegalStateException("Could not find image");

        return new ImageIcon(url);
    }
}

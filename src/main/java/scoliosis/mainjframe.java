package scoliosis;

import scoliosis.Libs.KeyLib;
import scoliosis.Libs.MouseLib;

import javax.swing.*;

import static scoliosis.Libs.ScreenLib.screenSize;

public class mainjframe extends JFrame {
    public static JFrame mainframe = new JFrame();


    public static void DrawDisplay() {
        Display game = new Display();

        mainframe = new JFrame("Mushroom Client Launcher");
        mainframe.setUndecorated(true);
        mainframe.add(game);

        mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainframe.setSize((int) screenSize.getWidth() / 2, (int) screenSize.getHeight() / 2);
        mainframe.setLocationRelativeTo(null);
        mainframe.setVisible(true);

        mainframe.addKeyListener(new KeyLib());
        mainframe.addMouseWheelListener(new MouseLib());

        game.startGame();
    }
}

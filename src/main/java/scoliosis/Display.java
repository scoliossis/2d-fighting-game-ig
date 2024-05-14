package scoliosis;

import scoliosis.Libs.ScreenLib;

import java.awt.*;
import java.awt.image.BufferStrategy;

import static scoliosis.mainjframe.mainframe;

public class Display extends Canvas {

    public void startGame() {

        while (true) {
            ScreenLib.width = mainframe.getWidth();
            ScreenLib.height = mainframe.getHeight();

            BufferStrategy bs = this.getBufferStrategy();

            if (bs == null) createBufferStrategy(3);

            Game.game(bs);
        }
    }
}
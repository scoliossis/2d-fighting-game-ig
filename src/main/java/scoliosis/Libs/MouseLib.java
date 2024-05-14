package scoliosis.Libs;

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import static scoliosis.mainjframe.mainframe;

public class MouseLib implements AWTEventListener, MouseWheelListener {

    public static boolean leftclicked = false;
    public static boolean middleclicked = false;
    public static boolean rightclicked = false;
    public static boolean otherclicked = false;

    public static boolean mouseLibLoaded = false;
    public static int scrolledAmount = 0;

    public static boolean isMouseOverCoords(int x, int y, int width, int height) {
        int mx = MouseInfo.getPointerInfo().getLocation().x - mainframe.getX();
        int my = MouseInfo.getPointerInfo().getLocation().y - mainframe.getY();

        x = (int) (x / 480f * (float) (mainframe.getWidth()));
        y = (int) (y / 270f * (float) (mainframe.getHeight()));
        width = (int) (width / 480f * (float) (mainframe.getWidth()));
        height = (int) (height / 270f * (float) (mainframe.getHeight()));

        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }


    public static int mousexcoord(float divnum) {
        return (int) (((MouseInfo.getPointerInfo().getLocation().x - mainframe.getX() - (8f / (mainframe.getWidth() / 480f))) / divnum) / (mainframe.getWidth() / 480f));
    }

    ;

    public static int realmousexcoord() {
        return (int) (((MouseInfo.getPointerInfo().getLocation().x - mainframe.getX() - (8f / (mainframe.getWidth() / 480f)))) / (mainframe.getWidth() / 480f));
    }

    ;

    public static int ActualRealMouseXcoord() {
        return MouseInfo.getPointerInfo().getLocation().x;
    }

    public static int ActualRealMouseYcoord() {
        return MouseInfo.getPointerInfo().getLocation().y;
    }

    ;

    public static int mouseycoord(float divnum) {
        return (int) (((MouseInfo.getPointerInfo().getLocation().y - mainframe.getY() - (8f / (mainframe.getHeight() / 270f))) / divnum) / (mainframe.getHeight() / 270f));
    }

    ;

    public static int realmouseycoord() {
        return (int) (((MouseInfo.getPointerInfo().getLocation().y - mainframe.getY() - (8f / (mainframe.getHeight() / 270f)))) / (mainframe.getHeight() / 270f));
    }

    ;

    public void eventDispatched(AWTEvent event) {
        mainframe.requestFocus();

        if (!mouseLibLoaded) {
            System.out.println("mouselib loaded ;3");
            mouseLibLoaded = true;
        }

        if (event.toString().contains("button=")) {
            int mousenumber = Integer.parseInt(event.toString().split("button=")[1].split(",")[0]);

            if (event.toString().contains("MOUSE_PRESSED")) {
                if (mousenumber == 1) leftclicked = true;
                else if (mousenumber == 2) middleclicked = true;
                else if (mousenumber == 3) rightclicked = true;
                else otherclicked = true;
            }

            if (event.toString().contains("MOUSE_RELEASED")) {
                if (mousenumber == 1) leftclicked = false;
                else if (mousenumber == 2) middleclicked = false;
                else if (mousenumber == 3) rightclicked = false;
                else otherclicked = false;

            }
        }

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        scrolledAmount += e.getWheelRotation();
    }
}

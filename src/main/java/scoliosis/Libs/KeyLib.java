package scoliosis.Libs;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class KeyLib extends KeyAdapter {

    static ArrayList<Integer> keysdown = new ArrayList<>();
    static ArrayList<Integer> newKeys = new ArrayList<>();
    static ArrayList newKeysForKeyboard = new ArrayList<>();

    public static boolean isKeyDown(int keycode) {
        try {
            for (int key : keysdown) {
                if (key == keycode) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }

        return false;
    }

    public static boolean keyPressed(int keycode) {
        for (int i = 0; i < keysdown.size(); i++) {
            if (keysdown.get(i) == keycode) {
                if (!newKeys.contains(keycode)) {
                    newKeys.add(keycode);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean anyKeyDown() {
        if (!keysdown.isEmpty()) return true;
        return false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!keysdown.contains(e.getKeyCode()) && !newKeys.contains(e.getKeyCode())) {
            keysdown.add(e.getKeyCode());
            newKeysForKeyboard.add(e.getKeyChar());
            newKeysForKeyboard.add(e.getKeyCode());
            newKeysForKeyboard.add(0l);
            newKeysForKeyboard.add(false);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysdown.remove((Integer) e.getKeyCode());
        newKeys.remove((Integer) e.getKeyCode());
    }
}
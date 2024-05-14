package scoliosis.Libs;

import scoliosis.Game;
import scoliosis.mainjframe;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import static scoliosis.mainjframe.mainframe;

public class TextBox {
    static int startSelectX;
    static int startSelectY;
    static int scrolledStart;
    // shift, ctrl, f1 keys, end, caps, accent keys
    static int[] badKeyCodes = {16, 17, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 20, 35, 128, 129, 524};
    public String fileText;
    public int boxX;
    public int boxY;
    public int boxW;
    public int boxH;
    public boolean scrolling;
    public boolean allowNewLines;
    public boolean drawBackground;
    public boolean passwordBox;

    public int currentKey = 0;
    public int lineKey = 0;
    public int lineNumber = 0;
    public boolean forceShow = true;
    public int selectedTextStart = 0;
    public int selectedTextEnd = 0;
    public String textBoxString = "";
    public ArrayList<String> undoMarkers = new ArrayList<>();
    public ArrayList<Integer> undoMarkersKeys = new ArrayList<>();
    public ArrayList<String> redoMarkers = new ArrayList<>();
    public ArrayList<Integer> redoMarkersKeys = new ArrayList<>();
    public boolean showSearchBar = false;
    boolean lastLeftClick;
    boolean boxFocused;
    public int scrolledAmount;
    boolean finderSelect;
    int lastMouseScroll;
    int holdDelay1 = 500;
    int holdDelay2 = 50;


    boolean allowTyping;
    public Color textColor;
    Font font;

    public TextBox(int boxX, int boxY, int boxW, int boxH, String fileText, boolean scrolling, boolean allowNewLines, boolean drawBackground, boolean passwordBox, boolean allowTyping, Color textColor, Font font) {
        this.boxX = boxX;
        this.boxY = boxY;
        this.boxW = boxW;
        this.boxH = boxH;

        this.fileText = fileText;

        this.scrolling = scrolling;
        this.allowNewLines = allowNewLines;

        this.drawBackground = drawBackground;

        this.passwordBox = passwordBox;

        this.allowTyping = allowTyping;

        this.textColor = textColor;
        this.font = font;
    }

    public static int averageCharWidth(String line) {
        // gets mean average
        return getStringWidth(line) / line.length();
    }

    static boolean notBadKey(int keyCode) {
        for (int i = 0; i < badKeyCodes.length; i++) {
            if (badKeyCodes[i] == keyCode) return false;
        }

        return true;
    }

    public static int getLineAtChar(String string, int CharacterNum) {
        return string.substring(0, CharacterNum).replaceAll("\n", "\n ").split("\n").length - 1;
    }

    public static int getAboveChar(String text, int CharNow, int Char2) {
        int lengthToGetRidOff = 0;

        String[] splitLines = text.split("\n");
        int lineNum = getLineAtChar(text, Char2);

        // gets exact char above instead of one inline width-wise
        if (lineNum > 0) {
            int lengthOfPrevLine = splitLines[lineNum - 1].length();

            if (CharNow > lengthOfPrevLine) lengthToGetRidOff = CharNow + 1;
            else lengthToGetRidOff = (lengthOfPrevLine - CharNow) + CharNow + 1;

        }

        return lengthToGetRidOff;
    }

    public static int getBelowChar(String text, int CharNow, int Char2) {
        int lengthToAddOn = 0;

        String[] splitLines = text.split("\n");
        int lineNum = getLineAtChar(text, Char2);

        if (lineNum < splitLines.length - 1) {
            int lengthOfNowLine = splitLines[lineNum].length();
            int lengthOfNextLine = splitLines[lineNum + 1].length();

            if (CharNow > lengthOfNextLine) lengthToAddOn = (lengthOfNowLine - CharNow) + lengthOfNextLine + 1;
            else lengthToAddOn = (lengthOfNowLine - CharNow) + CharNow + 1;

        }

        return lengthToAddOn;
    }

    static Font f = new Font("Sans Sheriff", Font.PLAIN, 16);


    public static int getStringWidth(String string) {
        return (int) (Game.g.getFontMetrics(f).stringWidth(string) / (mainjframe.mainframe.getWidth() / (480f)));
    }

    public static int lengthBeforeLine(String string, int line) {
        String FixedNewLines = " " + string.replaceAll("\n", "\n ");
        String[] splitLines = FixedNewLines.split("\n");
        int loops = 0;
        int length = 0;
        while (loops < line) {
            length += splitLines[loops].length();
            loops++;
        }
        return length;
    }

    public static int amountOfLines(String s) {
        return s.replaceAll("\n", "\n ").split("\n").length;
    }

    public void draw() {
        Game.g.setFont(RenderLib.getFont(font));
        if (MouseLib.leftclicked && !lastLeftClick) {
            if (MouseLib.isMouseOverCoords(boxX, boxY, boxW, boxH) && MouseLib.leftclicked && !lastLeftClick)
                boxFocused = true;
            else {
                finderSelect = false;
                boxFocused = false;
            }
        }

        if (scrolling && lastMouseScroll != MouseLib.scrolledAmount)
            scrolledAmount += (MouseLib.scrolledAmount - lastMouseScroll) * 10;

        if (drawBackground) {
            RenderLib.drawRoundRect(boxX, boxY, boxW, boxH, 10, 10, new Color(100, 100, 100), Game.g);
        }
        if (boxFocused && allowTyping) fileText = doKeyBoard(fileText);

        String drawnFileText = fileText;
        if (passwordBox) {
            drawnFileText = "";

            for (int i = 0; i < fileText.length(); i++) {
                // likely bugs out somewhere but legit doesnt matter because u cant put new lines in password box
                if (fileText.charAt(i) == '\n') drawnFileText += "\n";
                else drawnFileText += "*";
            }
        }


        String FixedNewLines = " " + drawnFileText.replaceAll("\n", "\n ");


        String[] splitLines = FixedNewLines.split("\n");


        // convert mouse cord to text number
        // probably the thing I was most worried about optimising, but I think it went surprisingly well.
        if (MouseLib.leftclicked && !finderSelect) {
            resetSelected();

            startSelectX = MouseLib.realmousexcoord();
            startSelectY = MouseLib.realmouseycoord();

            scrolledStart = scrolledAmount;

            finderSelect = true;

            int lineY = getYcordAtMouse(startSelectY);

            if (lineY < splitLines.length && lineY > -1) {
                String fixTex = splitLines[lineY].substring(1);
                if (fixTex.isEmpty()) fixTex = " ";

                int newKeyCoord = ((startSelectX - boxX) / averageCharWidth(fixTex));

                newKeyCoord = Math.min(Math.max(0, newKeyCoord), fixTex.length());


                if (fixTex.equals(" ")) newKeyCoord--;


                // more precise
                while (newKeyCoord > 0 && getStringWidth(splitLines[lineY].substring(0, newKeyCoord)) >= startSelectX - boxX) {
                    newKeyCoord--;
                }

                int prevLinesLength = lengthBeforeLine(drawnFileText, lineY);
                newKeyCoord += prevLinesLength;

                currentKey = newKeyCoord;

                currentKey = Math.max(Math.min(currentKey, drawnFileText.length()), 0);


                lineNumber = lineY;
            }

        }

        // check when mouse moved
        if (MouseLib.leftclicked && finderSelect && boxFocused) {

            int lineY = getYcordAtMouse(MouseLib.mouseycoord(1));
            int lineYOG = getYcordAtMouse(startSelectY - (scrolledAmount - scrolledStart));

            lineYOG = Math.max(Math.min(lineYOG, splitLines.length - 1), 0);
            lineY = Math.min(lineY, splitLines.length - 1);

            if (lineY > -1) {

                String fixTex = splitLines[lineY].substring(1);

                if (fixTex.isEmpty()) fixTex = " ";

                // use while loop on this start to make it perfect on lines with long characters
                // too lazy to do rn
                int guessEnd = ((MouseLib.realmousexcoord() - boxX) / averageCharWidth(fixTex));
                guessEnd = Math.min(Math.max(0, guessEnd), fixTex.length());
                if (fixTex.equals(" ")) guessEnd--;

                String fixTex2 = splitLines[lineYOG].substring(1);

                int guessStart = ((startSelectX - boxX) / averageCharWidth(splitLines[lineYOG]));
                guessStart = Math.min(Math.max(0, guessStart), fixTex2.length());
                if (fixTex2.equals(" ")) guessStart--;

                // makes it ALMOST perfectly precise :)
                // also the further along the text is the further off the guess is, please help me fix this, it's unoptimized on long lines
                // actually in general, if your reading this, please help with optimizations on any of my stuff :) t.me/escamas1337
                //if (guessStart > splitLines[lineYOG].length()) guessStart = splitLines[lineYOG].length();
                while (guessStart > 0 && getStringWidth(splitLines[lineYOG].substring(0, guessStart)) >= startSelectX - boxX) {
                    guessStart--;
                }


                //if (guessEnd > splitLines[lineY].length()) guessEnd = splitLines[lineY].length();
                while (guessEnd > 0 && getStringWidth(splitLines[lineY].substring(0, guessEnd)) >= MouseLib.realmousexcoord() - boxX) {
                    guessEnd--;
                }

                // add on the length of all past non selected lines
                guessStart += lengthBeforeLine(drawnFileText, lineYOG);
                guessEnd += lengthBeforeLine(drawnFileText, lineY);


                if (guessStart > guessEnd) {
                    int temp = guessStart;
                    guessStart = guessEnd;
                    guessEnd = temp;
                }

                selectedTextStart = guessStart;
                selectedTextEnd = guessEnd;
            }


            if (MouseLib.mouseycoord(1) > 250) {
                addMouseScrolled(-(250 - MouseLib.mouseycoord(1)));
            }
            if (MouseLib.mouseycoord(1) < 10) {
                addMouseScrolled(-(10 - MouseLib.mouseycoord(1)));
            }
        }

        selectedTextStart = Math.min(Math.max(0, selectedTextStart), drawnFileText.length());

        selectedTextEnd = Math.min(Math.max(0, selectedTextEnd), drawnFileText.length());

        if (!lastLeftClick && MouseLib.leftclicked) {
            finderSelect = false;
            selectedTextEnd = selectedTextStart;
        }
        // code to draw the highlighted text
        // horribly optimised :)
        /*
        for (int i = selectedTextStart; i < selectedTextEnd; i++) {
            int textLine = getLineAtChar(drawnFileText, i);
            int xCoord = getCharacterX(drawnFileText, i, g);
            int yCoord = getYcord(textLine);
            if (yCoord > 0 && yCoord < mainframe.getHeight() && xCoord > 0 && xCoord < mainframe.getWidth()) {

                int width = getStringWidth(g, font, drawnFileText.substring(i, i + 1)) + 1;
                int height = 10; // might do something w this later (g.getFontMetrics(font).getHeight() prob)

                int blueNum = (int) ((((Math.cos((xCoord + pAdd - (yCoord)) / 30f) + 1f) / 2) * 100) + 150);

                RenderLib.drawRect(xCoord, yCoord, width, height, new Color(255 - (blueNum / 2), 100, blueNum), g);
            }
        }
         */

        currentKey = Math.max(Math.min(currentKey, drawnFileText.length()), 0);

        int lineChar = getLineAtChar(drawnFileText, selectedTextStart);
        int lineChar2 = getLineAtChar(drawnFileText, selectedTextEnd);

        int minY = (scrolledAmount - boxY) / 10;
        int maxY = ((mainframe.getHeight()) / 10) + minY;

        minY = Math.max(0, minY);
        maxY = Math.min(splitLines.length, maxY);

        for (int i = minY; i < maxY; i++) {
            int height = 10; // might do something w this later (g.getFontMetrics(font).getHeight() prob)

            int blueNum = (int) ((((Math.cos((Game.pAdd - (i * 6)) / 30f) + 1f) / 2) * 100) + 150);


            if (i == lineChar && i < lineChar2) // highlight full highest line
                RenderLib.drawRect(getCharacterX(drawnFileText, selectedTextStart), getYcord(i) + 2, getStringWidth(splitLines[i].substring(1)) + 1 - getCharacterX(drawnFileText, selectedTextStart) + 5 + boxX, height, new Color(255 - (blueNum / 2), 100, blueNum), Game.g);
            else if (i == lineChar) // highlight part of highest line
                RenderLib.drawRect(getCharacterX(drawnFileText, selectedTextStart), getYcord(i) + 2, getCharacterX(drawnFileText, selectedTextEnd) - getCharacterX(drawnFileText, selectedTextStart), height, new Color(255 - (blueNum / 2), 100, blueNum), Game.g);

            else if (i > lineChar && i < lineChar2) // just highlights all inbetween 2 points, yay + fast
                RenderLib.drawRect(5 + boxX, getYcord(i) + 2, getStringWidth(splitLines[i].substring(1)) + 1, height, new Color(255 - (blueNum / 2), 100, blueNum), Game.g);

            if (i == lineChar2 && lineChar2 > lineChar) // highlight lowest line
                RenderLib.drawRect(5 + boxX, getYcord(i) + 2, getCharacterX(drawnFileText, selectedTextEnd) - boxX, height, new Color(255 - (blueNum / 2), 100, blueNum), Game.g);

            RenderLib.drawString(Game.g, splitLines[i].substring(1), 5 + boxX, getYcord(i) + 10, font.getSize(), font.getName(), font.getStyle(), textColor);

            if (i == lineNumber && (System.currentTimeMillis() % 1000 > 300 || anyKeyDown())) {
                if (boxFocused)
                    RenderLib.drawRect(getCharacterX(drawnFileText, currentKey), getYcord(lineNumber) + 2, 1, 10, new Color(255, 255, 255, 200), Game.g);
            }
        }

        lastLeftClick = MouseLib.leftclicked;
        lastMouseScroll = MouseLib.scrolledAmount;
    }

    public int getYcord(int line) {
        return (10 + (line * 10)) - scrolledAmount - 10 + boxY;
    }

    int getYcordAtMouse(int mouseY) {
        int firstLineInt = getYcord(0);
        int secondLineInt = getYcord(1);

        int multiplier = secondLineInt - firstLineInt;
        int startOffset = firstLineInt / multiplier;

        int mouseLine = (mouseY / multiplier) - startOffset;

        return mouseLine;
    }

    public void addMouseScrolled(int amountScrolled) {
        scrolledAmount += amountScrolled;
        scrolledAmount = Math.max(0, scrolledAmount);

        if (totalLineHeight(fileText) > 140) scrolledAmount = Math.min(scrolledAmount, totalLineHeight(fileText) - 140);
        else scrolledAmount = Math.min(scrolledAmount, 0);
    }

    public boolean isKeyDown(int keycode) {
        try {
            for (int key : KeyLib.keysdown) {
                if (key == keycode) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }

        return false;
    }

    public boolean anyKeyDown() {
        if (!KeyLib.keysdown.isEmpty()) return true;
        return false;
    }

    public String doKeyBoard(String keyboardAlr) {
        try {

            textBoxString = keyboardAlr;

            if (!KeyLib.newKeysForKeyboard.isEmpty()) {

                currentKey = Math.max(0, Math.min(keyboardAlr.length(), currentKey));

                lineNumber = getLineAtChar(keyboardAlr, currentKey);

                lineKey = currentKey;
                for (int i = 0; i < lineNumber; i++) {
                    String fakerkebd = " " + keyboardAlr.replaceAll("\n", "\n ");
                    lineKey = fakerkebd.substring(0, currentKey).split("\n")[fakerkebd.substring(0, currentKey).split("\n").length - 1].length();
                }

                forceShow = false;

                for (int i = 0; i < KeyLib.newKeysForKeyboard.size(); i += 4) {
                    if (notBadKey((Integer) KeyLib.newKeysForKeyboard.get(i + 1))) {
                        if (!KeyLib.isKeyDown((Integer) KeyLib.newKeysForKeyboard.get(i + 1))) {
                            KeyLib.newKeysForKeyboard.remove(i);
                            KeyLib.newKeysForKeyboard.remove(i);
                            KeyLib.newKeysForKeyboard.remove(i);
                            KeyLib.newKeysForKeyboard.remove(i);
                            continue;
                        }

                        boolean gogogo = false;

                        if ((long) KeyLib.newKeysForKeyboard.get(i + 2) != 0L) {
                            if ((Boolean) KeyLib.newKeysForKeyboard.get(i + 3) && System.currentTimeMillis() - (long) KeyLib.newKeysForKeyboard.get(i + 2) > holdDelay2) {
                                gogogo = true;
                            }

                            if (System.currentTimeMillis() - (long) KeyLib.newKeysForKeyboard.get(i + 2) > holdDelay1) {
                                addUndoMarker(keyboardAlr);

                                KeyLib.newKeysForKeyboard.set(i + 3, true);
                                gogogo = true;
                            }
                        }

                        if ((long) KeyLib.newKeysForKeyboard.get(i + 2) == 0l || gogogo) {
                            KeyLib.newKeysForKeyboard.set(i + 2, System.currentTimeMillis());
                            if ((int) KeyLib.newKeysForKeyboard.get(i + 1) == 27) {
                                continue;
                            }
                            // backspace
                            else if ((int) KeyLib.newKeysForKeyboard.get(i + 1) == 8) {
                                if (currentKey > 0) {

                                    addUndoMarker(keyboardAlr);

                                    if (selectedTextStart != selectedTextEnd) {
                                        keyboardAlr = keyboardAlr.substring(0, selectedTextStart) + keyboardAlr.substring(selectedTextEnd);
                                        currentKey = selectedTextStart;

                                    } else if (isKeyDown(17)) {

                                        int prevSpace = getPrevCharNumber(keyboardAlr);
                                        keyboardAlr = keyboardAlr.substring(0, prevSpace) + keyboardAlr.substring(currentKey);
                                        currentKey = prevSpace;

                                    } else {
                                        keyboardAlr = keyboardAlr.substring(0, currentKey - 1) + keyboardAlr.substring(currentKey);
                                        currentKey = Math.max(0, currentKey - 1);
                                    }
                                }
                            }

                            // arrow keys
                            else if ((int) KeyLib.newKeysForKeyboard.get(i + 1) == 37) {

                                if (!isKeyDown(17)) {
                                    if (0 > currentKey - 1) {
                                        //currentKey = keyboardAlr.length();
                                        currentKey = 0;
                                    } else currentKey--;
                                } else {
                                    currentKey = getPrevCharNumber(keyboardAlr);
                                }

                                if (isKeyDown(16)) {
                                    if (currentKey < selectedTextStart) selectedTextStart = currentKey;
                                    else selectedTextEnd = currentKey;
                                }

                            } else if ((int) KeyLib.newKeysForKeyboard.get(i + 1) == 39) {

                                if (!isKeyDown(17)) {
                                    if (keyboardAlr.length() < currentKey + 1) {
                                        //currentKey = 0;
                                        currentKey = keyboardAlr.length();
                                    } else currentKey++;
                                } else {
                                    currentKey = getNextCharNumber(keyboardAlr);
                                }

                                if (isKeyDown(16)) {
                                    if (currentKey > selectedTextEnd) selectedTextEnd = currentKey;
                                    else selectedTextStart = currentKey;
                                }
                            }

                            // up and down arrow keys
                            else if ((int) KeyLib.newKeysForKeyboard.get(i + 1) == 38) {
                                currentKey -= getAboveChar(keyboardAlr, lineKey, currentKey);
                            } else if ((int) KeyLib.newKeysForKeyboard.get(i + 1) == 40) {
                                currentKey += getBelowChar(keyboardAlr, lineKey, currentKey);
                            }

                            // enter
                            else if ((int) KeyLib.newKeysForKeyboard.get(i + 1) == 10) {
                                if (allowNewLines) {
                                    addUndoMarker(keyboardAlr);

                                    if (selectedTextStart != selectedTextEnd) {
                                        keyboardAlr = keyboardAlr.substring(0, selectedTextStart) + keyboardAlr.substring(selectedTextEnd);
                                        currentKey = selectedTextStart;
                                    }

                                    keyboardAlr = keyboardAlr.substring(0, currentKey) + "\n" + keyboardAlr.substring(currentKey);
                                    currentKey++;
                                }
                            }


                            // ctrl keys :)
                            else if (isKeyDown(17)) {
                                // ctrl + a
                                if ((int) KeyLib.newKeysForKeyboard.get(i + 1) == 65) {
                                    selectedTextStart = 0;
                                    selectedTextEnd = keyboardAlr.length();
                                }

                                // ctrl + c (most importent for coders along with ctrl + v)
                                else if ((int) KeyLib.newKeysForKeyboard.get(i + 1) == 67) {
                                    StringSelection selectedText = new StringSelection(keyboardAlr.substring(selectedTextStart, selectedTextEnd));
                                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selectedText, selectedText);
                                }

                                // ctrl + v
                                else if ((int) KeyLib.newKeysForKeyboard.get(i + 1) == 86) {
                                    try {
                                        String clipboardText = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                                        addUndoMarker(keyboardAlr);


                                        keyboardAlr = keyboardAlr.substring(0, currentKey) + clipboardText + keyboardAlr.substring(currentKey);
                                        currentKey += clipboardText.length();

                                        resetSelected();
                                    } catch (UnsupportedFlavorException | IOException | HeadlessException e) {
                                        System.out.println("image in clipboard, not string >:(");
                                    }

                                }

                                // ctrl + x
                                else if ((int) KeyLib.newKeysForKeyboard.get(i + 1) == 88) {
                                    addUndoMarker(keyboardAlr);

                                    StringSelection selectedText = new StringSelection(keyboardAlr.substring(selectedTextStart, selectedTextEnd));
                                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selectedText, selectedText);

                                    keyboardAlr = keyboardAlr.substring(0, selectedTextStart) + keyboardAlr.substring(selectedTextEnd);
                                    currentKey = selectedTextStart;

                                    resetSelected();
                                }

                                // ctrl z
                                else if ((int) KeyLib.newKeysForKeyboard.get(i + 1) == 90) {
                                    if (!undoMarkers.isEmpty()) {
                                        resetSelected();

                                        addRedoMarker(keyboardAlr);

                                        currentKey = undoMarkersKeys.get(undoMarkersKeys.size() - 1);
                                        keyboardAlr = undoMarkers.get(undoMarkers.size() - 1);

                                        removeUndoMarker();
                                    }
                                }

                                // ctrl y
                                else if ((int) KeyLib.newKeysForKeyboard.get(i + 1) == 89) {
                                    if (!redoMarkers.isEmpty()) {
                                        resetSelected();

                                        currentKey = redoMarkersKeys.get(redoMarkersKeys.size() - 1);
                                        keyboardAlr = redoMarkers.get(redoMarkers.size() - 1);

                                        removeRedoMarker();
                                    }
                                }

                                // ctrl f
                                else if ((int) KeyLib.newKeysForKeyboard.get(i + 1) == 70) {
                                    showSearchBar = true;
                                }
                            }

                            // normal text
                            else {
                                if ((int) KeyLib.newKeysForKeyboard.get(i + 1) == 32) {
                                    addUndoMarker(keyboardAlr);
                                }


                                if (selectedTextStart != selectedTextEnd) {
                                    keyboardAlr = keyboardAlr.substring(0, selectedTextStart) + keyboardAlr.substring(selectedTextEnd);
                                    currentKey = selectedTextStart;
                                }

                                if (!isKeyDown(17)) {
                                    //System.out.println(KeyLib.newKeysForKeyboard.get(i + 1));
                                    keyboardAlr = keyboardAlr.substring(0, currentKey) + KeyLib.newKeysForKeyboard.get(i) + keyboardAlr.substring(currentKey);
                                    currentKey++;
                                }
                            }
                        }

                        if (!isKeyDown(16) && !isKeyDown(17)) {
                            resetSelected();
                            redoMarkers.clear();
                        }

                        while (getCharacterY(keyboardAlr, currentKey) >= 260) {
                            MouseLib.scrolledAmount += 10;
                        }
                        while (getCharacterY(keyboardAlr, currentKey) <= lineBaseAddition()) {
                            MouseLib.scrolledAmount -= 10;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return keyboardAlr;
    }

    int getPrevCharNumber(String string) {
        int newNum = currentKey;

        if (string.length() >= currentKey) {
            if (string.substring(0, currentKey).contains(" ") || string.substring(0, currentKey).contains("\n")) {
                if (string.charAt(newNum - 1) == ' ' || string.charAt(newNum - 1) == '\n') {
                    while (string.charAt(newNum - 1) == ' ' || string.charAt(newNum - 1) == '\n') {
                        newNum--;
                    }
                }

                if (string.substring(0, newNum).contains(" ") || string.substring(0, newNum).contains("\n")) {
                    while (string.charAt(newNum - 1) != ' ' && string.charAt(newNum - 1) != '\n') {
                        newNum--;
                    }
                } else return 0;
            } else return 0;
        }

        return newNum;
    }

    int getNextCharNumber(String string) {
        int newNum = currentKey;

        if (string.length() >= currentKey) {
            if (string.substring(currentKey).contains(" ") || string.substring(currentKey).contains("\n")) {
                if (string.charAt(newNum) == ' ' || string.charAt(newNum) == '\n') {
                    while (string.charAt(newNum) == ' ' || string.charAt(newNum) == '\n') {
                        newNum++;
                    }
                }

                if (string.substring(newNum).contains(" ") || string.substring(newNum).contains("\n")) {
                    while (string.charAt(newNum) != ' ' && string.charAt(newNum) != '\n') {
                        newNum++;
                    }
                } else return string.length();
            } else return string.length();
        }

        return newNum;
    }

    public void resetSelected() {
        selectedTextStart = currentKey;
        selectedTextEnd = currentKey;

        startSelectX = MouseLib.realmousexcoord();
        startSelectY = MouseLib.realmouseycoord();
        scrolledStart = MouseLib.scrolledAmount;
    }

    public int getCharacterX(String text, int CharacterNum) {
        String textLengthFix = text.substring(0, CharacterNum).replaceAll("\n", "\n ");
        String[] splitLines = textLengthFix.split("\n");
        int lineNum = getLineAtChar(text, CharacterNum);
        String lineText = splitLines[lineNum];

        int lineWidth = 5 + getStringWidth(lineText);
        if (lineNum > 0) {
            lineWidth -= getStringWidth(lineText.substring(0, 1));
        }

        return lineWidth + boxX;
    }

    public int getCharacterY(String text, int CharacterNum) {
        int textLine = getLineAtChar(text, CharacterNum);

        return getYcord(textLine);
    }

    public void addUndoMarker(String keyb) {
        undoMarkers.add(keyb);
        undoMarkersKeys.add(currentKey);
    }

    public void removeUndoMarker() {
        undoMarkers.remove(undoMarkers.size() - 1);
        undoMarkersKeys.remove(undoMarkersKeys.size() - 1);
    }

    public void addRedoMarker(String keyb) {
        redoMarkers.add(keyb);
        redoMarkersKeys.add(currentKey);
    }

    public void removeRedoMarker() {
        redoMarkers.remove(redoMarkers.size() - 1);
        redoMarkersKeys.remove(redoMarkersKeys.size() - 1);
    }

    public int totalLineHeight(String s) {
        int lines = amountOfLines(s);
        int highestMouseLine = (lineBaseAddition() + (lines * lineMultiplier()));

        return highestMouseLine;
    }

    public int lineMultiplier() {
        int firstLineInt = getYcord(0);
        int secondLineInt = getYcord(1);

        return secondLineInt - firstLineInt;
    }

    public int lineBaseAddition() {
        return getYcord(0) + MouseLib.scrolledAmount;
    }
}

package scoliosis;

import scoliosis.GameLibs.Entity;
import scoliosis.GameLibs.MoveLib;
import scoliosis.Libs.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

import static scoliosis.Libs.ScreenLib.screenSize;
import static scoliosis.mainjframe.mainframe;

public class Game {

    public static int prevWidth = (int) (screenSize.getWidth() / 2);
    public static int prevHeight = (int) (screenSize.getHeight() / 2);
    public static int pAdd = 0;
    public static Graphics g = null;
    static boolean lastLeftClick = false;
    static Color[] circleColors = new Color[]{new Color(200, 90, 0), new Color(40, 200, 40), new Color(200, 40, 40)};
    static boolean resizing = false;
    static int prevLocationX = 0;
    static int prevLocationY = 0;
    static int startingMouseX = 0;
    static int startingMouseY = 0;
    static int startingPosX = 0;
    static int startingPosY = 0;
    static boolean lockedOnDrag = false;
    static boolean fullScreen = false;
    static boolean splitScreen = false;
    static int ClickCircleMillis = 1000;
    static int DragCircleMillis = 400;
    static int UnClickCircleMillis = 1000;
    static int movingMouseLengthInMillis = 200;
    static long lastPminis = 0;
    static ArrayList<Long> clickCircleCoords = new ArrayList<>();
    static ArrayList<Long> DragCircleCoords = new ArrayList<>();
    static ArrayList<Long> UNclickCircleCoords = new ArrayList<>();
    static ArrayList<Long> movingCurserCoords = new ArrayList<>();
    static boolean lockedOnResizeSide = false;
    static boolean lockedOnResizeUp = false;
    static int p = 0;

    static boolean finderSelect = false;

    static boolean first = true;

    static long loadingStart = 0;
    static int prevScrolled = 0;

    static String popUp = "";
    static long popUpTime = 0;

    static boolean lastMiddleClick = false;

    static boolean coolTrail = false;


    //public static Byte[] coordinates = new Byte[]{};

    public static coordinates[] hitboxes = new coordinates[]{};

    public static Entity playerEntity = new Entity(100, 30, 0,0,30,40,1, 3, 1.3f, 1);
    public static Entity NPCEntity = new Entity(300, 30, 0,0,30,40,2, 3, 1.3f, 2);

    static Entity[] ents = new Entity[] {playerEntity, NPCEntity};

    public record Camera(int x, int y, float zoom) {

    }

    static boolean firstTickLeftClick = false;
    static boolean firstTickRightClick = false;


    public static Camera camera = new Camera(0,100,1);

    public static void game(BufferStrategy bs) {
        if (System.currentTimeMillis() > lastPminis + 200) {
            lastPminis = System.currentTimeMillis();
            pAdd -= 10;
        }

        if (bs != null) {
            g = bs.getDrawGraphics();


            RenderLib.drawImage(0, 0, 480, 270, RenderLib.getBufferedImage("skybox"));
            RenderLib.drawCenteredImage(240 + camera.x, 135 + camera.y, (int) (480f * camera.zoom), (int) (270 * camera.zoom), RenderLib.getBufferedImage("stagedrawn"));

            playerEntity.inputs = new MoveLib.Inputs(
                    KeyLib.keyPressed(KeyEvent.VK_W), KeyLib.isKeyDown(KeyEvent.VK_W),
                    KeyLib.keyPressed(KeyEvent.VK_S), KeyLib.isKeyDown(KeyEvent.VK_S),
                    KeyLib.keyPressed(KeyEvent.VK_A), KeyLib.isKeyDown(KeyEvent.VK_A),
                    KeyLib.keyPressed(KeyEvent.VK_D), KeyLib.isKeyDown(KeyEvent.VK_D),
                    KeyLib.keyPressed(KeyEvent.VK_SPACE), KeyLib.isKeyDown(KeyEvent.VK_SPACE),
                    KeyLib.keyPressed(KeyEvent.VK_SHIFT), KeyLib.isKeyDown(KeyEvent.VK_SHIFT),
                    !firstTickLeftClick && MouseLib.leftclicked, MouseLib.leftclicked,
                    !firstTickRightClick && MouseLib.rightclicked, MouseLib.rightclicked

            );

            for (Entity ent : ents) {
                MoveLib.MoveLibChecks(ent, ent.inputs);

                // fix idol animation
                //RenderLib.getBufferedImage("mushroom\\idol\\idol-" + (int) (((System.currentTimeMillis() * 0.01) % 4)+1))
                RenderLib.drawImage(ent.x + camera.x + (ent.renderDirection == 1 ? 0 : ent.width), ent.y - ent.height + camera.y, ent.width*ent.renderDirection, ent.height, RenderLib.getBufferedImage(Entity.getAnimationStep(ent)));
                if (System.currentTimeMillis() - ent.dodgeTime < MoveLib.maxDodgeTime) {
                    long dodgeTime = System.currentTimeMillis() - ent.dodgeTime;
                    int dodgeFrameTick = Math.min(Math.max((int) (MoveLib.shieldFrames - (MoveLib.maxDodgeTime - dodgeTime) / (MoveLib.maxDodgeTime / MoveLib.shieldFrames)), 0), MoveLib.shieldFrames);

                    BufferedImage shieldTick = RenderLib.getBufferedImage("animations\\shield\\shieldTick" + dodgeFrameTick);
                    RenderLib.drawImage(ent.x - 10 + camera.x, ent.y - ent.height + camera.y - 10, ent.width + 20, ent.height + 20, shieldTick);
                }
            }

            MoveLib.hurtBoxes.removeIf(hurtBox -> System.currentTimeMillis() - hurtBox.timeStart() > hurtBox.timeAroundFor());

            for (Game.HurtBox hurtBox : MoveLib.hurtBoxes) {
                if (MoveLib.ValidHurtBox(hurtBox)) {
                    Color color = new Color(232, 86, 164, 128);
                    if (hurtBox.entity.entityID == playerEntity.entityID) color = new Color(113, 238, 162, 128);
                    RenderLib.drawRect((int) ((hurtBox.x()*hurtBox.direction) + camera.x + hurtBox.entity.x+((hurtBox.entity.width/2f)-(hurtBox.w/2f))), (int) (hurtBox.y() + camera.y + hurtBox.entity.y), hurtBox.w(), hurtBox.h(), color, Game.g);
                }
            }


            firstTickLeftClick = MouseLib.leftclicked;
            firstTickRightClick = MouseLib.rightclicked;



















            // other code from my first ever java thing
            if ((MouseLib.realmousexcoord() < 10 || MouseLib.realmousexcoord() < 470) && MouseLib.realmouseycoord() > 20 && MouseLib.realmouseycoord() < 265 && MouseLib.leftclicked && !lastLeftClick) {
                lockedOnResizeSide = true;
            } else if (MouseLib.realmousexcoord() > 100 && MouseLib.realmousexcoord() < 400 && MouseLib.realmouseycoord() > 265) {
                lockedOnResizeUp = true;
            }

            if (!mainframe.isVisible()) mainframe.setVisible(true);

            // buttons in top right
            for (int i = 0; i < 3; i++) {
                RenderLib.drawCircle(457 + (i * 8), 1, 5, 5, circleColors[i], g);

                if (MouseLib.isMouseOverCoords(453 + (i * 8), 1, 12, 12) && MouseLib.leftclicked) {
                    // make screen go away
                    if (i == 0) {
                        mainframe.toBack();
                        mainframe.setVisible(false);

                        MouseLib.leftclicked = false;

                    }

                    // fullscreen
                    else if (i == 1) {
                        if (!resizing) {
                            resizing = true;

                            if (fullScreen) releaseFullScreen();
                            else goFullScreen();
                        }
                    }

                    // close!!
                    else {
                        System.exit(0);
                    }
                }
            }


            // chroma outline
            if (!fullScreen) {
                p = pAdd;

                if (!lockedOnDrag) {
                    for (int i = 0; i < ScreenLib.width; i++) {
                        p++;

                        int blueNum = (int) ((((Math.cos(p / 20f) + 1f) / 2) * 100) + 150);
                        g.setColor(new Color(255 - (blueNum / 2), 20, blueNum));
                        g.drawLine(i, 0, i + 1, 0);
                    }

                    for (int i = 0; i < ScreenLib.height; i++) {
                        p++;

                        int blueNum = (int) ((((Math.cos(p / 20f) + 1f) / 2) * 100) + 150);
                        g.setColor(new Color(255 - (blueNum / 2), 20, blueNum));
                        g.drawLine(ScreenLib.width - 1, i, ScreenLib.width - 1, i + 1);
                    }

                    for (int i = 0; i < ScreenLib.width; i++) {
                        p++;

                        int blueNum = (int) ((((Math.cos(p / 20f) + 1f) / 2) * 100) + 150);
                        g.setColor(new Color(255 - (blueNum / 2), 20, blueNum));
                        g.drawLine(ScreenLib.width - i, ScreenLib.height - 1, ScreenLib.width - i + 1, ScreenLib.height - 1);
                    }

                    for (int i = 0; i < ScreenLib.height; i++) {
                        p++;

                        int blueNum = (int) ((((Math.cos(p / 20f) + 1f) / 2) * 100) + 150);
                        g.setColor(new Color(255 - (blueNum / 2), 20, blueNum));
                        g.drawLine(0, ScreenLib.height - i, 0, ScreenLib.height - i + 1);
                    }
                } else {
                    for (int i = 0; i < ScreenLib.width; i++) {
                        p++;

                        int blueNum = (int) ((((Math.cos(p / 20f) + 1f) / 2) * 100) + 150);
                        g.setColor(new Color(blueNum, 20, 255 - (blueNum / 2)));
                        g.drawLine(i, 0, i + 1, 0);
                    }

                    for (int i = 0; i < ScreenLib.height; i++) {
                        p++;

                        int blueNum = (int) ((((Math.cos(p / 20f) + 1f) / 2) * 100) + 150);
                        g.setColor(new Color(blueNum, 20, 255 - (blueNum / 2)));
                        g.drawLine(ScreenLib.width - 1, i, ScreenLib.width - 1, i + 1);
                    }

                    for (int i = 0; i < ScreenLib.width; i++) {
                        p++;

                        int blueNum = (int) ((((Math.cos(p / 20f) + 1f) / 2) * 100) + 150);
                        g.setColor(new Color(blueNum, 20, 255 - (blueNum / 2)));
                        g.drawLine(ScreenLib.width - i, ScreenLib.height - 1, ScreenLib.width - i + 1, ScreenLib.height - 1);
                    }

                    for (int i = 0; i < ScreenLib.height; i++) {
                        p++;

                        int blueNum = (int) ((((Math.cos(p / 20f) + 1f) / 2) * 100) + 150);
                        g.setColor(new Color(blueNum, 20, 255 - (blueNum / 2)));
                        g.drawLine(0, ScreenLib.height - i, 0, ScreenLib.height - i + 1);
                    }
                }
            }


            if (MouseLib.isMouseOverCoords(0, 0, 480, 8) && MouseLib.leftclicked && !lockedOnDrag) {
                startingMouseX = MouseLib.ActualRealMouseXcoord();
                startingMouseY = MouseLib.ActualRealMouseYcoord();

                startingPosX = mainframe.getX();
                startingPosY = mainframe.getY();

                lockedOnDrag = true;
            }


            if (MouseLib.middleclicked && !lastMiddleClick) {
                coolTrail = !coolTrail;
                doPopUp("cool trail " + (coolTrail ? "activated!" : "deactivated :("));
            }

            if (!lockedOnDrag) {
                if (coolTrail) {
                    if (MouseLib.leftclicked && !lastLeftClick) {
                        clickCircleCoords.add((long) MouseLib.mousexcoord(1));
                        clickCircleCoords.add((long) MouseLib.mouseycoord(1));
                        clickCircleCoords.add(System.currentTimeMillis());
                    } else if (MouseLib.leftclicked) {
                        DragCircleCoords.add((long) MouseLib.mousexcoord(1));
                        DragCircleCoords.add((long) MouseLib.mouseycoord(1));
                        DragCircleCoords.add(System.currentTimeMillis());
                    } else if (lastLeftClick) {
                        UNclickCircleCoords.add((long) MouseLib.mousexcoord(1));
                        UNclickCircleCoords.add((long) MouseLib.mouseycoord(1));
                        UNclickCircleCoords.add(System.currentTimeMillis());
                    } else {
                        movingCurserCoords.add((long) MouseLib.mousexcoord(1));
                        movingCurserCoords.add((long) MouseLib.mouseycoord(1));
                        movingCurserCoords.add(System.currentTimeMillis());
                    }


                    // awesome sauce
                    if (!DragCircleCoords.isEmpty()) CoolerFollowMouseCode(DragCircleCoords, DragCircleMillis);
                    //if (!clickCircleCoords.isEmpty()) followMouseCode(clickCircleCoords, ClickCircleMillis, new Color(255, 142, 255, 255));
                    //if (!UNclickCircleCoords.isEmpty()) followMouseCode(UNclickCircleCoords, UnClickCircleMillis, new Color(252, 149, 149, 255));

                    if (!movingCurserCoords.isEmpty()) {
                        for (int i = 0; i < movingCurserCoords.size() - 3; i += 3) {

                            double time = System.currentTimeMillis() - movingCurserCoords.get(i + 2);
                            int circleX = Math.toIntExact(movingCurserCoords.get(i)) + 2;
                            int nextCircleX = Math.toIntExact(movingCurserCoords.get(i + 3)) + 2;

                            int circleY = Math.toIntExact(movingCurserCoords.get(i + 1)) + 2;
                            int nextCircleY = Math.toIntExact(movingCurserCoords.get(i + 4)) + 2;

                            int blueNum = (int) ((((Math.cos((i + pAdd) / 20f) + 1f) / 2) * 100) + 150);

                            //if ((nextCircleX != MouseLib.mousexcoord(1)+2 && nextCircleY != MouseLib.mouseycoord(1)+2))
                            RenderLib.drawLine(circleX, circleY, nextCircleX, nextCircleY, new Color(255 - (blueNum / 2), 0, blueNum, 200), g);

                            if (time > movingMouseLengthInMillis) {
                                movingCurserCoords.remove(0);
                                movingCurserCoords.remove(0);
                                movingCurserCoords.remove(0);
                            }
                        }
                    }
                }
            } else {
                int movedMouseAmountX = MouseLib.ActualRealMouseXcoord() - startingMouseX;
                int newLocationX = startingPosX + movedMouseAmountX;

                int movedMouseAmountY = MouseLib.ActualRealMouseYcoord() - startingMouseY;
                int newLocationY = startingPosY + movedMouseAmountY;

                if (!fullScreen) {

                    if (System.currentTimeMillis() > lastPminis + 100) {
                        lastPminis = System.currentTimeMillis();
                        pAdd += 10;
                    }

                    p = pAdd;

                    mainframe.setLocation(newLocationX, newLocationY);
                }

                if (newLocationY > 5 && fullScreen) {
                    releaseFullScreen();

                    startingMouseX = MouseLib.ActualRealMouseXcoord();
                    startingMouseY = MouseLib.ActualRealMouseYcoord();

                    startingPosX = mainframe.getX();
                    startingPosY = mainframe.getY();

                    mainframe.setLocation(MouseLib.ActualRealMouseXcoord(), MouseLib.ActualRealMouseYcoord());
                } else if (MouseLib.ActualRealMouseYcoord() < 5 && !fullScreen) {
                    RenderLib.drawRect(0, 0, 480, 270, new Color(100, 100, 255, 100), g);

                    if (!MouseLib.leftclicked && lastLeftClick) {
                        goFullScreen();
                    }
                } else if (MouseLib.ActualRealMouseXcoord() < 20) {
                    RenderLib.drawRect(0, 0, 480, 270, new Color(100, 100, 255, 100), g);

                    if (!MouseLib.leftclicked && lastLeftClick) {
                        lockedOnDrag = false;
                        splitScreen = true;
                        prevWidth = mainframe.getWidth();
                        prevHeight = mainframe.getHeight();

                        mainframe.setSize((int) screenSize.getWidth() / 2, (int) screenSize.getHeight());
                        mainframe.setLocation(0, 0);
                    }
                } else if (MouseLib.ActualRealMouseXcoord() > screenSize.getWidth() - 20) {
                    RenderLib.drawRect(0, 0, 480, 270, new Color(100, 100, 255, 100), g);

                    if (!MouseLib.leftclicked) {
                        lockedOnDrag = false;

                        splitScreen = true;

                        prevWidth = mainframe.getWidth();
                        prevHeight = mainframe.getHeight();

                        mainframe.setSize((int) screenSize.getWidth() / 2, (int) screenSize.getHeight());
                        mainframe.setLocation((int) (screenSize.getWidth() / 2), 0);
                    }
                } else if (splitScreen && (movedMouseAmountX > 5 || movedMouseAmountX < -5)) {
                    mainframe.setSize(prevWidth, prevHeight);
                }
            }



            /*
            String cursorImage = "cursor";

            if (!fullScreen) {
                if (MouseLib.realmousexcoord() < 10 && MouseLib.realmouseycoord() > 20) {
                    cursorImage = "resizeCursor";
                }
                if (MouseLib.realmousexcoord() > 470 && MouseLib.realmouseycoord() > 20) {
                    cursorImage = "resizeCursor";
                }

                if (MouseLib.realmousexcoord() > 100 && MouseLib.realmousexcoord() < 400 && MouseLib.realmouseycoord() > 265) {
                    cursorImage = "resizeCursor2";
                }
            }
             */


            g.dispose();
            bs.show();

            if (first) {
                first = false;
                loadShit();
                System.out.println("loaded resources");
                //ForkJoinPool.commonPool().execute(Game::loadShit);
            }
        }

        if (!MouseLib.leftclicked) {
            lockedOnResizeSide = false;
            lockedOnResizeUp = false;
            resizing = false;
            lockedOnDrag = false;
            finderSelect = false;

        }
        lastLeftClick = MouseLib.leftclicked;
        lastMiddleClick = MouseLib.middleclicked;
        prevScrolled = MouseLib.scrolledAmount;

    }

    public record coordinates(int x, int y) {

    }

    public record HurtBox(float x, float y, int w, int h, int direction, float kbX, float kbY, boolean setKB, float power, long timeStart, long timeAroundFor, long hurtStunTime, long invulnerableTime, Entity entity) {

    }

    public static void loadShit() {
        Main.loadresources();
        RenderLib.loadImages();
        loadingStart = System.currentTimeMillis();
        mainframe.setIconImage(RenderLib.getBufferedImage("icon"));


        BufferedImage stageHitboxes = RenderLib.getBufferedImage("stage");
        ArrayList<coordinates> tempArray = new ArrayList<>();

        for (int x = 0; x < stageHitboxes.getWidth(); x++) {
            for (int y = 0; y < stageHitboxes.getHeight(); y++) {
                if (stageHitboxes.getRGB(x,y) != new Color(255,255,255).getRGB()) {
                    tempArray.add(new coordinates(x,y));
                }
            }
        }

        hitboxes = new coordinates[tempArray.size()];

        for (int i = 0; i < hitboxes.length; i++) {
            hitboxes[i] = tempArray.get(i);
        }

        //System.out.println(Arrays.toString(hitboxes));
    }

    public static void goFullScreen() {
        splitScreen = false;
        prevWidth = mainframe.getWidth();
        prevHeight = mainframe.getHeight();
        prevLocationX = mainframe.getX();
        prevLocationY = mainframe.getY();

        fullScreen = true;

        mainframe.setSize((int) screenSize.getWidth(), (int) screenSize.getHeight());
        mainframe.setLocation(0, 0);

        lockedOnDrag = false;
    }

    public static void releaseFullScreen() {
        mainframe.setSize(prevWidth, prevHeight);
        mainframe.setLocation(prevLocationX, prevLocationY);

        fullScreen = false;
    }

    static void CoolerFollowMouseCode(ArrayList<Long> array, int delayBeforeReset) {
        for (int i = 0; i < array.size(); i += 3) {

            double time = System.currentTimeMillis() - array.get(i + 2);
            int circleRadius = (int) (Math.sin(time / (delayBeforeReset / 4d)) * 5);
            int circleX = Math.toIntExact(array.get(i)) - circleRadius / 2 + 2;
            int circleY = Math.toIntExact(array.get(i + 1)) - circleRadius / 2 + 2;

            int blueNum = (int) ((((Math.cos((i + pAdd) / 20f) + 1f) / 2) * 100) + 100);

            RenderLib.drawCircle(circleX, circleY, circleRadius, circleRadius, new Color(255 - (blueNum / 4), 20, blueNum + 50, 200), g);

            if (time > delayBeforeReset) {
                array.remove(0);
                array.remove(0);
                array.remove(0);
            }
        }
    }

    public static File getFile(String filePath) {
        return new File(Path.of(filePath).toUri());
    }

    public static String getPath(String filePath) {
        Path path1 = Path.of(filePath);
        String path = path1.toUri().getPath();
        if (path.startsWith("/")) path = path.substring(1);
        if (new File(path1.toUri()).isDirectory()) return path.substring(0, path.length() - 1);
        return path;
    }


    public static void doPopUp(String popup) {
        popUp = popup;
        popUpTime = System.currentTimeMillis();
    }

}

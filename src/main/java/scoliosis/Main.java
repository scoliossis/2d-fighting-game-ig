package scoliosis;

import scoliosis.Libs.MouseLib;
import scoliosis.Libs.SoundLib;
import scoliosis.Options.Config;
import scoliosis.Options.Configs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

import static scoliosis.mainjframe.DrawDisplay;


public class Main {

    /*
    todo: make todo list
    chat this is a really old jframe base idrk what going on anywhere, hf
     */


    // no settings added yet!!
    public static ArrayList settings = Config.collect(Configs.class);
    public static String scoliosis = System.getenv("APPDATA") + "\\scoliosis";
    public static String baseName = System.getenv("APPDATA") + "\\scoliosis\\fightingThing";
    public static String resourcesFile = baseName + "\\resources";

    public static void main(String[] args) {
        DrawDisplay();
    }

    public static void loadresources() {
        Toolkit.getDefaultToolkit().addAWTEventListener(new MouseLib(), AWTEvent.MOUSE_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK);

        //if (!Game.getFile(scoliosis).isDirectory()) Game.getFile(scoliosis).mkdirs();
        //if (!Game.getFile(baseName).isDirectory()) Game.getFile(baseName).mkdirs();
        if (!Game.getFile(resourcesFile).isDirectory()) Game.getFile(resourcesFile).mkdirs();


        // copy files outside of resources file
        try {

            copyFileOut("files.txt");


            String[] files = Files.readAllLines(Paths.get(resourcesFile + "\\files.txt")).toString().replace("]", "").split(",");

            for (String file : files) {
                copyFileOut(file);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // check if resources directory is there (it always is but just in case)
        if (Files.isDirectory(Paths.get(resourcesFile))) {

            // get time at start or loading shit
            double starttime = System.currentTimeMillis();

            // for every file in the resources folder
            for (File file : Objects.requireNonNull(Paths.get(resourcesFile).toFile().listFiles())) {
                if (file.isDirectory()) {
                    loadDirectory(file);
                }

                // .wav loader is very nice!
                // goes from taking 323ms to start playing file to 3ms on first play!
                // actually big dif no way!!!
                if (file.toString().endsWith(".wav")) {
                    try {
                        SoundLib.playSound(file.getName());
                        System.out.println("loaded " + file.getName());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // png loader is sorta useless, saves ~ 10 milliseconds on the first draw of each image
                // when i tested on a 95.5KB image it went from 41 millis to 31 millis to do first draw
                else if (file.toString().endsWith(".png")) {
                    try {

                        // opens file once, so it will open faster next time (barely saves any time)
                        ImageIO.read(file);
                        System.out.println("loaded " + file.getName());
                    } catch (IOException e) {

                    }
                }
            }
            System.out.println("time took to load resources: " + (System.currentTimeMillis() - starttime) / 1000 + " seconds");
        } else {
            System.out.println("cant find resources file??????");
        }
    }


    public static void loadDirectory(File directory) {
        for (File file : Objects.requireNonNull(Paths.get(directory.toURI()).toFile().listFiles())) {
            if (file.isDirectory()) {
                loadDirectory(file);
            }

            // .wav loader is very nice!
            // goes from taking 323ms to start playing file to 3ms on first play!
            // actually big dif no way!!!
            if (file.toString().endsWith(".wav")) {
                try {
                    SoundLib.playSound(file.getName());
                    System.out.println("loaded " + file.getName());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // png loader is sorta useless, saves ~ 10 milliseconds on the first draw of each image
            // when i tested on a 95.5KB image it went from 41 millis to 31 millis to do first draw
            else if (file.toString().endsWith(".png")) {
                try {

                    // opens file once, so it will open faster next time (barely saves any time)
                    ImageIO.read(file);
                    System.out.println("loaded " + file.getName());
                } catch (IOException e) {

                }
            }
        }
    }


    public static void copyFileOut(String filename) throws IOException {
        ClassLoader classLoader = Main.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(filename);

        if (Objects.equals(filename, "files.txt") && Files.exists(Paths.get(resourcesFile + "\\" + filename)))
            Files.delete(Paths.get(resourcesFile + "\\" + filename));

        if (!Files.exists(Paths.get(resourcesFile + "\\" + filename)) && inputStream != null) {
            String filePath = filename.replace(filename.split("\\\\")[filename.split("\\\\").length - 1], "");
            Game.getFile(resourcesFile + "\\" + filePath).mkdirs();
            Files.copy(inputStream, Paths.get(resourcesFile + "\\" + filename));
        }
    }

}
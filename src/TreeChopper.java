import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;

/*
    Assumes UI scaling is at 100%
    Assumes Axe is slot 0
    Assumes smart cursor is not toggled on
    Assumes smart cursor is bound to control
    Assumes autoselect is bound to TAB
    Assumes resolution is 1920x1080
    Assumes you have grappling hook (only for better stopping)
    Assumes grappling hook is bound to E
    Assumes no hermes boots
    Assumes jump is bound to SPACE

    Learn threading to do an event notifier instead of continuous testing!
*/

public class TreeChopper {

    final static private int FEET_X = 960;
    final static private int FEET_Y = 555;
    final static private int AXE_X = 433;
    final static private int AXE_Y = 25;
    final static private int TRUNK_OUTLINE_X = 953;
    final static private int TRUNK_OUTLINE_Y = 518;
    final static private int BLOCK_SIZE = 16;
    private static final int THRESHOLD = 35;
    private static final int GRAPPLE_DELAY = 200;

    static private int AUTOSELECT;
    static private int SMART_CURSOR;
    static private int LEFT;
    static private int RIGHT;
    static private int JUMP;
    static private int GRAPPLE;
    private static boolean currentlyHooked;

    private static boolean hasHook;
    private static boolean hasBoots;

    static private Robot bot;
    static private boolean activated = false;
    static private boolean started;
    static private GlobalKeyboardHook keyboardHook;

    public static void initiateBot(boolean boots, boolean grapple) throws AWTException {
        // Only one is allowed
        if (!activated) {
            keyboardHook = new GlobalKeyboardHook(true);
            AUTOSELECT = KeyEvent.VK_TAB;
            SMART_CURSOR = KeyEvent.VK_CONTROL;
            LEFT = KeyEvent.VK_A;
            RIGHT = KeyEvent.VK_D;
            JUMP = KeyEvent.VK_SPACE;
            GRAPPLE = KeyEvent.VK_E;
            hasHook = grapple;
            hasBoots = boots;
            activated = true;
            started = false;
            bot = new Robot();
            initializeHook();
            startChopper();
        }
    }

    
    private static void initializeHook() {
        keyboardHook.addKeyListener(new GlobalKeyAdapter() {

            public void keyPressed(GlobalKeyEvent event) {
				if (event.getVirtualKeyCode() == GlobalKeyEvent.VK_UP) {
					started = true;
				} else if (event.getVirtualKeyCode() == GlobalKeyEvent.VK_DOWN) {
                    activated = false;
                    killBot();
                }
			}
        });
    }


    // Main method
    private static void startChopper() {
        while(!started) {

        }
        initializingKeystrokes();
        mouseToFeet();
        while (started) {
            bot.keyPress(RIGHT);
            while (!axeSelected()) {
                // Will continue walking until axe lights up
            }
            correctPosition();
            chopUntilMined();
            repositionBeforeAdvancing();
        }
        
    }

    // Corrects for how the character slides upon stopping
    private static void correctPosition() {
        bot.keyRelease(RIGHT);
        if (hasHook) {
            grappleSlightLeft();
            if (atRoot()) {
                // Need to walk right one tile, this would put us above the trunk
                ungrapple();
                grappleOneTileRight();
            } 
        } else {
            final int NO_HOOK_WALK_CORRECTION = 550;
            holdKey(LEFT ,NO_HOOK_WALK_CORRECTION);
        }   
    
    }


    private static boolean atRoot() {
        final int[] TREE_OUTLINE = {58, 48, 42};
        final int[] FAT_TREE = {120, 85, 60};
        int[] currentRGB = getColorArray(bot.getPixelColor(TRUNK_OUTLINE_X, TRUNK_OUTLINE_Y));
        boolean atNormalTree = colorWithinThreshold(TREE_OUTLINE, currentRGB, THRESHOLD);
        boolean atFatTree = colorWithinThreshold(FAT_TREE, currentRGB, THRESHOLD);
        return !(atNormalTree || atFatTree);
    }

    private static void grappleOneTileRight() {
        bot.mouseMove(FEET_X + BLOCK_SIZE, FEET_Y);
        tapKey(GRAPPLE);
        currentlyHooked = true;
        mouseToFeet();
        bot.delay(GRAPPLE_DELAY); // Needed to give bot time to travel 
    }


    // Moves mouse half a tile to the left and shoots hook
    private static void grappleSlightLeft() {
        bot.mouseMove(FEET_X - BLOCK_SIZE / 2, FEET_Y);
        tapKey(GRAPPLE);
        currentlyHooked = true;
        mouseToFeet();
        bot.delay(GRAPPLE_DELAY); // Needed to give bot time to travel 
    }


    // Turns on autoselect and smart cursor
    private static void initializingKeystrokes() {
        bot.keyPress(AUTOSELECT);
        tapKey(SMART_CURSOR);
    }

    // Moves mouse to the character's feet
    private static void mouseToFeet() {
        bot.mouseMove(FEET_X, FEET_Y);
    }

    // Retrieves the pixel to determine if the axe has been autoselected
    private static boolean axeSelected() {
        final int[] ACTIVATED_HOTBAR = {255, 253, 76};
        int[] currentRGB = getColorArray(bot.getPixelColor(AXE_X, AXE_Y));
        return colorWithinThreshold(ACTIVATED_HOTBAR, currentRGB, THRESHOLD);
    }

    private static int[] getColorArray(Color pixel) {
        int[] result = new int[3];
        result[0] = pixel.getRed();
        result[1] = pixel.getGreen();
        result[2] = pixel.getBlue();
        return result;
    }

    private static boolean colorWithinThreshold(int[] target, int[] current, int threshold) {
        for (int i = 0; i < 3; i++) {
            if (!(current[i] <= (target[i] + threshold) && current[i] >= (target[i] - threshold))) {
                return false;
            }
        }
        return true;
    } 

    // Holds left click in short intervals until axe is no longer selected
    private static void chopUntilMined() {
        final int MS_DELAY = 50; // Need be at least ~30
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        bot.delay(MS_DELAY);
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        bot.delay(MS_DELAY);
        while (axeSelected()){
            bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            bot.delay(MS_DELAY);
            bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            bot.delay(MS_DELAY);
        }
    }

    // Backs up a bit, giving wood time to fall and correcting over- and undershoots
    private static void repositionBeforeAdvancing() {
        if (hasHook) {
            ungrapple();
        }
        final int MS_WALKING = 500;
        holdKey(LEFT, MS_WALKING);
    }

    private static void ungrapple() {
        tapKey(JUMP);
        currentlyHooked = false;
    }

    // Presses a key for what is intended to be an instantaneous amount of time
    private static void tapKey(int keyMask) {
        final int DELAY = 30;
        holdKey(keyMask, DELAY);
    }

    // Presses and holds a key for the amount of time specified, in milliseconds
    private static void holdKey (int keyMask, int time) {
        bot.keyPress(keyMask);
        bot.delay(time);
        bot.keyRelease(keyMask);
    }

    // Releases all keys that could have been held and kills
    public static void killBot() {
        if (currentlyHooked) {
            ungrapple();
        } else {
            bot.keyRelease(JUMP);
        }
        bot.keyRelease(AUTOSELECT);
        tapKey(SMART_CURSOR);
        bot.keyRelease(LEFT);
        bot.keyRelease(RIGHT);
        bot.keyRelease(GRAPPLE);
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        activated = false;
        keyboardHook.shutdownHook();
        System.exit(0);
    }


}

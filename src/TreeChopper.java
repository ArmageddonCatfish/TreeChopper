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
    final static private int BLOCK_SIZE = 15;

    static private int AUTOSELECT;
    static private int SMART_CURSOR;
    static private int LEFT;
    static private int RIGHT;
    static private int JUMP;
    static private int GRAPPLE;

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
        situateMouse();
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
            grappleInPlace();
        } else {
            final int NO_HOOK_WALK_CORRECTION = 550;
            holdKey(LEFT ,NO_HOOK_WALK_CORRECTION);
        }        
    }

    // Moves mouse to feet and grapples to floor
    private static void grappleInPlace() {
        final int GRAPPLE_DELAY = 200;
        bot.mouseMove(FEET_X - BLOCK_SIZE / 2, FEET_Y);
        tapKey(GRAPPLE);
        bot.delay(GRAPPLE_DELAY);
        bot.mouseMove(FEET_X, FEET_Y);
        tapKey(JUMP);
    }


    // Turns on autoselect and smart cursor
    private static void initializingKeystrokes() {
        bot.keyPress(AUTOSELECT);
        tapKey(SMART_CURSOR);
    }

    // Moves mouse to the character's feet
    private static void situateMouse() {
        bot.mouseMove(FEET_X, FEET_Y);
    }

    // Retrieves the pixel to determine if the axe has been autoselected
    private static boolean axeSelected() {
        final int THRESHOLD = 35;
        final int[] targetRGB = {255, 253, 76};
        int[] currentRGB = new int[3];
        Color pixel = bot.getPixelColor(AXE_X, AXE_Y);
        currentRGB[0] = pixel.getRed();
        currentRGB[1] = pixel.getGreen();
        currentRGB[2] = pixel.getBlue();
        for (int i = 0; i < 3; i++) {
            if (!colorWithinThreshold(targetRGB[i], currentRGB[i], THRESHOLD)) {
                return false;
            }
        }
        return true;
    }

    private static boolean colorWithinThreshold(int target, int current, int threshold) {
        return (current <= (target + threshold) && current >= (target - threshold));
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
        final int MS_WALKING = 500;
        holdKey(LEFT, MS_WALKING);
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
        bot.keyRelease(AUTOSELECT);
        tapKey(SMART_CURSOR);
        bot.keyRelease(LEFT);
        bot.keyRelease(RIGHT);
        bot.keyRelease(JUMP);
        bot.keyRelease(GRAPPLE);
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        activated = false;
        keyboardHook.shutdownHook();
        System.exit(0);
    }


}

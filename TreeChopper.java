package src;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

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

    final static private int AUTOSELECT = KeyEvent.VK_TAB;
    final static private int SMART_CURSOR = KeyEvent.VK_CONTROL;

    private static boolean hasHook = false;

    static private Robot bot;

    public static void main (String[] args) throws AWTException {
        final int START_DELAY = 1000;
        bot = new Robot();
        bot.delay(START_DELAY);
        startChopper();
    }

    
    // Main method
    private static void startChopper() {
        initializingKeystrokes();
        situateMouse();
        while (true) {
            bot.keyPress(KeyEvent.VK_D);
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
        bot.keyRelease(KeyEvent.VK_D);
        if (hasHook) {
            grappleInPlace();
        } else {
            final int NO_HOOK_WALK_CORRECTION = 550;
            holdKey(NO_HOOK_WALK_CORRECTION, KeyEvent.VK_A);
        }        
    }

    // Moves mouse to feet and grapples to floor
    private static void grappleInPlace() {
        bot.mouseMove(FEET_X - BLOCK_SIZE / 2, FEET_Y);
        tapKey(KeyEvent.VK_E);
        bot.delay(200);
        bot.mouseMove(FEET_X, FEET_Y);
        tapKey(KeyEvent.VK_SPACE);
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
        holdKey(KeyEvent.VK_A, MS_WALKING);
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
}

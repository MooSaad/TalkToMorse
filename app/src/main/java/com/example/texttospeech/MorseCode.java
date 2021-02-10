package com.example.texttospeech;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import java.util.HashMap;

public class MorseCode {

    static String[] ALPHA = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r",
            "s", "t", "u", "v", "w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "!", ",", "?",
            ".", "'" };
    static String[] MORSE = { ".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..",
            "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..", ".----",
            "..---", "...--", "....-", ".....", "-....", "--...", "---..", "----.", "-----", "-.-.--", "--..--",
            "..--..", ".-.-.-", ".----.", };

    public static HashMap<String, String> ALPHA_TO_MORSE = new HashMap<>();

    static {
        for (int i = 0; i < ALPHA.length  &&  i < MORSE.length; i++) {
            ALPHA_TO_MORSE.put(ALPHA[i], MORSE[i]);
        }
    }

    public static String alphaToMorse(String englishCode) {
        StringBuilder builder = new StringBuilder();
        String[] words = englishCode.trim().split(" ");

        for (String word : words) {
            for (int i = 0; i < word.length(); i++) {
                String morse = ALPHA_TO_MORSE.get(word.substring(i, i + 1).toLowerCase());
                builder.append(morse).append(" ");
            }

            builder.append("  ");
        }

        return builder.toString();
    }




    public static final String[] mAlphabet = {
            ". -", "- . . .", "- . - .", "- . .",
            ".", ". . - .", "- - .", ". . . .",
            ". .", ".- - -", "- . -", ". - . .",
            "- -", "- .", "- - -", ". - - .",
            "- - . -", ". - .", ". . .", "-",
            ". . -", ". . . -", ". - -", "- . . -",
            "- . - -", "- - . ."
    };
    // array of numbers represented in Morse code from 0 to 9
    public static final String[] mNumbers = {
            ". - - - -", ". . - - -", ". . . - -", ". . . . -", // 0, 1, 2, 3
            ". . . . .", "- . . . .", "- - . . .", "- - - . .", // 4, 5, 6, 7
            "- - - - .", "- - - - -"
    };


    public static String vibrateTranslate(String st) {
        String s = st.toLowerCase(); //upper and lower case represented by same character in Morse code

        // StringBuilder class makes it easier to append to String objects
        StringBuilder sb = new StringBuilder();
        // goes through user inputted String to translate to Morse code
        for (char ch : s.toCharArray()) {
            if (ch >= 'a' && ch <= 'z') { // between a and z
                int i = ch - 'a'; // specific index of mAlphabet
                sb.append(mAlphabet[i]).append("!"); // in vibration this symbolizes a space between a letter in morse code
            } else if (ch >= '0' && ch <= '9') { // between 0 and 9
                int j = ch - '0';
                sb.append(mNumbers[j]).append("!"); // in vibration this symbolized a space between a number in morse code
            } else if (ch == ' ') {
                sb.append("~");// in vibration, this symbolizes a space between a word in morse code
            }
            else{
                return"x";
            }

        }
        // appended char array is then returned as a String
        return sb.toString().trim();
    }


    
    
    
    
    
    
    
    
    
    
    
    public static Camera cam = null;

    //checks to see if the camera is off before turning it off. This should prevent any errors.
    private static void releaseCameraAndPreview() {
        if (cam != null) {
            cam.release();
            cam = null;
        }
    }

    //Method to turn the camera's main flashlight on.
    private static void turnFlashlightOn() {
        Parameters p = cam.getParameters();
        p.setFlashMode(Parameters.FLASH_MODE_TORCH);
        cam.setParameters(p);
    }

    //Method to turn the flashlight off.
    private static void turnFlashlightOff() {
        Parameters p = cam.getParameters();
        p.setFlashMode(Parameters.FLASH_MODE_OFF);
        cam.setParameters(p);
    }

    //flashTranslate should run in a separate thread to prevent "not responding" errors. This method should prevent overflow and skipping of frames.
    public static void flashInOwnThread(final String st) {
        Thread t= new Thread(new Runnable() {
            public void run() {
                flashTranslate(st);
            }
        });
        try{
            t.start();
            t.join();
        }catch(InterruptedException e) {}

    }


    // in off/on, code sleep method with parameter determined through for-loop, then in other method, call on first method coded with sleep() for morse code in flashlight
    public static void flashTranslate(String st) {

        //length of one Morse Code unit. Details about the International Morse Code are in the manual.
        int unit = 500;
        String output = alphaToMorse(st) + " ";

        // Open the camera resource upon the call of this method.
        try {
            releaseCameraAndPreview();
            cam = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // taking input from the string to translate them into flashlight
        for (char ch : output.toCharArray()) {
            if (ch == '.') {
                try {
                    turnFlashlightOn();
                    Thread.sleep(unit); // keeps flashlight on for 1 unit
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                }
            }
            else if (ch == '-') {
                try {
                    turnFlashlightOn();
                    Thread.sleep((3 * unit)); // keeps flashlight on for 3 units
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                }
            }
            else {
                try {
                    turnFlashlightOff();
                    Thread.sleep((unit)); // keeps flashlight off for one unit for every space in the input String
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                }
            }
            //
        }

        // Close and Release camera resource
        releaseCameraAndPreview();

    }


    
    
    
    
    
    
    
}
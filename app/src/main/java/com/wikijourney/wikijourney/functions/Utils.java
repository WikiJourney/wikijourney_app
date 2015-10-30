package com.wikijourney.wikijourney.functions;

/**
 * Class containing functions for everything...<br/>
 * Created by Thomas on 08/08/2015.
 */
class Utils {

    /**
     * Capitalizes the first letter of a String
     * @param s The String to capitalize
     * @return The capitalized String
     */
    public static String capitalizeFirstLetter(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}

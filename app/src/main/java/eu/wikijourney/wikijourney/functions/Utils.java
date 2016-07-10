package eu.wikijourney.wikijourney.functions;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Class containing functions for everything...<br/>
 * Created by Thomas on 08/08/2015.
 */
public class Utils {

    /**
     * Capitalizes the first letter of a String
     * @param s The String to capitalize
     * @return The capitalized String
     */
    public static String capitalizeFirstLetter(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    /**
     * Hides the keyboard in anything (Activity or Fragment)
     * @param context The Context (Activity) containing the input field
     * @param view The View corresponding to the input field
     */
    // From : https://stackoverflow.com/a/17789187/3641865, inspired by the Shaarlier project
    public static void hideKeyboard(Context context, View view) {
        if ((context != null) && (view != null)) {
            // else crash if there is no view with focus
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

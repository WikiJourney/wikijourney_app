package eu.wikijourney.wikijourney.functions;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

/**
 * Contains all UI related functions<br/><br/>
 *
 * Created by Thomas on 03/08/2015.
 */
public class UI {
    /**
     * Displays a pop-up with a OK button
     * @param context The Activity where the pop-up should be created
     * @param popUpTitle The title of the pop-up
     * @param popUpMessage The message of the pop-up
     */
    public static void openPopUp(Context context, String popUpTitle, String popUpMessage)
    {
        AlertDialog.Builder builder;
        AlertDialog dialog;

        // 1. Instantiate an AlertDialog.Builder with its constructor
        builder = new AlertDialog.Builder(context);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(popUpMessage)
                .setTitle(popUpTitle);

        // Add the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });

        // 3. Get the AlertDialog from create()
        dialog = builder.create();

        dialog.show();//Show it.
    }
}

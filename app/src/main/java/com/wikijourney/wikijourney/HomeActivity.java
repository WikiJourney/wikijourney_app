package com.wikijourney.wikijourney;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class HomeActivity extends ActionBarActivity {
    public final static String EXTRA_COORD = "com.wikijourney.wikijourney.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
//        return true;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void goMap(View pView) {
        Intent mapIntent = new Intent(this, MapActivity.class);
        startActivity(mapIntent);
    }

    public void goToDest(View pView) {
        // We get the values entered by the user, and store them in a double array
        double[] coord = new double[2];
        EditText nsCoordInput = (EditText)findViewById(R.id.n_s_coord);
        coord[0] = Double.parseDouble(nsCoordInput.getText().toString());
        EditText ewCoordInput = (EditText)findViewById(R.id.e_w_coord);
        coord[1] = Double.parseDouble(ewCoordInput.getText().toString());

        // We add the extras to an intent
        Intent goToDestIntent = new Intent(this, MapActivity.class);
        goToDestIntent.putExtra(EXTRA_COORD, coord);
//        goToDestIntent.putExtra("ewCoord", ewCoord);

        // We start the activity using the intent
        startActivity(goToDestIntent);
    }
}

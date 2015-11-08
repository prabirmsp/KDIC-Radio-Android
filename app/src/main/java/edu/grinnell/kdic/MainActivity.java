package edu.grinnell.kdic;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import edu.grinnell.kdic.schedule.GetSchedule;
import edu.grinnell.kdic.schedule.ScheduleFragment;
import edu.grinnell.kdic.visualizer.VisualizeFragment;

public class MainActivity extends AppCompatActivity {

    boolean isVisualizeShown;
    VisualizeFragment visualizeFragment;
    ScheduleFragment scheduleFragment;
    Toolbar playbackToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set toolbar as actionbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_main));

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            scheduleFragment = new ScheduleFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            scheduleFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, scheduleFragment).commit();

            getSupportFragmentManager().addOnBackStackChangedListener(
                    new FragmentManager.OnBackStackChangedListener() {
                        @Override
                        public void onBackStackChanged() {

                            //Enable Up button only  if there are entries in the back stack
                            boolean canBack = getSupportFragmentManager().getBackStackEntryCount() > 0;
                            getSupportActionBar().setDisplayHomeAsUpEnabled(canBack);
                        }
                    }
            );

        }

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS, 0);
        if (sharedPreferences.getBoolean(Constants.FIRST_RUN, true)) {
            GetSchedule getSchedule = new GetSchedule(MainActivity.this);
            getSchedule.execute();
            sharedPreferences.edit().putBoolean(Constants.FIRST_RUN, false).apply();
        }

        visualizeFragment = new VisualizeFragment();

        playbackToolbar = (Toolbar) findViewById(R.id.playback_toolbar);
        View.OnClickListener onToggleVisualizeFragment = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleVisualizeFragment();

            }
        };
        playbackToolbar.setNavigationOnClickListener(onToggleVisualizeFragment);
        playbackToolbar.setOnClickListener(onToggleVisualizeFragment);

    }

    public void toggleVisualizeFragment() {
        if (isVisualizeShown) {
            // hide visualize fragment

            getSupportFragmentManager().popBackStack();
            playbackToolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_up_white_24dp);
        } else {
            // show visualize fragment
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_bottom, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_bottom)
                    .replace(R.id.fragment, visualizeFragment)
                    .addToBackStack(null)
                    .commit();
            playbackToolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_down_white_24dp);
        }
        isVisualizeShown = !isVisualizeShown;
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (isVisualizeShown)
            toggleVisualizeFragment();
        else
            getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.update_schedule) {
            GetSchedule getSchedule = new GetSchedule(MainActivity.this);
            getSchedule.execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isVisualizeShown) {
            playbackToolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_up_white_24dp);
            isVisualizeShown = false;
        }
    }
}

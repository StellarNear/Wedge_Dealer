package stellarnear.wedge_companion.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;

public class PetActivity extends AppCompatActivity {
    private FrameLayout mainFrameFrag;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (settings.getBoolean("switch_fullscreen_mode", getApplicationContext().getResources().getBoolean(R.bool.switch_fullscreen_mode_DEF))) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);
        PersoManager.setPetPJ();

        buildMainPage();
    }

    private void buildMainPage() {
        int themeId=getResources().getIdentifier("AppTheme"+PersoManager.getCurrentPJ().getID(), "style", getPackageName());
        setTheme(themeId);
        setContentView(R.layout.activity_pet);
        mainFrameFrag = findViewById(R.id.fragment_main_frame_layout);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PersoManager.swap();
                buildMainPage();
                return false;
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().show();
        toolbar.setTitle("Companion animal : "+PersoManager.getCurrentNamePJ());
        int drawableId=getResources().getIdentifier("background_banner"+PersoManager.getCurrentPJ().getID(), "drawable", getPackageName());
        toolbar.setBackground(getDrawable(drawableId));
        Window window = getWindow();
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryDark,typedValue,true);
        window.setStatusBarColor(typedValue.data);
        startFragment();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkOrientStart(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (PersoManager.getCurrentPJ() != null) {
            PersoManager.setPetPJ();
            PersoManager.getCurrentPJ().refresh();
            buildMainPage();
        }
    }

    private void startFragment() {
        Fragment fragment = new PetActivityFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(mainFrameFrag.getId(), fragment,"frag_main");
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
        finish();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            unlockOrient();
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra("fromActivity","PetActivity");
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        final Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        if (display.getRotation()==Surface.ROTATION_0) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }

        if (display.getRotation()==Surface.ROTATION_180) {
            /*Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
            */
        }
    }

    private void checkOrientStart(int screenOrientation) {
        if (getRequestedOrientation() != screenOrientation) {
            setRequestedOrientation(screenOrientation);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                }
            }, 2000);
        }
    }

    private void lockOrient() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void unlockOrient() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }
}

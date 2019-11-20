package stellarnear.wedge_companion.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;

public class MainActivity extends AppCompatActivity {
    private boolean loading = false;
    private boolean touched = false;
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

        if (PersoManager.getCurrentPJ() == null) {
            Window window = getWindow();
            window.setStatusBarColor(getColor(R.color.start_back_color));
            lockOrient();

            Thread persoCreation = new Thread(new Runnable() {
                public void run() {
                    MainActivity.this.runOnUiThread(new Runnable()
                    {
                        public void run() {
                            PersoManager.initPJs(getApplicationContext());
                            loading = true;
                        }
                    });
                }});

            persoCreation.start();

            final ImageView image = new ImageView(getApplicationContext());
            image.setBackgroundColor(getColor(R.color.start_back_color));
            setContentView(image);

            Thread loadListner = new Thread(new Runnable() {
                public void run() {
                    setLoadCompleteListner(image);
                }
            });
            loadListner.start();

        } else {
            buildMainPage();
        }
    }

    private void setLoadCompleteListner(final ImageView image) {
        Timer timerRefreshLoading = new Timer();
        timerRefreshLoading.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (loading) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            image.setImageDrawable(getDrawable(R.drawable.background));
                            image.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View arg0, MotionEvent arg1) {
                                    unlockOrient();
                                    touched = true;
                                    buildMainPage();
                                    return true;//always return true to consume event
                                }
                            });
                        }
                    });
                }
            }
        }, 333, 333);
    }

    private void buildMainPage() {
        int themeId=getResources().getIdentifier("AppTheme"+PersoManager.getCurrentPJ().getID(), "style", getPackageName());
        setTheme(themeId);
        setContentView(R.layout.activity_main);
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
        new Tools().customToast(getApplicationContext(),"Personnage selectionné : "+PersoManager.getCurrentNamePJ(),"center");
        toolbar.setTitle(PersoManager.getCurrentNamePJ()+" companion");
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
        checkOrientStart(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (PersoManager.getCurrentPJ() != null && loading && touched) {
            PersoManager.getCurrentPJ().refresh();
            buildMainPage();
        }
    }

    private void startFragment() {
        Fragment fragment = new MainActivityFragment();
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
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        final Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                //on y est déja
                break;

            case Surface.ROTATION_90:
                //Intent intent_stance = new Intent(MainActivity.this, StanceActivity.class);
               // startActivity(intent_stance);
                finish();
                break;

            case Surface.ROTATION_270:
                //Intent intent_help = new Intent(MainActivity.this, HelpActivity.class);
                //startActivity(intent_help);
                finish();
                break;
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

package stellarnear.wedge_dealer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import stellarnear.wedge_dealer.Perso.Perso;
import stellarnear.wedge_dealer.Rolls.Roll;
import stellarnear.wedge_dealer.Rolls.RollFactory;
import stellarnear.wedge_dealer.Rolls.RollList;
import stellarnear.wedge_dealer.TextFilling.Damages;
import stellarnear.wedge_dealer.TextFilling.PostRandValues;
import stellarnear.wedge_dealer.TextFilling.PreRandValues;
import stellarnear.wedge_dealer.TextFilling.RangesAndProba;
import stellarnear.wedge_dealer.TextFilling.SetupCheckboxes;

public class MainActivity extends AppCompatActivity {
    private Context mC;
    private View mainPage;
    private Drawable ori_background;
    public static Perso wedge;

    private FloatingActionButton fabAtk;
    private FloatingActionButton fabDmg;
    private FloatingActionButton fabDmgDet;
    private ImageButton simpleAtk;
    private ImageButton barrageShot;

    private RollList rollList;
    private RollList selectedRolls;
    private boolean firstDmgRoll = true;
    private boolean firstAtkRoll = true;
    private String mode;

    private PreRandValues preRandValues;
    private PostRandValues postRandValues;
    private SetupCheckboxes setupCheckboxes;
    private Damages damages;
    private RangesAndProba rangesAndProba;
    private DisplayRolls displayRolls;

    private Tools tools = new Tools();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     //shouldExecuteOnResume = false;
        this.mC = getApplicationContext();
        wedge = new Perso(mC);
    }

    @Override
    protected void onResume() {
        super.onResume();
        wedge.getAllResources().refreshMaxs();
        resetScreen();
    }

    public void resetScreen() {
        mode="fullround";
        firstDmgRoll = true;
        firstAtkRoll = true;
        setContentView(R.layout.activity_main);
        mainPage = findViewById(R.id.mainPage);
        ori_background = mainPage.getBackground();
        mainPage.setBackgroundResource(R.drawable.background);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundResource(R.drawable.background_banner);
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetScreen();
            }
        });

        mainPage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                mainPage.setBackground(ori_background);
                hideButtons(0);
                startPreRand();
                mainPage.setOnTouchListener(null);
                return true;//always return true to consume event
            }
        });

        simpleAtk = findViewById(R.id.button_simple_atk);
        simpleAtk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode="simple";
                hideButtons(1);
                startPreRand();
            }
        });
        barrageShot = findViewById(R.id.button_barrage_shot);
        barrageShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wedge.getAllResources().getResource("mythic_points").getCurrent()>=1) {
                    mode = "barrage_shot";
                    hideButtons(2);
                    wedge.getAllResources().getResource("mythic_points").spend(1);
                    new PostData(mC,new PostDataElement("Lancement de tir de barrage mythique","-1pt mythique"));
                    tools.customToast(mC,"Il te reste "+wedge.getResourceValue("mythic_points")+" points mythiques","center");
                    startPreRand();
                } else { tools.customToast(mC,"Tu n'as pas assez de points mythiques","center"); }
            }
        });

        fabAtk = (FloatingActionButton) findViewById(R.id.fabAtk);
        setListenerFabAtk();
        fabDmg = (FloatingActionButton) findViewById(R.id.fab_damage);
        setListenerFabDmg();
        fabDmgDet = (FloatingActionButton) findViewById(R.id.fab_damage_detail);
        setListenerFabDmgDet();

        ((TextView) findViewById(R.id.leg_pts_txt)).setText(String.valueOf(wedge.getResourceValue("legendary_points")));
        ((TextView) findViewById(R.id.mythic_pts_txt)).setText(String.valueOf(wedge.getResourceValue("mythic_points")));
    }

    private void setListenerFabAtk() {
        fabAtk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode.equalsIgnoreCase("")){mode="fullround";}
                if (firstAtkRoll) {
                    hideButtons(0);
                    startRandAtk();
                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Demande de confirmation")
                            .setMessage("Voulez vous relancer les jets d'attaque ?")
                            .setIcon(android.R.drawable.ic_menu_help)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    startRandAtk();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            }
        });
    }

    private void clearStep(Integer step) {
        switch (step) {
            case 0:
                if (mainPage.getBackground() != ori_background) {
                    mainPage.setBackground(ori_background);
                }
                mainPage.setOnTouchListener(null);
                if (preRandValues != null) {
                    preRandValues.hideViews();
                }
            case 1:
                if (postRandValues != null) {
                    postRandValues.hideViews();
                }
                if (setupCheckboxes != null) {
                    setupCheckboxes.hideViews();
                    displayRolls = null;
                }
            case 2:
                if (damages != null) {
                    damages.hideViews();
                }
            case 3:
                if (rangesAndProba != null) {
                    rangesAndProba.hideViews();
                }
            case 4:
                if (displayRolls == null || displayRolls.size()==0) {
                    fabDmgDet.setEnabled(false);
                }
        }
    }

    private void startPreRand() {
        clearStep(0);
        this.rollList = new RollFactory(MainActivity.this,mC,mode).getRollList();
        preRandValues = new PreRandValues(mC, mainPage, rollList);
        if (damages != null) {
            damages.hideViews();
        }
    }

    private void hideButtons(int buttonClicked) {
        simpleAtk.setOnClickListener(null);
        barrageShot.setOnClickListener(null);
        FrameLayout frame = findViewById(R.id.leg_pts);
        frame.animate().translationXBy(200).setDuration(1000).start();
        FrameLayout frame2 = findViewById(R.id.mythic_pts);
        frame2.animate().translationXBy(-200).setDuration(1000).start();

        switch (buttonClicked){
            case 0:
                simpleAtk.animate().translationXBy(-200).setDuration(1000).start();
                barrageShot.animate().translationXBy(200).setDuration(1000).start();
                break;
            case 1:
                simpleAtk.animate().scaleX(2).scaleY(2).alpha(0).setDuration(1000).start();
                barrageShot.animate().translationXBy(200).setDuration(1000).start();
                break;
            case 2:
                simpleAtk.animate().translationXBy(-200).setDuration(1000).start();
                barrageShot.animate().scaleX(2).scaleY(2).alpha(0).setDuration(1000).start();
                break;
        }
    }

    private void startRandAtk() {
        firstAtkRoll = false;
        firstDmgRoll = true;
        showDivider();
        fabAtk.animate().setDuration(1000).translationX(-400).start();       //decale le bouton à gauche pour l'apparition du suivant
        Snackbar.make(mainPage, "Lancement des dés en cours... ", Snackbar.LENGTH_SHORT).show();
        startPreRand();
        if (postRandValues != null) {
            postRandValues.hideViews();
        }
        if (setupCheckboxes != null) {
            setupCheckboxes.hideViews();
        }
        postRandValues = new PostRandValues(mC, mainPage, rollList);
        setupCheckboxes = new SetupCheckboxes(mC, mainPage, rollList);
        new PostData(mC,new PostDataElement(rollList,"atk"));
    }

    private void showDivider() {
        mainPage.findViewById(R.id.bar_sep).setVisibility(View.VISIBLE);
    }

    private void setListenerFabDmg() {
        fabDmg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatingActionButton fab_dmg = (FloatingActionButton) findViewById(R.id.fab_damage);
                fab_dmg.animate().setDuration(1000).translationX(+400).start();

                Snackbar.make(view, "Calcul des dégâts en cours... ", Snackbar.LENGTH_SHORT).show();
                AlphaAnimation anim = new AlphaAnimation(0, 1);
                anim.setDuration(2000);

                checkSelectedRolls();
                if (firstDmgRoll) {
                    startDamage();
                    firstDmgRoll = false;
                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Demande de confirmation")
                            .setMessage("Voulez vous relancer des jets de dégâts pour l'attaque en cours ?")
                            .setIcon(android.R.drawable.ic_menu_help)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    startDamage();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            }
        });
    }

    private void startDamage() {
        displayRolls = null;
        clearStep(2);
        damages = new Damages(MainActivity.this,mC, mainPage, selectedRolls);  //calcul et affiche les degats
        if (selectedRolls.getDmgDiceList().getList().size() > 0) {
            rangesAndProba = new RangesAndProba(mC, mainPage, selectedRolls);
            fabDmgDet.setVisibility(View.VISIBLE);
            fabDmgDet.setEnabled(true);
        }
        postRandValues.refreshPostRandValues();
        new PostData(mC,new PostDataElement(selectedRolls,"dmg"));
    }

    private void checkSelectedRolls() {
        selectedRolls = new RollList();
        for (Roll roll : rollList.getList()) {
            if (roll.isInvalid() || (!roll.isHitConfirmed() && !roll.isMissed() )) {
                continue;
            }
            selectedRolls.add(roll);
        }
    }

    private void setListenerFabDmgDet() {
        fabDmgDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(displayRolls==null){displayRolls = new DisplayRolls(MainActivity.this, mC, selectedRolls);}
                displayRolls.showPopup();
            }
        });

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
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

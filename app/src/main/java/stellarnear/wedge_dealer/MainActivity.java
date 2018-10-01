package stellarnear.wedge_dealer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stellarnear.wedge_dealer.Perso.Perso;
import stellarnear.wedge_dealer.Rolls.Roll;
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
    private RollList rollList;
    private RollList selectedRolls;
    private Tools tools = new Tools();
    private boolean firstDmgRoll = true;
    private boolean firstAtkRoll = true;

    private PreRandValues preRandValues;
    private PostRandValues postRandValues;
    private SetupCheckboxes setupCheckboxes;
    private Damages damages;
    private RangesAndProba rangesAndProba;
    private DisplayRolls displayRolls;

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     //shouldExecuteOnResume = false;
        this.mC = getApplicationContext();
        this.settings = PreferenceManager.getDefaultSharedPreferences(mC);
        wedge = new Perso(mC);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetScreen();
    }

    public void resetScreen() {
        firstDmgRoll = true;
        firstAtkRoll = true;
        setContentView(R.layout.activity_main);
        mainPage = findViewById(R.id.mainPage);
        ori_background = mainPage.getBackground();
        mainPage.setBackgroundResource(R.drawable.background);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundResource(R.drawable.background_banner);
        setSupportActionBar(toolbar);

        mainPage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                mainPage.setBackground(ori_background);
                startPreRand();
                mainPage.setOnTouchListener(null);
                return true;//always return true to consume event
            }
        });

        fabAtk = (FloatingActionButton) findViewById(R.id.fabAtk);
        setListenerFabAtk();

        fabDmg = (FloatingActionButton) findViewById(R.id.fab_damage);
        setListenerFabDmg();

        fabDmgDet = (FloatingActionButton) findViewById(R.id.fab_damage_detail);
        setListenerFabDmgDet();
    }

    private void setListenerFabAtk() {
        fabAtk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mainPage.getBackground() != ori_background) {
                    mainPage.setBackground(ori_background);
                }

                mainPage.setOnTouchListener(null);
                if (firstAtkRoll) {
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
        setRollList();
        preRandValues = new PreRandValues(mC, mainPage, rollList);
        if (damages != null) {
            damages.hideViews();
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
    }

    private void setRollList() {
        this.rollList = new RollList();
        String baseAtksTxt = settings.getString("jet_att", mC.getResources().getString(R.string.jet_att_def));     //cherche la clef     jet_att dans les setting sinon valeur def (xml)
        String delim = ",";

        List<Integer> baseAtks = tools.toInt(Arrays.asList(baseAtksTxt.split(delim)));
        List<Integer> allAtks = new ArrayList<>(baseAtks);
        Integer prouesse = tools.toInt(settings.getString("prouesse_val", String.valueOf(mC.getResources().getInteger(R.integer.prouesse_def))));
        Integer prouesseAttrib = tools.toInt(settings.getString("prouesse_attrib", String.valueOf(mC.getResources().getInteger(R.integer.prouesse_attrib_def))));
        allAtks.set(prouesseAttrib - 1, prouesse + baseAtks.get(prouesseAttrib - 1));

        if (settings.getBoolean("rapid_enchant_switch", mC.getResources().getBoolean(R.bool.rapid_enchant_switch_def))) {
            allAtks.add(0, allAtks.get(0));
        }
        if (settings.getBoolean("tir_rapide", mC.getResources().getBoolean(R.bool.tir_rapide_switch_def))) {
            allAtks.add(0, allAtks.get(0));
        }
        for (Integer atk : allAtks) {
            this.rollList.add(new Roll(MainActivity.this,mC, atk));
        }
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
            displayRolls = new DisplayRolls(MainActivity.this, mC, selectedRolls);
            fabDmgDet.setVisibility(View.VISIBLE);
            fabDmgDet.setEnabled(true);
        }
    }

    private void checkSelectedRolls() {
        selectedRolls = new RollList();
        for (Roll roll : rollList.getList()) {
            if (!roll.isHitConfirmed() || roll.isInvalid()) {
                continue;
            }
            selectedRolls.add(roll);
        }
    }

    private void setListenerFabDmgDet() {
        fabDmgDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

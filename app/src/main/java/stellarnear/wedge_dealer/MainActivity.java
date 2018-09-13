package stellarnear.wedge_dealer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import stellarnear.stellarnear.R;
import stellarnear.wedge_dealer.Rolls.Roll;
import stellarnear.wedge_dealer.Rolls.RollList;
import stellarnear.wedge_dealer.TextFilling.PostRandValues;
import stellarnear.wedge_dealer.TextFilling.PreRandValues;

public class MainActivity extends AppCompatActivity {
    //boolean shouldExecuteOnResume;    //permet de setup que certaine chose lors de la premeire execuution

    private Context mC;
    private View mainPage;
    private Drawable ori_background;
    private FloatingActionButton fabAtk;
    private FloatingActionButton fabDmg;
    private FloatingActionButton fabDmgDet;
    private RollList rollList;
    private Tools tools=new Tools();

    private SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //shouldExecuteOnResume = false;
        this.mC=getApplicationContext();
        this.settings = PreferenceManager.getDefaultSharedPreferences(mC);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetScreen();
        //pour l'instant rien de spécial en resume (on reecrit pas apre sorti des menu settings)
        //if shouldExecuteOnResume = false pour faire qu'une fois et pas au refresh
    }

    public void resetScreen() {

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


        // affichage premier de la base d'attaque calcul aussi n_att

        //premier bouton (jet d'attaque)
        fabAtk = (FloatingActionButton) findViewById(R.id.fabAtk);
        setListenerFabAtk();

        //bouton de degat
        fabDmg = (FloatingActionButton) findViewById(R.id.fab_damage);
        setListenerFabDmg();

        //bouton detail degat
        fabDmgDet = (FloatingActionButton) findViewById(R.id.fab_damage_detail);
        setFabDmgDet();
    }

    private void startPreRand() {
        setRollList();
        new PreRandValues(mC,mainPage,rollList);
    }

    private void setRollList() {
        this.rollList = new RollList(mC);
        String baseAtksTxt = settings.getString("jet_att", mC.getResources().getString(R.string.jet_att_def));     //cherche la clef     jet_att dans les setting sinon valeur def (xml)
        String delim = ",";

        List<Integer> baseAtks=tools.toInt(Arrays.asList(baseAtksTxt.split(delim)));
        List<Integer> allAtks=new ArrayList<>(baseAtks);
        Integer prouesse = tools.toInt(settings.getString("prouesse_val", mC.getResources().getString(R.string.prouesse_def)));
        Integer prouesseAttrib = tools.toInt(settings.getString("prouesse_attrib", mC.getResources().getString(R.string.prouesse_attrib_def)));
        allAtks.set(prouesseAttrib - 1, prouesse + baseAtks.get(prouesseAttrib - 1));

        if (settings.getBoolean("rapid_enchant_switch", mC.getResources().getBoolean(R.bool.rapid_enchant_switch_def))) {
            allAtks.add(0,allAtks.get(0));
        }
        if (settings.getBoolean("tir_rapide", mC.getResources().getBoolean(R.bool.tir_rapide_switch_def))) {
            allAtks.add(0,allAtks.get(0));
        }
        for(Integer atk:allAtks){
            this.rollList.add(new Roll(mC,atk));
        }
    }

    private void setListenerFabAtk() {
        fabAtk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mainPage.getBackground() != ori_background) {
                    mainPage.setBackground(ori_background);
                }
                startPreRand();
                new PostRandValues(mC,mainPage,rollList);

                fabAtk.animate().setDuration(1000).translationX(-400).start();       //decale le bouton à gauche pour l'apparition du suivant
                Snackbar.make(view, "Lancement des dés en cours... ", Snackbar.LENGTH_SHORT).show();
            }
        });
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
//                fab_damage_det_view.startAnimation(anim);
//                fab_damage_det_view.setVisibility(View.VISIBLE);
//
//                //si c'est pas la premeire fois qu'on lance les degat on demande confirmation
//                if (firstDmgRoll) {
//                    affich_damage();   //calcul et affiche les degats
//                } else {
//                    new AlertDialog.Builder(MainActivity.this)
//                            .setTitle("Demande de confirmation")
//                            .setMessage("Voulez vous relancer des jets de dégâts pour l'attaque en cours ?")
//                            .setIcon(android.R.drawable.ic_menu_help)
//                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton) {
//                                    View proba_text_crit = findViewById(R.id.all_dmg_phy_crit_proba);
//                                    proba_text_crit.setVisibility(View.INVISIBLE);
//                                    affich_damage();
//                                }
//                            })
//                            .setNegativeButton(android.R.string.no, null).show();
//
//                }
//
//                //bouton detail des degats
//
//                FloatingActionButton fab_dmg_det = (FloatingActionButton) findViewById(R.id.fab_damage_detail);
//                if (all_dices_str.equals("")) {
//                    fab_dmg_det.setEnabled(false);
//                } else {
//                    fab_dmg_det.setEnabled(true);
//                }

            }
        });
    }

    private void setFabDmgDet() {
        fabDmgDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                display_dmg_detail();
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

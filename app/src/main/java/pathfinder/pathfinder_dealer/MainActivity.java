package pathfinder.pathfinder_dealer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    boolean shouldExecuteOnResume;

    Integer n_att;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shouldExecuteOnResume = false;
        // reset les pref au defaut
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        jet_att_print();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.animate().setDuration(1000).translationX(-400).start();

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                Snackbar.make(view, "Lancement des dés en cours... ",Snackbar.LENGTH_LONG).show();

                set_grid();

                TextView multi_text = (TextView) findViewById(R.id.multishot);
                multi_text.setVisibility(View.INVISIBLE);

                if (settings.getBoolean("feu_nourri_switch",getResources().getBoolean(R.bool.feu_nourri_switch_def)))  {
                    //TextView multi_text = (TextView) findViewById(R.id.multishot);
                    multi_text.setVisibility(View.VISIBLE);
                }

                CheckBox checkBox6 = (CheckBox) findViewById(R.id.checkBox6);
                checkBox6.setVisibility(View.INVISIBLE);
                CheckBox checkBox5 = (CheckBox) findViewById(R.id.checkBox5);
                checkBox5.setVisibility(View.INVISIBLE);

                TextView hit_text = (TextView) findViewById(R.id.hit_text);
                hit_text.setVisibility(View.VISIBLE);

                set_checkbox_hit();

                CheckBox checkBox6_crit = (CheckBox) findViewById(R.id.checkBox6_crit);
                checkBox6_crit.setVisibility(View.INVISIBLE);
                CheckBox checkBox5_crit = (CheckBox) findViewById(R.id.checkBox5_crit);
                checkBox5_crit.setVisibility(View.INVISIBLE);

                TextView crit_text = (TextView) findViewById(R.id.crit_text);
                crit_text.setVisibility(View.VISIBLE);

                set_checkbox_crit();

                View bar_sep = findViewById(R.id.bar_sep);
                bar_sep.setVisibility(View.VISIBLE);


                View fab_damage_view = findViewById(R.id.fab_damage);

                AlphaAnimation anim = new AlphaAnimation(0,1);
                anim.setDuration(2000);
                fab_damage_view.startAnimation(anim);


            }});

        FloatingActionButton fab_dmg = (FloatingActionButton) findViewById(R.id.fab_damage);
        fab_dmg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatingActionButton fab_dmg = (FloatingActionButton) findViewById(R.id.fab_damage);
                fab_dmg.animate().setDuration(1000).translationX(+400).start();

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                Snackbar.make(view, "Calcul des dégâts en cours... ",Snackbar.LENGTH_LONG).show();



                View fab_damage_det_view = findViewById(R.id.fab_damage_detail);
                fab_damage_det_view.setVisibility(View.VISIBLE);
                AlphaAnimation anim = new AlphaAnimation(0,1);
                anim.setDuration(2000);
                fab_damage_det_view.startAnimation(anim);


            }});
    }



    @Override
    protected void onResume() {
        super.onResume();
        if(shouldExecuteOnResume){
            jet_att_print();
        } else {
            shouldExecuteOnResume = true;
        }
    }

    private void jet_att_print() {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        //calcul des valeurs d'attaque

        String att_base = settings.getString("jet_att",getResources().getString(R.string.jet_att_def));
        String delim = ",";

        String[] list_att_base_string = att_base.split(delim);

        List<Integer> list_att_base = new ArrayList<Integer>();


        for (String each : list_att_base_string) {
            list_att_base.add(Integer.parseInt(each));
        }

        List<Integer> list_att = new ArrayList<Integer>(list_att_base);

        Integer thor;
        Integer predi;
        Integer neuf_m;
        Integer magic;

        if (settings.getBoolean("thor_switch",getResources().getBoolean(R.bool.thor_switch_def))) {
            thor = 1;
        }  else { thor = 0; }
        if (settings.getBoolean("predil_switch",getResources().getBoolean(R.bool.predil_switch_def)))  {
            predi = 1;
        }    else { predi = 0; }
        if (settings.getBoolean("neuf_m_switch",getResources().getBoolean(R.bool.neuf_m_switch_def)))  {
            neuf_m = 1;
        }    else { neuf_m = 0; }
        if (settings.getBoolean("magic_switch",getResources().getBoolean(R.bool.magic_switch_def)))  {
            magic = 1;
        }    else { magic = 0; }


        String val_dex_str = settings.getString("mod_dex",getResources().getString(R.string.mod_dex_def));
        Integer val_dex = Integer.parseInt(val_dex_str);

        String magic_val_str = settings.getString("magic_val",getResources().getString(R.string.magic_val_def));
        Integer magic_val = Integer.parseInt(magic_val_str);

        String epic_str = settings.getString("epic_val",getResources().getString(R.string.epic_val_def));
        Integer epic_val = Integer.parseInt(epic_str);


        String prouesse_str = settings.getString("prouesse_val",getResources().getString(R.string.prouesse_def));
        Integer prouesse = Integer.parseInt(prouesse_str);

        String prouesse_attrib_str = settings.getString("prouesse_attrib",getResources().getString(R.string.prouesse_attrib_def));
        Integer prouesse_attrib = Integer.parseInt(prouesse_attrib_str);

        list_att.set(prouesse_attrib-1,prouesse+list_att.get(prouesse_attrib-1));

        int max_att_jet = list_att_base.get(0) + 3*thor + predi  + val_dex + magic*magic_val + neuf_m + epic_val;



        int length = list_att.size();
        n_att = length;

        for(int i=0; i<n_att; i++){
            list_att.set(i,list_att.get(i) + 3*thor + 1*predi  + val_dex + magic*magic_val + neuf_m + epic_val);
        }


        if (settings.getBoolean("rapid_enchant_switch",getResources().getBoolean(R.bool.rapid_enchant_switch_def))) {
            n_att=n_att+1;
            list_att.add(0,max_att_jet);
        }



        if (settings.getBoolean("tir_rapide",getResources().getBoolean(R.bool.tir_rapide_switch_def))) {
            n_att=n_att+1;
            list_att.add(0,max_att_jet);
            for(int i=0; i<n_att; i++){
                list_att.set(i,list_att.get(i) -2);
            }
        }

        if (settings.getBoolean("viser",getResources().getBoolean(R.bool.viser_switch_def))) {
            for(int i=0; i<n_att; i++){
                String malus_viser_str = settings.getString("viser_val",getResources().getString(R.string.viser_val_def));
                Integer malus_viser = Integer.parseInt(malus_viser_str);
                list_att.set(i,list_att.get(i) -malus_viser);
            }
        }




        StringBuilder affichage_att = new StringBuilder();
        String delim2=" / +";
        for (Integer each : list_att) {
            affichage_att.append(delim2).append(String.valueOf(each));
        }

        String affichage_att_str = affichage_att.substring(delim2.length()-1);


        TextView text_curent_att = (TextView) findViewById(R.id.jet_att_textbox);
        text_curent_att.setText( n_att + " attaques\n" +affichage_att_str);

    }

    private void jet_att_print(Integer jet1,Integer jet2,Integer jet3,Integer jet4,Integer jet5,Integer jet6) {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Integer[] all_jet= new Integer[]{jet1,jet2,jet3,jet4,jet5,jet6};

        //calcul des valeurs d'attaque

        String att_base = settings.getString("jet_att",getResources().getString(R.string.jet_att_def));
        String delim = ",";

        String[] list_att_base_string = att_base.split(delim);

        List<Integer> list_att_base = new ArrayList<Integer>();


        for (String each : list_att_base_string) {
            list_att_base.add(Integer.parseInt(each));
        }

        List<Integer> list_att = new ArrayList<Integer>(list_att_base);

        Integer thor;
        Integer predi;
        Integer neuf_m;
        Integer magic;

        if (settings.getBoolean("thor_switch",getResources().getBoolean(R.bool.thor_switch_def))) {
            thor = 1;
        }  else { thor = 0; }
        if (settings.getBoolean("predil_switch",getResources().getBoolean(R.bool.predil_switch_def)))  {
            predi = 1;
        }    else { predi = 0; }
        if (settings.getBoolean("neuf_m_switch",getResources().getBoolean(R.bool.neuf_m_switch_def)))  {
            neuf_m = 1;
        }    else { neuf_m = 0; }
        if (settings.getBoolean("magic_switch",getResources().getBoolean(R.bool.magic_switch_def)))  {
            magic = 1;
        }    else { magic = 0; }


        String val_dex_str = settings.getString("mod_dex",getResources().getString(R.string.mod_dex_def));
        Integer val_dex = Integer.parseInt(val_dex_str);

        String magic_val_str = settings.getString("magic_val",getResources().getString(R.string.magic_val_def));
        Integer magic_val = Integer.parseInt(magic_val_str);

        String epic_str = settings.getString("epic_val",getResources().getString(R.string.epic_val_def));
        Integer epic_val = Integer.parseInt(epic_str);


        String prouesse_str = settings.getString("prouesse_val",getResources().getString(R.string.prouesse_def));
        Integer prouesse = Integer.parseInt(prouesse_str);

        String prouesse_attrib_str = settings.getString("prouesse_attrib",getResources().getString(R.string.prouesse_attrib_def));
        Integer prouesse_attrib = Integer.parseInt(prouesse_attrib_str);

        list_att.set(prouesse_attrib-1,prouesse+list_att.get(prouesse_attrib-1));

        int max_att_jet = list_att_base.get(0) + 3*thor + predi  + val_dex + magic*magic_val + neuf_m + epic_val;


        int length = list_att.size();
        n_att = length;

        for(int i=0; i<n_att; i++){
            list_att.set(i,list_att.get(i) + 3*thor + 1*predi  + val_dex + magic*magic_val + neuf_m + epic_val);
        }


        if (settings.getBoolean("rapid_enchant_switch",getResources().getBoolean(R.bool.rapid_enchant_switch_def))) {
            n_att=n_att+1;
            list_att.add(0,max_att_jet);
        }



        if (settings.getBoolean("tir_rapide",getResources().getBoolean(R.bool.tir_rapide_switch_def))) {
            n_att=n_att+1;
            list_att.add(0,max_att_jet);
            for(int i=0; i<n_att; i++){
                list_att.set(i,list_att.get(i) -2);
            }
        }

        if (settings.getBoolean("viser",getResources().getBoolean(R.bool.viser_switch_def))) {
            for(int i=0; i<n_att; i++){
                String malus_viser_str = settings.getString("viser_val",getResources().getString(R.string.viser_val_def));
                Integer malus_viser = Integer.parseInt(malus_viser_str);
                list_att.set(i,list_att.get(i) -malus_viser);
            }
        }

        for(int i=0; i<n_att; i++){
            list_att.set(i,list_att.get(i) + all_jet[i]);
        }

        StringBuilder affichage_att = new StringBuilder();
        String delim2="  /  ";
        for (Integer each : list_att) {
            affichage_att.append(delim2).append(String.valueOf(each));
        }

        String affichage_att_str = affichage_att.substring(delim2.length()-1);

        TextView text_curent_att = (TextView) findViewById(R.id.jet_att_ajout_textbox);
        text_curent_att.setVisibility(View.VISIBLE);
        text_curent_att.setText(affichage_att_str);

    }


    private void set_grid() {
        //setContentView(R.layout.grid_map);  C'est ca qui efface toute la view
        GridView grid = (GridView) findViewById(R.id.grid_dice_id);

        // Instance of ImageAdapter Class

        Integer jet1= rand(20),jet2= rand(20),jet3= rand(20),jet4= rand(20),jet5 = rand(20) ,jet6 = rand(20);
        String jet1_str=String.valueOf(jet1),jet2_str=String.valueOf(jet2),jet3_str=String.valueOf(jet3),
                jet4_str=String.valueOf(jet4),jet5_str=String.valueOf(jet5),jet6_str=String.valueOf(jet6);

        switch (n_att) {
            case 6:
                List<String> dice_list6 = new ArrayList<>(Arrays.asList("d20_" + jet1_str, "d20_" + jet2_str, "d20_" + jet3_str, "d20_" + jet4_str, "d20_" + jet5_str, "d20_" + jet6_str));
                grid.setAdapter(new ImageAdapter(this, dice_list6));
                GridView dice_lin6 = (GridView) findViewById(R.id.grid_dice_id);
                dice_lin6.setNumColumns(6);

                break;
            case 5:
                List<String> dice_list5 = new ArrayList<>(Arrays.asList("d20_" + jet1_str, "d20_" + jet2_str, "d20_" + jet3_str, "d20_" + jet4_str, "d20_" + jet5_str));
                grid.setAdapter(new ImageAdapter(this, dice_list5));
                GridView dice_lin5 = (GridView) findViewById(R.id.grid_dice_id);
                dice_lin5.setNumColumns(5);
                dice_lin5.setPadding(25,0,0,0);
                break;
            case 4:
                List<String> dice_list4 = new ArrayList<>(Arrays.asList("d20_" + jet1_str, "d20_" + jet2_str, "d20_" + jet3_str, "d20_" + jet4_str));
                grid.setAdapter(new ImageAdapter(this, dice_list4));
                GridView dice_lin4 = (GridView) findViewById(R.id.grid_dice_id);
                dice_lin4.setNumColumns(4);
                dice_lin4.setPadding(65,0,0,0);
                break;

        }
        jet_att_print(jet1,jet2,jet3,jet4,jet5,jet6);




    }

    private Integer rand(Integer dice) {
        Random r = new Random();
        int jet = r.nextInt(dice)+1;
        return jet;
    }

    private void set_checkbox_hit() {
        LinearLayout check_lin = (LinearLayout) findViewById(R.id.grid_check_id);

        check_lin.setPadding(-100*n_att+650,0,0,0);
        check_lin.setWeightSum(n_att);

        switch(n_att) {
            case 6:
                CheckBox checkBox6 = (CheckBox) findViewById(R.id.checkBox6);
                checkBox6.setVisibility(View.VISIBLE);

            case 5:
                CheckBox checkBox5 = (CheckBox) findViewById(R.id.checkBox5);
                checkBox5.setVisibility(View.VISIBLE);

            case 4:
                CheckBox checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
                CheckBox checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
                CheckBox checkBox3 = (CheckBox) findViewById(R.id.checkBox3);
                CheckBox checkBox4 = (CheckBox) findViewById(R.id.checkBox4);
                checkBox1.setVisibility(View.VISIBLE);
                checkBox2.setVisibility(View.VISIBLE);
                checkBox3.setVisibility(View.VISIBLE);
                checkBox4.setVisibility(View.VISIBLE);


        }
    }

    private void set_checkbox_crit() {
        LinearLayout check_lin = (LinearLayout) findViewById(R.id.grid_check_crit_id);

        check_lin.setPadding(-100*n_att+650,0,0,0);
        check_lin.setWeightSum(n_att);

        switch(n_att) {
            case 6:
                CheckBox checkBox6 = (CheckBox) findViewById(R.id.checkBox6_crit);
                checkBox6.setVisibility(View.VISIBLE);

            case 5:
                CheckBox checkBox5 = (CheckBox) findViewById(R.id.checkBox5_crit);
                checkBox5.setVisibility(View.VISIBLE);

            case 4:
                CheckBox checkBox1 = (CheckBox) findViewById(R.id.checkBox1_crit);
                CheckBox checkBox2 = (CheckBox) findViewById(R.id.checkBox2_crit);
                CheckBox checkBox3 = (CheckBox) findViewById(R.id.checkBox3_crit);
                CheckBox checkBox4 = (CheckBox) findViewById(R.id.checkBox4_crit);
                checkBox1.setVisibility(View.VISIBLE);
                checkBox2.setVisibility(View.VISIBLE);
                checkBox3.setVisibility(View.VISIBLE);
                checkBox4.setVisibility(View.VISIBLE);


        }
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

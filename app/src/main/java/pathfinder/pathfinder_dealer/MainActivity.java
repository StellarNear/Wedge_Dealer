package pathfinder.pathfinder_dealer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    boolean shouldExecuteOnResume;

    Integer n_att;
    boolean firstDmgRoll;
    String all_dices_str="";

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
                    set_multishot_text();
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

                View dmg_text = findViewById(R.id.dmg_text);
                View dmg_elem = findViewById(R.id.grid_element);
                View dmg_all_dmg = findViewById(R.id.all_dmg);
                View dmg_all_range = findViewById(R.id.all_dmg_range);
                View dmg_all_percent_view =  findViewById(R.id.all_dmg_percent);

                dmg_text.setVisibility(View.INVISIBLE);
                dmg_elem.setVisibility(View.INVISIBLE);
                dmg_all_dmg.setVisibility(View.INVISIBLE);
                dmg_all_range.setVisibility(View.INVISIBLE);
                dmg_all_percent_view.setVisibility(View.INVISIBLE);
                all_dices_str="";

                CheckBox hit_1 = (CheckBox) findViewById(R.id.checkBox1) ;
                CheckBox hit_2 = (CheckBox) findViewById(R.id.checkBox2) ;
                CheckBox hit_3 = (CheckBox) findViewById(R.id.checkBox3) ;
                CheckBox hit_4 = (CheckBox) findViewById(R.id.checkBox4) ;
                CheckBox hit_5 = (CheckBox) findViewById(R.id.checkBox5) ;
                CheckBox hit_6 = (CheckBox) findViewById(R.id.checkBox6) ;
                List<CheckBox> all_check_hit = new ArrayList<>(Arrays.asList( hit_1,hit_2,hit_3,hit_4,hit_5,hit_6));
                for (CheckBox box : all_check_hit )  {
                    box.setChecked(false);
                }

                CheckBox hit_1_crit = (CheckBox) findViewById(R.id.checkBox1_crit) ;
                CheckBox hit_2_crit = (CheckBox) findViewById(R.id.checkBox2_crit) ;
                CheckBox hit_3_crit = (CheckBox) findViewById(R.id.checkBox3_crit) ;
                CheckBox hit_4_crit = (CheckBox) findViewById(R.id.checkBox4_crit) ;
                CheckBox hit_5_crit = (CheckBox) findViewById(R.id.checkBox5_crit) ;
                CheckBox hit_6_crit = (CheckBox) findViewById(R.id.checkBox6_crit) ;
                List<CheckBox> all_check_crit = new ArrayList<>(Arrays.asList( hit_1_crit,hit_2_crit,hit_3_crit,hit_4_crit,hit_5_crit,hit_6_crit));
                for (CheckBox box : all_check_crit )  {
                    box.setChecked(false);
                }

                firstDmgRoll=true;


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

                affich_damage();



            }});

        FloatingActionButton fab_dmg_det = (FloatingActionButton) findViewById(R.id.fab_damage_detail);
        fab_dmg_det.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                display_dmg_detail();
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


    private void set_multishot_text() {
        StringBuilder affichage_multi = new StringBuilder();
        String delim = "         ";
        for (int i=0; i<n_att; i++) {
            affichage_multi.append(delim+"x"+getString(R.string.multi_value));
        }

        String affichage_multi_str = affichage_multi.substring(delim.length());


        TextView multishot_view = (TextView) findViewById(R.id.multishot);
        multishot_view.setText(affichage_multi_str);
        multishot_view.setVisibility(View.VISIBLE);
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
        String delim2=" / ";
        for (Integer each : list_att) {
            affichage_att.append(delim2).append("+"+String.valueOf(each));
        }

        String affichage_att_str = affichage_att.substring(delim2.length());


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

        String affichage_att_str = affichage_att.substring(delim2.length());

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
                grid.setAdapter(new ImageAdapter(this, dice_list6,160));
                GridView dice_lin6 = (GridView) findViewById(R.id.grid_dice_id);
                dice_lin6.setNumColumns(6);
                dice_lin6.setPadding(-5,0,0,0);
                break;
            case 5:
                List<String> dice_list5 = new ArrayList<>(Arrays.asList("d20_" + jet1_str, "d20_" + jet2_str, "d20_" + jet3_str, "d20_" + jet4_str, "d20_" + jet5_str));
                grid.setAdapter(new ImageAdapter(this, dice_list5,160));
                GridView dice_lin5 = (GridView) findViewById(R.id.grid_dice_id);
                dice_lin5.setNumColumns(5);
                dice_lin5.setPadding(-25,0,0,0);
                break;
            case 4:
                List<String> dice_list4 = new ArrayList<>(Arrays.asList("d20_" + jet1_str, "d20_" + jet2_str, "d20_" + jet3_str, "d20_" + jet4_str));
                grid.setAdapter(new ImageAdapter(this, dice_list4,160));
                GridView dice_lin4 = (GridView) findViewById(R.id.grid_dice_id);
                dice_lin4.setNumColumns(4);
                dice_lin4.setPadding(-75,0,0,0);
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

    private void affich_damage() {

        View dmg_view = findViewById(R.id.dmg_text);
        dmg_view.setVisibility(View.VISIBLE);

        GridView grid_element = (GridView) findViewById(R.id.grid_element);
        grid_element.setVisibility(View.VISIBLE);
        // Instance of ImageAdapter Class
        if (firstDmgRoll) {
            //dmg_all_view,dmg_all_range_view,dmg_all_percent_view
            String[] triple_text_dmg= new String[] {"","",""};
            Integer n_type_dmg=1;
            List<String> list_element = new ArrayList<>(Arrays.asList("physique"));
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if (settings.getBoolean("feu_intense_switch",getResources().getBoolean(R.bool.feu_intense_switch_def))) {
                list_element.add("feu");
                n_type_dmg++;
            }
            if (settings.getBoolean("foudre_intense_switch",getResources().getBoolean(R.bool.foudre_intense_switch_def))) {
                list_element.add("foudre");
                n_type_dmg++;
            }
            if (settings.getBoolean("froid_intense_switch",getResources().getBoolean(R.bool.froid_intense_switch_def))) {
                list_element.add("froid");
                n_type_dmg++;
            }

            TextView dmg_all_view = (TextView) findViewById(R.id.all_dmg);
            dmg_all_view.setVisibility(View.VISIBLE);
            TextView dmg_all_range_view = (TextView) findViewById(R.id.all_dmg_range);
            dmg_all_range_view.setVisibility(View.VISIBLE);
            TextView dmg_all_percent_view = (TextView) findViewById(R.id.all_dmg_percent);
            dmg_all_percent_view.setVisibility(View.VISIBLE);

            grid_element.setAdapter(new ImageAdapter(this, list_element, 75));

            if (n_type_dmg.equals(3)){
                grid_element.setPadding(240, 0, 0, 0);
                dmg_all_view.setPadding(-25, 0, 0, 0);
                dmg_all_range_view.setPadding(-25, 0, 0, 0);
            }  else if (n_type_dmg.equals(2)) {
                grid_element.setPadding(370, 0, 0, 0);
                dmg_all_view.setPadding(-50, 0, 0, 0);
                dmg_all_range_view.setPadding(-50, 0, 0, 0);
            } else if (n_type_dmg.equals(1)) {
                grid_element.setPadding(470, 0, 0, 0);
                dmg_all_view.setPadding(0, 0, 0, 0);
                dmg_all_range_view.setPadding(0, 0, 0, 0);
            }  else  {
                grid_element.setPadding(0,0,0,0);
                dmg_all_view.setPadding(0, 0, 0, 0);
                dmg_all_range_view.setPadding(0, 0, 0, 0);
            }
            triple_text_dmg = calcul_damage();
            dmg_all_view.setText(Html.fromHtml(triple_text_dmg[0]));
            dmg_all_range_view.setText(Html.fromHtml(triple_text_dmg[1]));
            dmg_all_percent_view.setText(Html.fromHtml(triple_text_dmg[2]));

            firstDmgRoll=false;
        }

    }

    private void display_dmg_detail(){
        Intent intent = new Intent(this, DisplayDamageDetail.class);

        while (all_dices_str.substring(all_dices_str.length() - 1, all_dices_str.length()).equals(",") || all_dices_str.substring(all_dices_str.length() - 1, all_dices_str.length()).equals(";"))
        {
            all_dices_str = all_dices_str.substring(0, all_dices_str.length() - 1);
        }

        Log.d("STATE",all_dices_str );
        intent.putExtra("all_dices_str",all_dices_str);


        startActivity(intent);

    }


    private String[] calcul_damage() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Integer multi_val=1;
        if (settings.getBoolean("feu_nourri_switch",getResources().getBoolean(R.bool.feu_nourri_switch_def)))  {
            multi_val = Integer.parseInt(getString(R.string.multi_value));
        }

        /////////// Degat commun attaques phy
        Integer thor;
        Integer neuf_m;
        Integer magic;
        Integer composi;
        Integer sumPhy,sumFeu,sumFoudre,sumFroid,minPhy,maxPhy,minFeu,maxFeu,minFoudre,maxFoudre,minFroid,maxFroid;
        sumPhy=sumFeu=sumFoudre=sumFroid=minPhy=maxPhy=minFeu=maxFeu=minFoudre=maxFoudre=minFroid=maxFroid=0;


        if (settings.getBoolean("thor_switch",getResources().getBoolean(R.bool.thor_switch_def))) {
            thor = 1;
        }  else { thor = 0; }
        if (settings.getBoolean("neuf_m_switch",getResources().getBoolean(R.bool.neuf_m_switch_def)))  {
            neuf_m = 1;
        }    else { neuf_m = 0; }
        if (settings.getBoolean("magic_switch",getResources().getBoolean(R.bool.magic_switch_def)))  {
            magic = 1;
        }    else { magic = 0; }
        if (settings.getBoolean("composite_switch",getResources().getBoolean(R.bool.composite_switch_def)))  {
            composi = 1;
        }    else { composi = 0; }


        String magic_val_str = settings.getString("magic_val",getResources().getString(R.string.magic_val_def));
        Integer magic_val = Integer.parseInt(magic_val_str);

        String epic_dmg_str = settings.getString("epic_dmg_val",getResources().getString(R.string.epic_dmg_val_def));
        Integer epic_dmg_val = Integer.parseInt(epic_dmg_str);

        Integer bonus_viser=0;
        if (settings.getBoolean("viser",getResources().getBoolean(R.bool.viser_switch_def))) {
            String malus_viser_str = settings.getString("viser_val",getResources().getString(R.string.viser_val_def));
            bonus_viser = 2*Integer.parseInt(malus_viser_str);
        }

        Integer ajout_dmg = 3*thor+neuf_m+magic*magic_val+4*composi+epic_dmg_val+bonus_viser;


        /////////// Lancement des attaques

        CheckBox hit_1 = (CheckBox) findViewById(R.id.checkBox1) ;
        CheckBox hit_2 = (CheckBox) findViewById(R.id.checkBox2) ;
        CheckBox hit_3 = (CheckBox) findViewById(R.id.checkBox3) ;
        CheckBox hit_4 = (CheckBox) findViewById(R.id.checkBox4) ;
        CheckBox hit_5 = (CheckBox) findViewById(R.id.checkBox5) ;
        CheckBox hit_6 = (CheckBox) findViewById(R.id.checkBox6) ;
        CheckBox[] all_check_hit = new CheckBox[]{hit_1,hit_2,hit_3,hit_4,hit_5,hit_6};
        CheckBox hit_1_crit = (CheckBox) findViewById(R.id.checkBox1_crit) ;
        CheckBox hit_2_crit = (CheckBox) findViewById(R.id.checkBox2_crit) ;
        CheckBox hit_3_crit = (CheckBox) findViewById(R.id.checkBox3_crit) ;
        CheckBox hit_4_crit = (CheckBox) findViewById(R.id.checkBox4_crit) ;
        CheckBox hit_5_crit = (CheckBox) findViewById(R.id.checkBox5_crit) ;
        CheckBox hit_6_crit = (CheckBox) findViewById(R.id.checkBox6_crit) ;
        CheckBox[] all_check_crit = new CheckBox[]{hit_1_crit,hit_2_crit,hit_3_crit,hit_4_crit,hit_5_crit,hit_6_crit};

        ///robustesse un crit n'est pas un crit si il n'a pas touché
        for (int i=0;i<=all_check_hit.length-1;i++) {
            if (!all_check_hit[i].isChecked() && all_check_crit[i].isChecked()) {
                all_check_crit[i].setChecked(false);
            }
        }

        /// debut des degat des fleches
        for (int i=0;i<=all_check_hit.length-1;i++)  {
            if (all_check_hit[i].isChecked() && !all_check_crit[i].isChecked())  /// pour chaque coup non crit
            {
                for (int j=0;j<=multi_val-1;j++) {
                    //////degat phy
                    Integer jet = rand(8);
                    all_dices_str += "d8_" + String.valueOf(jet) + ",";
                    sumPhy += jet + ajout_dmg;
                    minPhy += 1 + ajout_dmg;
                    maxPhy += 8 + ajout_dmg;

                    //////degat feu
                    if (settings.getBoolean("feu_intense_switch", getResources().getBoolean(R.bool.feu_intense_switch_def))) {
                        Integer jet_feu = rand(6);
                        Integer jet2_feu = rand(6);
                        all_dices_str += "feu_d6_" + String.valueOf(jet_feu) + ",feu_d6_" + String.valueOf(jet2_feu) + ",";
                        sumFeu += jet_feu + jet2_feu;
                        minFeu += 1 + 1;
                        maxFeu += 6 + 6;
                    }

                    //////degat foudre
                    if (settings.getBoolean("foudre_intense_switch", getResources().getBoolean(R.bool.foudre_intense_switch_def))) {
                        Integer jet_foudre = rand(6);
                        Integer jet2_foudre = rand(6);
                        all_dices_str += "foudre_d6_" + String.valueOf(jet_foudre) + ",foudre_d6_" + String.valueOf(jet2_foudre) + ",";
                        sumFoudre += jet_foudre + jet2_foudre;
                        minFoudre += 1 + 1;
                        maxFoudre += 6 + 6;
                    }

                    //////degat froid
                    if (settings.getBoolean("froid_intense_switch", getResources().getBoolean(R.bool.froid_intense_switch_def))) {
                        Integer jet_froid = rand(6);
                        Integer jet2_froid = rand(6);
                        all_dices_str += "froid_d6_" + String.valueOf(jet_froid) + ",froid_d6_" + String.valueOf(jet2_froid) + ",";
                        sumFroid += jet_froid + jet2_froid;
                        minFroid += 1 + 1;
                        maxFroid += 6 + 6;
                    }
                    if (!all_dices_str.substring(all_dices_str.length()-1,all_dices_str.length()).equals(";")) {all_dices_str +=";";}
                }

            }
            if (all_check_hit[i].isChecked() && all_check_crit[i].isChecked())  /// pour chaque coup critique
            {
                //// degat du crit
                //////degat phy
                Integer jet_crit=rand(8);
                all_dices_str+="d8_"+String.valueOf(jet_crit)+",";
                sumPhy +=  (jet_crit + ajout_dmg)*3;
                minPhy+=(1+ ajout_dmg)*3;
                maxPhy+=(8+ ajout_dmg)*3;

                //////degat feu
                if (settings.getBoolean("feu_intense_switch",getResources().getBoolean(R.bool.feu_intense_switch_def))) {
                    Integer jet_feu=rand(6);
                    Integer jet2_feu=rand(6);
                    Integer jet3_feu=rand(10);
                    Integer jet4_feu=rand(10);
                    all_dices_str+="feu_d6_"+String.valueOf(jet_feu)+",feu_d6_"+String.valueOf(jet2_feu)+",feu_d10_"+String.valueOf(jet3_feu)+",feu_d10_"+String.valueOf(jet4_feu)+",";
                    sumFeu+=jet_feu+jet2_feu+jet3_feu+jet4_feu;
                    minFeu+=1+1+1+1;
                    maxFeu+=6+6+10+10;
                }

                //////degat foudre
                if (settings.getBoolean("foudre_intense_switch",getResources().getBoolean(R.bool.foudre_intense_switch_def))) {
                    Integer jet_foudre=rand(6);
                    Integer jet2_foudre=rand(6);
                    Integer jet3_foudre=rand(10);
                    Integer jet4_foudre=rand(10);
                    all_dices_str+="foudre_d6_"+String.valueOf(jet_foudre)+",foudre_d6_"+String.valueOf(jet2_foudre)+",foudre_d10_"+String.valueOf(jet3_foudre)+",foudre_d10_"+String.valueOf(jet4_foudre)+",";
                    sumFoudre+=jet_foudre+jet2_foudre+jet3_foudre+jet4_foudre;
                    minFoudre+= 1+1+1+1;
                    maxFoudre+=6+6+10+10;
                }

                //////degat froid
                if (settings.getBoolean("froid_intense_switch",getResources().getBoolean(R.bool.froid_intense_switch_def))) {
                    Integer jet_froid=rand(6);
                    Integer jet2_froid=rand(6);
                    Integer jet3_froid=rand(6);
                    Integer jet4_froid=rand(6);
                    all_dices_str+="froid_d6_"+String.valueOf(jet_froid)+",froid_d6_"+String.valueOf(jet2_froid)+",froid_d10_"+String.valueOf(jet3_froid)+",froid_d10_"+String.valueOf(jet4_froid)+",";
                    sumFroid+=jet_froid+jet2_froid+jet3_froid+jet4_froid;
                    minFroid+=1+1+1+1;
                    maxFroid+=6+6+10+10;
                }
                if (!all_dices_str.substring(all_dices_str.length()-1,all_dices_str.length()).equals(";")) {all_dices_str +=";";}
                //// on voit si il reste des fleche normale à tirer (les multishot ne crit pas)
                if (multi_val > 1) {
                    for (int j = 0; j <= multi_val - 2; j++) {
                        //////degat phy
                        Integer jet = rand(8);
                        all_dices_str += "d8_" + String.valueOf(jet) + ",";
                        sumPhy += jet + ajout_dmg;
                        minPhy += 1 + ajout_dmg;
                        maxPhy += 8 + ajout_dmg;

                        //////degat feu
                        if (settings.getBoolean("feu_intense_switch", getResources().getBoolean(R.bool.feu_intense_switch_def))) {
                            Integer jet_feu = rand(6);
                            Integer jet2_feu = rand(6);
                            all_dices_str += "feu_d6_" + String.valueOf(jet_feu) + ",feu_d6_" + String.valueOf(jet2_feu) + ",";
                            sumFeu += +jet_feu + jet2_feu;
                            minFeu += 1 + 1;
                            maxFeu += 6 + 6;
                        }

                        //////degat foudre
                        if (settings.getBoolean("foudre_intense_switch", getResources().getBoolean(R.bool.foudre_intense_switch_def))) {
                            Integer jet_foudre = rand(6);
                            Integer jet2_foudre = rand(6);
                            all_dices_str += "foudre_d6_" + String.valueOf(jet_foudre) + ",foudre_d6_" + String.valueOf(jet2_foudre) + ",";
                            sumFoudre += jet_foudre + jet2_foudre;
                            minFoudre += 1 + 1;
                            maxFoudre += 6 + 6;
                        }

                        //////degat froid
                        if (settings.getBoolean("froid_intense_switch", getResources().getBoolean(R.bool.froid_intense_switch_def))) {
                            Integer jet_froid = rand(6);
                            Integer jet2_froid = rand(6);
                            all_dices_str += "froid_d6_" + String.valueOf(jet_froid) + ",froid_d6_" + String.valueOf(jet2_froid) + ",";
                            sumFroid += jet_froid + jet2_froid;
                            minFroid += 1 + 1;
                            maxFroid += 6 + 6;
                        }
                        if (!all_dices_str.substring(all_dices_str.length()-1,all_dices_str.length()).equals(";")) {all_dices_str +=";";}
                    }

                }

            }

        }

        ///////////////////////////////////////////////////////////////////////////////////////////
        //partie à modifié pour s'integer et construire les fleche une a une et au final return la chaine de str html à affihcer
        ///////////////////////////////////////////////////////////////////////////////////////////

        String text_all_dmg="";
        String text_all_dmg_range="";
        String text_all_dmg_percent="";

        Integer phy_percent =0;
        Integer feu_percent =0;
        Integer foudre_percent =0;
        Integer froid_percent =0;

        String sep_html = "&#160&#160";
        String sep_html2 = "&#160&#160&#160";
        String sep_html3 = "&#160&#160&#160&#160&#160&#160&#160&#160&#160&#160";

        Integer phy_ecart =  maxPhy - minPhy;
        if(!phy_ecart.equals(0)) {
            phy_percent = 100*(sumPhy - minPhy) / phy_ecart;
        }
        String text_dmg_phy = "<font color=#000000>"+sumPhy+"</font>";
        text_all_dmg+=sep_html+text_dmg_phy;
        String text_dmg_phy_range = "<font color=#000000>["+minPhy+"-"+maxPhy+"]</font>";
        text_all_dmg_range += sep_html2+text_dmg_phy_range ;
        String text_dmg_phy_percent = "<font color=#000000>"+phy_percent +"%</font>";
        text_all_dmg_percent += sep_html3+text_dmg_phy_percent;

        Integer feu_ecart =  maxFeu - minFeu;
        if(!feu_ecart.equals(0)) {
            feu_percent = 100*(sumFeu - minFeu) / feu_ecart;
            Log.d("STATE feu",String.valueOf(feu_percent) );
        }
        String text_dmg_feu = "<font color=#DF0101>"+sumFeu+"</font>";
        String text_dmg_feu_range = "<font color=#DF0101>["+minFeu+"-"+maxFeu+"]</font>";
        String text_dmg_feu_percent = "<font color=#DF0101>"+feu_percent +"%</font>";


        Integer foudre_ecart =  maxFoudre - minFoudre;
        if(!foudre_ecart.equals(0)) {
            foudre_percent = 100*(sumFoudre - minFoudre) / foudre_ecart;
            Log.d("STATE foudre",String.valueOf(foudre_percent) );
        }
        String text_dmg_foudre = "<font color=#A9D0F5>"+sumFoudre+"</font>";
        String text_dmg_foudre_range = "<font color=#A9D0F5>["+minFoudre+"-"+maxFoudre+"]</font>";
        String text_dmg_foudre_percent = "<font color=#A9D0F5>"+foudre_percent +"%</font>";


        Integer froid_ecart =  maxFroid - minFroid;
        if(!froid_ecart.equals(0)) {
            froid_percent = 100*(sumFroid - minFroid) / froid_ecart;
            Log.d("STATE froid",String.valueOf(froid_percent) );
        }
        String text_dmg_froid = "<font color=#0404B4>"+sumFroid+"</font>";
        String text_dmg_froid_range = "<font color=#0404B4>["+minFroid+"-"+maxFroid+"]</font>";
        String text_dmg_froid_percent = "<font color=#0404B4>"+froid_percent +"%</font>";


        if (settings.getBoolean("feu_intense_switch", getResources().getBoolean(R.bool.froid_intense_switch_def))) {
            text_all_dmg+=sep_html+text_dmg_feu;
            text_all_dmg_range += sep_html2+text_dmg_feu_range ;
            text_all_dmg_percent += sep_html3+text_dmg_feu_percent;
        }
        if (settings.getBoolean("foudre_intense_switch", getResources().getBoolean(R.bool.foudre_intense_switch_def))) {
            text_all_dmg+=sep_html+text_dmg_foudre;
            text_all_dmg_range += sep_html2+text_dmg_foudre_range ;
            text_all_dmg_percent += sep_html3+text_dmg_foudre_percent;
        }
        if (settings.getBoolean("froid_intense_switch", getResources().getBoolean(R.bool.froid_intense_switch_def))) {

            text_all_dmg_percent+= sep_html3+text_dmg_froid_percent;
            text_all_dmg_range += sep_html2+text_dmg_froid_range ;
            text_all_dmg+=sep_html+text_dmg_froid;
        }

        text_all_dmg=text_all_dmg.substring(sep_html.length());
        text_all_dmg_range=text_all_dmg_range.substring(sep_html2.length());
        text_all_dmg_percent=text_all_dmg_percent.substring(sep_html3.length());

        Log.d("STATE text_all_dmg",text_all_dmg );
        Log.d("STATE text_all_dmg_range",text_all_dmg_range );
        Log.d("STATE text_all_dmg_percent",text_all_dmg_percent );


        return new String[] {text_all_dmg, text_all_dmg_range , text_all_dmg_percent};

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

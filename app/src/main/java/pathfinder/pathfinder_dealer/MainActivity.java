package pathfinder.pathfinder_dealer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    boolean shouldExecuteOnResume;
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

        jet_att_print("base");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Snackbar.make(view, "Replace with your own action "+"lol"+settings.getString("jet_att",getResources().getString(R.string.jet_att_def)), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                //set_grid();

                jet_att_print("ajout");


                if (settings.getBoolean("feu_nourri_switch",getResources().getBoolean(R.bool.feu_nourri_switch_def)))  {
                    TextView multi_text = (TextView) findViewById(R.id.multishot);
                    multi_text.setVisibility(View.VISIBLE);
                }


                TextView hit_text = (TextView) findViewById(R.id.hit_text);
                hit_text.setVisibility(View.VISIBLE);


                LinearLayout check_lin = (LinearLayout) findViewById(R.id.grid_check_id);

                // 6 attaques
                check_lin.setPadding(50,0,0,0);
                check_lin.setWeightSum(6);
                CheckBox checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
                CheckBox checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
                CheckBox checkBox3 = (CheckBox) findViewById(R.id.checkBox3);
                CheckBox checkBox4 = (CheckBox) findViewById(R.id.checkBox4);
                CheckBox checkBox5 = (CheckBox) findViewById(R.id.checkBox5);
                CheckBox checkBox6 = (CheckBox) findViewById(R.id.checkBox6);
                checkBox1.setVisibility(View.VISIBLE);
                checkBox2.setVisibility(View.VISIBLE);
                checkBox3.setVisibility(View.VISIBLE);
                checkBox4.setVisibility(View.VISIBLE);
                checkBox5.setVisibility(View.VISIBLE);
                checkBox6.setVisibility(View.VISIBLE);

                /* 5 attaques
                check_lin.setPadding(120,0,0,0);
                check_lin.setWeightSum(5);
                CheckBox checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
                CheckBox checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
                CheckBox checkBox3 = (CheckBox) findViewById(R.id.checkBox3);
                CheckBox checkBox4 = (CheckBox) findViewById(R.id.checkBox4);
                CheckBox checkBox5 = (CheckBox) findViewById(R.id.checkBox5);

                checkBox1.setVisibility(View.VISIBLE);
                checkBox2.setVisibility(View.VISIBLE);
                checkBox3.setVisibility(View.VISIBLE);
                checkBox4.setVisibility(View.VISIBLE);
                checkBox5.setVisibility(View.VISIBLE);
                */
                /*4 attaques
                check_lin.setPadding(250,0,0,0);
                check_lin.setWeightSum(4);
                CheckBox checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
                CheckBox checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
                CheckBox checkBox3 = (CheckBox) findViewById(R.id.checkBox3);
                CheckBox checkBox4 = (CheckBox) findViewById(R.id.checkBox4);

                checkBox1.setVisibility(View.VISIBLE);
                checkBox2.setVisibility(View.VISIBLE);
                checkBox3.setVisibility(View.VISIBLE);
                checkBox4.setVisibility(View.VISIBLE);
                */

            }});
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(shouldExecuteOnResume){
            jet_att_print("base");
        } else {
            shouldExecuteOnResume = true;
        }
    }

    private void jet_att_print(String mode) {

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
        int n_att = length;

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
        for (Integer each : list_att) {
            affichage_att.append(" / +").append(String.valueOf(each));
        }

        String affichage_att_str = affichage_att.substring(delim.length()+1);

        if (mode.equals("base")) {
            TextView text_curent_att = (TextView) findViewById(R.id.jet_att_textbox);
            text_curent_att.setText( n_att + " attaques\n" +affichage_att_str);
        }   else if (mode.equals("ajout")) {
            TextView text_curent_att = (TextView) findViewById(R.id.jet_att_ajout_textbox);
            text_curent_att.setVisibility(View.VISIBLE);
            text_curent_att.setText(affichage_att_str);
        }
    }

    private void set_grid() {
        //setContentView(R.layout.grid_map);  C'est ca qui efface toute la view
        GridView grid = (GridView) findViewById(R.id.grid_dice_id);
        grid.setNumColumns(6);
        // Instance of ImageAdapter Class
        grid.setAdapter(new ImageAdapter(this));

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

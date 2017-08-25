package pathfinder.pathfinder_dealer;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisplayDamageDetail extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);
        setContentView(R.layout.damagedetail);

        display_dmg_detail();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_damage_detail_ondetlay);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }});


    }


    private void display_dmg_detail(){
        GridLayout grid_1 = (GridLayout) findViewById(R.id.GridLayout1);
        List<String> dice_list1 = new ArrayList<>(Arrays.asList("d8_1", "feu_d6_1","feu_d6_1","foudre_d6_1","foudre_d6_1","froid_d6_1","froid_d6_1","froid_d10_1","froid_d10_1"));
        //grid_1. (new ImageAdapter(this, dice_list1,160));


    }

}

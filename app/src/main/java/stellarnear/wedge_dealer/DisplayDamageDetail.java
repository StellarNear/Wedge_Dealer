package stellarnear.wedge_dealer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import java.util.Arrays;
import java.util.List;

import stellarnear.stellarnear.R;

public class DisplayDamageDetail extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);
        setContentView(R.layout.damagedetail);

        String all_dices_str = getIntent().getStringExtra("all_dices_str");
        display_dmg_detail(all_dices_str);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_damage_detail_ondetlay);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }});


    }

    private void display_dmg_detail(String param_all_dices_str){
        ScrollView  scroll = (ScrollView) findViewById(R.id.scroll) ;
        LinearLayout arrows = new LinearLayout(getApplicationContext());
        arrows.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(arrows);

        List<String> list_dice_per_arrow = Arrays.asList(param_all_dices_str.split(";"));
        for (String arrow_str : list_dice_per_arrow) {
            LinearLayout arrow_layout = new LinearLayout(getApplicationContext());
            arrow_layout.setOrientation(LinearLayout.HORIZONTAL);
            List<String> dices_per_arrow = Arrays.asList(arrow_str.split(","));
            for ( String dice_str : dices_per_arrow) {
                ImageView dice = new ImageView(getApplicationContext());
                //dice.setImageResource(ImageAdapter.dice_map.get(dice_str));
                arrow_layout.addView(dice);
            }
            arrows.addView(arrow_layout);
        }

    }

}

package stellarnear.wedge_dealer.SettingsFraments;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stellarnear.wedge_dealer.MainActivity;
import stellarnear.wedge_dealer.Perso.Perso;
import stellarnear.wedge_dealer.R;
import stellarnear.wedge_dealer.Stats.Stat;
import stellarnear.wedge_dealer.Stats.StatsList;
import stellarnear.wedge_dealer.Tools;

public class DisplayStatsScreenFragmentDmgSubManager {
    private Perso wedge = MainActivity.wedge;
    private DisplayStatsScreenFragmentDmgChartMaker chartMaker;
    private PieChart pieChart;


    private Context mC;
    private View mainView;
    private List<String> listElems= Arrays.asList("","fire","shock","frost");
    private Map<String,CheckBox> mapElemCheckbox=new HashMap<>();
    private List<Stat> selectedStats=new ArrayList<>();
    private int infoTxtSize = 12;


    private Tools tools=new Tools();


    public DisplayStatsScreenFragmentDmgSubManager(View mainView, Context mC) {
        this.mainView = mainView;
        this.mC = mC;
    }

    public void initSubdetails() {
        final ImageView text = mainView.findViewById(R.id.button_chart_dmg_sub_text);
        final ImageView graph = mainView.findViewById(R.id.button_chart_dmg_sub_graph);
        text.animate().alpha(0).setStartDelay(0).setDuration(0).scaleX(0.5f).scaleY(0.5f).start();
        //175 ms de transition du panneau
        text.animate().alpha(1).setStartDelay(175).scaleX(1f).scaleY(1f).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        graph.animate().alpha(0).setStartDelay(0).setDuration(0).translationX(100).start();
        graph.animate().setStartDelay(1175).setDuration(1000).alpha(1).translationX(0).setInterpolator(new DecelerateInterpolator()).start();

        setClickSubListener(text);
        setClickSubListener(graph);

        addInfos();
    }

    private void addInfos(){
        final LinearLayout mainLin = mainView.findViewById(R.id.chart_dmg_sub_text);
        mainLin.removeAllViews();
        TextView t = new TextView(mC);
        t.setText("Tout");
        t.setTextSize(22);
        t.setTextColor(Color.BLACK);
        mainLin.addView(t);

        TextView t2 = new TextView(mC);
        t2.setText("Total nimp : 4465");
        t2.setTextSize(18);
        t2.setTextColor(Color.YELLOW);
        mainLin.addView(t2);

        TextView t3 = new TextView(mC);
        t3.setText("total feu : 455");
        t3.setTextSize(18);
        t3.setTextColor(Color.RED);
        mainLin.addView(t3);

        TextView t4 = new TextView(mC);
        t4.setText("total frost : 5995");
        t4.setTextSize(18);
        t4.setTextColor(Color.BLUE);
        mainLin.addView(t4);



    }

    private void setClickSubListener(final ImageView graph) {
        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popIn(graph);
            }
        });

    }

    private void popIn(final ImageView fab) {
        fab.animate().setStartDelay(0).scaleX(1.5f).scaleY(1.5f).setDuration(100).withEndAction(new Runnable() {
            @Override
            public void run() {
                fab.animate().setStartDelay(0).scaleX(1f).scaleY(1f).setDuration(400);
            }
        });
    }


}


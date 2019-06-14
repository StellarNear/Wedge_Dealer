package stellarnear.wedge_dealer.SettingsFraments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stellarnear.wedge_dealer.MainActivity;
import stellarnear.wedge_dealer.Perso.Perso;
import stellarnear.wedge_dealer.R;
import stellarnear.wedge_dealer.Stats.Stat;
import stellarnear.wedge_dealer.Tools;

public class DisplayStatsScreenFragment {
    private Perso wedge= MainActivity.wedge;
    private Activity mA;
    private Context mC;

    private Tools tools=new Tools();
    private View mainView;
    private boolean page2=false;

    private DisplayStatsScreenFragmentAtk fragAtk;

    public DisplayStatsScreenFragment(Activity mA, Context mC) {
        this.mA=mA;
        this.mC=mC;
    }

    public void addStatsScreen() {
        ViewGroup window = mA.findViewById(android.R.id.content);
        LayoutInflater inflater = LayoutInflater.from(mC);
        mainView = inflater.inflate(R.layout.stats_charts, null);

        TextView nAtkTxt = mainView.findViewById(R.id.nAtkTxt);
        nAtkTxt.setText(wedge.getStats().getNAtksTot()+ " attaques");

        // create a new chart object
        fragAtk = new DisplayStatsScreenFragmentAtk(mainView,mC);

        buttonSetup();
        window.addView(mainView);
    }

    private void buttonSetup() {
        final ViewSwitcher switcher = mainView.findViewById(R.id.stats_switcher);

        FloatingActionButton fabAtk = mainView.findViewById(R.id.fab_stat_atk);
        fabAtk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(page2){switcher.showPrevious();}
                fragAtk.reset();
            }
        });
        FloatingActionButton fabDmg = mainView.findViewById(R.id.fab_stat_dmg);
        fabDmg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page2=true;
                switcher.showNext();
            }
        });
    }
}

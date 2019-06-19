package stellarnear.wedge_dealer.SettingsFraments;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
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

public class DisplayStatsScreenFragmentDmg {
    private Perso wedge = MainActivity.wedge;
    private DisplayStatsScreenFragmentDmgChartMaker chartMaker;
    private DisplayStatsScreenFragmentDmgSubManager subManager;
    private PieChart pieChart;


    private Context mC;
    private View mainView;
    private List<String> listElems= Arrays.asList("","fire","shock","frost");
    private Map<String,CheckBox> mapElemCheckbox=new HashMap<>();
    private StatsList selectedStats=new StatsList();
    private int infoTxtSize = 12;


    private Tools tools=new Tools();


    public DisplayStatsScreenFragmentDmg(View mainView, Context mC) {
        this.mainView = mainView;
        this.mC = mC;

        TextView nAtkTxt = mainView.findViewById(R.id.nDmgTxt);
        nAtkTxt.setText(wedge.getStats().getStatsList().getNDmgTot() + " jets de dégâts");

        CheckBox checkPhy = mainView.findViewById(R.id.dmg_type_phy);
        CheckBox checkFire = mainView.findViewById(R.id.dmg_type_fire);
        CheckBox checkShock = mainView.findViewById(R.id.dmg_type_shock);
        CheckBox checkFrost = mainView.findViewById(R.id.dmg_type_frost);
        mapElemCheckbox.put("",checkPhy);
        mapElemCheckbox.put("fire",checkFire);
        mapElemCheckbox.put("shock",checkShock);
        mapElemCheckbox.put("frost",checkFrost);

        chartMaker = new DisplayStatsScreenFragmentDmgChartMaker((BarChart)mainView.findViewById(R.id.bar_chart_dmg),mapElemCheckbox,mC);
        setCheckboxListeners();
        initChartSelectEvent();
        initPieChart();
        subManager=new DisplayStatsScreenFragmentDmgSubManager(mainView,mapElemCheckbox,mC);
    }

    private void setCheckboxListeners() {
        for(String elem : listElems){
            onCheckboxClicked(mapElemCheckbox.get(elem));
        }
    }

    public void onCheckboxClicked(CheckBox view) {
        switch(view.getId()) {
            case R.id.dmg_type_phy:
                view.setCompoundDrawablesWithIntrinsicBounds(tools.resize(mC,R.drawable.phy_logo,75),null,null,null);
                break;
            case R.id.dmg_type_fire:
                view.setCompoundDrawablesWithIntrinsicBounds(tools.resize(mC,R.drawable.fire_logo,75),null,null,null);
                break;
            case R.id.dmg_type_shock:
                view.setCompoundDrawablesWithIntrinsicBounds(tools.resize(mC,R.drawable.shock_logo,75),null,null,null);
                break;
            case R.id.dmg_type_frost:
                view.setCompoundDrawablesWithIntrinsicBounds(tools.resize(mC,R.drawable.frost_logo,75),null,null,null);
                break;
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chartMaker.resetChart();
                chartMaker.buildChart();
                resetPieChart();
                buildPieChart();
                subManager.addInfos(null);
            }
        });
    }

    private void initChartSelectEvent() {
        chartMaker.getChart().setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                resetPieChart();
                selectedStats=chartMaker.getMapIStepSelectedListStat().get((int)e.getX());
                buildPieChart();
                subManager.setSubSelectionBracket(chartMaker.getLabels().get((int)e.getX()));
                subManager.addInfos(selectedStats);
            }

            @Override
            public void onNothingSelected() {
                if(chartMaker.getChart().isFocusable()) {
                    reset();
                    resetPieChart();
                    subManager.addInfos(null);
                    buildPieChart();
                }
            }
        });
    }

    // Pie chart

    private void initPieChart(){
        pieChart = mainView.findViewById(R.id.pie_chart_dmg_percent);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        buildPieChart();
        pieChart.animateXY(100,1000);
        pieChart.getLegend().setEnabled(false);
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                pieChart.invalidate();
                pieChart.setCenterText(e.getData().toString());
            }

            @Override
            public void onNothingSelected() {
                resetPieChart();
            }
        });
    }

    private void buildPieChart() {
        PieData data = new PieData();
        data.addDataSet(computePieDataSet());
        data.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setData(data);
    }

    private PieDataSet computePieDataSet() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        List<Integer> colorList= new ArrayList<>();

        StatsList list;
        if(selectedStats!=null && selectedStats.size()!=0){
            list=selectedStats;
        } else {
            list=wedge.getStats().getStatsList();
        }
        int totalDmg=list.getSumDmgTot();
        for(String elem : listElems) {
            if (mapElemCheckbox.get(elem).isChecked()) {
                float percent = 100f * (list.getSumDmgTotElem(elem) / (float) totalDmg);
                int colorInt = 0;
                String appendix = "";
                switch (elem) {
                    case "":
                        appendix = "physique";
                        colorInt = mC.getColor(R.color.phy);
                        break;
                    case "fire":
                        appendix = "feu";
                        colorInt = mC.getColor(R.color.fire);
                        break;
                    case "shock":
                        appendix = "foudre";
                        colorInt = mC.getColor(R.color.shock);
                        break;
                    case "frost":
                        appendix = "froid";
                        colorInt = mC.getColor(R.color.frost);
                        break;
                }

                if (percent > 0f) {
                    entries.add(new PieEntry(percent, "", (int) list.getSumDmgTotElem(elem) + " dégats " + appendix));
                    colorList.add(colorInt);
                }
            }
        }
        PieDataSet dataset = new PieDataSet(entries,"");
        dataset.setValueTextSize(infoTxtSize);
        dataset.setColors(colorList);
        dataset.setSliceSpace(3);
        return dataset;
    }

    // Resets

    public void reset() {
        for(String elem : listElems){
            mapElemCheckbox.get(elem).setChecked(true);
        }
        chartMaker.resetChart();
        chartMaker.buildChart();

        resetPieChart();
        buildPieChart();
    }


    private void resetPieChart() {
        selectedStats=new StatsList();
        pieChart.invalidate();
        pieChart.setCenterText("");
        pieChart.highlightValue(null);
    }
}


package stellarnear.wedge_dealer.SettingsFraments.DisplayStatsScreenFragment;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stellarnear.wedge_dealer.Elems.ElemsManager;
import stellarnear.wedge_dealer.MainActivity;
import stellarnear.wedge_dealer.Perso.Perso;
import stellarnear.wedge_dealer.R;
import stellarnear.wedge_dealer.Stats.StatsList;
import stellarnear.wedge_dealer.Tools;

public class DSSFDmg {
    private Perso wedge = MainActivity.wedge;
    private DSSFDmgChartMaker chartMaker;
    private DSSFDmgInfoManager subManager;
    private PieChart pieChart;

    private Context mC;
    private View mainView;
    private ElemsManager elems;
    private Map<String,CheckBox> mapElemCheckbox=new HashMap<>();
    private StatsList selectedStats=new StatsList();
    private int infoTxtSize = 12;

    private Tools tools=new Tools();

    public DSSFDmg(View mainView, Context mC) {
        this.mainView = mainView;
        this.mC = mC;
        this.elems=ElemsManager.getInstance(mC);

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

        chartMaker = new DSSFDmgChartMaker((BarChart)mainView.findViewById(R.id.bar_chart_dmg),mapElemCheckbox,mC);
        setCheckboxListeners();
        initChartSelectEvent();
        initPieChart();
        subManager=new DSSFDmgInfoManager(mainView,mapElemCheckbox,mC);
    }

    private void setCheckboxListeners() {
        for(String elem : elems.getListKeys()){
            mapElemCheckbox.get(elem).setCompoundDrawablesWithIntrinsicBounds(tools.resize(mC,elems.getDrawableId(elem),75),null,null,null);
            mapElemCheckbox.get(elem).setOnClickListener(new View.OnClickListener() {
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
        for(String elem : elems.getListKeys()) {
            if (mapElemCheckbox.get(elem).isChecked()) {
                float percent = 100f * (list.getSumDmgTotElem(elem) / (float) totalDmg);
                if (percent > 0f) {
                    entries.add(new PieEntry(percent, "", (int) list.getSumDmgTotElem(elem) + " dégats " + elems.getName(elem)));
                    colorList.add(elems.getColorId(elem));
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
        for(String elem : elems.getListKeys()){
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


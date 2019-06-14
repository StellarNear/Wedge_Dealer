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

public class DisplayStatsScreenFragmentAtk {
    private Perso wedge= MainActivity.wedge;
    private BarChart chart;
    private PieChart pieChart;
    private PieChart pieChartCrit;

    private Context mC;
    private View mainView;
    private int infoTxtSize=12;
    private int nthAtkSelectedForPieChart=0;

    public DisplayStatsScreenFragmentAtk(View mainView,Context mC) {
        this.mainView=mainView;
        this.mC=mC;
        buildChart();
        buildPieChart();
        buildPieChartCrit();
    }

    private void buildChart() {
        chart = mainView.findViewById(R.id.bar_chart);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        chart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.setFitBars(true);
        BarData data = new BarData();
        data.addDataSet(computeBarDataSet("hit"));
        data.addDataSet(computeBarDataSet("miss"));
        chart.setData(data);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setGranularity(1.0f);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0); // this replaces setStartAtZero(true)

        chart.getAxisRight().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1.0f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                resetPieChart();
                resetPieChartCrit();
                nthAtkSelectedForPieChart=(int) e.getX();
                buildPieChart();
                buildPieChartCrit();
            }

            @Override
            public void onNothingSelected() {
                resetPieChart();
                resetPieChartCrit();
                nthAtkSelectedForPieChart=0;
                buildPieChart();
                buildPieChartCrit();
            }
        });
    }

    private BarDataSet computeBarDataSet(String mode) {
        Map<Integer,Integer> nthAtkCountHit = new HashMap<>();
        int nAtkMax=0;
        for (Stat stat : wedge.getStats().getListStats()){
            List<Integer> listNatk = new ArrayList<>();
            if (mode.equalsIgnoreCase("hit")){
                listNatk=stat.getListNthAtksHit();
            } else  if (mode.equalsIgnoreCase("miss")){
                listNatk=stat.getListNthAtksMiss();
            }
            for (int nAtk : listNatk){
                int count = 0;
                if (nthAtkCountHit.get(nAtk) != null ) {
                    count=nthAtkCountHit.get(nAtk);
                }
                nthAtkCountHit.put(nAtk,count+1);
                if(nAtk>nAtkMax){nAtkMax=nAtk;}
            }
        }
        ArrayList<BarEntry> listVal = new ArrayList<>();
        for (int i=1;i<=nAtkMax;i++){
            int count = 0;
            if (nthAtkCountHit.get(i) != null ) {
                count=nthAtkCountHit.get(i);
            }
            listVal.add(new BarEntry((int)i,(int)count));
        }
        BarDataSet set = new BarDataSet(listVal,"");
        String text="";
        if (mode.equalsIgnoreCase("hit")){
            text="attaque qui touche";
            set.setColor(mC.getColor(R.color.hit_stat));
        } else  if (mode.equalsIgnoreCase("miss")){
            text="attaque qui rate";
            set.setColor(mC.getColor(R.color.miss_stat));
        }
        set.setLabel(text);
        set.setValueTextSize(infoTxtSize);
        set.setValueFormatter(new LargeValueFormatter());
        return set;
    }


    private void buildPieChart() {
        pieChart = mainView.findViewById(R.id.pie_chart);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        PieData data = new PieData();
        data.addDataSet(computePieDataSet());
        data.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setData(data);
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

    private PieDataSet computePieDataSet() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        List<Integer> colorList= new ArrayList<>();
        float hitVal,missVal,percent;
        if(nthAtkSelectedForPieChart==0){
            hitVal=wedge.getStats().getNAtksHit();
            missVal=wedge.getStats().getNAtksMiss();
            percent = 100f*(hitVal/(hitVal+missVal));
        } else {
            hitVal=wedge.getStats().getNAtksHitNthAtk(nthAtkSelectedForPieChart);
            missVal=wedge.getStats().getNAtksMissNthAtk(nthAtkSelectedForPieChart);
            percent = 100f*(hitVal/(hitVal+missVal));
        }
        if(percent>0f) {
            entries.add(new PieEntry(percent, "", (int) hitVal + " coups touchés"));
            colorList.add(mC.getColor(R.color.hit_stat));
        }
        if(percent<100f){
            entries.add(new PieEntry(100f-percent,"",(int)missVal+" coups ratés"));
            colorList.add(mC.getColor(R.color.miss_stat));
        }
        PieDataSet dataset = new PieDataSet(entries,"");
        dataset.setValueTextSize(infoTxtSize);
        dataset.setColors(colorList);
        dataset.setSliceSpace(3);
        return dataset;
    }

    private void buildPieChartCrit() {
        pieChartCrit = mainView.findViewById(R.id.pie_chart_crit);
        pieChartCrit.setUsePercentValues(true);
        pieChartCrit.getDescription().setEnabled(false);
        pieChartCrit.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        PieData data = new PieData();
        data.addDataSet(computePieDataSetCrit());
        data.setValueFormatter(new PercentFormatter(pieChartCrit));
        pieChartCrit.setEntryLabelTextSize(infoTxtSize/1.33f);
        pieChartCrit.setEntryLabelColor(Color.BLACK);
        pieChartCrit.setData(data);
        pieChartCrit.getLegend().setEnabled(false);
        pieChartCrit.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                pieChartCrit.invalidate();
                pieChartCrit.setCenterText(e.getData().toString());
            }

            @Override
            public void onNothingSelected() {
                resetPieChartCrit();
            }
        });
    }

    private PieDataSet computePieDataSetCrit() {
        float nHit,nCrit,nCritNat;
        if(nthAtkSelectedForPieChart==0){
            nHit=wedge.getStats().getNAtksHit();
            nCrit=wedge.getStats().getNCrit();
            nCritNat=wedge.getStats().getNCritNat();
        } else {
            nHit=wedge.getStats().getNAtksHitNthAtk(nthAtkSelectedForPieChart);
            nCrit=wedge.getStats().getNCritNth(nthAtkSelectedForPieChart);
            nCritNat=wedge.getStats().getNCritNatNth(nthAtkSelectedForPieChart);
        }
        float normalPercent=100f*(nHit-nCrit)/nHit;
        float critPercent=100f*(nCrit-nCritNat)/nHit;
        float critNatPercent=100f*(nCritNat/nHit);
        ArrayList<PieEntry> entries = new ArrayList<>();
        List<Integer> colorList= new ArrayList<>();
        if(normalPercent>0f){
            entries.add(new PieEntry(normalPercent,"normal",((int)(nHit-nCrit))+" coups normaux"));
            colorList.add(mC.getColor(R.color.hit_stat));
        }
        if(critPercent>0f){
            entries.add(new PieEntry(critPercent,"crit",((int)(nCrit-nCritNat))+" coups critiques"));
            colorList.add(mC.getColor(R.color.crit_stat));
        }
        if(critNatPercent>0f){
            entries.add(new PieEntry(critNatPercent,"critNat",(int)nCritNat+" coups critiques naturels"));
            colorList.add(mC.getColor(R.color.crit_nat_stat));
        }
        PieDataSet dataset = new PieDataSet(entries,"");
        dataset.setValueTextSize(infoTxtSize);
        dataset.setColors(colorList);
        dataset.setSliceSpace(3);
        return dataset;
    }

    public void reset() {
        nthAtkSelectedForPieChart=0;
        resetChart();
        resetPieChart();
        resetPieChartCrit();
        buildChart();
        buildPieChart();
        buildPieChartCrit();
    }

    private void resetChart() {
        chart.invalidate();
        chart.fitScreen();
        chart.highlightValue(null);
    }
    private void resetPieChart() {
        pieChart.invalidate();
        pieChart.setCenterText("");
        pieChart.highlightValue(null);
    }
    private void resetPieChartCrit() {
        pieChartCrit.invalidate();
        pieChartCrit.setCenterText("");
        pieChartCrit.highlightValue(null);
    }
}

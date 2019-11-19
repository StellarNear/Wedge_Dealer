package stellarnear.wedge_companion.SettingsFraments.DisplayStatsScreenFragment;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

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

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Stats.Stat;

public class DSSFAtk {
    private Perso pj = PersoManager.getCurrentPJ();
    private BarChart chart;
    private PieChart pieChart;
    private PieChart pieChartCrit;

    private Context mC;
    private View mainView;
    private int infoTxtSize=12;
    private int nthAtkSelectedForPieChart=0;

    public DSSFAtk(View mainView, Context mC) {
        this.mainView=mainView;
        this.mC=mC;

        TextView nAtkTxt = mainView.findViewById(R.id.nAtkTxt);
        nAtkTxt.setText(pj.getStats().getStatsList().getNAtksTot()+ " attaques");

        initChart();
        buildChart();
        initPieChart();
        buildPieChart();
        initPieChartCrit();
        buildPieChartCrit();
    }

    private void initChart() {
        chart = mainView.findViewById(R.id.bar_chart_atk);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        chart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.setFitBars(true);

        buildChart();

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setGranularity(1.0f);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0); // this replaces setStartAtZero(true)

        chart.getAxisRight().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1.0f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        chart.animateXY(500, 1000);
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

    private void buildChart() {
        BarData data = new BarData();
        data.addDataSet(computeBarDataSet("hit"));
        data.addDataSet(computeBarDataSet("miss"));
        chart.setData(data);
    }

    private BarDataSet computeBarDataSet(String mode) {
        Map<Integer,Integer> nthAtkCountHit = new HashMap<>();
        int nAtkMax=0;
        for (Stat stat : pj.getStats().getStatsList().asList()){
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


    private void initPieChart() {
        pieChart = mainView.findViewById(R.id.pie_chart_atk_hit);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        buildPieChart();

        pieChart.getLegend().setEnabled(false);
        pieChart.animateXY(100,1000);
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
        float hitVal,missVal,percent;
        if(nthAtkSelectedForPieChart==0){
            hitVal=pj.getStats().getStatsList().getNAtksHit();
            missVal=pj.getStats().getStatsList().getNAtksMiss();
            percent = 100f*(hitVal/(hitVal+missVal));
        } else {
            hitVal=pj.getStats().getStatsList().getNAtksHitNthAtk(nthAtkSelectedForPieChart);
            missVal=pj.getStats().getStatsList().getNAtksMissNthAtk(nthAtkSelectedForPieChart);
            percent = 100f*(hitVal/(hitVal+missVal));
        }
        if(percent>0f) {
            entries.add(new PieEntry(percent, "",  new LargeValueFormatter().getFormattedValue(1f*hitVal) + " coups touchés"));
            colorList.add(mC.getColor(R.color.hit_stat));
        }
        if(percent<100f){
            entries.add(new PieEntry(100f-percent,"",new LargeValueFormatter().getFormattedValue(1f*missVal)+" coups ratés"));
            colorList.add(mC.getColor(R.color.miss_stat));
        }
        PieDataSet dataset = new PieDataSet(entries,"");
        dataset.setValueTextSize(infoTxtSize);
        dataset.setColors(colorList);
        dataset.setSliceSpace(3);
        return dataset;
    }

    private void initPieChartCrit() {
        pieChartCrit = mainView.findViewById(R.id.pie_chart_atk_crit);
        pieChartCrit.setUsePercentValues(true);
        pieChartCrit.getDescription().setEnabled(false);
        pieChartCrit.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        buildPieChartCrit();
        pieChartCrit.setEntryLabelTextSize(infoTxtSize/1.33f);
        pieChartCrit.setEntryLabelColor(Color.BLACK);
        pieChartCrit.getLegend().setEnabled(false);
        pieChartCrit.animateXY(100,1000);
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

    private void buildPieChartCrit() {
        PieData data = new PieData();
        data.addDataSet(computePieDataSetCrit());
        data.setValueFormatter(new PercentFormatter(pieChartCrit));
        pieChartCrit.setData(data);
    }

    private PieDataSet computePieDataSetCrit() {
        float nHit,nCrit,nCritNat;
        if(nthAtkSelectedForPieChart==0){
            nHit=pj.getStats().getStatsList().getNAtksHit();
            nCrit=pj.getStats().getStatsList().getNCrit();
            nCritNat=pj.getStats().getStatsList().getNCritNat();
        } else {
            nHit=pj.getStats().getStatsList().getNAtksHitNthAtk(nthAtkSelectedForPieChart);
            nCrit=pj.getStats().getStatsList().getNCritNth(nthAtkSelectedForPieChart);
            nCritNat=pj.getStats().getStatsList().getNCritNatNth(nthAtkSelectedForPieChart);
        }
        float normalPercent=100f*(nHit-nCrit)/nHit;
        float critPercent=100f*(nCrit-nCritNat)/nHit;
        float critNatPercent=100f*(nCritNat/nHit);
        ArrayList<PieEntry> entries = new ArrayList<>();
        List<Integer> colorList= new ArrayList<>();
        if(normalPercent>0f){
            entries.add(new PieEntry(normalPercent,"normal",new LargeValueFormatter().getFormattedValue(((int)(nHit-nCrit)))+" coups normaux"));
            colorList.add(mC.getColor(R.color.hit_stat));
        }
        if(critPercent>0f){
            entries.add(new PieEntry(critPercent,"crit",new LargeValueFormatter().getFormattedValue(((int)(nCrit-nCritNat)))+" coups critiques"));
            colorList.add(mC.getColor(R.color.crit_stat));
        }
        if(critNatPercent>0f){
            entries.add(new PieEntry(critNatPercent,"critNat",new LargeValueFormatter().getFormattedValue((int)nCritNat)+" coups critiques naturels"));
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

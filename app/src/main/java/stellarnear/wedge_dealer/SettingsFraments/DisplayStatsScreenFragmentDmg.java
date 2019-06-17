package stellarnear.wedge_dealer.SettingsFraments;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
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

public class DisplayStatsScreenFragmentDmg {
    private Perso wedge = MainActivity.wedge;
    private BarChart chart;
    private PieChart pieChart;


    private Context mC;
    private View mainView;
    private int infoTxtSize = 12;

    private int minRound,maxRound,nSteps;
    private int sizeStep=50;

    private Tools tools=new Tools();


    public DisplayStatsScreenFragmentDmg(View mainView, Context mC) {
        this.mainView = mainView;
        this.mC = mC;

        TextView nAtkTxt = mainView.findViewById(R.id.nDmgTxt);
        nAtkTxt.setText(wedge.getStats().getNDmgTot() + " jets de dégâts");

        int minDmg = wedge.getStats().getMinDmgTot();
        int maxDmg = wedge.getStats().getMaxDmgTot();
        minRound = ((int) minDmg / sizeStep) * sizeStep;
        maxRound = (((int) maxDmg / sizeStep) + 1) * sizeStep;
        nSteps = (maxRound-minRound)/sizeStep;

        buildChart();
        buildPieChart();

        CheckBox rad1 = mainView.findViewById(R.id.dmg_type_phy);
        CheckBox rad2 = mainView.findViewById(R.id.dmg_type_fire);
        onCheckboxClicked(rad1);
        onCheckboxClicked(rad2);
    }

    private void buildChart() {
        addDataChart();
        addAxisChart();
        addLimitsChart();

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                resetPieChart();
                //nthAtkSelectedForPieChart=(int) e.getX();
                buildPieChart();
            }

            @Override
            public void onNothingSelected() {
                resetPieChart();
                //nthAtkSelectedForPieChart=0;
                buildPieChart();
            }
        });
    }



    private void addDataChart() {
        chart = mainView.findViewById(R.id.bar_chart_dmg);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        chart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        chart.getXAxis().setDrawGridLines(false);
        chart.setFitBars(true);
        chart.setDescription(null);

        BarData data = new BarData();
        data.addDataSet(computeBarDataSet());
        chart.setData(data);
    }


    private BarDataSet computeBarDataSet(){
        Map<Integer, Integer> histo = new HashMap<>();
        for (Stat stat : wedge.getStats().getListStats()) {
            int sumDmg = stat.getSumDmg();
            int iStep = (int)((sumDmg - minRound) / sizeStep);
            if (histo.get(iStep) == null) {
                histo.put(iStep, 1);
            } else {
                histo.put(iStep, histo.get(iStep) + 1);
            }
        }
        ArrayList<BarEntry> listVal = new ArrayList<>();
        for (int i = 0; i < nSteps; i++) {
            if (histo.get(i) != null) {
                listVal.add(new BarEntry(i, (int) histo.get(i)));
            } else {
                listVal.add(new BarEntry(i, 0));
            }
        }

        BarDataSet set = new BarDataSet(listVal, "");
        set.setColor(mC.getColor(R.color.dmg_stat));
        set.setDrawValues(false);
        return set;
    }



    private void addAxisChart() {
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setGranularity(1.0f);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0); // this replaces setStartAtZero(true)

        chart.getAxisRight().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(computeBarDataSetLabel()));
        xAxis.setLabelRotationAngle(-90);

        chart.getLegend().setEnabled(false);
    }

    private List<String> computeBarDataSetLabel(){
        ArrayList<String> listLabels = new ArrayList<>();
        for (int i = 0; i < nSteps; i++) {
            listLabels.add("["+ String.valueOf(minRound + i * sizeStep)+"-"+String.valueOf( minRound + (i+1) * sizeStep)+"[");
        }
        return listLabels;
    }

    private void addLimitsChart() {
        XAxis leftAxis = chart.getXAxis();
        int sumDmg = wedge.getStats().getListStats().get(wedge.getStats().getListStats().size()-1).getSumDmg();
        int iStep = (int)((sumDmg - minRound) / sizeStep);
        LimitLine ll = new LimitLine(iStep, "récent");
        if((sumDmg-minRound)<((maxRound-minRound)/2)){
            ll.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        } else {
            ll.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        }
        ll.setLineColor(mC.getColor(R.color.recent_stat));
        ll.setLineWidth(2f);
        ll.setTextColor(mC.getColor(R.color.recent_stat));
        ll.setTextSize(12f);
        ll.enableDashedLine(10f,10f,0f);
// .. and more styling options

        leftAxis.addLimitLine(ll);
    }

    // Pie chart


    private void buildPieChart() {
        pieChart = mainView.findViewById(R.id.pie_chart_dmg_percent);
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
        hitVal=wedge.getStats().getNAtksHit();
        missVal=wedge.getStats().getNAtksMiss();
        percent = 100f*(hitVal/(hitVal+missVal));
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

    public void reset() {
        resetChart();
        resetPieChart();
        buildChart();
        buildPieChart();

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

    public void onCheckboxClicked(CheckBox view) {
        // Is the button now checked?
        String text="-";
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.dmg_type_phy:
                text="lol";
                view.setCompoundDrawablesWithIntrinsicBounds(tools.resize(mC,R.drawable.phy_logo,75),null,null,null);
                break;
            case R.id.dmg_type_fire:
                text="paslol";
                view.setCompoundDrawablesWithIntrinsicBounds(tools.resize(mC,R.drawable.fire_logo,75),null,null,null);
                break;
            case R.id.dmg_type_shock:
                text="lolShock";
                view.setCompoundDrawablesWithIntrinsicBounds(tools.resize(mC,R.drawable.shock_logo,75),null,null,null);
                break;
            case R.id.dmg_type_frost:
                text="lolFro";
                view.setCompoundDrawablesWithIntrinsicBounds(tools.resize(mC,R.drawable.frost_logo,75),null,null,null);
                break;
        }
        final String txt = text;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Tools().customToast(mC,txt);
            }
        });
    }

}


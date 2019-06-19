package stellarnear.wedge_dealer.SettingsFraments;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stellarnear.wedge_dealer.MainActivity;
import stellarnear.wedge_dealer.Perso.Perso;
import stellarnear.wedge_dealer.R;
import stellarnear.wedge_dealer.Stats.Stat;
import stellarnear.wedge_dealer.Tools;

public class DisplayStatsScreenFragmentDmgChartMaker {
    private Perso wedge = MainActivity.wedge;
    private BarChart chart;
    private PieChart pieChart;


    private Context mC;
    private View mainView;
    private List<String> listElems= Arrays.asList("","fire","shock","frost");
    private Map<String,CheckBox> mapElemCheckbox=new HashMap<>();
    private List<String> elemsSelected;
    private Boolean barGroupMode=false;
    private Map<Integer,List<Stat>> mapIStepSelectedListStat=new HashMap<>();
    private ArrayList<String> listLabels;
    private int infoTxtSize = 12;

    private int minRound,maxRound,nSteps;
    private int sizeStep=50;

    private Tools tools=new Tools();


    public DisplayStatsScreenFragmentDmgChartMaker(BarChart chart,Map<String,CheckBox> mapElemCheckbox,Context mC) {
        this.chart=chart;
        this.mapElemCheckbox=mapElemCheckbox;
        this.mC=mC;

        initChart();
    }

    private void initChart() {
        formatChart();
        buildChart();
        chart.animateXY(500, 1000);
    }

    private void formatChart() {
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        chart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        chart.setFitBars(true);
        chart.setDescription(null);
    }

    public void buildChart() {
        calculateElemToShow();
        calculateMinMaxRound();

        addDataChart();

        computeBarDataSetLabel();
        formatAxisChart();
        addLimitsChart();
    }

    private void calculateElemToShow() {
        elemsSelected=new ArrayList<>();
        for(String elem : listElems){
            if(mapElemCheckbox.get(elem).isChecked()){
                elemsSelected.add(elem);
            }
        }
        barGroupMode=(elemsSelected.size()>1 && elemsSelected.size()!=4);
    }

    private void calculateMinMaxRound() {
        int minDmg,maxDmg;
        if(elemsSelected.size()==4) {
            minDmg = wedge.getStats().getStatsList().getMinDmgTot();
            maxDmg = wedge.getStats().getStatsList().getMaxDmgTot();
        } else {
            int currentMin=0,currentMax=0;
            for (String elem:elemsSelected){
                int minElem=wedge.getStats().getStatsList().getMinDmgElem(elem);
                int maxElem=wedge.getStats().getStatsList().getMaxDmgElem(elem);
                if(currentMin==0 && minElem!=0 ){
                    currentMin=minElem;
                }
                if (minElem!=0 && minElem<currentMin){
                    currentMin=minElem;
                }
                if(currentMax==0 && maxElem!=0 ){
                    currentMax=maxElem;
                }
                if (maxElem!=0 && maxElem>currentMax){
                    currentMax=maxElem;
                }
            }
            minDmg=currentMin;
            maxDmg=currentMax;
        }
        minRound = ((int) minDmg / sizeStep) * sizeStep;
        maxRound = (((int) maxDmg / sizeStep) + 1) * sizeStep;
        nSteps = (maxRound-minRound)/sizeStep;
    }

    private void addDataChart() {
        BarData data = new BarData();
        //(barwidth+barspace)*nbBar +groupsspace = 1

        float barSpace = 0.0f; // x2 dataset
        float groupSpace = 0.1f;

        float barWidth = 0.8f;
        if(barGroupMode){
            barWidth=((1f-groupSpace)/(1f*elemsSelected.size()))-barSpace;
        }
        data.setBarWidth(barWidth);

        if(elemsSelected.size()==4){
            data.addDataSet(computeBarDataSet("all"));
        } else {
            for(String elem:elemsSelected) {
                data.addDataSet(computeBarDataSet(elem));
            }
        }

        chart.setData(data);
        chart.getXAxis().setAxisMinimum(0-barWidth/2);
        if(barGroupMode) {
            chart.groupBars(0, groupSpace, barSpace);
            chart.getXAxis().setAxisMaximum(nSteps+(barWidth/2));
        } else {
            chart.getXAxis().setAxisMaximum(nSteps-1+(barWidth/2));
        }
        if(elemsSelected.size()!=4){chart.getBarData().setHighlightEnabled(false);}
    }

    private BarDataSet computeBarDataSet(String elemsSelected){
        Map<Integer, Integer> histo = new HashMap<>();
        for (Stat stat : wedge.getStats().getStatsList().asList()) {
            int sumDmg;
            if(elemsSelected.equalsIgnoreCase("all")) {
                sumDmg = stat.getSumDmg();
            } else {
                sumDmg=stat.getElemSumDmg().get(elemsSelected);
            }
            int iStep = (int)((sumDmg - minRound) / sizeStep);
            if(mapIStepSelectedListStat.get(iStep)==null){
                mapIStepSelectedListStat.put(iStep,new ArrayList<Stat>());
            }
            mapIStepSelectedListStat.get(iStep).add(stat);
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
        switch (elemsSelected){
            case "":
                set.setColor(mC.getColor(R.color.phy));
                break;
            case "fire":
                set.setColor(mC.getColor(R.color.fire));
                break;
            case "shock":
                set.setColor(mC.getColor(R.color.shock));
                break;
            case "frost":
                set.setColor(mC.getColor(R.color.frost));
                break;
            case "all":
                set.setColor(mC.getColor(R.color.dmg_stat));
                break;
        }
        set.setDrawValues(false);
        return set;
    }

    private void computeBarDataSetLabel(){
        listLabels = new ArrayList<>();
        for (int i = 0; i < nSteps; i++) {
            listLabels.add("["+ String.valueOf(minRound + i * sizeStep)+"-"+String.valueOf( minRound + (i+1) * sizeStep)+"[");
        }
    }

    private void formatAxisChart() {

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setGranularity(1.0f);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0); // this replaces setStartAtZero(true)

        chart.getAxisRight().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(listLabels));
        xAxis.setLabelRotationAngle(-90);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);

        if(barGroupMode){xAxis.setCenterAxisLabels(true);}else{xAxis.setCenterAxisLabels(false);}
        chart.getLegend().setEnabled(false);
    }

    private void addLimitsChart() {
        if(elemsSelected.size()==4){
            addLimitLine("all");
        } else {
            for (String elem : elemsSelected){
                addLimitLine(elem);
            }
        }
    }

    private void addLimitLine(String elem) {

        XAxis leftAxis = chart.getXAxis();
        int sumDmg = 0;
        String label="récent";
        if(barGroupMode){label="";}
        if(elem.equalsIgnoreCase("all")){
            sumDmg = wedge.getStats().getStatsList().asList().get(wedge.getStats().getStatsList().asList().size()-1).getSumDmg();
        } else {
            sumDmg = wedge.getStats().getStatsList().asList().get(wedge.getStats().getStatsList().asList().size()-1).getElemSumDmg().get(elem);
        }

        int lineColor;
        switch (elem){
            case "":
                lineColor=mC.getColor(R.color.recent_phy);
                break;
            case "fire":
                lineColor=mC.getColor(R.color.recent_fire);
                break;
            case "shock":
                lineColor=mC.getColor(R.color.recent_shock);
                break;
            case "frost":
                lineColor=mC.getColor(R.color.recent_frost);
                break;
            default:
                lineColor=mC.getColor(R.color.recent_stat);
                break;
        }


        int iStep=((sumDmg - minRound) / sizeStep);
        float fStepAdjust =iStep;
        if(barGroupMode){
            fStepAdjust +=chart.getData().getBarWidth()/2f;
            fStepAdjust +=0.05f;
            int nthElem=0;
            for (String previousElm: elemsSelected){
                nthElem++;
                if(previousElm.equalsIgnoreCase(elem)){
                    break;
                }
            }
            fStepAdjust +=chart.getData().getBarWidth()*(nthElem-1);//+0.05f*iStep;//+0.4f*(nthElem-1)*((elemsSelected.size()*1f));//+0.4f;
        }
        LimitLine ll = new LimitLine(fStepAdjust, label);
        if((sumDmg-minRound)<((maxRound-minRound)/2)){
            ll.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        } else {
            ll.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        }
        ll.setTextSize(12f);
        ll.enableDashedLine(10f,10f,0f);
        ll.setLineWidth(2f);
        ll.setLineColor(lineColor);
        ll.setTextColor(lineColor);

// .. and more styling options

        leftAxis.addLimitLine(ll);
    }

    public void resetChart() {
        chart.getXAxis().removeAllLimitLines();
        chart.invalidate();
        chart.fitScreen();
        chart.highlightValue(null);
    }

    public BarChart getChart() {
        return chart;
    }

    public void adjustScreen() {
        chart.fitScreen();
        chart.invalidate();
    }

    public Map<Integer, List<Stat>> getMapIStepSelectedListStat() {
        return mapIStepSelectedListStat;
    }
}


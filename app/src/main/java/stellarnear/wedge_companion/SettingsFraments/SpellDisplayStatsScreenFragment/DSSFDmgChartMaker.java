package stellarnear.wedge_companion.SettingsFraments.SpellDisplayStatsScreenFragment;


import android.content.Context;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.widget.CheckBox;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stellarnear.wedge_companion.Elems.ElemsManager;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Stats.SpellStats.DamagesShortList;
import stellarnear.wedge_companion.Stats.SpellStats.DamagesShortListElement;
import stellarnear.wedge_companion.Tools;


public class DSSFDmgChartMaker {
    private Perso pj = PersoManager.getCurrentPJ();
    private BarChart chart;
    private Context mC;
    private ElemsManager elems;
    private Map<String, CheckBox> mapElemCheckbox=new HashMap<>();
    private List<String> elemsSelected;
    private Boolean barGroupMode=false;
    private Map<Integer, DamagesShortList> mapIStepSelectedDamagesShortList =new HashMap<>();
    private ArrayList<String> listLabels;
    private int infoTxtSize = 12;

    private int minRound,maxRound,nSteps;
    private int sizeStep;
    private Tools tools=new Tools();

    public DSSFDmgChartMaker(BarChart chart, Map<String,CheckBox> mapElemCheckbox, Context mC) {
        this.chart=chart;
        this.mapElemCheckbox=mapElemCheckbox;
        this.elems=ElemsManager.getInstance();
        this.mC=mC;

        this.sizeStep=tools.toInt(PreferenceManager.getDefaultSharedPreferences(mC).getString("display_stats_bin", String.valueOf(mC.getResources().getInteger(R.integer.display_stats_bin_def))));
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
        if(pj.getSpellStats().getSpellStatsList().size()>=1)addLimitsChart();
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                adjustScreen();
            }
        }, 50);
    }

    private void calculateElemToShow() {
        elemsSelected = new ArrayList<>();
        for(Map.Entry<String,CheckBox> entry:mapElemCheckbox.entrySet()){
            if (entry.getValue().isChecked()) {
                elemsSelected.add(entry.getKey());
            }
        }
        barGroupMode=(elemsSelected.size()>1 && elemsSelected.size()!=mapElemCheckbox.entrySet().size());
        if(barGroupMode){chart.setFocusable(false);}else{chart.setFocusable(true);}
    }

    private void calculateMinMaxRound() {
        int minDmg,maxDmg;
        if(elemsSelected.size()==mapElemCheckbox.entrySet().size()) {
            minDmg = pj.getSpellStats().getSpellStatsList().getDamageShortList().getMinDmg();
            maxDmg = pj.getSpellStats().getSpellStatsList().getDamageShortList().getMaxDmg();
        } else {
            int currentMin=0,currentMax=0;
            for (String elem:elemsSelected){
                int minElem= pj.getSpellStats().getSpellStatsList().getDamageShortList().filterByElem(elem).getMinDmg();
                int maxElem= pj.getSpellStats().getSpellStatsList().getDamageShortList().filterByElem(elem).getMaxDmg();
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
        float barSpace = 0.0f; //(barwidth+barspace)*nbBar +groupsspace = 1  si on veut que les label soit alignés
        float groupSpace = 0.1f;

        float barWidth = 1f;
        if(barGroupMode){
            barWidth=((1f-groupSpace)/(1f*elemsSelected.size()))-barSpace;
        }
        data.setBarWidth(barWidth);

        if(elemsSelected.size()==mapElemCheckbox.entrySet().size()){
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
        if(elemsSelected.size()!=mapElemCheckbox.entrySet().size()){chart.getBarData().setHighlightEnabled(false);}
    }

    private BarDataSet computeBarDataSet(String elemsSelected){
        Map<Integer, Integer> histo = new HashMap<>();
        mapIStepSelectedDamagesShortList =new HashMap<>();
        DamagesShortList damagesShortList;
        if(elemsSelected.equalsIgnoreCase("all")){
            damagesShortList= pj.getSpellStats().getSpellStatsList().getDamageShortList();
        } else {
            damagesShortList= pj.getSpellStats().getSpellStatsList().getDamageShortListForElem(elemsSelected);
        }
        for(DamagesShortListElement element : damagesShortList.asList() ) {
            int dmg = element.getDmgSum();
            if (dmg <= 0) {
                continue;
            }
            int iStep = (int) ((dmg - minRound) / sizeStep);
            if (mapIStepSelectedDamagesShortList.get(iStep) == null) {
                mapIStepSelectedDamagesShortList.put(iStep, new DamagesShortList());
            }
            mapIStepSelectedDamagesShortList.get(iStep).add(element);
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
        String labelSet= elemsSelected.equalsIgnoreCase("all")? "tout" : elems.getName(elemsSelected);
        BarDataSet set = new BarDataSet(listVal, labelSet);
        if(elemsSelected.equalsIgnoreCase("all")){
            set.setColor(ContextCompat.getColor(mC,R.color.all_stat));
        } else {
            set.setColor(elems.getColorIdDark(elemsSelected));
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
        //chart.getLegend().setEnabled(false);
    }

    private void addLimitsChart() {
        if(elemsSelected.size()==mapElemCheckbox.entrySet().size()){
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
            sumDmg = pj.getSpellStats().getSpellStatsList().getLastStat().getSumDmg();
        } else {
            sumDmg = pj.getSpellStats().getSpellStatsList().getLastStat().getSumDmgElem(elem);
        }

        int lineColor;
        if(elem.equalsIgnoreCase("all")){
            lineColor=ContextCompat.getColor(mC,R.color.all_recent_stat);
        } else {
            lineColor=elems.getColorId(elem);
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

    private void adjustScreen() {
        chart.fitScreen();
        chart.invalidate();
    }

    public Map<Integer, DamagesShortList> getMapIStepSelectedDamagesShortList() {
        return mapIStepSelectedDamagesShortList;
    }

    public List<String> getLabels() {
        return listLabels;
    }

}

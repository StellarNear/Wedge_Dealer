package stellarnear.wedge_companion.SettingsFraments.SpellDisplayStatsScreenFragment;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stellarnear.wedge_companion.Elems.ElemsManager;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Stats.SpellStats.SpellStat;
import stellarnear.wedge_companion.Tools;


public class DSSFGraph {
    private Perso pj = PersoManager.getCurrentPJ();
    private Context mC;
    private View mainView;
    private ElemsManager elems;
    private List<String> elemsSelected;
    private Map<String,CheckBox> mapElemCheckbox=new HashMap<>();
    private int rankMax=-1;
    private int nMetaMax=-1;
    private int infoTxtSize=10;
    private LineChart chartDmgRank;
    private LineChart chartMetaDmg;
    private Tools tools=new Tools();

    public DSSFGraph(View mainView, Context mC) {
        this.mainView = mainView;
        this.mC = mC;
        this.elems= ElemsManager.getInstance();
        CheckBox checkNone = mainView.findViewById(R.id.line_type_aucun);
        CheckBox checkAcid = mainView.findViewById(R.id.line_type_acid);
        CheckBox checkFire = mainView.findViewById(R.id.line_type_fire);
        CheckBox checkShock = mainView.findViewById(R.id.line_type_shock);
        CheckBox checkFrost = mainView.findViewById(R.id.line_type_frost);
        mapElemCheckbox.put("none",checkNone);mapElemCheckbox.put("acid",checkAcid); mapElemCheckbox.put("fire",checkFire);  mapElemCheckbox.put("shock",checkShock); mapElemCheckbox.put("frost",checkFrost);
        setCheckboxListeners();
        initLineCharts();
    }

    private void setCheckboxListeners() {
        for(String elem : elems.getListSpellsKeys()){
            mapElemCheckbox.get(elem).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    calculateElemToShow();
                    resetChartRank();
                    setDmgData();
                    resetChartMeta();
                    setMetaData();
                }
            });
        }
    }

    private void initLineCharts() {
        calculateElemToShow();
        initLineChartRankDmg();
        initLineChartMeta();
        buildCharts();
        chartDmgRank.animateXY(750, 1000);
        chartMetaDmg.animateXY(750, 1000);
    }

    private void initLineChartRankDmg(){
        chartDmgRank =mainView.findViewById(R.id.line_chart_dmg_rank);
        setChartPara(chartDmgRank);
        chartDmgRank.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                tools.customToast(mC,e.getData().toString(),"center");
            }
            @Override
            public void onNothingSelected() {
                resetChartRank();
            }
        });
    }

    private void setChartPara(LineChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        chart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        chart.getXAxis().setDrawGridLines(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setGranularity(1.0f);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0); // this replaces setStartAtZero(true)
        chart.getAxisRight().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1.0f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    private void calculateElemToShow() {
        elemsSelected = new ArrayList<>();
        for (String elem : elems.getListSpellsKeys()) {
            if (mapElemCheckbox.get(elem).isChecked()) {
                elemsSelected.add(elem);
            }
        }
    }

    private void initLineChartMeta() {
        chartMetaDmg =mainView.findViewById(R.id.line_chart_crit_elem);
        setChartPara(chartMetaDmg);

        chartMetaDmg.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                tools.customToast(mC,e.getData().toString(),"center");
            }

            @Override
            public void onNothingSelected() {
                resetChartMeta();
            }
        });
    }

    private void buildCharts() {
        buildMapRank();
        setDmgData();
        chartDmgRank.getXAxis().setLabelCount(rankMax+1);
        setMetaData();
        chartMetaDmg.getXAxis().setLabelCount(nMetaMax+1);
    }

    private void buildMapRank() {
        rankMax=-1;
        nMetaMax=-1;
        for (SpellStat stat : pj.getSpellStats().getSpellStatsList().asList()){
            for (int rank : stat.getListRank()){
                if(rank>rankMax){rankMax=rank;}
            }
            for(int nMeta:stat.getListMetaUprank()){
                if(nMeta>nMetaMax){nMetaMax=nMeta;}
            }
        }
    }

    private void setDmgData() {
        LineData data = new LineData();
        if(elemsSelected.size()==5){
            addDmgAllData(data);
        } else {
            addDmgElemsData(data);
        }
        data.setValueTextSize(infoTxtSize);
        chartDmgRank.setData(data);
    }

    private void addDmgAllData(LineData data) {
        ArrayList<Entry> listVal = new ArrayList<>();
        for(int i=0;i<=rankMax;i++){
            float dmgSumMoy=0;
            int count=0;
            for (SpellStat stat : pj.getSpellStats().getSpellStatsList().asList()){
                if(stat.getRankMoyDmg(i)>0){
                    dmgSumMoy+=stat.getRankMoyDmg(i);
                    count++;
                }
            }
            if(dmgSumMoy>0) {
                listVal.add(new Entry((int) i,Math.round(dmgSumMoy/count), Math.round(dmgSumMoy/count) + " dégâts en moyenne\npour l'ensemble des sorts de rang " + i));
            }
        }
        if(listVal.size()>0) {
            LineDataSet elemSet = new LineDataSet(listVal, "tout");
            setLinePara(elemSet, R.color.all_stat);
            data.addDataSet(elemSet);
        }
    }

    private void addDmgElemsData(LineData data) {
        for(String elem:elemsSelected){
            ArrayList<Entry> listVal = new ArrayList<>();
            for(int i=0;i<=rankMax;i++){
                float dmgSumMoyElem=0;
                int count=0;
                for (SpellStat stat : pj.getSpellStats().getSpellStatsList().asList()){
                    if(stat.getRankElemMoyDmg(i,elem)>0){
                        dmgSumMoyElem+=stat.getRankElemMoyDmg(i,elem);
                        count++;
                    }
                }
                if(dmgSumMoyElem>0) {
                    listVal.add(new Entry((int) i,Math.round(dmgSumMoyElem/count), Math.round(dmgSumMoyElem/count) + " dégâts en moyenne\npour les sorts de type " + elem + " de rang " + i));
                }
            }
            if(listVal.size()>0) {
                LineDataSet elemSet = new LineDataSet(listVal, elems.getName(elem));
                setLinePara(elemSet, elems.getColorIdDark(elem));
                data.addDataSet(elemSet);
            }
        }
    }

    private void setMetaData() {
        LineData data = new LineData();
        if(elemsSelected.size()==5){
            addMetaAllData(data);
        } else {
            addMetaElemsData(data);
        }
        data.setValueTextSize(infoTxtSize);
        chartMetaDmg.setData(data);
    }

    private void addMetaAllData(LineData data) {

            ArrayList<Entry> listVal = new ArrayList<>();
            for(int iMeta=0;iMeta<=nMetaMax;iMeta++){
                float dmgSumMoyMeta=0;
                int count=0;
                for (SpellStat stat : pj.getSpellStats().getSpellStatsList().asList()){
                    if(stat.getMetaMoyDmg(iMeta)>0){
                        dmgSumMoyMeta+=stat.getMetaMoyDmg(iMeta);
                        count++;
                    }
                }
                if(dmgSumMoyMeta>0) {
                    listVal.add(new Entry((int) iMeta, Math.round(dmgSumMoyMeta/count),  Math.round(dmgSumMoyMeta/count) + " dégâts en moyenne\npour " + iMeta + " rang de métamagie pour l'ensemble des sorts"));
                }
            }
            if(listVal.size()>0) {
                LineDataSet elemSet = new LineDataSet(listVal, "tout");
                setLinePara(elemSet, R.color.all_stat);
                data.addDataSet(elemSet);
            }

    }

    private void addMetaElemsData(LineData data) {
        for(String elem:elemsSelected){
            ArrayList<Entry> listVal = new ArrayList<>();
            for(int iMeta=0;iMeta<=nMetaMax;iMeta++){
                float dmgSumMoyMeta=0;
                int count=0;
                for (SpellStat stat : pj.getSpellStats().getSpellStatsList().asList()){
                    if(stat.getMetaElemMoyDmg(iMeta,elem)>0){
                        dmgSumMoyMeta+=stat.getMetaElemMoyDmg(iMeta,elem);
                        count++;
                    }
                }
                if(dmgSumMoyMeta>0) {
                    listVal.add(new Entry((int) iMeta, Math.round(dmgSumMoyMeta/count),  Math.round(dmgSumMoyMeta/count) + " dégâts en moyenne\npour " + iMeta + " rang de métamagie sur des sorts de type"+elem));
                }
            }
            if(listVal.size()>0) {
                LineDataSet elemSet = new LineDataSet(listVal, elems.getName(elem));
                setLinePara(elemSet, elems.getColorIdDark(elem));
                data.addDataSet(elemSet);
            }
        }
    }

    private void setLinePara(LineDataSet set,int color) {
        try {
            set.setColors(mC.getColor(color));
            set.setCircleColor(mC.getColor(color));
        } catch (Exception e) {
            e.printStackTrace();
        }
        set.setLineWidth(2f);   set.setCircleRadius(4f);  set.setValueFormatter(new LargeValueFormatter());
    }

    // Resets
    public void reset() {
        for(String elem : elems.getListSpellsKeys()){
            mapElemCheckbox.get(elem).setChecked(true);
        }
        calculateElemToShow();
        resetChartRank();
        resetChartMeta();
        buildCharts();
    }

    private void resetChartRank() {
        chartDmgRank.invalidate();
        chartDmgRank.fitScreen();
        chartDmgRank.highlightValue(null);
    }

    private void resetChartMeta() {
        chartMetaDmg.invalidate();
        chartMetaDmg.fitScreen();
        chartMetaDmg.highlightValue(null);
    }
}


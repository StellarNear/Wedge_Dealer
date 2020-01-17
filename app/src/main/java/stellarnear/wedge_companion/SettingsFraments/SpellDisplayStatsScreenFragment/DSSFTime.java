package stellarnear.wedge_companion.SettingsFraments.SpellDisplayStatsScreenFragment;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import stellarnear.wedge_companion.Elems.ElemsManager;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Stats.SpellStats.DamagesShortListElement;
import stellarnear.wedge_companion.Stats.SpellStats.SpellStat;
import stellarnear.wedge_companion.Stats.SpellStats.SpellStatsList;
import stellarnear.wedge_companion.Tools;

public class DSSFTime {
    private Perso pj = PersoManager.getCurrentPJ();
    private Context mC;
    private View mainView;
    private ElemsManager types;
    private List<String> typeSelected;
    private Map<String, SpellStatsList> mapDatetxtStatslist = new LinkedHashMap<>();
    private List<String> labelList=new ArrayList<>();
    private Map<String, CheckBox> mapElemCheckbox=new HashMap<>();
    private int infoTxtSize=10;
    private int nDate=0;
    private LineChart chartDmg;
    private LineChart chartRank;
    private Tools tools=new Tools();

    public DSSFTime(View mainView, Context mC) {
        this.mainView = mainView;
        this.mC = mC;
        this.types = ElemsManager.getInstance();
        determineElemsPresents();
        setCheckboxListeners();
        initLineCharts();
    }

    private void determineElemsPresents() {
        SpellStatsList allStats = pj.getSpellStats().getSpellStatsList();
        mapElemCheckbox=new HashMap<>();
        for(SpellStat spellStat :allStats.asList()) {
            for(DamagesShortListElement subDamageIndivSpell : spellStat.getDamageShortList().getDamageElementList()){
                String elementSpell = subDamageIndivSpell.getElement();
                if (mapElemCheckbox.get(elementSpell)==null) {
                    mapElemCheckbox.put(elementSpell,addCheckBox(elementSpell));
                }
            }
        }
    }

    private CheckBox addCheckBox(String elementSpell) {
        CheckBox checkBox = new CheckBox(mC);
        checkBox.setButtonTintList(ColorStateList.valueOf(types.getColorId(elementSpell)));
        Drawable drawable;
        try {
            drawable = mC.getDrawable(types.getDrawableId(elementSpell));
        } catch (Exception e) {
            drawable = mC.getDrawable(R.drawable.mire_test);
            e.printStackTrace();
        }
        ((LinearLayout)mainView.findViewById(R.id.third_panel_elems_checkboxes)).addView(checkBox);
        ImageView logo = new ImageView(mC);
        logo.setImageDrawable(tools.resize(mC,drawable,80));
        ((LinearLayout)mainView.findViewById(R.id.third_panel_elems_checkboxes)).addView(logo);
        checkBox.setChecked(true);
        return checkBox;
    }

    private void setCheckboxListeners() {
        for(Map.Entry<String,CheckBox> entry:mapElemCheckbox.entrySet()){
            entry.getValue().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    calculateElemToShow();
                    resetChartDmg();
                    resetChartRank();
                    setDmgData();
                    setRankData();
                }
            });
        }
    }

    private void initLineCharts() {
        calculateElemToShow();
        initLineChartDmg();
        initLineChartRank();
        buildCharts();
        chartDmg.animateXY(750, 1000);
        chartRank.animateXY(750, 1000);
    }

    private void initLineChartDmg(){
        chartDmg =mainView.findViewById(R.id.line_chart_time);
        setChartPara(chartDmg);
        chartDmg.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                tools.customToast(mC,e.getData().toString(),"center");
            }

            @Override
            public void onNothingSelected() {
                resetChartDmg();
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
        chart.getAxisRight().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1.0f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    private void calculateElemToShow() {
        typeSelected = new ArrayList<>();
        for(Map.Entry<String,CheckBox> entry:mapElemCheckbox.entrySet()){
            if (entry.getValue().isChecked()) {
                typeSelected.add(entry.getKey());
            }
        }
    }

    private void initLineChartRank() {
        chartRank =mainView.findViewById(R.id.line_chart_time_dmg);
        setChartPara(chartRank);
        chartRank.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
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

    private void buildCharts() {
        computeHashmaps();
        setDmgData();
        setRankData();
        formatAxis(chartDmg);
        formatAxis(chartRank);
    }

    private void formatAxis(LineChart chart) {
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setGranularity(1.0f);
        leftAxis.setGranularityEnabled(true);

        chart.getAxisRight().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelList));
        xAxis.setLabelRotationAngle(-90);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);
    }

    private void computeHashmaps() {
        mapDatetxtStatslist = new LinkedHashMap<>();
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy", Locale.FRANCE);
        for (SpellStat stat : pj.getSpellStats().getSpellStatsList().asList()){
            String dateTxt = formater.format(stat.getDate());
            if(mapDatetxtStatslist.get(dateTxt)==null) {
                mapDatetxtStatslist.put(dateTxt,new SpellStatsList());
            }
            mapDatetxtStatslist.get(dateTxt).add(stat);
        }
    }

    private void setDmgData() {
        labelList=new ArrayList<>();
        LineData data = new LineData();

        if(typeSelected.size()==6 || (typeSelected.size()==mapElemCheckbox.entrySet().size() && !typeSelected.contains(""))){
            addDmgAllTypesData(data);
        } else {
            addDmgTypesData(data);
        }
        data.setValueTextSize(infoTxtSize);
        chartDmg.setData(data);
    }

    private void addDmgAllTypesData(LineData data) {
        ArrayList<Entry> listVal = new ArrayList<>();
        nDate=0;
        for (String key : mapDatetxtStatslist.keySet()){
            float dmgSumMoy=0;
            int count=0;
            for (SpellStat stat : pj.getSpellStats().getSpellStatsList().filterByDate(key).asList()){
                if(stat.getMoyDmg()>0){
                    dmgSumMoy+=stat.getMoyDmg();
                    count++;
                }
            }
            if(dmgSumMoy>0) {
                listVal.add(new Entry((int) nDate, Math.round(dmgSumMoy/count), Math.round(dmgSumMoy/count) + " dégâts en moyenne\nle " + key + " pour l'ensemble des sorts"));
            }
            nDate++;
            labelList.add(key);
        }
        if(listVal.size()>0) {
            LineDataSet elemSet = new LineDataSet(listVal, "tout");
            setLinePara(elemSet,ContextCompat.getColor(mC,R.color.all_stat));
            data.addDataSet(elemSet);
        }
    }

    private void addDmgTypesData(LineData data) {
        for(String type : typeSelected) {
            ArrayList<Entry> listVal = new ArrayList<>();
            nDate=0;
            for (String key : mapDatetxtStatslist.keySet()){
                float dmgSumMoy=0;
                int count=0;
                for (SpellStat stat : pj.getSpellStats().getSpellStatsList().filterByDate(key).asList()){
                    if(stat.getElemMoyDmg(type)>0){
                        dmgSumMoy+=stat.getElemMoyDmg(type);
                        count++;
                    }
                }
                if(dmgSumMoy>0) {
                    listVal.add(new Entry((int) nDate, Math.round(dmgSumMoy/count), Math.round(dmgSumMoy/count) + " dégâts en moyenne\nle " + key + " pour les sorts de type "+types.getName(type)));
                }
                nDate++;
                labelList.add(key);

            }

            if(listVal.size()>0) {
                LineDataSet elemSet = new LineDataSet(listVal, types.getName(type));
                setLinePara(elemSet, types.getColorIdDark(type));
                data.addDataSet(elemSet);
            }
        }
    }


    private void setRankData() {
        LineData data = new LineData();

        if(typeSelected.size()==6){
            addMetaAllTypesData(data);
        } else {
            addmetaTypesData(data);
        }
        data.setValueTextSize(infoTxtSize);
        chartRank.setData(data);
    }

    private void addMetaAllTypesData(LineData data) {
        ArrayList<Entry> listVal = new ArrayList<>();
        nDate=0;
        for (String key : mapDatetxtStatslist.keySet()){
            float rankSumMoy=0;
            int count=0;
            for (SpellStat stat : pj.getSpellStats().getSpellStatsList().filterByDate(key).asList()){
                if(stat.getRankMoy()>0){
                    rankSumMoy+=stat.getRankMoy();
                    count++;
                }
            }
            if(rankSumMoy>0) {
                listVal.add(new Entry((int) nDate, Math.round(rankSumMoy/count), Math.round(rankSumMoy/count) + " rang de sort en moyenne\nlancé le " + key + " pour l'ensemble des sorts"));
            }
            nDate++;
        }

        if(listVal.size()>0) {
            LineDataSet elemSet = new LineDataSet(listVal, "tout");
            setLinePara(elemSet, ContextCompat.getColor(mC,R.color.all_stat));
            data.addDataSet(elemSet);
        }
    }

    private void addmetaTypesData(LineData data) {
        for(String type : typeSelected) {
            ArrayList<Entry> listVal = new ArrayList<>();
            nDate=0;
            for (String key : mapDatetxtStatslist.keySet()){
                float rankSumMoy=0;
                int count=0;
                for (SpellStat stat : pj.getSpellStats().getSpellStatsList().filterByDate(key).asList()){
                    if(stat.getElemRankMoy(type)>0){
                        rankSumMoy+=stat.getElemRankMoy(type);
                        count++;
                    }
                }
                if(rankSumMoy>0) {
                    listVal.add(new Entry((int) nDate, Math.round(rankSumMoy/count), Math.round(rankSumMoy/count) + " rang de sort en moyenne\nlancé le " + key + " pour les sorts de type"+types.getName(type)));
                }
                nDate++;
            }

            if(listVal.size()>0) {
                LineDataSet elemSet = new LineDataSet(listVal, types.getName(type));
                setLinePara(elemSet, types.getColorIdDark(type));
                data.addDataSet(elemSet);
            }
        }
    }


    private void setLinePara(LineDataSet set,int color) {
        try {
            set.setColors(color);
            set.setCircleColor(color);
        } catch (Exception e) {
            e.printStackTrace();
        }
        set.setLineWidth(2f);   set.setCircleRadius(4f);  set.setValueFormatter(new LargeValueFormatter());
    }

    // Resets
    public void reset() {
        for(Map.Entry<String,CheckBox> entry:mapElemCheckbox.entrySet()){
            entry.getValue().setChecked(true);
        }
        resetChartDmg();
        resetChartRank();
        buildCharts();
    }

    private void resetChartDmg() {
        chartDmg.invalidate();
        chartDmg.fitScreen();
        chartDmg.highlightValue(null);
    }

    private void resetChartRank() {
        calculateElemToShow();
        chartRank.invalidate();
        chartRank.fitScreen();
        chartRank.highlightValue(null);
    }
}


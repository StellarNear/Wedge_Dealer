package stellarnear.wedge_companion.SettingsFraments.DisplayStatsScreenFragment;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stellarnear.wedge_companion.Elems.ElemsManager;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Stats.Stat;
import stellarnear.wedge_companion.Stats.StatsList;
import stellarnear.wedge_companion.Tools;

public class DSSFGraph {
    private Perso pj = PersoManager.getCurrentPJ();
    private Context mC;
    private View mainView;
    private ElemsManager elems;
    private List<String> elemsSelected;
    private Map<Integer,StatsList> mapNHitStats = new HashMap<>();
    private Map<Integer,StatsList> mapNCritStats = new HashMap<>();
    private Map<Integer,StatsList> mapNCritNatStats = new HashMap<>();
    private Map<Integer,StatsList> mapNAllCritStats = new HashMap<>();
    private Map<String,CheckBox> mapElemCheckbox=new HashMap<>();
    private int infoTxtSize=10;
    private LineChart chartDmgNAtk;
    private LineChart chartElemDmgNCrit;
    private int nthAtkMax=0;
    private Tools tools=new Tools();

    public DSSFGraph(View mainView, Context mC) {
        this.mainView = mainView;
        this.mC = mC;
        this.elems= ElemsManager.getInstance(mC);
        CheckBox checkPhy = mainView.findViewById(R.id.line_type_phy);
        CheckBox checkFire = mainView.findViewById(R.id.line_type_fire);
        CheckBox checkShock = mainView.findViewById(R.id.line_type_shock);
        CheckBox checkFrost = mainView.findViewById(R.id.line_type_frost);
        mapElemCheckbox.put("",checkPhy); mapElemCheckbox.put("fire",checkFire);  mapElemCheckbox.put("shock",checkShock); mapElemCheckbox.put("frost",checkFrost);
        setCheckboxListeners();
        initLineCharts();
    }

    private void setCheckboxListeners() {
        for(String elem : elems.getListKeysWedgeDamage()){
            mapElemCheckbox.get(elem).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    calculateElemToShow();
                    resetChartElemDmgNcrit();
                    setElemData();
                }
            });
        }
    }

    private void initLineCharts() {
        initLineChartDmgNAtk();
        calculateElemToShow();
        initLineChartElemDmgNCrit();
        buildCharts();
        chartDmgNAtk.animateXY(750, 1000);
        chartElemDmgNCrit.animateXY(750, 1000);
    }

    private void initLineChartDmgNAtk(){
        chartDmgNAtk=mainView.findViewById(R.id.line_chart_dmg_atk);
        setChartPara(chartDmgNAtk);
        chartDmgNAtk.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                tools.customToast(mC,e.getData().toString(),"center");
            }
            @Override
            public void onNothingSelected() {
                resetChartDmgNatk();
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
        for (String elem : elems.getListKeysWedgeDamage()) {
            if (mapElemCheckbox.get(elem).isChecked()) {
                elemsSelected.add(elem);
            }
        }
    }

    private void initLineChartElemDmgNCrit() {
        chartElemDmgNCrit=mainView.findViewById(R.id.line_chart_crit_elem);
        setChartPara(chartElemDmgNCrit);

        chartElemDmgNCrit.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                tools.customToast(mC,e.getData().toString(),"center");
            }

            @Override
            public void onNothingSelected() {
                resetChartElemDmgNcrit();
            }
        });
    }

    private void buildCharts() {
        computeHashmaps();
        setDmgData();
        setElemData();
    }

    private void computeHashmaps() {
        mapNHitStats = new HashMap<>();
        mapNCritStats = new HashMap<>();
        mapNCritNatStats = new HashMap<>();
        mapNAllCritStats = new HashMap<>();
        mapNHitStats.put(0,new StatsList());
        nthAtkMax=0;
        for (Stat stat : pj.getStats().getStatsList().asList()){
            int nAtk=stat.getNAtksHit();
            if(nAtk>0){
                if(mapNHitStats.get(nAtk)!=null){
                    mapNHitStats.get(nAtk).add(stat);
                } else {
                    StatsList newStatL=new StatsList();
                    newStatL.add(stat);
                    mapNHitStats.put(nAtk,newStatL);
                }
            }
            if(nAtk>nthAtkMax){nthAtkMax=nAtk;}

            int nCrit=stat.getNCrit()-stat.getNCritNat();
            if(mapNCritStats.get(nCrit)!=null){
                mapNCritStats.get(nCrit).add(stat);
            } else {
                StatsList newStatL=new StatsList();
                newStatL.add(stat);
                mapNCritStats.put(nCrit,newStatL);
            }

            int nCritNat=stat.getNCritNat();
            if(mapNCritNatStats.get(nCritNat)!=null){
                mapNCritNatStats.get(nCritNat).add(stat);
            } else {
                StatsList newStatL=new StatsList();
                newStatL.add(stat);
                mapNCritNatStats.put(nCritNat,newStatL);
            }

            int nAllCrit=stat.getNCrit();
            if(mapNAllCritStats.get(nAllCrit)!=null){
                mapNAllCritStats.get(nAllCrit).add(stat);
            } else {
                StatsList newStatL=new StatsList();
                newStatL.add(stat);
                mapNAllCritStats.put(nAllCrit,newStatL);
            }
        }
    }

    private void setDmgData() {
        ArrayList<Entry> listValHit = new ArrayList<>();
        ArrayList<Entry> listValCrit = new ArrayList<>();
        ArrayList<Entry> listValCritNat = new ArrayList<>();
        for (int i=0;i<=nthAtkMax;i++){
            if (mapNHitStats.get(i) != null ) {
                int dmgMoy=mapNHitStats.get(i).getMoyDmg();
                listValHit.add(new Entry((int) i, (int) dmgMoy,dmgMoy+" dégâts en moyenne\nlorsque on a "+i+" coups touchent"));
            }
            if (mapNCritStats.get(i) != null ) {
                int dmgMoyCrit=mapNCritStats.get(i).getMoyDmg();
                listValCrit.add(new Entry((int) i, (int) dmgMoyCrit,dmgMoyCrit+" dégâts en moyenne\nlorsque on a "+i+" coups critiques"));
            }
            if (mapNCritNatStats.get(i) != null ) {
                int dmgMoyCritNat=mapNCritNatStats.get(i).getMoyDmg();
                listValCritNat.add(new Entry((int) i, (int) dmgMoyCritNat,dmgMoyCritNat+" dégâts en moyenne\nlorsque on a "+i+" coups critiques naturels"));
            }
        }
        LineDataSet setHit = new LineDataSet(listValHit,"nHit");
        setLinePara(setHit,mC.getColor(R.color.hit_stat));
        LineDataSet setCrit = new LineDataSet(listValCrit,"nCrit");
        setLinePara(setCrit,mC.getColor(R.color.crit_stat));
        LineDataSet setCritNat = new LineDataSet(listValCritNat,"nCritNat");
        setLinePara(setCritNat,mC.getColor(R.color.crit_nat_stat));

        LineData data = new LineData();
        data.addDataSet(setHit);
        data.addDataSet(setCrit);
        data.addDataSet(setCritNat);
        data.setValueTextSize(infoTxtSize);
        chartDmgNAtk.setData(data);
    }

    private void setElemData() {
        LineData data = new LineData();
        int minAxis=0;
        for(String elem : elemsSelected) {
            ArrayList<Entry> listValElem = new ArrayList<>();
            for (int i=0;i<=nthAtkMax;i++){
                if (mapNAllCritStats.get(i) != null ) {
                    int dmgMoyElem=mapNAllCritStats.get(i).getMoyDmgElem(elem);
                    if(minAxis==0){ minAxis=dmgMoyElem; }
                    if(minAxis>dmgMoyElem){ minAxis=dmgMoyElem; }
                    listValElem.add(new Entry((int) i, (int) dmgMoyElem,dmgMoyElem+" dégâts "+elems.getName(elem)+" en moyenne\nlorsque on a "+i+" coups critiques (crit+critNat)"));
                }
            }
            LineDataSet setElem = new LineDataSet(listValElem,elems.getName(elem));
            setLinePara(setElem,elems.getColorId(elem));
            data.addDataSet(setElem);

            if(elem.equalsIgnoreCase("")){
                ArrayList<Entry> listValElemCritNat = new ArrayList<>();
                for (int i=0;i<=nthAtkMax;i++){
                    if (mapNCritNatStats.get(i) != null ) {
                        int dmgCritPhyNat=mapNCritNatStats.get(i).getMoyDmgElem(elem);
                        listValElemCritNat.add(new Entry((int) i, (int) dmgCritPhyNat,dmgCritPhyNat+" dégâts "+elems.getName(elem)+" en moyenne\nlorsque on a "+i+" coups critiques naturels"));
                    }
                }
                LineDataSet setElemCrit = new LineDataSet(listValElemCritNat,elems.getName(elem)+" critNat");
                setLinePara(setElemCrit,elems.getColorId(elem));
                setElemCrit.setColor(elems.getColorId(elem),150);
                setElemCrit.enableDashedLine(10f,10f,0f);
                data.addDataSet(setElemCrit);
            }
        }
        data.setValueTextSize(infoTxtSize);
        chartElemDmgNCrit.getAxisLeft().setAxisMinimum(1f*minAxis);
        chartElemDmgNCrit.setData(data);
    }

    private void setLinePara(LineDataSet set,int color) {
        set.setColors(color);   set.setLineWidth(2f);   set.setCircleRadius(4f); set.setCircleColor(color); set.setValueFormatter(new LargeValueFormatter());
    }

    // Resets
    public void reset() {
        for(String elem : elems.getListKeysWedgeDamage()){
            mapElemCheckbox.get(elem).setChecked(true);
        }
        resetChartDmgNatk();
        resetChartElemDmgNcrit();
        buildCharts();
    }

    private void resetChartDmgNatk() {
        chartDmgNAtk.invalidate();
        chartDmgNAtk.fitScreen();
        chartDmgNAtk.highlightValue(null);
    }

    private void resetChartElemDmgNcrit() {
        calculateElemToShow();
        chartElemDmgNCrit.invalidate();
        chartElemDmgNCrit.fitScreen();
        chartElemDmgNCrit.highlightValue(null);
    }
}


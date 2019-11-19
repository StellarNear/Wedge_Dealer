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

import stellarnear.wedge_companion.Activities.MainActivity;
import stellarnear.wedge_companion.Elems.ElemsManager;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Stats.Stat;
import stellarnear.wedge_companion.Stats.StatsList;
import stellarnear.wedge_companion.Tools;

public class DSSFTime {
    private Perso wedge = MainActivity.wedge;
    private Context mC;
    private View mainView;
    private ElemsManager elems;
    private List<String> elemsSelected;
    private LinkedHashMap<String, StatsList> mapDatetxtStatslist = new LinkedHashMap<>();
    private List<String> labelList=new ArrayList<>();
    private Map<String,CheckBox> mapElemCheckbox=new HashMap<>();
    private int infoTxtSize=10;
    private LineChart chartAtk;
    private LineChart chartDmg;
    private Tools tools=new Tools();

    public DSSFTime(View mainView, Context mC) {
        this.mainView = mainView;
        this.mC = mC;
        this.elems= ElemsManager.getInstance(mC);
        CheckBox checkPhy = mainView.findViewById(R.id.line_type_time_phy);
        CheckBox checkFire = mainView.findViewById(R.id.line_type_time_fire);
        CheckBox checkShock = mainView.findViewById(R.id.line_type_time_shock);
        CheckBox checkFrost = mainView.findViewById(R.id.line_type_time_frost);
        mapElemCheckbox.put("",checkPhy); mapElemCheckbox.put("fire",checkFire);  mapElemCheckbox.put("shock",checkShock); mapElemCheckbox.put("frost",checkFrost);
        setCheckboxListeners();
        initLineCharts();
    }

    private void setCheckboxListeners() {
        for(String elem : elems.getListKeys()){
            mapElemCheckbox.get(elem).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    calculateElemToShow();
                    resetChartDmg();
                    setDmgData();
                }
            });
        }
    }

    private void initLineCharts() {
        initLineChartAtk();
        calculateElemToShow();
        initLineChartDmg();
        buildCharts();
        chartAtk.animateXY(750, 1000);
        chartDmg.animateXY(750, 1000);
    }

    private void initLineChartAtk(){
        chartAtk =mainView.findViewById(R.id.line_chart_time);
        setChartPara(chartAtk);
        chartAtk.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                tools.customToast(mC,e.getData().toString(),"center");
            }

            @Override
            public void onNothingSelected() {
                resetChartAtk();
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
        elemsSelected = new ArrayList<>();
        for (String elem : elems.getListKeys()) {
            if (mapElemCheckbox.get(elem).isChecked()) {
                elemsSelected.add(elem);
            }
        }
    }

    private void initLineChartDmg() {
        chartDmg =mainView.findViewById(R.id.line_chart_time_dmg);
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

    private void buildCharts() {
        computeHashmaps();
        setAtkData();
        setDmgData();
        formatAxis(chartAtk);
        formatAxis(chartDmg);
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
        for (Stat stat : wedge.getStats().getStatsList().asList()){
            String dateTxt = formater.format(stat.getDate());
            if(mapDatetxtStatslist.get(dateTxt)==null) {
                mapDatetxtStatslist.put(dateTxt,new StatsList());
            }
            mapDatetxtStatslist.get(dateTxt).add(stat);
        }
    }

    private void setAtkData() {
        labelList=new ArrayList<>();
        ArrayList<Entry> listValHit = new ArrayList<>();
        ArrayList<Entry> listValCrit = new ArrayList<>();
        ArrayList<Entry> listValCritNat = new ArrayList<>();
        int index=0;
        for (String key : mapDatetxtStatslist.keySet()){
            int sumHit=mapDatetxtStatslist.get(key).getNAtksHit();
            int nTot = mapDatetxtStatslist.get(key).getNAtksTot();
            int nCrit = mapDatetxtStatslist.get(key).getNCrit();
            int nCritNat = mapDatetxtStatslist.get(key).getNCritNat();

            listValHit.add(new Entry((int) index, (int) (100f*sumHit/nTot),(int) (100f*sumHit/nTot)+"% touché en moyenne le "+key));
            listValCrit.add(new Entry((int) index, (int) (100f*(nCrit-nCritNat)/nTot),(int) (100f*(nCrit-nCritNat)/nTot)+"% critique en moyenne le "+key));
            listValCritNat.add(new Entry((int) index, (int) (100f*nCritNat/nTot),(int) (100f*nCritNat/nTot)+"% critique naturel en moyenne le "+key));
            labelList.add(key);
            index++;
        }
        LineDataSet setHit = new LineDataSet(listValHit,"Hit");
        setLinePara(setHit,mC.getColor(R.color.hit_stat));

        LineDataSet setCrit = new LineDataSet(listValCrit,"Crit");
        setLinePara(setCrit,mC.getColor(R.color.crit_stat));

        LineDataSet setCritNat = new LineDataSet(listValCritNat,"CritNat");
        setLinePara(setCritNat,mC.getColor(R.color.crit_nat_stat));

        LineData data = new LineData();
        data.addDataSet(setHit);
        data.addDataSet(setCrit);
        data.addDataSet(setCritNat);
        data.setValueTextSize(infoTxtSize);
        chartAtk.setData(data);
    }


    private void setDmgData() {
        LineData data;
        if(elemsSelected.size()==4){
            data=getDmgDataSolo();
        } else {
            data=getDmgDataElems();
        }
        data.setValueTextSize(infoTxtSize);
        chartDmg.setData(data);
    }

    private LineData getDmgDataSolo() {
        ArrayList<Entry> listValDmgMoy = new ArrayList<>();
        int index=0;
        for (String key : mapDatetxtStatslist.keySet()){
            int dmgMoy=mapDatetxtStatslist.get(key).getMoyDmg();
            listValDmgMoy.add(new Entry((int) index, dmgMoy,dmgMoy+" dégâts en moyenne le "+key));
            index++;
        }
        LineDataSet setHit = new LineDataSet(listValDmgMoy,"tout");
        setLinePara(setHit,mC.getColor(R.color.dmg_stat));
        LineData data = new LineData();
        data.addDataSet(setHit);
        return data;
    }

    private LineData getDmgDataElems() {
        LineData data = new LineData();
        for(String elem : elemsSelected) {
            ArrayList<Entry> listDmgMoy = new ArrayList<>();
            int index = 0;
            for (String key : mapDatetxtStatslist.keySet()) {
                int dmgMoy = mapDatetxtStatslist.get(key).getMoyDmgElem(elem);
                listDmgMoy.add(new Entry((int) index, dmgMoy,dmgMoy+" dégâts de "+elems.getName(elem)+" en moyenne le "+key));
                index++;
            }
            LineDataSet setVal = new LineDataSet(listDmgMoy, elems.getName(elem));
            setLinePara(setVal, elems.getColorId(elem));
            data.addDataSet(setVal);
        }
        return data;
    }

    private void setLinePara(LineDataSet set,int color) {
        set.setColors(color);   set.setLineWidth(2f);   set.setCircleRadius(4f); set.setCircleColor(color); set.setValueFormatter(new LargeValueFormatter());
    }

    // Resets
    public void reset() {
        for(String elem : elems.getListKeys()){
            mapElemCheckbox.get(elem).setChecked(true);
        }
        resetChartAtk();
        resetChartDmg();
        buildCharts();
    }

    private void resetChartAtk() {
        chartAtk.invalidate();
        chartAtk.fitScreen();
        chartAtk.highlightValue(null);
    }

    private void resetChartDmg() {
        calculateElemToShow();
        chartDmg.invalidate();
        chartDmg.fitScreen();
        chartDmg.highlightValue(null);
    }
}


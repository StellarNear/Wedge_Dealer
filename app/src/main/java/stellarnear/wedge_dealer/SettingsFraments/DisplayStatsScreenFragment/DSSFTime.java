package stellarnear.wedge_dealer.SettingsFraments.DisplayStatsScreenFragment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
import java.util.TreeMap;

import stellarnear.wedge_dealer.Elems.ElemsManager;
import stellarnear.wedge_dealer.MainActivity;
import stellarnear.wedge_dealer.Perso.Perso;
import stellarnear.wedge_dealer.R;
import stellarnear.wedge_dealer.Stats.Stat;
import stellarnear.wedge_dealer.Stats.StatsList;
import stellarnear.wedge_dealer.Tools;

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
    private int nthAtkMax=0;
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
        chart.getLegend().setEnabled(false);
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

            listValHit.add(new Entry((int) index, (int) (100f*sumHit/nTot)));
            listValCrit.add(new Entry((int) index, (int) (100f*(nCrit-nCritNat)/nTot)));
            listValCritNat.add(new Entry((int) index, (int) (100f*nCritNat/nTot)));
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


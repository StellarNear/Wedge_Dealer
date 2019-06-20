package stellarnear.wedge_dealer.SettingsFraments.DisplayStatsScreenFragment;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.CheckBox;

import com.github.mikephil.charting.animation.Easing;
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

import stellarnear.wedge_dealer.MainActivity;
import stellarnear.wedge_dealer.Perso.Perso;
import stellarnear.wedge_dealer.R;
import stellarnear.wedge_dealer.Stats.Stat;
import stellarnear.wedge_dealer.Stats.StatsList;
import stellarnear.wedge_dealer.Tools;

public class DSSFGraph {
    private Perso wedge = MainActivity.wedge;

    private Context mC;
    private View mainView;
    private List<String> listElems= Arrays.asList("","fire","shock","frost");
    private Map<String,CheckBox> mapElemCheckbox=new HashMap<>();
    private int infoTxtSize=10;

    private LineChart chartDmgNAtk;
    private LineChart chartElemDmgNCrit;

    private Tools tools=new Tools();


    public DSSFGraph(View mainView, Context mC) {
        this.mainView = mainView;
        this.mC = mC;

        CheckBox checkPhy = mainView.findViewById(R.id.line_type_phy);
        CheckBox checkFire = mainView.findViewById(R.id.line_type_fire);
        CheckBox checkShock = mainView.findViewById(R.id.line_type_shock);
        CheckBox checkFrost = mainView.findViewById(R.id.line_type_frost);
        mapElemCheckbox.put("",checkPhy);
        mapElemCheckbox.put("fire",checkFire);
        mapElemCheckbox.put("shock",checkShock);
        mapElemCheckbox.put("frost",checkFrost);

        initLineChartDmgNAtk();

        initLineChartElemDmgNCrit();
        setCheckboxListeners();
    }




    private void initLineChartDmgNAtk() {
        chartDmgNAtk=mainView.findViewById(R.id.line_chart_dmg_atk);
        chartDmgNAtk.getDescription().setEnabled(false);
        chartDmgNAtk.setDrawGridBackground(false);
        chartDmgNAtk.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        chartDmgNAtk.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        chartDmgNAtk.getXAxis().setDrawGridLines(false);

        YAxis leftAxis = chartDmgNAtk.getAxisLeft();
        leftAxis.setGranularity(1.0f);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0); // this replaces setStartAtZero(true)

        chartDmgNAtk.getAxisRight().setEnabled(false);
        XAxis xAxis = chartDmgNAtk.getXAxis();
        xAxis.setGranularity(1.0f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

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

        buildChart();
        chartDmgNAtk.animateXY(750, 1000);
    }

    private void buildChart() {
        Map<Integer,StatsList> mapNHitStats = new HashMap<>();
        Map<Integer,StatsList> mapNCritStats = new HashMap<>();
        Map<Integer,StatsList> mapNCritNatStats = new HashMap<>();
        mapNHitStats.put(0,new StatsList());

        int nthAtkMax=0;
        for (Stat stat : wedge.getStats().getStatsList().asList()){
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
        }

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

    private void setLinePara(LineDataSet set,int color) {
        set.setColors(color);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setCircleColor(color);
        set.setValueFormatter(new LargeValueFormatter());
    }

    private void initLineChartElemDmgNCrit() {
        chartElemDmgNCrit=mainView.findViewById(R.id.line_chart_crit_elem);
    }

    private void setCheckboxListeners() {
        for(String elem : listElems){
            onCheckboxClicked(mapElemCheckbox.get(elem));
        }
    }

    private void onCheckboxClicked(CheckBox view) {
        switch(view.getId()) {
            case R.id.line_type_phy:
                view.setCompoundDrawablesWithIntrinsicBounds(tools.resize(mC,R.drawable.phy_logo,75),null,null,null);
                break;
            case R.id.line_type_fire:
                view.setCompoundDrawablesWithIntrinsicBounds(tools.resize(mC,R.drawable.fire_logo,75),null,null,null);
                break;
            case R.id.line_type_shock:
                view.setCompoundDrawablesWithIntrinsicBounds(tools.resize(mC,R.drawable.shock_logo,75),null,null,null);
                break;
            case R.id.line_type_frost:
                view.setCompoundDrawablesWithIntrinsicBounds(tools.resize(mC,R.drawable.frost_logo,75),null,null,null);
                break;
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo
            }
        });
    }

    // Resets

    public void reset() {
        for(String elem : listElems){
            mapElemCheckbox.get(elem).setChecked(true);
        }
        resetChartDmgNatk();
    }

    private void resetChartDmgNatk() {
        chartDmgNAtk.invalidate();
        chartDmgNAtk.fitScreen();
        chartDmgNAtk.highlightValue(null);
    }


}


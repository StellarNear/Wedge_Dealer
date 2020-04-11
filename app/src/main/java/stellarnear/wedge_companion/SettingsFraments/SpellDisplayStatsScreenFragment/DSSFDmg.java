package stellarnear.wedge_companion.SettingsFraments.SpellDisplayStatsScreenFragment;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
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

import stellarnear.wedge_companion.Elems.ElemsManager;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Stats.SpellStats.DamagesShortList;
import stellarnear.wedge_companion.Stats.SpellStats.DamagesShortListElement;
import stellarnear.wedge_companion.Stats.SpellStats.SpellStat;
import stellarnear.wedge_companion.Stats.SpellStats.SpellStatsList;
import stellarnear.wedge_companion.Tools;

public class DSSFDmg {
    private Perso pj = PersoManager.getCurrentPJ();
    private DSSFDmgChartMaker chartMaker;
    private DSSFDmgInfoManager subManager;
    private PieChart pieChart;

    private Context mC;
    private View mainView;
    private ElemsManager elems;
    private Map<String, CheckBox> mapElemCheckbox = new HashMap<>();
    private DamagesShortList selectedDamagesShortList = new DamagesShortList();
    private int infoTxtSize = 12;

    private Tools tools = Tools.getTools();

    public DSSFDmg(View mainView, Context mC) {
        this.mainView = mainView;
        this.mC = mC;
        this.elems = ElemsManager.getInstance();

        TextView nAtkTxt = mainView.findViewById(R.id.nDmgTxt);
        nAtkTxt.setText(pj.getSpellStats().getSpellStatsList().getNDamageSpell() + " jets de dégâts");
        determineElemsPresents();
        setCheckboxListeners();
        chartMaker = new DSSFDmgChartMaker((BarChart) mainView.findViewById(R.id.bar_chart_dmg), mapElemCheckbox, mC);

        initChartSelectEvent();
        initPieChart();
        subManager = new DSSFDmgInfoManager(mainView, mapElemCheckbox, mC);
    }

    private void determineElemsPresents() {
        SpellStatsList allStats = pj.getSpellStats().getSpellStatsList();
        mapElemCheckbox = new HashMap<>();
        for (SpellStat spellStat : allStats.asList()) {
            for (DamagesShortListElement subDamageIndivSpell : spellStat.getDamageShortList().getDamageElementList()) {
                String elementSpell = subDamageIndivSpell.getElement();
                if (mapElemCheckbox.get(elementSpell) == null && !elementSpell.equalsIgnoreCase("")) { // pour le sdegat on ignore les spell utils
                    mapElemCheckbox.put(elementSpell, addCheckBox(elementSpell));
                }
            }
        }
    }

    private CheckBox addCheckBox(String elementSpell) {
        CheckBox checkBox = new CheckBox(mC);
        checkBox.setButtonTintList(ColorStateList.valueOf(elems.getColorId(elementSpell)));
        Drawable drawable;
        try {
            drawable = mC.getDrawable(elems.getDrawableId(elementSpell));
        } catch (Exception e) {
            drawable = mC.getDrawable(R.drawable.mire_test);
            e.printStackTrace();
        }
        ((LinearLayout) mainView.findViewById(R.id.fourth_panel_elems_checkboxes)).addView(checkBox);
        ImageView logo = new ImageView(mC);
        logo.setImageDrawable(drawable);
        tools.resize(logo, 80);
        ((LinearLayout) mainView.findViewById(R.id.fourth_panel_elems_checkboxes)).addView(logo);
        checkBox.setChecked(true);
        return checkBox;
    }

    private void setCheckboxListeners() {
        for (Map.Entry<String, CheckBox> entry : mapElemCheckbox.entrySet()) {
            entry.getValue().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chartMaker.resetChart();
                    chartMaker.buildChart();
                    resetPieChart();
                    buildPieChart();
                    subManager.addInfos(null);
                }
            });
        }
    }

    private void initChartSelectEvent() {
        chartMaker.getChart().setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                resetPieChart();
                selectedDamagesShortList = chartMaker.getMapIStepSelectedDamagesShortList().get((int) e.getX());
                buildPieChart();
                subManager.setSubSelectionBracket(chartMaker.getLabels().get((int) e.getX()));
                subManager.addInfos(selectedDamagesShortList);
            }

            @Override
            public void onNothingSelected() {
                if (chartMaker.getChart().isFocusable()) {
                    reset();
                    resetPieChart();
                    subManager.addInfos(null);
                    buildPieChart();
                }
            }
        });
    }

    // Pie chart

    private void initPieChart() {
        pieChart = mainView.findViewById(R.id.pie_chart_dmg_percent);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        buildPieChart();
        pieChart.animateXY(100, 1000);
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

    private void buildPieChart() {
        PieData data = new PieData();
        data.addDataSet(computePieDataSet());
        data.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setData(data);
    }

    private PieDataSet computePieDataSet() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        List<Integer> colorList = new ArrayList<>();

        DamagesShortList list;
        if (selectedDamagesShortList != null && selectedDamagesShortList.size() != 0) {
            list = selectedDamagesShortList;
        } else {
            list = pj.getSpellStats().getSpellStatsList().getDamageShortList();
        }
        int totalDmg = list.getSumDmgTot();
        for (Map.Entry<String, CheckBox> entry : mapElemCheckbox.entrySet()) {
            if (mapElemCheckbox.get(entry.getKey()).isChecked()) {
                float percent = 100f * (list.filterByElem(entry.getKey()).getDmgSum() / (float) totalDmg);
                if (percent > 0f) {
                    entries.add(new PieEntry(percent, "", new LargeValueFormatter().getFormattedValue(list.filterByElem(entry.getKey()).getDmgSum()) + " dégats " + elems.getName(entry.getKey())));
                    colorList.add(elems.getColorIdDark(entry.getKey()));
                }
            }
        }
        PieDataSet dataset = new PieDataSet(entries, "");
        dataset.setValueTextSize(infoTxtSize);
        dataset.setColors(colorList);
        dataset.setSliceSpace(3);
        return dataset;
    }

    // Resets

    public void reset() {
        for (Map.Entry<String, CheckBox> entry : mapElemCheckbox.entrySet()) {
            entry.getValue().setChecked(true);
        }
        chartMaker.resetChart();
        chartMaker.buildChart();
        subManager.addInfos(null);
        resetPieChart();
        buildPieChart();
    }


    private void resetPieChart() {
        selectedDamagesShortList = new DamagesShortList();
        pieChart.invalidate();
        pieChart.setCenterText("");
        pieChart.highlightValue(null);
    }
}

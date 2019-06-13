package stellarnear.wedge_dealer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.StackedValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

import com.github.mikephil.charting.charts.BarChart;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import stellarnear.wedge_dealer.Perso.Perso;
import stellarnear.wedge_dealer.Stats.Stat;


public class StatsDummy extends Preference {
    private BarChart chart;
    private Tools tools=new Tools();
    private Perso wedge = MainActivity.wedge;

    public StatsDummy(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public StatsDummy(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }
    public StatsDummy(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent )
    {
        super.onCreateView(parent);
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View mainBar = inflater.inflate(R.layout.stats_dummy, null);

        // create a new chart object
        chart = mainBar.findViewById(R.id.bar_chart);
        chart.getDescription().setEnabled(false);

        /*
        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv);*/

        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        BarData data = new BarData();

        Map<Integer,Integer> nthAtkCount = new HashMap<>();

        for (Stat stat : wedge.getStats().getListStats()){
            for (int nAtk : stat.getNthAtksHit()){
                int count = 0;
                if (nthAtkCount.get(nAtk) != null ) {
                    count=nthAtkCount.get(nAtk);
                }
                nthAtkCount.put(nAtk,count+1);
            }
        }
        ArrayList<BarEntry> listVal = new ArrayList<>();
        for (int i=1;i<=6;i++){
            int count = 0;
            if (nthAtkCount.get(i) != null ) {
                count=nthAtkCount.get(i);
            }
            listVal.add(new BarEntry((int)i,(int)count));
        }

        BarDataSet set = new BarDataSet(listVal,"nAtkNth");
        set.setValueTextSize(14);
       set.setValueFormatter(new LargeValueFormatter());
        data.addDataSet(set);
        chart.setData(data);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setGranularity(1.0f);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0); // this replaces setStartAtZero(true)

        chart.getAxisRight().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        //xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        return chart;
    }
}

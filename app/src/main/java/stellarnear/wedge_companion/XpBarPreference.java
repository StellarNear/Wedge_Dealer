package stellarnear.wedge_companion;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class XpBarPreference extends Preference {
    private Tools tools=Tools.getTools();

    public XpBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public XpBarPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }
    public XpBarPreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent )
    {
        super.onCreateView(parent);
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View mainBar = inflater.inflate(R.layout.xp_bar, null);

        mainBar.post(new Runnable() {
            @Override
            public void run() {
                TextView percent = mainBar.findViewById(R.id.xp_bar_percent);
                ImageView backgroundBar = mainBar.findViewById(R.id.xp_bar_background);
                ViewGroup.LayoutParams para = (ViewGroup.LayoutParams) backgroundBar.getLayoutParams();
                ImageView overlayBar = mainBar.findViewById(R.id.xp_bar_overlay);
                int oriWidth = overlayBar.getMeasuredWidth();
                int oriHeight = overlayBar.getMeasuredHeight();
                int currentXp = tools.toInt(settings.getString("current_xp", String.valueOf(getContext().getResources().getInteger(R.integer.current_xp_def))));
                int nextLvlXp = tools.toInt(settings.getString("next_level", ""));
                int previousLvlXp = tools.toInt(settings.getString("previous_level", ""));
                Double coef = (double) (currentXp - previousLvlXp) / (nextLvlXp - previousLvlXp);
                if (coef < 0d) {
                    coef = 0d;
                }
                if (coef > 1d) {
                    coef = 1d;
                }
                percent.setText(String.valueOf((int) (100 * coef)) + "%");
                para.width = (int) (coef * oriWidth);
                para.height = oriHeight;
                backgroundBar.setLayoutParams(para);
            }
        });

        return mainBar;
    }
}

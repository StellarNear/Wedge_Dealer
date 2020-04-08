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

import stellarnear.wedge_companion.Perso.PersoManager;


public class MythicBarPreference extends Preference {
    private Tools tools = Tools.getTools();

    public MythicBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public MythicBarPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public MythicBarPreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View mainBar = inflater.inflate(R.layout.mythic_bar, null);

        mainBar.post(new Runnable() {
            @Override
            public void run() {
                TextView percent = mainBar.findViewById(R.id.mythic_bar_percent);
                ImageView backgroundBar = mainBar.findViewById(R.id.mythic_bar_background);
                ViewGroup.LayoutParams para = backgroundBar.getLayoutParams();
                ImageView overlayBar = mainBar.findViewById(R.id.mythic_bar_overlay);
                int oriWidth = overlayBar.getMeasuredWidth();
                int oriHeight = overlayBar.getMeasuredHeight();
                int idValDef = getContext().getResources().getIdentifier("mythic_tier_def" + PersoManager.getPJSuffix(), "integer", getContext().getPackageName());
                int currentTier = tools.toInt(settings.getString("mythic_tier" + PersoManager.getPJSuffix(), String.valueOf(getContext().getResources().getInteger(idValDef))));

                Double coef = (double) currentTier / 10;
                if (coef < 0d) {
                    coef = 0d;
                }
                if (coef > 1d) {
                    coef = 1d;
                }
                percent.setText((int) (100 * coef) + "%");
                para.width = (int) (coef * oriWidth);
                para.height = oriHeight;
                backgroundBar.setLayoutParams(para);
            }
        });


        return mainBar;
    }
}

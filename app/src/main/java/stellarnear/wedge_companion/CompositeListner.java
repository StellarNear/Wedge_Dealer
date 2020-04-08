package stellarnear.wedge_companion;

import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import java.util.ArrayList;

public class CompositeListner implements OnCheckedChangeListener {
    private ArrayList<OnCheckedChangeListener> listeners = new ArrayList<>();

    public void addOnclickListener(OnCheckedChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        for (OnCheckedChangeListener l : listeners) {
            l.onCheckedChanged(compoundButton, isChecked);
        }
    }
}
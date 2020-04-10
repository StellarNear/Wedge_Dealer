package stellarnear.wedge_companion;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.wedge_companion.Perso.AllAdditionalsInfos;
import stellarnear.wedge_companion.Perso.Capacity;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;

public class DisplayInfos {
    private Perso pj = PersoManager.getCurrentPJ();
    private Activity mA;
    private Context mC;
    private CustomAlertDialog tooltipAlert;
    private LinearLayout mainLin;

    public DisplayInfos(Activity mA, Context mC) {
        this.mA = mA;
        this.mC = mC;
        buildAlertDialog();
        tooltipAlert.showAlert();
    }

    private void buildAlertDialog() {
        View tooltip = mA.getLayoutInflater().inflate(R.layout.infos_dialog, null);
        tooltipAlert = new CustomAlertDialog(mA, mC, tooltip);
        tooltipAlert.setFill("widthheight");
        tooltipAlert.setPermanent(true);
        tooltipAlert.clickToHide(tooltip.findViewById(R.id.infos_dialog_main_title_frame));

        mainLin = tooltip.findViewById(R.id.infos_main_fill_lin);
        fillMainLin();
    }

    private void fillMainLin() {
        mainLin.removeAllViews();
        for (final AllAdditionalsInfos.Info info : pj.getAllInfos().getListInfos()) {
            LinearLayout line = (LinearLayout) mA.getLayoutInflater().inflate(R.layout.additional_info_full_line, null);

            line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
            line.setWeightSum(10);
            if (info.getType().equalsIgnoreCase("capacity")) {
                View subValue = mA.getLayoutInflater().inflate(R.layout.additional_info_value, null);
                subValue.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 10));

                final Capacity capa = pj.getAllCapacities().getCapacity(info.getValue());
                ((TextView) subValue.findViewById(R.id.additional_info_value_text)).setText(capa.getName());
                ((TextView) subValue.findViewById(R.id.additional_info_value_text)).setTextColor(mC.getColor(R.color.darker_gray));

                subValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupLongTooltip(capa);
                    }
                });

                line.addView(subValue);
            } else {
                View subLabel = mA.getLayoutInflater().inflate(R.layout.additional_info_label, null);
                subLabel.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3));
                subLabel.findViewById(R.id.additional_info_label_icon).setVisibility(View.GONE);
                ((TextView) subLabel.findViewById(R.id.additional_info_label_text)).setText(info.getName() + " : ");
                ((TextView) subLabel.findViewById(R.id.additional_info_label_text)).setTextColor(mC.getColor(R.color.darker_gray));
                line.addView(subLabel);

                View subValue = mA.getLayoutInflater().inflate(R.layout.additional_info_value, null);
                subValue.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 7));
                ((TextView) subValue.findViewById(R.id.additional_info_value_text)).setText(info.getValue());
                ((TextView) subValue.findViewById(R.id.additional_info_value_text)).setTextColor(mC.getColor(R.color.darker_gray));
                line.addView(subValue);
            }
            mainLin.addView(line);
        }
    }

    private void popupLongTooltip(Capacity capa) {
        View tooltip = mA.getLayoutInflater().inflate(R.layout.custom_toast_info_form_capacity, null);
        final CustomAlertDialog tooltipAlert = new CustomAlertDialog(mA, mC, tooltip);
        tooltipAlert.setPermanent(true);
        ((TextView) tooltip.findViewById(R.id.toast_textName)).setText(capa.getName());
        ((TextView) tooltip.findViewById(R.id.toast_textDescr)).setText(capa.getDescr());
        tooltip.findViewById(R.id.button_use_capacity).setVisibility(View.GONE);
        tooltipAlert.clickToHide(tooltip.findViewById(R.id.toast_LinearLayout));
        tooltipAlert.showAlert();
    }


}
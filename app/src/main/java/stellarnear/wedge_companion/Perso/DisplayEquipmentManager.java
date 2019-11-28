package stellarnear.wedge_companion.Perso;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import stellarnear.wedge_companion.CustomAlertDialog;
import stellarnear.wedge_companion.R;

public class DisplayEquipmentManager {
    private Activity mA;
    private boolean editable;
    private Context mC;
    private List<Equipment> listEquipments;
    private OnRefreshEventListener mListener;
    private OnSaveEventListener mListenerSave;
    private Perso pj=PersoManager.getCurrentPJ();

    public DisplayEquipmentManager(Activity mA,Context mC, List<Equipment> listEquipments){
        this.mA=mA;
        this.mC=mC;
        this.listEquipments=listEquipments;
    }

    public void showSlot(Activity mA, String slotId, Boolean editable) {
        this.mA=mA;
        this.editable=editable;
        if (slotId.equalsIgnoreCase("other_slot")){
            customInfo(getSlotListEquipment("other_slot"));
        } else {
            customInfo(getEquipmentsEquiped(slotId));
        }
    }


    public Equipment getEquipmentsEquiped(String slot) {
        Equipment equiFind = null;
        for (Equipment equipment : listEquipments) {
            if (equipment.isEquiped() && equipment.getSlotId().equalsIgnoreCase(slot)) {
                equiFind=equipment;
            }
        }
        return equiFind;
    }

    private void customInfo(Equipment equi) {
        LayoutInflater inflater = mA.getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_toast_info, (ViewGroup) mA.findViewById(R.id.toast_RelativeLayout));
        CustomAlertDialog ct = new CustomAlertDialog(mA, mC, view);
        ct.clickToHide(view.findViewById(R.id.toast_LinearLayout));

        ImageView img = view.findViewById(R.id.toast_image);
        if (equi.getImg(mC) != null) {
            img.setImageDrawable(equi.getImg(mC));
        } else {
            img.setVisibility(View.GONE);
        }
        TextView name = view.findViewById(R.id.toast_textName);
        name.setText(equi.getName());
        TextView value = view.findViewById(R.id.toast_textVal);
        value.setText("Valeur : " + equi.getValue());
        TextView descr = view.findViewById(R.id.toast_textDescr);
        if (!equi.getDescr().equalsIgnoreCase("")) {
            descr.setText(equi.getDescr());
        } else {
            descr.setVisibility(View.GONE);
        }
        TextView stats = view.findViewById(R.id.toast_textStats);
        String allAddStats="";
        if (equi.getArmor()>0) {
            allAddStats += "\nArmure : +" + equi.getArmor();
        }
        if (equi.getMapAbilityUp()!=null && equi.getMapAbilityUp().size()>0) {
            allAddStats += "\nBonus Stats : " + makeStringLineFromMap(equi.getMapAbilityUp());
        }
        if (equi.getMapSkillUp()!=null && equi.getMapSkillUp().size()>0) {
            allAddStats += "\nBonus Compétence : " + makeStringLineFromMap(equi.getMapSkillUp());
        }
        if (!allAddStats.equalsIgnoreCase("")) {
            stats.setText(allAddStats);
        } else {
            stats.setVisibility(View.GONE);
        }

        if(editable) {
            List<Equipment> spareEquipments = getSpareEquipment(equi.getSlotId());
            if (spareEquipments.size() > 0) {
                ImageView swap = view.findViewById(R.id.toast_info_swap);
                setButtonToSwap(swap, spareEquipments, ct);
            } else {
                ImageView unequip = view.findViewById(R.id.toast_info_unequip);
                setButtonToUnequip(unequip, equi, ct);
            }
        }
        ct.showAlert();
    }

    private String makeStringLineFromMap(Map<String, Integer> mapUp) {
        String line="";
        for(Map.Entry<String,Integer> entry : mapUp.entrySet()) {
            try {
                String nameUp = entry.getKey().contains("skill_")? pj.getAllSkills().getSkill(entry.getKey()).getName():pj.getAllAbilities().getAbi(entry.getKey()).getName();
                line+="\n+"+entry.getValue()+" "+nameUp;
            } catch (Exception e) {}
        }
        return line;
    }

    private void setButtonToSwap(ImageView swap, final List<Equipment> spareEquipments, final CustomAlertDialog ct) {
        swap.setVisibility(View.VISIBLE);
        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ct.dismissAlert();
                showSpareList(spareEquipments);
            }
        });
    }

    public void showSpareList(List<Equipment> spareEquipments) {
        customInfo(spareEquipments,true);
    }

    public void showSpareList(Activity mA,List<Equipment> spareEquipments,Boolean editable) {
        this.mA=mA;
        this.editable=editable;
        customInfo(spareEquipments,true);
    }

    private void setButtonToUnequip(ImageView unequip, final Equipment equi, final  CustomAlertDialog ct) {
        unequip.setVisibility(View.VISIBLE);
        unequip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mA)
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setTitle("Enlevern")
                        .setMessage("Es-tu sûre de vouloir déséquiper cet objet ?")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                equi.setEquiped(false);
                                if(mListenerSave!=null){ mListenerSave.onEvent();}
                                ct.dismissAlert();
                                if(mListener!=null){ mListener.onEvent();}

                            }
                        })
                        .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ct.dismissAlert();
                            }
                        }).show();
            }
        });
    }

    private void customInfo(List<Equipment> equipmentsList,Boolean... selectToEquipBool) {
        Boolean selectToEquip=selectToEquipBool.length > 0 ? selectToEquipBool[0] : false;
        LayoutInflater inflater = mA.getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_toast_list_info, null);
        final CustomAlertDialog ca = new CustomAlertDialog(mA, mC, view);
        ca.setPermanent(true);
        ca.clickToHide(view.findViewById(R.id.toast_list_title_frame));
        if(selectToEquip){
            TextView title = view.findViewById(R.id.toast_list_title);
            title.setText("Rechanges possibles");
        }

        LinearLayout scrollLin = view.findViewById(R.id.toast_list_scroll_mainlin);
        scrollLin.removeAllViews();
        for (final Equipment equi : equipmentsList) {
            View yourLayout = inflater.inflate(R.layout.custom_toast_list_element, null);
            ImageView img = yourLayout.findViewById(R.id.toast_list_element_image);
            if (equi.getImg(mC) != null) {
                img.setImageDrawable(equi.getImg(mC));
            } else {
                img.setVisibility(View.GONE);
            }
            TextView name = yourLayout.findViewById(R.id.toast_list_element_textName);
            name.setText(equi.getName());
            TextView value = yourLayout.findViewById(R.id.toast_list_element_textVal);
            value.setText("Valeur : " + equi.getValue());
            TextView descr = yourLayout.findViewById(R.id.toast_list_element_textDescr);
            if (!equi.getDescr().equalsIgnoreCase("")) {
                descr.setText(equi.getDescr());
            } else {
                descr.setVisibility(View.GONE);
            }

            if(selectToEquip && editable){
                yourLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        equip(equi);
                        ca.dismissAlert();
                        if(mListener!=null){ mListener.onEvent();}
                    }
                });
            }
            scrollLin.addView(yourLayout);
        }
        ca.showAlert();
    }

    public void equip(Equipment equiToPut) {
        for (Equipment equi: getSlotListEquipment(equiToPut.getSlotId())){
            if(equi!=equiToPut){
                equi.setEquiped(false);
            }
        }
        equiToPut.setEquiped(true);
        if(mListenerSave!=null){ mListenerSave.onEvent();}
    }

    public List<Equipment> getSpareEquipment(String slot) {
        List<Equipment> list = new ArrayList<>();
        for (Equipment equipment : listEquipments) {
            if (equipment.getSlotId().equalsIgnoreCase(slot) && !equipment.isEquiped()) {
                list.add(equipment);
            }
        }
        return list;
    }

    public void createEquipment(Equipment equi) {
        listEquipments.add(equi);
        if(mListenerSave!=null){ mListenerSave.onEvent();}
    }

    public void remove(Equipment equi) {
        listEquipments.remove(equi);
        if(mListenerSave!=null){ mListenerSave.onEvent();}
    }

    public List<Equipment> getSlotListEquipment(String slot) {
        List<Equipment> list = new ArrayList<>();
        for (Equipment equipment : listEquipments) {
            if (equipment.getSlotId().equalsIgnoreCase(slot)) {
                list.add(equipment);
            }
        }
        return list;
    }

    public List<Equipment> getAllSpareEquipment() {
        List<Equipment> list = new ArrayList<>();
        for (Equipment equipment : listEquipments) {
            if (!equipment.isEquiped()) {
                list.add(equipment);
            }
        }
        return list;
    }


    public interface OnRefreshEventListener {
        void onEvent();
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListener = eventListener;
    }

    public interface OnSaveEventListener {
        void onEvent();
    }

    public void setSaveEventListener(OnSaveEventListener eventListener) {
        mListenerSave = eventListener;
    }

}

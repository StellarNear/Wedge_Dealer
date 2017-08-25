package pathfinder.pathfinder_dealer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageAdapter  extends BaseAdapter {
    private Context mContext;
    Integer  size_height;

    // Keep all Images in array
    public List<Integer> mThumbIds;

    public static final Map<String,Integer> dice_map= new HashMap<>();
    static {
        dice_map.put("d20_1",R.mipmap.d20_1);
        dice_map.put("d20_10",R.mipmap.d20_10);
        dice_map.put("d20_11",R.mipmap.d20_11);
        dice_map.put("d20_12",R.mipmap.d20_12);
        dice_map.put("d20_13",R.mipmap.d20_13);
        dice_map.put("d20_14",R.mipmap.d20_14);
        dice_map.put("d20_15",R.mipmap.d20_15);
        dice_map.put("d20_16",R.mipmap.d20_16);
        dice_map.put("d20_17",R.mipmap.d20_17);
        dice_map.put("d20_18",R.mipmap.d20_18);
        dice_map.put("d20_19",R.mipmap.d20_19);
        dice_map.put("d20_2",R.mipmap.d20_2);
        dice_map.put("d20_20",R.mipmap.d20_20);
        dice_map.put("d20_3",R.mipmap.d20_3);
        dice_map.put("d20_4",R.mipmap.d20_4);
        dice_map.put("d20_5",R.mipmap.d20_5);
        dice_map.put("d20_6",R.mipmap.d20_6);
        dice_map.put("d20_7",R.mipmap.d20_7);
        dice_map.put("d20_8",R.mipmap.d20_8);
        dice_map.put("d20_9",R.mipmap.d20_9);
        dice_map.put("d6_1",R.mipmap.d6_1);
        dice_map.put("d6_2",R.mipmap.d6_2);
        dice_map.put("d6_3",R.mipmap.d6_3);
        dice_map.put("d6_4",R.mipmap.d6_4);
        dice_map.put("d6_5",R.mipmap.d6_5);
        dice_map.put("d6_6",R.mipmap.d6_6);
        dice_map.put("d8_1",R.mipmap.d8_1);
        dice_map.put("d8_2",R.mipmap.d8_2);
        dice_map.put("d8_3",R.mipmap.d8_3);
        dice_map.put("d8_4",R.mipmap.d8_4);
        dice_map.put("d8_5",R.mipmap.d8_5);
        dice_map.put("d8_6",R.mipmap.d8_6);
        dice_map.put("d8_7",R.mipmap.d8_7);
        dice_map.put("d8_8",R.mipmap.d8_8);
        dice_map.put("feu_d10_1",R.mipmap.feu_d10_1);
        dice_map.put("feu_d10_10",R.mipmap.feu_d10_10);
        dice_map.put("feu_d10_2",R.mipmap.feu_d10_2);
        dice_map.put("feu_d10_3",R.mipmap.feu_d10_3);
        dice_map.put("feu_d10_4",R.mipmap.feu_d10_4);
        dice_map.put("feu_d10_5",R.mipmap.feu_d10_5);
        dice_map.put("feu_d10_6",R.mipmap.feu_d10_6);
        dice_map.put("feu_d10_7",R.mipmap.feu_d10_7);
        dice_map.put("feu_d10_8",R.mipmap.feu_d10_8);
        dice_map.put("feu_d10_9",R.mipmap.feu_d10_9);
        dice_map.put("feu_d6_1",R.mipmap.feu_d6_1);
        dice_map.put("feu_d6_2",R.mipmap.feu_d6_2);
        dice_map.put("feu_d6_3",R.mipmap.feu_d6_3);
        dice_map.put("feu_d6_4",R.mipmap.feu_d6_4);
        dice_map.put("feu_d6_5",R.mipmap.feu_d6_5);
        dice_map.put("feu_d6_6",R.mipmap.feu_d6_6);
        dice_map.put("foudre_d10_1",R.mipmap.foudre_d10_1);
        dice_map.put("foudre_d10_10",R.mipmap.foudre_d10_10);
        dice_map.put("foudre_d10_2",R.mipmap.foudre_d10_2);
        dice_map.put("foudre_d10_3",R.mipmap.foudre_d10_3);
        dice_map.put("foudre_d10_4",R.mipmap.foudre_d10_4);
        dice_map.put("foudre_d10_5",R.mipmap.foudre_d10_5);
        dice_map.put("foudre_d10_6",R.mipmap.foudre_d10_6);
        dice_map.put("foudre_d10_7",R.mipmap.foudre_d10_7);
        dice_map.put("foudre_d10_8",R.mipmap.foudre_d10_8);
        dice_map.put("foudre_d10_9",R.mipmap.foudre_d10_9);
        dice_map.put("foudre_d6_1",R.mipmap.foudre_d6_1);
        dice_map.put("foudre_d6_2",R.mipmap.foudre_d6_2);
        dice_map.put("foudre_d6_3",R.mipmap.foudre_d6_3);
        dice_map.put("foudre_d6_4",R.mipmap.foudre_d6_4);
        dice_map.put("foudre_d6_5",R.mipmap.foudre_d6_5);
        dice_map.put("foudre_d6_6",R.mipmap.foudre_d6_6);
        dice_map.put("froid_d10_1",R.mipmap.froid_d10_1);
        dice_map.put("froid_d10_10",R.mipmap.froid_d10_10);
        dice_map.put("froid_d10_2",R.mipmap.froid_d10_2);
        dice_map.put("froid_d10_3",R.mipmap.froid_d10_3);
        dice_map.put("froid_d10_4",R.mipmap.froid_d10_4);
        dice_map.put("froid_d10_5",R.mipmap.froid_d10_5);
        dice_map.put("froid_d10_6",R.mipmap.froid_d10_6);
        dice_map.put("froid_d10_7",R.mipmap.froid_d10_7);
        dice_map.put("froid_d10_8",R.mipmap.froid_d10_8);
        dice_map.put("froid_d10_9",R.mipmap.froid_d10_9);
        dice_map.put("froid_d6_1",R.mipmap.froid_d6_1);
        dice_map.put("froid_d6_2",R.mipmap.froid_d6_2);
        dice_map.put("froid_d6_3",R.mipmap.froid_d6_3);
        dice_map.put("froid_d6_4",R.mipmap.froid_d6_4);
        dice_map.put("froid_d6_5",R.mipmap.froid_d6_5);
        dice_map.put("froid_d6_6",R.mipmap.froid_d6_6);
        dice_map.put("froid",R.mipmap.froid);
        dice_map.put("feu",R.mipmap.feu);
        dice_map.put("foudre",R.mipmap.foudre);
        dice_map.put("physique",R.mipmap.physique);

    }


    // Constructor
    public ImageAdapter(Context c,List<String> dice_list,Integer size_height_grid){
        mThumbIds = new ArrayList<>();


        for (String each : dice_list)   {
            mThumbIds.add(dice_map.get(each));
        }

        mContext = c;
        size_height=size_height_grid;
    }

    @Override
    public int getCount() {
        return mThumbIds.size();
    }

    @Override
    public Object getItem(int position) {
        return mThumbIds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.FIT_END);
            imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, size_height));

        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(mThumbIds.get(position));
        return imageView;
    }

}
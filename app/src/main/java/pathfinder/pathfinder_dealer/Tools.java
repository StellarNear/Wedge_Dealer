package pathfinder.pathfinder_dealer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jchatron on 16/02/2018.
 */

public class Tools {

    public Integer toInt(String key) {
        Integer value = 0;
        try {
            value = Integer.parseInt(key);
        } catch (Exception e) {
        }
        return value;
    }

    public List<Integer> toInt(List<String> listKey) {
        List<Integer> list = new ArrayList<>();
        for (String key : listKey) {
            list.add(toInt(key));
        }
        return list;
    }

    public Long toLong(String key) {
        Long value = 0L;
        try {
            value = Long.parseLong(key);
        } catch (Exception e) {
        }
        return value;
    }

    public Boolean toBool(String key) {
        Boolean value = false;
        try {
            value = Boolean.valueOf(key);
        } catch (Exception e) {
        }
        return value;
    }

    public Drawable resize(Context mC, Drawable image, int pixelSizeIcon) {
        Bitmap b = ((BitmapDrawable) image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, pixelSizeIcon, pixelSizeIcon, false);
        return new BitmapDrawable(mC.getResources(), bitmapResized);
    }

    public Drawable resize(Context mC, int imageId, int pixel_size_icon) {
        Drawable image = mC.getDrawable(imageId);
        Bitmap b = ((BitmapDrawable) image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, pixel_size_icon, pixel_size_icon, false);
        return new BitmapDrawable(mC.getResources(), bitmapResized);
    }


    public void customToast(Context mC, String txt, String... modeInput) {
        // Set the toast and duration
        String mode = modeInput.length > 0 ? modeInput[0] : "";
        Toast mToastToShow = Toast.makeText(mC, txt, Toast.LENGTH_LONG);

        if (mode.contains("center")) {
            TextView v = (TextView) mToastToShow.getView().findViewById(android.R.id.message);
            if (v != null) v.setGravity(Gravity.CENTER);
        }
        mToastToShow.setGravity(Gravity.CENTER, 0, 0);
        mToastToShow.show();

    }

}

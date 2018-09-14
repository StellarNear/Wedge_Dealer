package stellarnear.wedge_dealer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import stellarnear.stellarnear.R;

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

    public BigInteger toBigInt(String key) {
        BigInteger value = BigInteger.ZERO;
        try {
            value = new BigInteger(key);
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
        Drawable image = null;
        try {
            image = mC.getDrawable(imageId);
        } catch (Exception e) {
            int drawableId = mC.getResources().getIdentifier("mire_test", "drawable", mC.getPackageName());
            image = mC.getDrawable(drawableId);
            e.printStackTrace();
        }
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

    public void playVideo(Activity activity,Context context, String rawPath) {
        LayoutInflater inflater = activity.getLayoutInflater();
        final View layoutRecordVideo = inflater.inflate(R.layout.video_full_screen, null);
        final CustomAlertDialog customVideo = new CustomAlertDialog(activity, context, layoutRecordVideo);
        customVideo.setPermanent(true);
        final VideoView video = (VideoView) layoutRecordVideo.findViewById(R.id.fullscreen_video);
        video.setVisibility(View.VISIBLE);
        String fileName = "android.resource://" + activity.getPackageName() + rawPath;
        video.setMediaController(null);
        video.setVideoURI(Uri.parse(fileName));
        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                video.stopPlayback();
                customVideo.dismissAlert();
            }
        });
        video.start();
        customVideo.showAlert();
    }


}

package org.researchstack.backbone.utils;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by User on 22-02-2018.
 */

public class AnimationsUtil {
    public static void blink(final ImageView imageView) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 1000;    //in milissegunds
                try {
                    Thread.sleep(timeToBlink);
                } catch (Exception e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (imageView.getVisibility() == View.VISIBLE) {
                            imageView.setVisibility(View.INVISIBLE);
                        } else {
                            imageView.setVisibility(View.VISIBLE);
                        }
                        blink(imageView);
                    }
                });
            }
        }).start();
    }


}

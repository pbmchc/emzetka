package com.pbmchc.emzetka.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pbmchc.emzetka.mzkandroid.R;

/**
 * Created by Piotrek on 2017-02-23.
 */
public class ImageUtils {

    public static void setBorderImageResource(ImageView imageView, int position) {

        imageView.requestLayout();
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        if (position == 0) {
            imageView.setImageResource(R.drawable.emzetka_list_stop_start);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else {
            imageView.setImageResource(R.drawable.emzetka_list_stop_end);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        }
        rlp.height = (int) Math.floor(rlp.height * 0.66);
        imageView.setLayoutParams(rlp);
    }

    public static int getImageResourceId(Context context, String imageTitle){
        return context.getResources().getIdentifier(imageTitle, "drawable", context.getPackageName());
    }
}

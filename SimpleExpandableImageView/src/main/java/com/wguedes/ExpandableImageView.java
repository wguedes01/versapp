package com.wguedes;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by william on 13/10/14.
 */
public class ExpandableImageView extends ImageView{

    private int originalHeight;
    private int originalWidth;
    private boolean expanded = false;

    public ExpandableImageView(Context context) {
        super(context);
        init(context);
    }

    public ExpandableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExpandableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(final Context context){

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                final ViewGroup.LayoutParams params = getLayoutParams();

                if (expanded){
                    params.height = originalHeight;
                    params.width = originalWidth;
                } else {
                    originalHeight = params.height;
                    originalWidth = params.width;

                    params.height = getDrawable().getIntrinsicHeight();
                    params.width = getDrawable().getIntrinsicWidth();
                }

                expanded = !expanded;

                setLayoutParams(params);
            }
        });

    }
}

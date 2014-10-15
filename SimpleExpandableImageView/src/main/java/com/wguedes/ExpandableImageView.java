package com.wguedes;

import android.app.ActionBar;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by william on 13/10/14.
 */
public class ExpandableImageView extends ImageView{

    private boolean expanded = false;

    private ViewGroup viewGroup;
    private RelativeLayout imageHolder;
    private ViewGroup.LayoutParams originalLayoutParams;

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

                if (viewGroup == null){
                    viewGroup = (ViewGroup) getParent();
                }

                if (imageHolder == null){
                    imageHolder = new RelativeLayout(context);
                }

                if (originalLayoutParams == null) {
                    originalLayoutParams = getLayoutParams();
                }

                imageHolder.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                if (expanded){
                    imageHolder.removeView(ExpandableImageView.this);
                    viewGroup.removeView(imageHolder);

                    ExpandableImageView.this.setLayoutParams(originalLayoutParams);
                    viewGroup.addView(ExpandableImageView.this);

                } else {
                    RelativeLayout.LayoutParams expandedParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    expandedParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

                    viewGroup.addView(imageHolder);

                    viewGroup.removeView(ExpandableImageView.this);
                    imageHolder.addView(ExpandableImageView.this);
                    setLayoutParams(expandedParams);
                }


                expanded = !expanded;

            }
        });

    }
}

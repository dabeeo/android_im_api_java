package com.dabeeo.imsdk.sample.view.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dabeeo.imsdk.sample.R;


public class MarkerTestView extends LinearLayout {


    private ImageView inflateImageView;
    private TextView inflateTextView;

    public MarkerTestView(Context context) {
        super(context);
        init();
    }

    public MarkerTestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MarkerTestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_marker_test, this, false);
        inflateImageView = view.findViewById(R.id.inflateImageView);
        inflateTextView = view.findViewById(R.id.inflateTextView);
        addView(view);
    }

    public void setTitle(String title) {
        inflateTextView.setText(title);
    }

    public void setResource(int resource) {
        inflateImageView.setImageResource(resource);
    }

}

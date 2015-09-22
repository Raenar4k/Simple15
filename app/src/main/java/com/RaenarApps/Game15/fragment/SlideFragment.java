package com.RaenarApps.Game15.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.RaenarApps.Game15.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Raenar on 09.08.2015.
 */
public class SlideFragment extends android.support.v4.app.Fragment {
    private int pageNumber = 0;

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.slide_generic, container, false);
        TextView textTop = (TextView) rootView.findViewById(R.id.textView);
        ImageView img = (ImageView) rootView.findViewById(R.id.imageView);
        TextView textBot = (TextView) rootView.findViewById(R.id.textView2);
        switch (pageNumber) {
            case 0:
                textTop.setText(R.string.slide1_top);
                Picasso.with(getActivity()).load(R.drawable.slide1).into(img);
                textBot.setText(R.string.slide1_bot);
                break;
            case 1:
                textTop.setText(R.string.slide2_top);
                Picasso.with(getActivity()).load(R.drawable.slide2).into(img);
                textBot.setText(R.string.slide2_bot);
                break;
            case 2:
                textTop.setText(R.string.slide3_top);
                Picasso.with(getActivity()).load(R.drawable.slide3).into(img);
                textBot.setText(R.string.slide3_bot);
                break;
            case 3:
                textTop.setText(R.string.slide4_top);
                Picasso.with(getActivity()).load(R.drawable.slide4).into(img);
                textBot.setText(R.string.slide4_bot);
                break;
            case 4:
                textTop.setText(R.string.slide5_top);
                Picasso.with(getActivity()).load(R.drawable.slide5).into(img);
                textBot.setText(R.string.slide5_bot);
                break;
            case 5:
                textTop.setText(R.string.slide6_top);
                Picasso.with(getActivity()).load(R.drawable.slide6).into(img);
                textBot.setText(R.string.slide6_bot);
                break;
            case 6:
                textTop.setText(R.string.slide7_top);
                Picasso.with(getActivity()).load(R.drawable.slide7).into(img);
                textBot.setText(R.string.slide7_bot);
                break;
        }
        return rootView;
    }
}

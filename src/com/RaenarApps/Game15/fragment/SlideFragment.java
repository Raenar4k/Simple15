package com.RaenarApps.Game15.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.RaenarApps.Game15.R;

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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.slide_page, container, false);
        switch (pageNumber) {
            case 0:
                rootView = (ViewGroup) inflater.inflate(R.layout.slide1, container, false);
                break;
            case 1:
                rootView = (ViewGroup) inflater.inflate(R.layout.slide2, container, false);
                break;
            case 2:
                rootView = (ViewGroup) inflater.inflate(R.layout.slide3, container, false);
                break;
            case 3:
                rootView = (ViewGroup) inflater.inflate(R.layout.slide4, container, false);
                break;
            case 4:
                rootView = (ViewGroup) inflater.inflate(R.layout.slide5, container, false);
                break;
            case 5:
                rootView = (ViewGroup) inflater.inflate(R.layout.slide6, container, false);
                break;
            case 6:
                rootView = (ViewGroup) inflater.inflate(R.layout.slide7, container, false);
                break;
        }
        return rootView;
    }
}

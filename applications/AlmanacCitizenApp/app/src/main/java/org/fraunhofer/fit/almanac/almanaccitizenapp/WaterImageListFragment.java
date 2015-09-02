package org.fraunhofer.fit.almanac.almanaccitizenapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by devasya on 13.08.2015.
 */
public class WaterImageListFragment extends  ImageListFragment {
    @Override
    public void publishDone() {
        //do nothing
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        rootView.findViewById(R.id.newIssue);

        FloatingActionButton imageButton = (FloatingActionButton) rootView.findViewById(R.id.newIssue);
        imageButton.setVisibility(View.GONE);

        GridView girid = (GridView) rootView.findViewById(R.id.gridview);
        girid.setVisibility(View.GONE);

        TextView comingSoon = (TextView) rootView.findViewById(R.id.comingSoon);
        comingSoon.setVisibility(View.VISIBLE);

        ImageView waterTap = (ImageView) rootView.findViewById(R.id.water_tap);
        waterTap.setVisibility(View.VISIBLE);
        return  rootView;
    }
}

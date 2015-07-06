package org.fraunhofer.fit.almanac.almanaccitizenapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;


public class ImageListFragment extends Fragment {

    public void setVisibility(int visibility) {
        getView().setVisibility(visibility);
    }

    public static ImageListFragment newInstance(int i) {
        return new ImageListFragment();
    }

    public interface OnNewIssueRequestListener{
        public void onNewIssueSelected();
    }

    OnNewIssueRequestListener mCallback ;

    public ImageListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnNewIssueRequestListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNewIssueRequestListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        GridView gridview = (GridView) getView().findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(getActivity().getApplicationContext()));

        Button createIssueButton = (Button) getView().findViewById(R.id.newIssue);
        createIssueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mCallback.onNewIssueSelected();
            }
        });
    }

    public void publishDone(){

        GridView gridview = (GridView) getView().findViewById(R.id.gridview);
        gridview.invalidateViews();//this is to redraw the grid view

    }


}

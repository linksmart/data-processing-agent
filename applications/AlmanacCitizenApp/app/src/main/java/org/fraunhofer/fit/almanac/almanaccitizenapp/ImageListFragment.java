package org.fraunhofer.fit.almanac.almanaccitizenapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import org.fraunhofer.fit.almanac.slider.ScreenSlideActivity;


public class ImageListFragment extends Fragment {

    private static final String TAG = "ImageListFragment" ;
    private static final Object SHOW_IMAGE = 32;


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

        final GridView gridview = (GridView) getView().findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(getActivity().getApplicationContext()));
        gridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridview.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub

                mode.setSubtitle("Select Items");
                mode.setSubtitle("One item selected");
                return true;

            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // TODO Auto-generated method stub


                int selectCount = gridview.getCheckedItemCount();
                Log.i(TAG,"One item long clicked1");
                switch (selectCount) {
                    case 1:
                        mode.setSubtitle("One item selected");

                        break;
                    default:
                        mode.setSubtitle("" + selectCount + " items selected");

                        break;
                }

                return true;
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                // TODO Auto-generated method stub


                int selectCount = gridview.getCheckedItemCount();
                Log.i(TAG,"One item long clicked2");
                //   RelativeLayout item = (RelativeLayout) gridview.getChildAt(position);
                //   item.setSelected(gridview.isItemChecked(position));
                //   item.setActivated(gridview.isItemChecked(position));
                // gridview.setItemChecked(position,gridview.isItemChecked(position));
                switch (selectCount) {
                    case 1:
                        mode.setSubtitle("One item selected");
                        break;
                    default:
                        mode.setSubtitle("" + selectCount + " items selected");
                        break;
                }

            }
        });

        gridview.setOnItemLongClickListener(itemLongClickListener);
        gridview.setOnItemClickListener(itemClickListener);
        // gridview.sele
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

    AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i(TAG, "Long clicked");
            return false;
        }
    };
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i(TAG, "Clicked");
            Intent intent = new Intent(getActivity(), ScreenSlideActivity.class);
            intent.putExtra(ScreenSlideActivity.INDEX_POSITION,position);
            getActivity().startActivity(intent);
        }
    };

    @Override
    public void onResume() {
        publishDone();
        super.onResume();
    }
}

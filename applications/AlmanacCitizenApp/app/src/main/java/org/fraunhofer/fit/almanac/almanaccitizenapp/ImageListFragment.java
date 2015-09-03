package org.fraunhofer.fit.almanac.almanaccitizenapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import org.fraunhofer.fit.almanac.model.IssueStatus;
import org.fraunhofer.fit.almanac.slider.ScreenSlideActivity;

import java.util.LinkedList;
import java.util.List;


public class ImageListFragment extends Fragment implements IssueTracker.ChangeListener {

    private static final String TAG = "ImageListFragment" ;
    private static final Object SHOW_IMAGE = 32;
    private static final String ARG_SECTION_NUMBER = "SectionNumber";
    private static final int BIN_COLLECTION = 1;
    private StickyGridHeadersGridView mGridview;
    private int mSectionNumber;
    private ImageAdapter mGridAdapter;


    public void setVisibility(int visibility) {
        getView().setVisibility(visibility);
    }

    public static ImageListFragment newInstance(int sectionNumber) {
        ImageListFragment fragment;
        if(sectionNumber == BIN_COLLECTION){
            fragment = new WasteImageListFragment();
        }else{
            fragment = new WaterImageListFragment();
        }
        return fragment;
    }


    AlmanacCitizen mCallback ;

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
            mCallback = (AlmanacCitizen) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNewIssueRequestListener");
        }

    }

    @Override
    public void setHasOptionsMenu(boolean hasMenu) {
        super.setHasOptionsMenu(hasMenu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView =  inflater.inflate(R.layout.fragment_image_list, container, false);

        TextView comingSoon = (TextView) rootView.findViewById(R.id.comingSoon);
        comingSoon.setVisibility(View.GONE);

        ImageView waterTap = (ImageView) rootView.findViewById(R.id.water_tap);
        waterTap.setVisibility(View.GONE);

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mGridview = (StickyGridHeadersGridView) getView().findViewById(R.id.gridview);
        mGridAdapter = new ImageAdapter(getActivity().getApplicationContext());
        mGridview.setAdapter(mGridAdapter);
        mGridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);

        mGridview.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

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
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.mode_context_menu, menu);
                return true;


            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // TODO Auto-generated method stub
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.menu_deleteSelected:
                        onDeleteSelected();
                        return true;
                    case R.id.menu_selectall:
                        selectAllItems();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                // TODO Auto-generated method stub


                int selectCount = mGridview.getCheckedItemCount();

                Log.i(TAG, "One item long clicked position:" + position + " child count:" + mGridview.getChildCount());
                //   RelativeLayout item = (RelativeLayout) gridview.getChildAt(position);
                //   item.setSelected(gridview.isItemChecked(position));
                //   item.setActivated(gridview.isItemChecked(position));
                // gridview.setItemChecked(position,gridview.isItemChecked(position));
                //   mGridview.getChildAt(position).findViewById(R.id.containerLayout);
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

        mGridview.setOnItemLongClickListener(itemLongClickListener);
        mGridview.setOnItemClickListener(itemClickListener);
        // gridview.sele
        FloatingActionButton createIssueButton = (FloatingActionButton) getView().findViewById(R.id.newIssue);
        createIssueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onNewIssueSelected();
            }
        });


    }



    private void onDeleteSelected() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(R.string.alert_confirm_unsubsccribe, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteSelectedItems();
            }
        });
        builder.setNegativeButton(R.string.alert_cancel_unsubscribe, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //do nothing
            }
        });
        builder.setMessage(getString(R.string.alert_dialog_message));
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void selectAllItems() {
        int itemCount = mGridview.getCount();
        for (int i = 0; i < itemCount; i++) {
            mGridview.setItemChecked(i, true);
        }
    }

    private void deleteSelectedItems() {
        int count = mGridview.getCount();
        List<IssueStatus> itemsTobeDeleted = new LinkedList(); //This is to avoid inconsistencies that occur in getItemAtPosition after deletion of items

        for(int i= 0; i < count; i++){
            if(mGridview.isItemChecked(i)) {
                mGridview.setItemChecked(i, false);

                int index = mGridAdapter.mapPositionToIndex(i,mGridview.getNumColumns());
                Log.i(TAG, i + "th items to be deleted index =" +index);
                if(index >= 0) {
                    IssueStatus issueStatus = (IssueStatus) mGridAdapter.getItem(index);
                    itemsTobeDeleted.add(issueStatus);
                }

            }
        }

        for (IssueStatus issue:itemsTobeDeleted){
            IssueTracker.getInstance().deleteIssue(issue.id);
        }
      //  mGridview.invalidateViews();//this is to redraw the grid view
    }

    public void publishDone(){
        Log.i(TAG, "publish done");
        onChange();
//        setUpdatedItems();
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
            Log.i(TAG, "Clicked pos "+position);
            Intent intent = new Intent(getActivity(), ScreenSlideActivity.class);
            intent.putExtra(ScreenSlideActivity.INDEX_POSITION, position);

            getActivity().startActivity(intent);
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        mGridview.invalidateViews();//this is to redraw the grid view
//        setUpdatedItems();
        IssueTracker.getInstance().subscribeChange(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        IssueTracker.getInstance().unSubscribeChange(this);
    }

    @Override
    public void onChange() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "calling invalidateViews");
                mGridview.invalidateViews();

            }
        });

    }
}
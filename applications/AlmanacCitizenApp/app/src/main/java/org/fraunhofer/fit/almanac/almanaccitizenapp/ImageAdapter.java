package org.fraunhofer.fit.almanac.almanaccitizenapp;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.fraunhofer.fit.almanac.model.IssueStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by devasya on 24.06.2015.
 */
public class ImageAdapter extends BaseAdapter {

    //private final String mFolderPath;
    private Context mContext;
        private String TAG="Citizen/ImageAdapter";
        private List<IssueStatus> mIssueList;

        IssueTracker mIssueTracker ;

        public ImageAdapter(Context c) {
            mContext = c;
            //mFolderPath = mContext.getFilesDir()+mContext.getString(R.string.folderPath);
            mIssueTracker = IssueTracker.getInstance();

            mIssueList = getIssueIds();
        }

        public int getCount() {
            mIssueList = getIssueIds();
            Log.i(TAG,"number of issues:"+mIssueList.size());
            return  mIssueList.size();

        }

        public Object getItem(int position) {
            return mIssueList.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View gridItem;
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes

                gridItem=  (View)inflater.inflate(R.layout.grid_item, null);


            } else {
                gridItem = (View) convertView;
            }

            IssueStatus issueStatus = mIssueList.get(position);
            imageView = (ImageView) gridItem.findViewById(R.id.imageView);
            if(issueStatus.picPath != null) {
                Uri imgUri = Uri.parse("file://" + issueStatus.picPath);
                imageView.setImageURI(imgUri);
            }else{
                imageView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.waste_container));
            }

            TextView nameOfIssue = (TextView) gridItem.findViewById(R.id.nameOfIssue);
            if(issueStatus.name.length()>13)
                nameOfIssue.setText(issueStatus.name.substring(0,10)+"...");
            else
                nameOfIssue.setText(issueStatus.name);
            TextView issuePriority = (TextView) gridItem.findViewById(R.id.issuePriority);
            if(issueStatus.priority != null){
                issuePriority.setText("Priority:"+ issueStatus.priority);
            }else{
                issuePriority.setText("Priority: NOT SET");
            }
            TextView issueState = (TextView) gridItem.findViewById(R.id.issueStatus);
            if(issueStatus.status != null){
                issueState.setText("Status:"+ issueStatus.status);
            }else{
                issueState.setText("Status: CREATED");
            }

  //          gridItem.setOnClickListener(clickListener);
//            gridItem.setOnLongClickListener(longClickListener);
            RelativeLayout detailsView = (RelativeLayout)gridItem.findViewById(R.id.containerLayout);
            if(issueStatus.isUpdated){

                detailsView.setBackground(mContext.getResources().getDrawable(R.drawable.grid_item_selector_highlighted,null));
                //IssueTracker.getInstance().resetUpdated(issueStatus.id);
                //detailsView.setSelected(true);
                Log.i(TAG, "the issue is updated");
            }else{
                detailsView.setBackground(mContext.getResources().getDrawable(R.drawable.grid_item_selector,null));
//                detailsView.setSelected(false);
            }
            Log.i(TAG, "returning position:"+position);
            return gridItem;
        }


        private List<IssueStatus> getIssueIds(){
            Log.i(TAG,"inside getIssueIds");
            List<IssueStatus> issueList = new ArrayList<IssueStatus>(mIssueTracker.getAllIssues());
            Collections.sort(issueList, new Comparator<IssueStatus>() {
                @Override
                public int compare(IssueStatus lhs, IssueStatus rhs) {
                    if(lhs.updationDate.after(rhs.updationDate))
                        return -1;
                    else
                        return 1;
                }
            });
            return issueList;
        }



    private RelativeLayout.OnClickListener clickListener = new RelativeLayout.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG,"clicked");
            return ;
        }

    };
    private  RelativeLayout.OnLongClickListener longClickListener = new RelativeLayout.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Log.i(TAG,"Longclicked");
            //RelativeLayout view = (RelativeLayout) v;
           // v.setSelected(true);
            return true;
        }
    };
}

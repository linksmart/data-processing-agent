package org.fraunhofer.fit.almanac.almanaccitizenapp;

import android.content.Context;
import android.net.Uri;
import android.text.Selection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.fraunhofer.fit.almanac.model.IssueStatus;

import java.util.ArrayList;
import java.util.Date;
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
            imageView = (ImageView) gridItem.findViewById(R.id.imageView);
            IssueStatus issueStatus = mIssueList.get(position);
            Uri imgUri=Uri.parse("file://"+ issueStatus.picPath);
            imageView.setImageURI(imgUri);

            TextView nameOfIssue = (TextView) gridItem.findViewById(R.id.nameOfIssue);
            nameOfIssue.setText("Name:"+ issueStatus.name);
            if(issueStatus.priority != null){
                TextView issuePriority = (TextView) gridItem.findViewById(R.id.issuePriority);
                issuePriority.setText("Priority:"+ issueStatus.name);
            }
            Date now = new Date();
            if(issueStatus.timeToCompletion!= null  && issueStatus.timeToCompletion.after(now)){
                TextView completionDate = (TextView) gridItem.findViewById(R.id.completionDate);
                completionDate.setText("completionDate:"+ issueStatus.timeToCompletion);
            }
  //          gridItem.setOnClickListener(clickListener);
//            gridItem.setOnLongClickListener(longClickListener);
            return gridItem;
        }


        private List<IssueStatus> getIssueIds(){

            List<IssueStatus> issueList = new ArrayList<IssueStatus>(mIssueTracker.getAllIssues());

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

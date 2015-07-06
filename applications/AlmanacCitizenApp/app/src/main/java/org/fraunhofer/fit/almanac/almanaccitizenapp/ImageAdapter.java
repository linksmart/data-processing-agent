package org.fraunhofer.fit.almanac.almanaccitizenapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.fraunhofer.fit.almanac.model.IssueStatus;

import java.util.ArrayList;
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
            LinearLayout gridItem;
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes

                gridItem=  (LinearLayout)inflater.inflate(R.layout.grid_item, null);


            } else {
                gridItem = (LinearLayout) convertView;
            }
            imageView = (ImageView) gridItem.findViewById(R.id.imageView);
            Uri imgUri=Uri.parse("file://"+ mIssueList.get(position).picPath);
            imageView.setImageURI(imgUri);

            return gridItem;
        }


        private List<IssueStatus> getIssueIds(){

            List<IssueStatus> issueList = new ArrayList<IssueStatus>(mIssueTracker.getAllIssues());

            return issueList;
        }
}

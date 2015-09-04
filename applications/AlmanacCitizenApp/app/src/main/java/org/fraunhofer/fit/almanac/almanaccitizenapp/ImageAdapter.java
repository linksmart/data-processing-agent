package org.fraunhofer.fit.almanac.almanaccitizenapp;

import android.content.Context;
import android.database.DataSetObserver;
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

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapterWrapper;

import org.fraunhofer.fit.almanac.model.IssueStatus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by devasya on 24.06.2015.
 */
public class ImageAdapter extends BaseAdapter implements StickyGridHeadersBaseAdapter {

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

    private LinkedHashMap<Date,List<IssueStatus>> dateToIssueMap = new LinkedHashMap <>();
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

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

    @Override
    public boolean hasStableIds() {
        return false;
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
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }else{
                imageView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.waste_container));
                imageView.setScaleType(ImageView.ScaleType.CENTER);
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
            //Log.i(TAG, "returning position:"+position);
            return gridItem;
        }

    private List<IssueStatus> getIssueIds(){

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



    private void sortListAccordingToDate(){
        Date curDate = null;
        List<IssueStatus> list = null;
        dateToIssueMap.clear();
        for(IssueStatus issueStatus: mIssueList){

            if(curDate == null || (curDate.getDate() != issueStatus.updationDate.getDate())||(curDate.getMonth() != issueStatus.updationDate.getMonth())||
            (curDate.getYear() != issueStatus.updationDate.getYear())){
                list = new ArrayList<>();
                curDate = issueStatus.updationDate;
                dateToIssueMap.put(curDate,list);
            }
            list.add(issueStatus);
        }

    }


    @Override
    public int getCountForHeader(int header) {
        int index = 0;
        int size = 0;
        for(Map.Entry<Date,List<IssueStatus>> entry:dateToIssueMap.entrySet()){
            if(index == header){
                size = entry.getValue().size();
            }
            index ++;
        }

        return size;
    }

    @Override
    public int getNumHeaders() {
        mIssueList = getIssueIds();
        sortListAccordingToDate();
        
        return dateToIssueMap.size();
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_header, null);

        }

        int index = 0;

        for(Map.Entry<Date,List<IssueStatus>> entry:dateToIssueMap.entrySet()){
            if(index == position){
                TextView headerName = (TextView)convertView.findViewById(R.id.headingName);
                headerName.setText(new SimpleDateFormat("dd MMM yy").format(entry.getKey()));
            }
            index ++;
        }


        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    /*the grid view will index to wrong position after considering headers
    This has to be adjusted to get actual index of the element
     */
    public int mapPositionToIndex(int position, int numColumns) {
        int headerCount = numColumns;//numColumns because a header takes numColumns positions
        int elmtCount = 0;
        int columnPadding = 0;
        for(Map.Entry<Date,List<IssueStatus>> entry:dateToIssueMap.entrySet()){
            columnPadding = entry.getValue().size()%numColumns;
            if(elmtCount+entry.getValue().size()+headerCount+columnPadding > position){
                break;
            }
//            Log.i(TAG,"columnPadding "+ columnPadding + " numColumns "+numColumns + " size " + entry.getValue().size());
            headerCount+=numColumns+columnPadding;
            elmtCount += entry.getValue().size();
        }
        if(elmtCount+headerCount > position){//ignore header positions
//            Log.i(TAG,"elmtCount " + elmtCount + " headerCount " + headerCount + " columnPadding" + columnPadding );
            return  -1;
        }else {
            return position - headerCount;
        }
    }
}

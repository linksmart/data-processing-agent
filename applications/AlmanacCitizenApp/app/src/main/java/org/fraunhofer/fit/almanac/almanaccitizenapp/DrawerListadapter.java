package org.fraunhofer.fit.almanac.almanaccitizenapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by devasya on 13.08.2015.
 */
public class DrawerListadapter extends ArrayAdapter<String> {

    private static final int TRASHCOLLECTION = 0;
    private static final int WATER = 1 ;
    private final Context mContext;
    private final String[] menuObjects;


    public DrawerListadapter(Context context, int resource, int textViewResourceId, String[] objects) {
        super(context, resource, textViewResourceId, objects);
        mContext = context;
        menuObjects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      //  View row = super.getView(position, convertView, parent);
        //
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row =  (View)inflater.inflate(R.layout.drawer_list_with_image, null);
        ImageView icon =  (ImageView) row.findViewById(R.id.listItemIcon);
        switch (position){
            case TRASHCOLLECTION:
                icon.setImageResource(R.drawable.citymap);
                break;
            case WATER:
                icon.setImageResource(R.drawable.water_tap);
                break;
            default:
                break;
        }
        TextView textView = (TextView) row.findViewById(R.id.listItemText);
        textView.setText(menuObjects[position]);
        return  row;
    }
}

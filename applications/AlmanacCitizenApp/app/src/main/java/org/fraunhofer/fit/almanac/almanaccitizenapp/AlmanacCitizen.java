package org.fraunhofer.fit.almanac.almanaccitizenapp;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import org.fraunhofer.fit.almanac.model.DuplicateIssue;
import org.fraunhofer.fit.almanac.model.Event;
import org.fraunhofer.fit.almanac.model.IssueEvent;
import org.fraunhofer.fit.almanac.model.IssueStatus;
import org.fraunhofer.fit.almanac.model.PicIssueUpdate;
import org.fraunhofer.fit.almanac.model.Priority;
import org.fraunhofer.fit.almanac.model.Status;
import org.fraunhofer.fit.almanac.protocols.MqttListener;
import org.fraunhofer.fit.almanac.util.UniqueId;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Locale;


public class AlmanacCitizen extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    private static final String TAG = "AlmanacCitizen";
    private static final int CAPTURE_AND_UPLOAD = 0x11;
    public static final int ID_NEW_UPDATE = 0;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;



    ImageListFragment mImageListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_almanac_citizen);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
     //   disconnectMqtt();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mImageListFragment.publishDone();
    }

    //    private void disconnectMqtt(){
//        MqttListener mqttListener = MqttListener.getInstance();
//        if (mqttListener != null) {
//            mqttListener.disconnect();
//        }
//    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        mImageListFragment = ImageListFragment.newInstance(position + 1);
        fragmentManager.beginTransaction()
                .replace(R.id.container, mImageListFragment)
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
//            case 3:
//                mTitle = getString(R.string.title_section3);
//                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        Log.i(TAG, "title = " + mTitle);
        actionBar.setTitle(mTitle);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher); //also displays wide logo

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.almanac_citizen, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }





    public void onNewIssueSelected() {
        Intent intent = new Intent(this,CaptureActivity.class);
        startActivityForResult(intent,CAPTURE_AND_UPLOAD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == CAPTURE_AND_UPLOAD){
//            if(resultCode == RESULT_OK){
//                mImageListFragment.publishDone();
//            }else if(resultCode== RESULT_CANCELED){
//
//            }
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }



}

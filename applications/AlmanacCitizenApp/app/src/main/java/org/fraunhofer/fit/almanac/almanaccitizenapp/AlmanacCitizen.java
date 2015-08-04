package org.fraunhofer.fit.almanac.almanaccitizenapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import org.fraunhofer.fit.almanac.model.DuplicateIssue;
import org.fraunhofer.fit.almanac.model.PicIssueUpdate;
import org.fraunhofer.fit.almanac.protocols.MqttListener;

import java.util.LinkedList;


public class AlmanacCitizen extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,ImageListFragment.OnNewIssueRequestListener{

    private static final String TAG = "AlmanacCitizen";
    private static final int CAPTURE_AND_UPLOAD = 0x11;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private String mClientID;//Client id to uniquely identify the client

    ImageListFragment mImageListFragment;

    IssueTracker mIssueTracker;

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
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.drawable.ic_drawer,
                R.string.app_name,
                R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.setDrawerIndicatorEnabled(false);

        setupMqtt();
        initializeIssueTracker();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectMqtt();
    }

    protected void  initializeIssueTracker(){
        mIssueTracker = IssueTracker.getInstance();
        mIssueTracker.setContext(getApplicationContext());
    }
    private void setupMqtt() {
        MqttListener mqttListener = MqttListener.getInstance();
        mqttListener.connect(getApplicationContext(),getClientId(),new LinkedList<String>());//TODO make a persistent list
        mqttListener.addNotificationListener(mMqttNotificationListener );
    }

    private void disconnectMqtt(){
        MqttListener mqttListener = MqttListener.getInstance();
        if (mqttListener != null) {
            mqttListener.disconnect();
        }
    }
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
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    MqttListener.MqttNotificationListener mMqttNotificationListener = new MqttListener.MqttNotificationListener() {

        @Override
        public void onIssueUpdate(final PicIssueUpdate issueUpdate) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(getActivity().getApplicationContext(),"Got a message on name"+issueUpdate.name,Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(getApplicationContext())
                            .setTitle(issueUpdate.topic)
                            .setMessage(issueUpdate.displayString())
                            .setPositiveButton(R.string.ok_dialog, null)
                            .show();
                    Log.i(TAG, "onIssueUpdate:"+issueUpdate.getString());
                    mIssueTracker.updateIssue(issueUpdate);
                }
            });
        }

        @Override
        public void onDuplicateIssue(final DuplicateIssue duplicateIssue) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(getActivity().getApplicationContext(),"Got a message on name"+issueUpdate.name,Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(getApplicationContext())
                            .setTitle(R.string.dialog_duplicate_title)
                            .setMessage(R.string.dialog_duplicate_message)
                            .setPositiveButton(R.string.ok_dialog, null)
                            .setNegativeButton(R.string.no_dialog,null)
                            .show();
                    Log.i(TAG, "already raised issue "+duplicateIssue.issueId()+ " for the issue " + duplicateIssue.dupIssueId());
                    mIssueTracker.updateIdToOriginalIssue(duplicateIssue.dupIssueId(),duplicateIssue.issueId());
                }
            });
        }
    };

    private String getClientId() {
        //TODO:make this persistent
        if(mClientID == null)
            mClientID = ((TelephonyManager) getBaseContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE)).getDeviceId()+"almanac";
        return  mClientID;
    }

    @Override
    public void onNewIssueSelected() {
        Intent intent = new Intent(this,CaptureActivity.class);
        startActivityForResult(intent,CAPTURE_AND_UPLOAD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAPTURE_AND_UPLOAD){
            if(resultCode == RESULT_OK){
                mImageListFragment.publishDone();
            }else if(resultCode== RESULT_CANCELED){

            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}

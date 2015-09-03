/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fraunhofer.fit.almanac.slider;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import org.fraunhofer.fit.almanac.almanaccitizenapp.AlmanacCitizen;
import org.fraunhofer.fit.almanac.almanaccitizenapp.IssueTracker;
import org.fraunhofer.fit.almanac.almanaccitizenapp.R;
import org.fraunhofer.fit.almanac.model.IssueStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Demonstrates a "screen-slide" animation using a {@link ViewPager}. Because {@link ViewPager}
 * automatically plays such an animation when calling {@link ViewPager#setCurrentItem(int)}, there
 * isn't any animation-specific code in this sample.
 *
 * <p>This sample shows a "next" button that advances the user to the next step in a wizard,
 * animating the current screen out (to the left) and the next screen in (from the right). The
 * reverse animation is played when the user presses the "previous" button.</p>
 *
 * @see ScreenSlidePageFragment
 */
public class ScreenSlideActivity extends FragmentActivity implements ScreenSlidePageFragment.FragmentEventHandler{
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 5;
    public static final String INDEX_POSITION = "position";
    private static final String TAG = "ScreenSlideActivity";

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.
                invalidateOptionsMenu();
            }
        });
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {


            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i(TAG, "onPageScrolled " + position);
                List<IssueStatus> issueList = new ArrayList<IssueStatus>(IssueTracker.getInstance().getAllIssues());
                Collections.sort(issueList, new Comparator<IssueStatus>() {
                    @Override
                    public int compare(IssueStatus lhs, IssueStatus rhs) {
                        if (lhs.updationDate.after(rhs.updationDate))
                            return -1;
                        else
                            return 1;
                    }
                });
                IssueStatus issueStatus = issueList.get(position >= issueList.size() ? issueList.size() - 1 : position);

                IssueTracker.getInstance().resetUpdated(issueStatus.id);
            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "onpageselected " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i(TAG, "onPageScrollStateChanged " + state);
            }
        });
        mPager.setCurrentItem(getIntent().getIntExtra(INDEX_POSITION,0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_screen_slide, menu);

        menu.findItem(R.id.action_previous).setEnabled(mPager.getCurrentItem() > 0);

        // Add either a "next" or "finish" button to the action bar, depending on which page
        // is currently selected.
        MenuItem item = menu.add(Menu.NONE, R.id.action_next, Menu.NONE,
                (mPager.getCurrentItem() == mPagerAdapter.getCount() - 1)
                        ? R.string.action_finish
                        : R.string.action_next);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                NavUtils.navigateUpTo(this, new Intent(this, AlmanacCitizen.class));
                return true;

            case R.id.action_previous:
                // Go to the previous step in the wizard. If there is no previous step,
                // setCurrentItem will do nothing.
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                return true;

            case R.id.action_next:
                // Advance to the next step in the wizard. If there is no next step, setCurrentItem
                // will do nothing.
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDeleteEvent(int position) {

        Log.i(TAG, "deleting the item " + position);

        if (mPagerAdapter.getCount() == 0) {
            finish();
        }
        mPager.setAdapter(mPagerAdapter);

        mPager.setCurrentItem(position);
    }

    /**
     * A simple pager adapter that represents 5 {@link ScreenSlidePageFragment} objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return ScreenSlidePageFragment.create(position);
            }

        @Override
        public int getCount() {
            return IssueTracker.getInstance().getAllIssues().size();
        }

    }
}
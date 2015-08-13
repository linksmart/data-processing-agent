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

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.fraunhofer.fit.almanac.almanaccitizenapp.DisplayImageActivity;
import org.fraunhofer.fit.almanac.almanaccitizenapp.IssueTracker;
import org.fraunhofer.fit.almanac.almanaccitizenapp.R;
import org.fraunhofer.fit.almanac.model.IssueStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 *
 */
public class ScreenSlidePageFragment extends android.support.v4.app.Fragment {

    public interface FragmentEventHandler{
        public void onDeleteEvent(int position);
    }
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;
    private IssueStatus mIssueStatus;
    private FragmentEventHandler eventHandler;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static ScreenSlidePageFragment create(int pageNumber) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenSlidePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        eventHandler = (FragmentEventHandler) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_screen_slide_page, container, false);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.capturedImage);

        List<IssueStatus> issueList = new ArrayList<IssueStatus>(IssueTracker.getInstance().getAllIssues());
        mIssueStatus = issueList.get(getPageNumber()>=issueList.size()?issueList.size()-1:getPageNumber());
        if(mIssueStatus.picPath != null)
            imageView.setImageURI(Uri.parse(mIssueStatus.picPath));
        else
            imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.waste_container));

        TextView nameOfIssue = (TextView) rootView.findViewById(R.id.nameOfIssue);
        nameOfIssue.setText("Name:"+mIssueStatus.name);

        if(mIssueStatus.priority != null) {
            TextView issuePriority = (TextView) rootView.findViewById(R.id.issuePriority);
            issuePriority.setText("Priority:" + mIssueStatus.priority.toString());
        }

        Date now = new Date();
        if(mIssueStatus.timeToCompletion!= null  && mIssueStatus.timeToCompletion.after(now)) {
            TextView completionDate = (TextView) rootView.findViewById(R.id.completionDate);
            completionDate.setText("completionDate:" + mIssueStatus.timeToCompletion.toString());
            ;
        }

        rootView.findViewById(R.id.unsubscribeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteItem();
            }
        });

        rootView.findViewById(R.id.capturedImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayImage();
            }
        });
        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }

    private void onDeleteItem(){
        IssueTracker.getInstance().deleteIssue(mIssueStatus.id);
        eventHandler.onDeleteEvent(getArguments().getInt(ARG_PAGE));
    }

    private void displayImage( ) {
        if(mIssueStatus.picPath != null) {
            Intent intent = new Intent(getActivity(), DisplayImageActivity.class);
            intent.putExtra(DisplayImageActivity.IMAGE_BITMAP, BitmapFactory.decodeFile(mIssueStatus.picPath));
            startActivity(intent);
        }
    }
}

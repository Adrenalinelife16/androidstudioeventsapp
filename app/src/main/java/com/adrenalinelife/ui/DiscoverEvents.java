package com.adrenalinelife.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.adrenalinelife.Browser;
import com.adrenalinelife.R;
import com.adrenalinelife.custom.CustomFragment;

public class DiscoverEvents extends CustomFragment {

    ImageButton yogaButton;
    ImageButton basketballButton;
    ImageButton soccerButton;
    ImageButton frisbeeButton;
    ImageButton golfButton;
    ImageButton tennisButton;
    ImageButton fishingButton;
    ImageButton moreButton;
    ImageButton kickballButton;
    ImageButton tablegamesButton;
    public Bundle mBundle;
    public Intent mIntent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View v = inflater.inflate(R.layout.discover_events, null);

        //yoga
        yogaButton = (ImageButton) v.findViewById(R.id.b_yoga);
        yogaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view ) {

                mBundle = new Bundle();
                mBundle.putString("Filter", "yoga");

                CategoryEvents catEvents = new CategoryEvents();
                catEvents.setArguments(mBundle);

                android.support.v4.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, catEvents);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        //ImageButton basketballButton;
        basketballButton = (ImageButton) v.findViewById(R.id.b_basketball);
        basketballButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view ) {
                mBundle = new Bundle();
                mBundle.putString("Filter", "basketball");
                CategoryEvents catEvents = new CategoryEvents();
                catEvents.setArguments(mBundle);
                android.support.v4.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, catEvents);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        //ImageButton soccerButton;
        soccerButton = (ImageButton) v.findViewById(R.id.b_soccer);
        soccerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view ) {
                mBundle = new Bundle();
                mBundle.putString("Filter", "soccer");
                CategoryEvents catEvents = new CategoryEvents();
                catEvents.setArguments(mBundle);
                android.support.v4.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, catEvents);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        //ImageButton golfButton;
        golfButton = (ImageButton) v.findViewById(R.id.b_golf);
        golfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view ) {
                mBundle = new Bundle();
                mBundle.putString("Filter", "golf");
                CategoryEvents catEvents = new CategoryEvents();
                catEvents.setArguments(mBundle);
                android.support.v4.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, catEvents);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        //ImageButton frisbeeButton;
        frisbeeButton = (ImageButton) v.findViewById(R.id.b_frisbee);
        frisbeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view ) {
                mBundle = new Bundle();
                mBundle.putString("Filter", "frisbee");
                CategoryEvents catEvents = new CategoryEvents();
                catEvents.setArguments(mBundle);
                android.support.v4.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, catEvents);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        //ImageButton tennisButton;
        tennisButton = (ImageButton) v.findViewById(R.id.b_tennis);
        tennisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view ) {
                mBundle = new Bundle();
                mBundle.putString("Filter", "tennis");
                CategoryEvents catEvents = new CategoryEvents();
                catEvents.setArguments(mBundle);
                android.support.v4.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, catEvents);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        //ImageButton fishingButton;
        fishingButton = (ImageButton) v.findViewById(R.id.b_fishing);
        fishingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view ) {
                mBundle = new Bundle();
                mBundle.putString("Filter", "fishing");
                CategoryEvents catEvents = new CategoryEvents();
                catEvents.setArguments(mBundle);
                android.support.v4.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, catEvents);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        //ImageButton moreButton;
        moreButton = (ImageButton) v.findViewById(R.id.b_more);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view ) {
                String url = "http://www.AdrenalineLife.org/events/categories/";
                Intent intent = new Intent(view.getContext(), Browser.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        //ImageButton kickballButton;
        kickballButton = (ImageButton) v.findViewById(R.id.b_kickball);
        kickballButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view ) {
                mBundle = new Bundle();
                mBundle.putString("Filter", "kickball");
                CategoryEvents catEvents = new CategoryEvents();
                catEvents.setArguments(mBundle);
                android.support.v4.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, catEvents);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        //ImageButton tablegamesButton;
        tablegamesButton = (ImageButton) v.findViewById(R.id.b_tablegames);
        tablegamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view ) {
                mBundle = new Bundle();
                mBundle.putString("Filter", "tablegames");
                CategoryEvents catEvents = new CategoryEvents();
                catEvents.setArguments(mBundle);
                android.support.v4.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, catEvents);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return v;
    }
}

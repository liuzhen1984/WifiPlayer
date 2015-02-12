package com.silveroak.playerclient.ui.base;

import android.app.FragmentManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import com.silveroak.playerclient.R;

/**
 * Created by John on 2015/2/12.
 */
public class PlayerBaseSearchBarActivity extends PlayerBaseActivity {
    protected SearchView mSearchView;
    protected ImageButton mSearchBtn;

    protected PlayerBaseSearchBarFragment mFragment;

    @Override
    protected void initContentView() {
        super.initContentView();
        setContentView(R.layout.activity_search_bar);

        mSearchView = (SearchView) findViewById(R.id.searchView);
        mSearchBtn = (ImageButton) findViewById(R.id.imgBtnSearch);
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragment.handleSearch(mSearchView.getQuery().toString());
            }
        });
    }

    @Override
    protected void initFragment() {
        super.initFragment();

        FragmentManager fm = getFragmentManager();
        PlayerBaseSearchBarFragment fragment = (PlayerBaseSearchBarFragment) fm.findFragmentById(R.id.container);
        if (fragment == null) {
            fragment = createFragment();
            fragment.mActivity = this;
            fm.beginTransaction().add(R.id.contentContainer, fragment).commit();
            mFragment = fragment;
        }
    }

    protected PlayerBaseSearchBarFragment createFragment() {
        return null;
    }
}

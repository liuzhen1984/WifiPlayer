package com.silveroak.playerclient.ui.base;

import android.app.FragmentManager;
import android.widget.ImageButton;
import android.widget.SearchView;
import com.silveroak.playerclient.R;

/**
 * Created by John on 2015/2/12.
 */
public class PlayerBaseSearchBarActivity extends PlayerBaseActivity {
    protected SearchView mSearchView;
    protected ImageButton mSearchBtn;

    @Override
    protected void initContentView() {
        super.initContentView();
        setContentView(R.layout.activity_search_bar);

        mSearchView = (SearchView) findViewById(R.id.searchView);
        mSearchBtn = (ImageButton) findViewById(R.id.imgBtnSearch);
    }

    @Override
    protected void initFragment() {
        super.initFragment();

        FragmentManager fm = getFragmentManager();
        PlayerBaseFragment fragment = (PlayerBaseFragment) fm.findFragmentById(R.id.container);
        if (fragment == null) {
            fragment = createFragment();
            fragment.mActivity = this;
            fm.beginTransaction().add(R.id.contentContainer, fragment).commit();
        }
    }

    protected PlayerBaseFragment createFragment() {
        return null;
    }
}

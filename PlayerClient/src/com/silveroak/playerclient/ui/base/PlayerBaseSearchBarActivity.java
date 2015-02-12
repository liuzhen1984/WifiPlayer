package com.silveroak.playerclient.ui.base;

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
    }

    @Override
    protected void initFragment() {
        super.initFragment();

    }
}

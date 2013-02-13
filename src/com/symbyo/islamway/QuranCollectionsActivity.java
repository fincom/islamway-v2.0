package com.symbyo.islamway;

import android.content.Intent;
import android.os.Bundle;
import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.fragments.SlideMenuFragment;

/**
 * @author kdehairy
 * @since 2/13/13
 */
public class QuranCollectionsActivity extends BaseSlidingActivity {
    public static final String EXTRA_SCHOLAR = "extra_key_scholar";

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        Intent intent = getIntent();
        Scholar scholar = (Scholar) intent.getParcelableExtra( EXTRA_SCHOLAR );
        setTitle( scholar.getName() );

    }

    /**
     * Called when an item in the sliding menu is clicked.
     *
     * @param item item selected from the sliding menu.
     */
    @Override
    protected void slideMenuItemClicked( SlideMenuFragment.SlideMenuItem item )
    {
        // TODO implement the method body.
    }
}

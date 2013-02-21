package com.symbyo.islamway;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.symbyo.islamway.domain.Collection;
import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.fragments.ScholarQuranCollectionFragment;
import com.symbyo.islamway.fragments.SlideMenuFragment;
import junit.framework.Assert;

/**
 * @author kdehairy
 * @since 2/13/13
 */
public class QuranCollectionsActivity extends BaseSlidingActivity
        implements ScholarQuranCollectionFragment.OnQuranItemClick {
    public static final String EXTRA_SCHOLAR = "extra_key_scholar";

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        Intent intent = getIntent();
        Scholar scholar = (Scholar) intent.getParcelableExtra( EXTRA_SCHOLAR );
        Assert.assertNotNull( "A scholar object must be passed to the activity",
                scholar );
        setTitle( scholar.getName() );

        if ( savedInstanceState == null ) {
            // TODO attach the scholar quran fragment.
            Bundle bndl = new Bundle();
            bndl.putParcelable( ScholarQuranCollectionFragment.SCHOLAR_KEY,
                    scholar );
            Fragment content = new ScholarQuranCollectionFragment();
            content.setArguments( bndl );
            getSupportFragmentManager().beginTransaction()
                    .replace( R.id.content_frame, content )
                    .commit();
        }

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

    @Override
    public void onQuranItemClick(
            Collection item )
    {
        // TODO implement the method body.
    }
}

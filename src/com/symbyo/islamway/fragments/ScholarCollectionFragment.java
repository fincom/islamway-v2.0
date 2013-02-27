package com.symbyo.islamway.fragments;

import android.app.Activity;
import android.os.Bundle;
import com.symbyo.islamway.BaseCollectionFragment;
import com.symbyo.islamway.ServiceHelper;
import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.domain.Section;
import junit.framework.Assert;

/**
 * @author kdehairy
 * @since 2/20/13
 */
public class ScholarCollectionFragment extends BaseCollectionFragment {

    public final static String SCHOLAR_KEY = "scholars_key";
    public static final String SECTION_KEY = "section_key";


    private Scholar               mScholar;
    private Section               mSection;

    @Override
    protected void doOnCreate( Bundle savedInstanceState )
    {
        // get the scholar
        mScholar = (Scholar) getArguments().getParcelable( SCHOLAR_KEY );
        mSection = (Section) getArguments().getParcelable( SECTION_KEY );
        Assert.assertNotNull( mScholar );
        getSherlockActivity().setTitle( mScholar.getName() );
    }

    @Override
    protected int doRequestCollections( ServiceHelper helper )
    {
        return helper.getScholarCollection( mScholar, mSection );
    }

    @Override
    protected void setActivityTitle( Activity activity )
    {
        activity.setTitle( mScholar.getName() );
    }
}

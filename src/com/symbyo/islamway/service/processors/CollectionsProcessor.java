package com.symbyo.islamway.service.processors;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import com.symbyo.islamway.IWApplication;
import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.service.IWService;

import java.util.List;

/**
 * @author kdehairy
 * @since 2/24/13
 */
public class CollectionsProcessor extends Processor {

    public CollectionsProcessor( Context context )
    {
        super( context );
    }

    @Override
    protected void doProcess(
            List<? extends DomainObject> collection, SQLiteDatabase db,
            Intent pIntent )
    {
        int key = IWApplication.putDomainObjects( collection );
        pIntent.putExtra( IWService.EXTRA_DATA_KEY, key );
    }

}

package com.symbyo.islamway.service.processors;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.symbyo.islamway.IWApplication;
import com.symbyo.islamway.domain.DomainObject;

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
            List<? extends DomainObject> collection, SQLiteDatabase db )
    {
        int key = IWApplication.putDomainObjects( collection );
        // TODO pass this key to the service .. law ragel!
    }
}

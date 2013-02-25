package com.symbyo.islamway.service.processors;

import java.util.List;

import android.content.Intent;
import junit.framework.Assert;
import org.eclipse.jdt.annotation.NonNull;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.persistance.Repository;

public abstract class Processor {

    protected final Context mContext;

    public Processor( Context context )
    {
        Assert.assertNotNull( context );
        mContext = context;
    }

    /**
     * @param domain_collection collection to be processed
     * @param pIntent           the pending intent that is to be sent to the
     *                          service caller. If the processor wants to
     *                          communicate any extra data with the caller.
     * @throws ProcessingException
     */
    public void
    process(
            List<? extends DomainObject> domain_collection,
            Intent pIntent )
            throws ProcessingException
    {
        try {
            SQLiteDatabase db = Repository.getInstance( mContext )
                                          .getWritableDatabase();
            if ( db == null ) {
                throw new SQLiteException();
            }
            Log.d( "IWService",
                    String.format( "processing %d objects",
                            domain_collection.size() ) );
            doProcess( domain_collection, db, pIntent );
        } catch ( SQLiteException e ) {
            try {
                wait( 1000 );
                SQLiteDatabase db = Repository.getInstance( mContext )
                                              .getWritableDatabase();
                if ( db == null ) {
                    throw new ProcessingException();
                }
                doProcess( domain_collection, db, pIntent );
            } catch ( SQLiteException ex ) {
                throw new ProcessingException();
            } catch ( InterruptedException e1 ) {
                throw new ProcessingException();
            }
        }

    }

    protected abstract void doProcess(
            List<? extends DomainObject> collection,
            @NonNull SQLiteDatabase db, Intent pIntent );
}

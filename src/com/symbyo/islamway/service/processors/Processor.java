package com.symbyo.islamway.service.processors;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.persistance.Repository;

public class Processor {

	protected Context mContext;

	public Processor(Context context) {
		mContext = context;
	}

	public void process(List<? extends DomainObject> domain_collection) {
		SQLiteDatabase db = Repository.getInstance(mContext)
				.getWritableDatabase();
	}

}

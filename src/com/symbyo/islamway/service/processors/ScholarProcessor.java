package com.symbyo.islamway.service.processors;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.persistance.UnitOfWork;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class ScholarProcessor extends Processor {
	
	public ScholarProcessor(@NonNull Context context) {
		super(context);
	}

	@Override
	protected void doProcess(List<? extends DomainObject> collection,
			@NonNull SQLiteDatabase db) {
		UnitOfWork.getCurrent().commit(db);
	}
}

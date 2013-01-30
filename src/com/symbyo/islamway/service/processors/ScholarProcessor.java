package com.symbyo.islamway.service.processors;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.symbyo.islamway.domain.DomainObject;
import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.persistance.UnitOfWork;
import com.symbyo.islamway.service.IWService.Section;


public class ScholarProcessor extends Processor {

	private final Section mSection;
	
	public ScholarProcessor(@NonNull Context context, Section section) {
		super(context);
		mSection = section;
	}

	@SuppressWarnings("null")
	@Override
	protected void doProcess(List<? extends DomainObject> collection,
			@NonNull SQLiteDatabase db) {
		if (mSection != null) {
			for (DomainObject obj : collection) {
				Scholar scholar = (Scholar) obj;
				scholar.addSection(mSection);
			}
		}
		UnitOfWork.getCurrent().commit(db);
	}
}

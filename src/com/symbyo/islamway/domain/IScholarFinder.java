package com.symbyo.islamway.domain;

import com.symbyo.islamway.domain.DomainObject.SyncState;

import java.util.List;

public interface IScholarFinder {
	public Scholar findByPk( int id );

	public List<Scholar> findScholarsBySection( Section section );

	public SyncState getSectionSyncState( Section section );
}

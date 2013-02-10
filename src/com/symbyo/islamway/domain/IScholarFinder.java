package com.symbyo.islamway.domain;

import java.util.List;

import com.symbyo.islamway.domain.DomainObject.SyncState;

public interface IScholarFinder {
	public Scholar findByPk( int id );
	public List<Scholar> findScholarsBySection( Section section );
	
	public SyncState getSectionSyncState( Section section );
}

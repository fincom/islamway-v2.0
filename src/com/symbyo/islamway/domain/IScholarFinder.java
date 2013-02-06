package com.symbyo.islamway.domain;

import java.util.List;

public interface IScholarFinder {
	public Scholar findByPk( int id );
	public List<Scholar> findScholarsBySection( Section section );
}

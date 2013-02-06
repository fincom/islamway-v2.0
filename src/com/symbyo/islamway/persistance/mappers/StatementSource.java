package com.symbyo.islamway.persistance.mappers;

interface StatementSource {
	String sql();

	String[] parameters();
}

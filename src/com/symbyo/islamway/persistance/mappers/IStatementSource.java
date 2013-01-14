package com.symbyo.islamway.persistance.mappers;


interface IStatementSource {
	String sql();
	String[] parameters();
}

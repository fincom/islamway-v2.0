CREATE TABLE scholar (
    _id INTEGER PRIMARY KEY,
    server_id INTEGER NOT NULL,
    name TEXT NOT NULL,
    image_url TEXT UNIQUE,
    image_local_path TEXT,
    /**
     * values are:
     * - 0: SYNC_NONE
     * - 2: SYNC_FULL
     */
    quran_sync_state INTEGER NOT NULL DEFAULT 0,
    lessons_sync_state INTEGER NOT NULL DEFAULT 0
);
CREATE INDEX ndx__scholar__name ON scholar (name ASC);
CREATE UNIQUE INDEX ndx__scholar__server_id ON scholar (server_id ASC);

/**
 * This table is pre-populated.
 */
CREATE TABLE section (
    _id INTEGER PRIMARY KEY,
    title TEXT NOT NULL UNIQUE,
    /**
     * values are:
     * - 1: SYNC_BASIC
     * - 2: SYNC_FULL
     */
    sync_state INTEGER NOT NULL DEFAULT 0
);
INSERT INTO section VALUES (1, 'quran', 1);
INSERT INTO section VALUES (2, 'lessons', 1);

CREATE TABLE scholar_section (
    _id INTEGER PRIMARY KEY,
    section_id TEXT,
    scholar_id INTEGER,
    CONSTRAINT fk__scholar_sections__scholar FOREIGN KEY (scholar_id) 
        REFERENCES scholar (_id) ON DELETE CASCADE,
    CONSTRAINT fk__scholar_sections__section FOREIGN KEY (section_id) 
        REFERENCES section (_id) ON DELETE CASCADE
);
CREATE INDEX ndx__scholar_sections__scholar_id ON scholar_section (scholar_id ASC);
CREATE INDEX ndx__scholar_sections__section_id ON scholar_section (section_id ASC);
CREATE UNIQUE INDEX ndx__scholar_section ON scholar_section (section_id, scholar_id);

CREATE TABLE entry (
    _id INTEGER PRIMARY KEY,
    server_id INTEGER NOT NULL,
    title TEXT NOT NULL,
    entries_count INTEGER,
    scholar_id INTEGER,
    parent_collection_id INTEGER,
    published_at TEXT,
    view_order INTEGER,
    narration TEXT,
    type TEXT NOT NULL,
    /**
     * values are:
     * - 1: SYNC_BASIC
     * - 2: SYNC_FULL
     */
    sync_state INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk__entry__scholar FOREIGN KEY (scholar_id)
        REFERENCES scholar (_id) ON DELETE CASCADE
);
CREATE INDEX ndx__entry__scholar_id ON entry (scholar_id ASC);
CREATE INDEX ndx__entry__title ON entry (title ASC);
CREATE INDEX ndx__entry__type ON entry (type ASC);
CREATE UNIQUE INDEX ndx__entry__server_id ON entry (server_id ASC);

CREATE TABLE resource (
    _id INTEGER PRIMARY KEY,
    server_id INTEGER UNIQUE NOT NULL,
    entry_id INTEGER NOT NULL,
    mime_type TEXT,
    size_kb INTEGER,
    bit_rate INTEGER,
    playtime_sec INTEGER,
    url TEXT NOT NULL UNIQUE,
    local_path TEXT,
    CONSTRAINT fk__resource__entry
         FOREIGN KEY (entry_id)
         REFERENCES entry(_id) ON DELETE CASCADE
);
CREATE INDEX ndx__resource__entry_id ON resource (entry_id ASC);

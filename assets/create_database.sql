CREATE TABLE scholar (
    _id INTEGER PRIMARY KEY,
    server_id INTEGER UNIQUE NOT NULL,
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
CREATE INDEX index__scholar__name ON scholar (name ASC);

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

CREATE TABLE quran_collection (
    _id INTEGER PRIMARY KEY,
    server_id INTEGER UNIQUE NOT NULL,
    title TEXT,
    entries_count INTEGER,
    scholar_id INTEGER NOT NULL,
    /**
     * values are:
     * - 1: SYNC_BASIC
     * - 2: SYNC_FULL
     */
    sync_state INTEGER NOT NULL DEFAULT 0,
    type TEXT NOT NULL DEFAULT 'quran',
    CONSTRAINT fk__quran_collection__scholar FOREIGN KEY (scholar_id) 
        REFERENCES scholar (_id) ON DELETE CASCADE
);
CREATE INDEX ndx__quran_collection__scholar_id 
    ON quran_collection (scholar_id ASC);
CREATE INDEX ndx__quran_collection__title ON quran_collection (title ASC);

CREATE TABLE recitation (
    _id INTEGER PRIMARY KEY,
    server_id INTEGER UNIQUE NOT NULL,
    title TEXT NOT NULL,
    quran_collection_id INTEGER NOT NULL,
    view_order INTEGER,
    published_at INTEGER,
    narration TEXT,
    type TEXT NOT NULL DEFAULT 'recitation',
    CONSTRAINT fk__recitation__quran_collection
        FOREIGN KEY (quran_collection_id) 
        REFERENCES quran_collection (_id) ON DELETE CASCADE
);
CREATE INDEX ndx__recitation__quran_collection_id
    ON recitation (quran_collection_id ASC);

CREATE TABLE recitation_resource (
    _id INTEGER PRIMARY KEY,
    server_id INTEGER UNIQUE NOT NULL,
    mime_type TEXT,
    size_kb INTEGER,
    bit_rate INTEGER,
    playtime_sec INTEGER,
    url TEXT NOT NULL UNIQUE,
    local_path TEXT,
    recitation_id INTEGER NOT NULL,
    CONSTRAINT fk__recitation_resource__recitation
         FOREIGN KEY (recitation_id)
         REFERENCES recitation(_id) ON DELETE CASCADE
);
CREATE INDEX ndx__recitation_resource__recitation_id
    ON recitation_resource (recitation_id ASC);

CREATE TABLE lesson (
    _id INTEGER PRIMARY KEY,
    server_id INTEGER UNIQUE NOT NULL,
    title TEXT NOT NULL,
    scholar_id INTEGER NOT NULL,
    parent_lesson_id INTEGER,
    published_at TEXT,
    type TEXT NOT NULL,
    sync_state INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk__lesson__scholar FOREIGN KEY (scholar_id)
        REFERENCES scholar (_id) ON DELETE CASCADE
);
CREATE INDEX ndx__lesson__title ON lesson (title ASC);
CREATE INDEX ndx__lesson__parent_lesson_id ON lesson (parent_lesson_id ASC);

CREATE TABLE lesson_resource (
    _id INTEGER PRIMARY KEY,
    sever_id INTEGER UNIQUE NOT NULL,
    lesson_id INTEGER NOT NULL,
    mime_type TEXT NOT NULL,
    size_kb INTEGER,
    bit_rate INTEGER,
    playtime INTEGER,
    url TEXT NOT NULL UNIQUE,
    local_path TEXT,
    CONSTRAINT fk__lesson_resource__lesson FOREIGN KEY (lesson_id) 
        REFERENCES lesson (_id) ON DELETE CASCADE
);
CREATE INDEX ndx__lesson_resource__lesson_id 
    ON lesson_resource (lesson_id ASC);

/**
 * This table is pre-populated.
 */
CREATE TABLE classification (
    _id INTEGER PRIMARY KEY,
    title TEXT NOT NULL
);

/**
 * This table is pre-populated.
 */
CREATE TABLE narration (
    _id INTEGER PRIMARY KEY,
    title TEXT NOT NULL
);

CREATE TABLE recitation (
    _id INTEGER PRIMARY KEY,
    title TEXT NOT NULL
);

CREATE TABLE scholar (
    _id INTEGER PRIMARY KEY,
    server_id INTEGER UNIQUE NOT NULL,
    name TEXT,
    email TEXT,
    phone TEXT,
    page_url TEXT,
    image_url TEXT,
    image_file TEXT,
    view_count INTEGER NOT NULL DEFAULT 0,
    popularity INTEGER NOT NULL DEFAULT 0
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
    description TEXT,
    scholar_id INTEGER NOT NULL,
    classification_id INTEGER,
    narration_id INTEGER NOT NULL,
    page_url TEXT,
    views_count INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk__quran_collection__scholar FOREIGN KEY (scholar_id) 
        REFERENCES scholar (_id) ON DELETE CASCADE,
    CONSTRAINT fk__quran_collection__classification 
        FOREIGN KEY (classification_id) 
        REFERENCES classification (_id) ON DELETE SET NULL,
    CONSTRAINT fk__quran_collection__narration FOREIGN KEY (narration_id) 
        REFERENCES narration (_id) ON DELETE SET NULL
);
CREATE INDEX ndx__quran_collection__scholar_id 
    ON quran_collection (scholar_id ASC);
CREATE INDEX ndx__quran_collection__classification_id 
    ON quran_collection (classification_id ASC);
CREATE INDEX ndx__quran_collection__narration_id 
    ON quran_collection (narration_id ASC);
CREATE INDEX ndx__quran_collection__title ON quran_collection (title ASC);

CREATE TABLE recitation_media (
    _id INTEGER PRIMARY KEY,
    server_id INTEGER UNIQUE NOT NULL,
    quran_collection_id INTEGER NOT NULL,
    recitation_id INTEGER NOT NULL,
    published_at INTEGER,
    resource_id INTEGER UNIQUE NOT NULL,
    resource_url TEXT UNIQUE NOT NULL,
    resource_path TEXT,
    size_kb INTEGER,
    duration_sec INTEGER,
    personal_views INTEGER NOT NULL DEFAULT 0,
    views_count INTEGER NOT NULL DEFAULT 0,
    vote_up_count INTEGER NOT NULL DEFAULT 0,
    vote_down_count INTEGER NOT NULL DEFAULT 0,
    popularity INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk__recitation_media__quran_collection 
        FOREIGN KEY (quran_collection_id) 
        REFERENCES quran_collection (_id) ON DELETE CASCADE,
    CONSTRAINT fk__recitation_media__recitation FOREIGN KEY (recitation_id) 
        REFERENCES recitation (_id)
);
CREATE INDEX ndx__recitation_media__quran_collection_id 
    ON recitation_media (quran_collection_id ASC);
CREATE INDEX ndx__recitation_media__recitation_id 
    ON recitation_media (recitation_id ASC);
CREATE UNIQUE INDEX ndx_recitation_media__quran_collection__recitation 
    ON recitation_media (quran_collection_id, recitation_id);

CREATE TABLE lesson (
    _id INTEGER PRIMARY KEY,
    server_id INTEGER UNIQUE NOT NULL,
    title TEXT,
    key_words TEXT,
    page_url TEXT,
    parent_lesson_id INTEGER,
    is_group INTEGER NOT NULL DEFAULT 0,
    is_series INTEGER NOT NULL DEFAULT 0,
    personal_views INTEGER NOT NULL DEFAULT 0,
    is_favorite INTEGER NOT NULL DEFAULT 0,
    published_at INTEGER,
    views_count INTEGER NOT NULL DEFAULT 0,
    vote_up_count INTEGER NOT NULL DEFAULT 0,
    vote_down_count INTEGER NOT NULL DEFAULT 0,
    popularity INTEGER NOT NULL DEFAULT 0
);
CREATE INDEX ndx__lesson__title ON lesson (title ASC);
CREATE INDEX ndx__lesson__parent_lesson_id ON lesson (parent_lesson_id ASC);

CREATE TABLE scholar_lesson (
    _id INTEGER PRIMARY KEY,
    scholar_id INTEGER NOT NULL,
    lesson_id INTEGER NOT NULL,
    CONSTRAINT fk__scholar_lesson__scholar FOREIGN KEY (scholar_id) 
        REFERENCES scholar (_id) ON DELETE CASCADE,
    CONSTRAINT fk__scholar_lesson__lesson FOREIGN KEY (lesson_id) 
        REFERENCES lesson (_id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX ndx__scholar_lesson__scholar_id__lesson_id 
    ON scholar_lesson (scholar_id, lesson_id);
CREATE INDEX ndx__scholar_lesson__scholar_id
    ON scholar_lesson (scholar_id ASC);
CREATE INDEX ndx__scholar_lesson_lesson_id
    ON scholar_lesson (lesson_id ASC);

CREATE TABLE lesson_resource (
    _id INTEGER PRIMARY KEY,
    sever_id INTEGER UNIQUE NOT NULL,
    url TEXT NOT NULL,
    size_kb INTEGER,
    duration_sec INTEGER,
    lesson_id INTEGER NOT NULL,
    part_number NOT NULL DEFAULT 1,
    type TEXT NOT NULL DEFAULT "audio",
    CONSTRAINT fk__lesson_resource__lesson FOREIGN KEY (lesson_id) 
        REFERENCES lesson (_id) ON DELETE CASCADE
);
CREATE INDEX ndx__lesson_resource__lesson_id 
    ON lesson_resource (lesson_id ASC);

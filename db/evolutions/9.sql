# --- !Ups

DROP TRIGGER ts_searchable_text ON "document";
DROP INDEX search_idx;

ALTER TABLE "document"
    ADD COLUMN tags character varying(1000);

UPDATE "document" set searchable_text = to_tsvector('pg_catalog.english', coalesce(title,'') || ' ' || coalesce(description,'') || ' ' || coalesce(source,'') || ' ' || coalesce(tags,''));

CREATE INDEX document_search_idx ON "document" USING GIN(searchable_text);
CREATE TRIGGER ts_searchable_text BEFORE INSERT OR UPDATE ON "document"
FOR EACH ROW EXECUTE PROCEDURE tsvector_update_trigger(searchable_text, 'pg_catalog.english', title, description, source, tags);

ALTER TABLE "category"
    DROP COLUMN documentcount;

ALTER TABLE "category"
    ADD COLUMN objecttype character varying(255);

ALTER TABLE "comment"
    ADD COLUMN objectid bigint;

ALTER TABLE "comment"
    ADD COLUMN objecttype character varying(255);

ALTER TABLE "comment"
    ADD COLUMN votecount integer DEFAULT 0 NOT NULL;

ALTER TABLE"comment"
    DROP COLUMN document_id;

ALTER TABLE "document"
    RENAME likecount  TO votecount;

ALTER TABLE "like"
    RENAME objectype  TO objecttype;

ALTER TABLE "like"
    RENAME TO "vote";

ALTER TABLE thumbnail
    DROP CONSTRAINT fk83434d34d0b1afe;

ALTER TABLE thumbnail
    DROP COLUMN document_id;

CREATE TABLE discussion (
  id bigint NOT NULL,
  created timestamp without time zone,
  updated timestamp without time zone,
  "content" character varying(5000),
  tags character varying(1000),
  title character varying(255),
  category_id bigint,
  user_id bigint,
  commentcount integer NOT NULL,
  votecount integer NOT NULL
) WITH (
  OIDS=FALSE
);

ALTER TABLE "document"
    ADD COLUMN discussioncount integer DEFAULT 0 NOT NULL;

ALTER TABLE discussion
    ADD CONSTRAINT discussion_pkey PRIMARY KEY (id);
ALTER TABLE discussion
    ADD CONSTRAINT fka0f5144847140efe FOREIGN KEY (user_id)
        REFERENCES user_ (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE CASCADE;
ALTER TABLE discussion
    ADD CONSTRAINT fka0f51448fa266c1e FOREIGN KEY (category_id)
        REFERENCES category (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE discussion ADD COLUMN searchable_text TSVECTOR;
UPDATE discussion set searchable_text = to_tsvector('pg_catalog.english', coalesce(title,'') || ' ' || coalesce(content,'') || ' ' || coalesce(tags,''));

CREATE INDEX discussion_search_idx
    ON discussion USING GIN(searchable_text);

CREATE TRIGGER ts_searchable_text
    BEFORE INSERT OR UPDATE ON discussion
    FOR EACH ROW EXECUTE PROCEDURE tsvector_update_trigger(searchable_text, 'pg_catalog.english', title, description, source);

# --- !Downs

DROP TRIGGER ts_searchable_text ON discussion;
DROP INDEX discussion_search_idx;
ALTER TABLE discussion DROP COLUMN searchable_text;

ALTER TABLE "document"
    DROP COLUMN discussioncount;

DROP TABLE discussion;

ALTER TABLE thumbnail
    ADD COLUMN document_id bigint;

ALTER TABLE thumbnail
    ADD CONSTRAINT fk83434d34d0b1afe FOREIGN KEY (document_id) REFERENCES "document" (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE vote
    RENAME TO "like";

ALTER TABLE "like"
    RENAME objecttype  TO objectype;

ALTER TABLE "document"
    RENAME votecount  TO likecount;

ALTER TABLE"comment"
    DROP COLUMN votecount;

ALTER TABLE "comment"
    ADD COLUMN document_id bigint;

ALTER TABLE"comment"
    DROP COLUMN objecttype;

ALTER TABLE"comment"
    DROP COLUMN objectid;

ALTER TABLE category
    DROP COLUMN objecttype;

ALTER TABLE category
    ADD COLUMN documentcount integer;

DROP TRIGGER ts_searchable_text ON "document";
DROP INDEX document_search_idx;
UPDATE "document" set searchable_text = to_tsvector('pg_catalog.english', coalesce(title,'') || ' ' || coalesce(description,'') || ' ' || coalesce(source,''));

CREATE INDEX search_idx ON "document" USING GIN(searchable_text);
CREATE TRIGGER ts_searchable_text BEFORE INSERT OR UPDATE ON "document"
FOR EACH ROW EXECUTE PROCEDURE tsvector_update_trigger(searchable_text, 'pg_catalog.english', title, description, source);

ALTER TABLE "document"
    DROP COLUMN tags;

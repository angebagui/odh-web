# --- !Ups
ALTER TABLE "document" ADD COLUMN searchable_text TSVECTOR;
UPDATE "document" set searchable_text = to_tsvector('pg_catalog.english', coalesce(title,'') || ' ' || coalesce(description,'') || ' ' || coalesce(source,''));

CREATE INDEX search_idx ON "document" USING GIN(searchable_text);
CREATE TRIGGER ts_searchable_text BEFORE INSERT OR UPDATE ON "document"
FOR EACH ROW EXECUTE PROCEDURE tsvector_update_trigger(searchable_text, 'pg_catalog.english', title, description, source);

# --- !Downs
DROP TRIGGER ts_searchable_text ON "document";
DROP INDEX search_idx;
ALTER TABLE "document" DROP COLUMN searchable_text;
# --- !Ups

ALTER TABLE "like" RENAME objectype  TO objecttype;
ALTER TABLE "like" RENAME TO like_;

ALTER TABLE thumbnail
    DROP CONSTRAINT fk83434d34d0b1afe;

ALTER TABLE thumbnail
    DROP COLUMN document_id;

# --- !Downs

ALTER TABLE thumbnail ADD COLUMN document_id bigint;
ALTER TABLE thumbnail
    ADD CONSTRAINT fk83434d34d0b1afe FOREIGN KEY (document_id) REFERENCES "document" (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE like_ RENAME TO "like";
ALTER TABLE "like" RENAME objecttype  TO objectype;
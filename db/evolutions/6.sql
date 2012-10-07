# --- !Ups
ALTER TABLE documentjobstatus ADD COLUMN result character varying(5000);
ALTER TABLE documentjobstatus DROP COLUMN resultdocumentid;
ALTER TABLE "document" DROP COLUMN originaldocument_id;
ALTER TABLE "document" DROP COLUMN isarchived;
ALTER TABLE "comment" DROP COLUMN isdeleted;
ALTER TABLE "comment" DROP COLUMN iscensored;
ALTER TABLE "comment" DROP COLUMN parent_id;
ALTER TABLE "comment" DROP COLUMN repliescount;

ALTER TABLE "comment" DROP CONSTRAINT fk9bde863fed0b1afe;
ALTER TABLE "comment"
    ADD CONSTRAINT fk9bde863fed0b1afe FOREIGN KEY (document_id)
    REFERENCES "document" (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE exportlink DROP CONSTRAINT fk834619eeed0b1afe;
ALTER TABLE exportlink ADD CONSTRAINT fk834619eeed0b1afe FOREIGN KEY (document_id)
	REFERENCES "document" (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE;

# --- !Downs
ALTER TABLE documentjobstatus DROP COLUMN result;
ALTER TABLE documentjobstatus ADD COLUMN resultdocumentid bigint;
ALTER TABLE documentjobstatus ALTER COLUMN resultdocumentid SET NOT NULL;
ALTER TABLE "comment" ADD COLUMN isdeleted boolean;
ALTER TABLE "comment" ALTER COLUMN isdeleted SET NOT NULL;
ALTER TABLE "comment" ADD COLUMN iscensored boolean;
ALTER TABLE "comment" ALTER COLUMN iscensored SET NOT NULL;
ALTER TABLE "comment" ADD COLUMN repliescount bigint;
ALTER TABLE "comment" ALTER COLUMN repliescount SET NOT NULL;
ALTER TABLE "comment" ADD COLUMN parent_id bigint;
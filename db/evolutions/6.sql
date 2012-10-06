# --- !Ups
ALTER TABLE documentjobstatus ADD COLUMN result character varying(5000);
ALTER TABLE documentjobstatus DROP COLUMN resultdocumentid;

# --- !Downs
ALTER TABLE documentjobstatus DROP COLUMN result;
ALTER TABLE documentjobstatus ADD COLUMN resultdocumentid bigint;
ALTER TABLE documentjobstatus ALTER COLUMN resultdocumentid SET NOT NULL;
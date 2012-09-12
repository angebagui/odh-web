# --- !Ups
CREATE TABLE documentjobstatus
(
  id bigint NOT NULL,
  status integer,
  resultdocumentid bigint NOT NULL,
  created timestamp with time zone,
  updated timestamp with time zone,
  CONSTRAINT documentjobstatus_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
DROP TABLE clonedocumentjobstatus;

# --- !Downs
DROP TABLE documentjobstatus;
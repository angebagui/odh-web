# --- !Ups

ALTER TABLE "document"
    RENAME clonecount  TO copycount;

ALTER TABLE "document"
    RENAME readcount  TO viewcount;

ALTER TABLE "document"
    ADD COLUMN likecount integer DEFAULT 0 NOT NULL;

CREATE TABLE "like" (
  id bigint NOT NULL,
  created timestamp without time zone,
  updated timestamp without time zone,
  user_id bigint,
  objectid bigint,
  objectype character varying(255),
  CONSTRAINT like_pkey PRIMARY KEY (id)
) WITH (
  OIDS=FALSE
);

ALTER TABLE "like"
    ADD CONSTRAINT fklikeuser FOREIGN KEY (user_id)
        REFERENCES user_ (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE CASCADE;

# --- !Downs
ALTER TABLE "like"
    DROP CONSTRAINT fklikeuser;

DROP TABLE "like";

ALTER TABLE "document"
    DROP COLUMN likecount;

ALTER TABLE "document"
    RENAME copycount  TO clonecount;

ALTER TABLE "document"
    RENAME viewcount  TO readcount;
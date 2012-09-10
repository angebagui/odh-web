# --- !Ups
CREATE TABLE category
(
  id bigint NOT NULL,
  created timestamp without time zone,
  updated timestamp without time zone,
  documentcount integer NOT NULL,
  "name" character varying(255),
  CONSTRAINT category_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE clonedocumentjobstatus
(
  id bigint NOT NULL,
  cloneddocumentid bigint NOT NULL,
  done boolean NOT NULL,
  CONSTRAINT clonedocumentjobstatus_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE "comment"
(
  id bigint NOT NULL,
  created timestamp without time zone,
  updated timestamp without time zone,
  "content" character varying(255),
  author_id bigint,
  document_id bigint,
  CONSTRAINT comment_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE "document"
(
  id bigint NOT NULL,
  created timestamp without time zone,
  updated timestamp without time zone,
  alternatelink character varying(255),
  clonecount integer NOT NULL,
  commentcount integer NOT NULL,
  description character varying(255),
  downloadcount integer NOT NULL,
  embedlink character varying(255),
  filesize bigint NOT NULL,
  googledrivefileid character varying(255),
  isarchived boolean NOT NULL,
  mimetype character varying(255),
  modifieddate character varying(255),
  readcount integer NOT NULL,
  slug character varying(255),
  source character varying(255),
  title character varying(255),
  category_id bigint,
  originaldocument_id bigint,
  owner_id bigint,
  thumbnail_id bigint,
  CONSTRAINT document_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE exportlink
(
  id bigint NOT NULL,
  created timestamp without time zone,
  updated timestamp without time zone,
  link character varying(255),
  mimetype character varying(255),
  document_id bigint,
  CONSTRAINT exportlink_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE thumbnail
(
  id bigint NOT NULL,
  image oid,
  mimetype character varying(255),
  CONSTRAINT thumbnail_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE user_
(
  id bigint NOT NULL,
  created timestamp without time zone,
  updated timestamp without time zone,
  bio character varying(255),
  documentcount integer,
  email character varying(255),
  googleoauthaccesstoken character varying(255),
  googleoauthrefreshtoken character varying(255),
  googleuserid character varying(255),
  "name" character varying(255),
  picture character varying(255),
  score integer,
  userroles bytea,
  CONSTRAINT user__pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

ALTER TABLE "comment"
    ADD CONSTRAINT fk9bde863fa7cd013e FOREIGN KEY (author_id)
        REFERENCES user_ (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    ADD CONSTRAINT fk9bde863fed0b1afe FOREIGN KEY (document_id)
        REFERENCES "document" (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "document"
    ADD CONSTRAINT fk3737353b120f48d FOREIGN KEY (originaldocument_id)
        REFERENCES "document" (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    ADD CONSTRAINT fk3737353bb2fabf16 FOREIGN KEY (owner_id)
        REFERENCES user_ (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    ADD CONSTRAINT fk3737353bdf01b96 FOREIGN KEY (thumbnail_id)
        REFERENCES thumbnail (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    ADD CONSTRAINT fk3737353bfa266c1e FOREIGN KEY (category_id)
        REFERENCES category (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE exportlink
    ADD CONSTRAINT fk834619eeed0b1afe FOREIGN KEY (document_id)
        REFERENCES "document" (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION;

CREATE SEQUENCE hibernate_sequence
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

# --- !Downs
ALTER TABLE "comment"
    DROP CONSTRAINT fk9bde863fa7cd013e,
    DROP CONSTRAINT fk9bde863fed0b1afe;
ALTER TABLE "document"
    DROP CONSTRAINT fk3737353b120f48d,
    DROP CONSTRAINT fk3737353bb2fabf16,
    DROP CONSTRAINT fk3737353bdf01b96,
    DROP CONSTRAINT fk3737353bfa266c1e;
ALTER TABLE exportlink
    DROP CONSTRAINT fk834619eeed0b1afe;

DROP TABLE category;
DROP TABLE clonedocumentjobstatus;
DROP TABLE "comment";
DROP TABLE "document";
DROP TABLE exportlink;
DROP TABLE thumbnail;
DROP TABLE user_;
DROP SEQUENCE hibernate_sequence;
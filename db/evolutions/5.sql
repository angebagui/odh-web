# --- !Ups

ALTER TABLE "document" ALTER "description" TYPE character varying(5000);
ALTER TABLE "comment" ALTER "content" TYPE character varying(5000);

# --- !Downs

ALTER TABLE "document" ALTER "description" TYPE character varying(255);
ALTER TABLE "comment" ALTER "content" TYPE character varying(255);
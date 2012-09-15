# --- !Ups
     
ALTER TABLE "comment"
    ADD COLUMN iscensored boolean NOT NULL,
    ADD COLUMN isdeleted boolean NOT NULL,
    ADD COLUMN repliescount bigint NOT NULL,
    ADD COLUMN parent_id bigint;
    
ALTER TABLE "comment"
    ADD CONSTRAINT fk9bde863fc152a78b FOREIGN KEY (parent_id)
        REFERENCES "comment" (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION;

# --- !Downs

ALTER TABLE "comment"
    DROP CONSTRAINT fk9bde863fc152a78b; 
    
ALTER TABLE "comment"
    DROP COLUMN iscensored,
    DROP COLUMN isdeleted,
    DROP COLUMN repliescount,
    DROP COLUMN parent_id;   
    
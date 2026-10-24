--liquibase formatted sql

--changeset titov:003-create-animal
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_schema = current_schema() AND table_name = 'animal'
CREATE TABLE animal (
                        id BIGSERIAL PRIMARY KEY,
                        species VARCHAR(255),
                        name VARCHAR(255),
                        age INTEGER,
                        breed VARCHAR(255),
                        description VARCHAR(255),
                        status VARCHAR(255),
                        created_at TIMESTAMP
);

--rollback DROP TABLE animal;

--changeset titov:004-create-adoption
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_schema = current_schema() AND table_name = 'adoption'
CREATE TABLE adoption (
                          id BIGSERIAL PRIMARY KEY,
                          animal_id BIGINT NOT NULL REFERENCES animal(id),
                          user_id BIGINT NOT NULL REFERENCES users(id),
                          start_date TIMESTAMP,
                          end_date TIMESTAMP,
                          status SMALLINT
);

CREATE INDEX idx_adoption_animal_id ON adoption(animal_id);
CREATE INDEX idx_adoption_user_id ON adoption(user_id);

--rollback DROP TABLE adoption;

CREATE TABLE fdpa_increased_volumes (
    id                    UUID PRIMARY KEY NOT NULL,
    submission_detail_id  UUID             NOT NULL,
    change_in_volumetrics BOOLEAN,
    comment               TEXT,

    CONSTRAINT fdpa_increased_volumes_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details(id)
);

CREATE INDEX fdpa_increased_volumes_idx ON fdpa_increased_volumes(submission_detail_id);
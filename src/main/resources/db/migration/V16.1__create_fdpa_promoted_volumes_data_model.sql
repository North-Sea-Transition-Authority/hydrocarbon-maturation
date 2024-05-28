CREATE TABLE fdpa_promoted_volumes (
    id                           UUID PRIMARY KEY NOT NULL,
    submission_detail_id         UUID             NOT NULL,
    resources_are_being_promoted BOOLEAN          NOT NULL,
    comment                      TEXT,

    CONSTRAINT fdpa_promoted_volumes_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details(id)
);

CREATE INDEX fdpa_promoted_volumes_idx ON fdpa_promoted_volumes(submission_detail_id);
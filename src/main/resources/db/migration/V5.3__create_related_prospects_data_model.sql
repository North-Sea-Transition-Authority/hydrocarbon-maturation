CREATE TABLE related_prospects (
    id UUID PRIMARY KEY,
    submission_detail_id UUID NOT NULL,
    related_to_another_prospect BOOLEAN,
    related_submission_detail_id UUID,
    preferred_view BOOLEAN,

    CONSTRAINT related_prospects_fk1 FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id),
    CONSTRAINT related_prospects_fk2 FOREIGN KEY (related_submission_detail_id) REFERENCES submission_details (id)
);

CREATE INDEX related_prospects_idx1 ON related_prospects(submission_detail_id);
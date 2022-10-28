CREATE TABLE additional_information (
    id UUID PRIMARY KEY,
    submission_detail_id UUID NOT NULL,
    has_additional_comments BOOLEAN,
    additional_comments TEXT,
    has_additional_documents BOOLEAN,
    CONSTRAINT additional_information_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id)
);

CREATE INDEX additional_information_idx1 ON additional_information(submission_detail_id);
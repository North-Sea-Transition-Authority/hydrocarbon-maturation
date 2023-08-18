CREATE TABLE reservoir_links (
    id UUID PRIMARY KEY,
    reservoir_submission_detail_id UUID NOT NULL,
    linked_reservoir_id UUID NOT NULL,
    UNIQUE(reservoir_submission_detail_id, linked_reservoir_id),
    CONSTRAINT reservoir_links_fk1 FOREIGN KEY (reservoir_submission_detail_id) REFERENCES reservoir_submission_details (id),
    CONSTRAINT reservoir_links_fk2 FOREIGN KEY (linked_reservoir_id) REFERENCES reservoirs (id)
);
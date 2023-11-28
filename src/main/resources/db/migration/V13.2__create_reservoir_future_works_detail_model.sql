CREATE TABLE reservoir_future_works_detail (
    id UUID PRIMARY KEY,
    reservoir_submission_detail_id UUID NOT NULL,
    is_reservoir_being_developed_under_future_phase BOOLEAN,

    CONSTRAINT reservoir_future_works_detail_fk FOREIGN KEY (reservoir_submission_detail_id) REFERENCES reservoir_submission_details(id)
);

CREATE INDEX idx_reservoir_future_works_detail ON reservoir_future_works_detail(reservoir_submission_detail_id);

CREATE TABLE planned_unsanctioned_wells (
    id UUID PRIMARY KEY NOT NULL,
    submission_detail_id UUID NOT NULL,
        CONSTRAINT planned_unsanctioned_wells_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details(id),

    number_of_planned_unsanctioned_wells INTEGER,
    total_capex NUMERIC
);

CREATE INDEX idx_planned_unsanctioned_wells_on_submission_detail_id ON planned_unsanctioned_wells(submission_detail_id);
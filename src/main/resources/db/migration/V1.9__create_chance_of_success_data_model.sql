CREATE TABLE chance_of_success (
    id UUID PRIMARY KEY,
    submission_detail_id UUID NOT NULL,
    play_source_percentage DECIMAL,
    play_source_comments TEXT,
    play_reservoir_percentage DECIMAL,
    play_reservoir_comments TEXT,
    play_top_seal_percentage DECIMAL,
    play_top_seal_comments TEXT,
    prospect_specific_source_percentage DECIMAL,
    prospect_specific_source_comments TEXT,
    prospect_specific_reservoir_percentage DECIMAL,
    prospect_specific_reservoir_comments TEXT,
    prospect_specific_top_seal_percentage DECIMAL,
    prospect_specific_top_seal_comments TEXT,
    prospect_specific_trap_percentage DECIMAL,
    prospect_specific_trap_comments TEXT,
    prospect_specific_migration_percentage DECIMAL,
    prospect_specific_migration_comments TEXT,
    geophysically_modified BOOLEAN,
    geophysically_modified_percentage DECIMAL,
    geophysically_modified_comments TEXT,
    related_to_specific_well_location BOOLEAN,
    well_modified_percentage DECIMAL,
    well_modified_comments TEXT,
    CONSTRAINT chance_of_success_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id)
);

CREATE INDEX chance_of_success_idx1 ON chance_of_success(submission_detail_id);
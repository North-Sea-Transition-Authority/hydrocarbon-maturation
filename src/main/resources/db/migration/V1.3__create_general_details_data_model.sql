CREATE TABLE hydrocarbon_maturation.general_details (
    id UUID PRIMARY KEY,
    submission_detail_id UUID NOT NULL,
    prospect_name TEXT,
    onshore_offshore TEXT,
    prospect_located_in_ukcs BOOLEAN,
    percentage_located_in_ukcs DECIMAL,
    information_source TEXT,
    other_information_source TEXT,
    licence_application_info_source TEXT,
    CONSTRAINT general_details_fk FOREIGN KEY (submission_detail_id) REFERENCES hydrocarbon_maturation.submission_details (id)
);

CREATE INDEX general_details_idx1 ON hydrocarbon_maturation.general_details(submission_detail_id);

CREATE TABLE hydrocarbon_maturation.quadrant_blocks (
    id UUID PRIMARY KEY,
    general_details_id UUID NOT NULL,
    quadrant_block TEXT,
    CONSTRAINT quadrant_blocks_fk FOREIGN KEY (general_details_id) REFERENCES hydrocarbon_maturation.general_details (id)
);
CREATE TABLE permeability_details (
    id UUID PRIMARY KEY,
    submission_detail_id UUID NOT NULL,
    has_permeability BOOLEAN,
    permeability_min DECIMAL,
    permeability_max DECIMAL,
    permeability_average DECIMAL,
    average_method_used TEXT,
    source TEXT,
    source_other TEXT,
    permeability_comments TEXT,
    kv_kh_min DECIMAL,
    kv_kh_max DECIMAL,
    kv_kh_average DECIMAl,
    kv_kh_average_method_used TEXT,
    kv_kh_average_method_used_other TEXT,
    kv_kh_source TEXT,
    kv_kh_source_other TEXT,
    kv_kh_comments TEXT,

    CONSTRAINT permeability_details_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id)
);

CREATE INDEX permeability_details_idx1 ON permeability_details(submission_detail_id);
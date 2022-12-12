CREATE TABLE drill_stem_test_results (
    id UUID PRIMARY KEY ,
    drill_stem_test_id UUID NOT NULL,
    formation_name TEXT,
    top_depth DECIMAL,
    bottom_depth DECIMAL,
    bhfp INTEGER,
    whfp INTEGER,
    flow_duration DECIMAL,
    gas_flowrate DECIMAL,
    oil_flowrate DECIMAL,
    water_flowrate DECIMAL,
    comments_interpretation TEXT,
    CONSTRAINT drill_stem_test_result_fk FOREIGN KEY(drill_stem_test_id) REFERENCES drill_stem_tests (id)
);

CREATE INDEX drill_stem_test_results_idx1 ON drill_stem_test_results(drill_stem_test_id);
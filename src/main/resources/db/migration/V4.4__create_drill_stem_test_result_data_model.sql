CREATE TABLE drill_stem_tests (
    id UUID PRIMARY KEY,
    submission_detail_id UUID NOT NULL,
    has_drill_stem_test_results BOOLEAN,
    CONSTRAINT drill_stem_tests_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id)
);

CREATE INDEX drill_stem_tests_idx1 ON drill_stem_tests(submission_detail_id);
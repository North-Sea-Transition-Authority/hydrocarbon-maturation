DELETE FROM drill_stem_tests;

ALTER TABLE drill_stem_tests
 DROP COLUMN submission_detail_id;

ALTER TABLE drill_stem_tests
    ADD COLUMN reservoir_submission_detail_id UUID NOT NULL;

ALTER TABLE drill_stem_tests
    ADD CONSTRAINT drill_stem_tests_fk FOREIGN KEY (reservoir_submission_detail_id) REFERENCES reservoir_submission_details (id);

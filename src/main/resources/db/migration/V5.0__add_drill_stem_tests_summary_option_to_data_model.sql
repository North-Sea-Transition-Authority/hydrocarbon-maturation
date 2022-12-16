ALTER TABLE drill_stem_tests
ALTER COLUMN has_drill_stem_test_results TYPE TEXT;
UPDATE drill_stem_tests
SET has_drill_stem_test_results = null;
ALTER TABLE drill_stem_tests
RENAME COLUMN has_drill_stem_test_results TO has_drill_stem_test;
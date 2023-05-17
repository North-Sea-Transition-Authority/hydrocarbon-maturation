ALTER TABLE general_details_quadrant_blocks
ADD COLUMN quadrant_block_reference TEXT;

ALTER TABLE general_details_quadrant_blocks
ADD COLUMN quadrant TEXT;

ALTER TABLE general_details_quadrant_blocks
ADD COLUMN block INT;

ALTER TABLE general_details_quadrant_blocks
ADD COLUMN onshore_offshore TEXT;

UPDATE general_details_quadrant_blocks
SET quadrant_block_reference = quadrant_block;

ALTER TABLE general_details_quadrant_blocks
DROP COLUMN quadrant_block;

ALTER TABLE general_details_quadrant_blocks
DROP COLUMN sort_key;
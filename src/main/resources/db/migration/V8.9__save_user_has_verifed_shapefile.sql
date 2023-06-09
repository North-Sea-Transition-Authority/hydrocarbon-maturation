CREATE TABLE outline_for_maximum_case (
  id UUID PRIMARY KEY,
  submission_detail_id UUID NOT NULL,
  has_verified_shapefile BOOLEAN,
  CONSTRAINT outline_for_maximum_case_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id)
);

CREATE INDEX outline_for_maximum_case_idx1 ON outline_for_maximum_case(submission_detail_id);
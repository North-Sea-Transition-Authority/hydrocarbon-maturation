CREATE TABLE hydrocarbon_maturation.opportunity_details (
  id UUID PRIMARY KEY,
  submission_detail_id UUID NOT NULL,
  opportunity_type TEXT,
  other_type_of_opportunity TEXT,
  opportunity_comments TEXT,
  key_evaluation_work_carried_out TEXT,
  key_work_required_for_further_maturation TEXT,
  CONSTRAINT opportunity_details_fk FOREIGN KEY (submission_detail_id) REFERENCES hydrocarbon_maturation.submission_details (id)
);

CREATE INDEX opportunity_details_idx1 ON hydrocarbon_maturation.opportunity_details(submission_detail_id);
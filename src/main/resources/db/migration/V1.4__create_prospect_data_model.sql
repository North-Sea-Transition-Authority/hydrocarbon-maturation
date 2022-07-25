CREATE TABLE hydrocarbon_maturation.opportunity_details (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4 (),
  submission_detail_id UUID,
  opportunity_type TEXT NOT NULL,
  other_type_of_opportunity TEXT,
  opportunity_comments TEXT,
  key_evaluation_work_carried_out TEXT NOT NULL,
  key_work_required_for_further_maturation TEXT,
  CONSTRAINT opportunity_details_fk FOREIGN KEY (submission_detail_id) REFERENCES hydrocarbon_maturation.submission_details (id)
);

CREATE INDEX opportunity_details_idx1 ON hydrocarbon_maturation.opportunity_details(submission_detail_id);
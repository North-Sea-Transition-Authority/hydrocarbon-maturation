CREATE TABLE remi.project_tracker_current_view (
    project_name TEXT,
    field_name TEXT,
    project_status TEXT,
    project_type TEXT,
    estimated_capex NUMERIC,
    expected_first_draft_csr_received_date DATE,
    expected_csr_approval_date DATE,
    expected_first_draft_fdp_received_date DATE,
    expected_consent_date DATE,
    expected_production_date DATE,
    actual_first_draft_csr_received_date DATE,
    actual_csr_approval_date DATE,
    actual_first_draft_fdp_received_date DATE,
    actual_consent_date DATE,
    actual_first_production_date DATE
);

CREATE TABLE remi.project_tracker_cop_current_view (
    project_name TEXT,
    field_name TEXT,
    estimated_final_production_date DATE,
    actual_first_cop_submission_received_date DATE,
    cop_no_objection_letter_sent DATE,
    operator_confirmation_of_cop_received DATE,
    actual_final_production_date DATE,
    field_stopped_production TEXT,
    removed_from_pprs TEXT,
    comments TEXT
);

CREATE TABLE remi.project_tracker_migration_log (
    project_name TEXT,
    field_name TEXT,
    migration_timestamp timestamptz,
    status TEXT,
    failure_reason TEXT
);
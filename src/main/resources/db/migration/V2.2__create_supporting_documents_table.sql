CREATE TABLE supporting_documents (
    id UUID PRIMARY KEY,
    submission_detail_id UUID NOT NULL,
    supporting_document_type TEXT NOT NULL,
    uploaded_file_id UUID NOT NULL,
    CONSTRAINT supporting_documents_fk1 FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id),
    CONSTRAINT supporting_documents_fk2 FOREIGN KEY (uploaded_file_id) REFERENCES uploaded_files (id)
);

CREATE INDEX supporting_documents_idx1 ON supporting_documents(submission_detail_id);
CREATE INDEX supporting_documents_idx2 ON supporting_documents(uploaded_file_id);
CREATE TABLE resource_supporting_documents (
    id UUID PRIMARY KEY,
    resource_id UUID NOT NULL,
    resource_supporting_document_type TEXT NOT NULL,
    uploaded_file_id UUID NOT NULL,
    CONSTRAINT resource_supporting_documents_fk1 FOREIGN KEY (resource_id) REFERENCES resources (id),
    CONSTRAINT resource_supporting_documents_fk2 FOREIGN KEY (uploaded_file_id) REFERENCES uploaded_files (id)
);

CREATE INDEX resource_supporting_documents_idx1 ON resource_supporting_documents(resource_id);
CREATE INDEX resource_supporting_documents_idx2 ON resource_supporting_documents(uploaded_file_id);
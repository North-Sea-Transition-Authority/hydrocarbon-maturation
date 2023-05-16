CREATE TABLE geojson_shapefile_link (
    id UUID PRIMARY KEY,
    shapefile_sd_id UUID NOT NULL,
    geojson_uf_id UUID NOT NULL,
    CONSTRAINT geojson_shapefile_link_fk1 FOREIGN KEY (shapefile_sd_id) REFERENCES supporting_documents (id),
    CONSTRAINT geojson_shapefile_link_fk2 FOREIGN KEY (geojson_uf_id) REFERENCES uploaded_files (id)
);

CREATE INDEX geojson_shapefile_link_idx1 ON geojson_shapefile_link (shapefile_sd_id);
CREATE INDEX geojson_shapefile_link_idx2 ON geojson_shapefile_link (geojson_uf_id);
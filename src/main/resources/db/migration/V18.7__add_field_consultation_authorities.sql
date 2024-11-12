ALTER TABLE fields
ADD COLUMN has_consulting_authorities BOOLEAN;

ALTER TABLE fields
ADD COLUMN consulting_authorities TEXT;

ALTER TABLE fields_aud
ADD COLUMN has_consulting_authorities BOOLEAN;

ALTER TABLE fields_aud
ADD COLUMN consulting_authorities TEXT;
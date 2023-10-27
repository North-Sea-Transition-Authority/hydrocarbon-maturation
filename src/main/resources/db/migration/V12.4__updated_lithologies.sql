UPDATE reservoir_properties
SET
    lithology = 'OTHER',
    lithology_other =
        CASE
            WHEN lithology = 'SALT' THEN 'Salt'
            WHEN lithology = 'EVAPORITES' THEN 'Evaporites (other than salt)'
            WHEN lithology = 'SHALE' THEN 'Shale'
            WHEN lithology = 'CEMENTED_SANDSTONE' THEN 'Cemented sandstone'
            WHEN lithology = 'NOT_PROVIDED' THEN 'Not provided'
            WHEN lithology = 'CLAYSTONE' THEN 'Claystone'
            WHEN lithology = 'COALS' THEN 'Coals'
            WHEN lithology = 'ARGILLACEOUS_DOLOMITE' THEN 'Argillaceous dolomite'
            WHEN lithology = 'ARGILLACEOUS_LIMESTONE' THEN 'Argillaceous limestone'
            WHEN lithology = 'CARBONATE' THEN 'Carbonate'
            ELSE lithology_other
            END
WHERE lithology IN (
                    'SALT', 'EVAPORITES', 'SHALE', 'CEMENTED_SANDSTONE',
                    'NOT_PROVIDED', 'CLAYSTONE', 'COALS', 'ARGILLACEOUS_DOLOMITE',
                    'ARGILLACEOUS_LIMESTONE', 'CARBONATE'
    );

UPDATE source_and_seal_details
SET
    source_lithology = 'OTHER',
    source_lithology_other =
        CASE
            WHEN source_lithology = 'SALT' THEN 'Salt'
            WHEN source_lithology = 'EVAPORITES' THEN 'Evaporites (other than salt)'
            WHEN source_lithology = 'SHALE' THEN 'Shale'
            WHEN source_lithology = 'CEMENTED_SANDSTONE' THEN 'Cemented sandstone'
            WHEN source_lithology = 'NOT_PROVIDED' THEN 'Not provided'
            WHEN source_lithology = 'CLAYSTONE' THEN 'Claystone'
            WHEN source_lithology = 'COALS' THEN 'Coals'
            WHEN source_lithology = 'ARGILLACEOUS_DOLOMITE' THEN 'Argillaceous dolomite'
            WHEN source_lithology = 'ARGILLACEOUS_LIMESTONE' THEN 'Argillaceous limestone'
            WHEN source_lithology = 'CARBONATE' THEN 'Carbonate'
            ELSE source_lithology_other
            END
WHERE source_lithology IN (
                    'SALT', 'EVAPORITES', 'SHALE', 'CEMENTED_SANDSTONE',
                    'NOT_PROVIDED', 'CLAYSTONE', 'COALS', 'ARGILLACEOUS_DOLOMITE',
                    'ARGILLACEOUS_LIMESTONE', 'CARBONATE'
    );

UPDATE source_and_seal_details
SET
    top_seal_lithology = 'OTHER',
    top_seal_lithology_other =
        CASE
            WHEN top_seal_lithology = 'SALT' THEN 'Salt'
            WHEN top_seal_lithology = 'EVAPORITES' THEN 'Evaporites (other than salt)'
            WHEN top_seal_lithology = 'SHALE' THEN 'Shale'
            WHEN top_seal_lithology = 'CEMENTED_SANDSTONE' THEN 'Cemented sandstone'
            WHEN top_seal_lithology = 'NOT_PROVIDED' THEN 'Not provided'
            WHEN top_seal_lithology = 'CLAYSTONE' THEN 'Claystone'
            WHEN top_seal_lithology = 'COALS' THEN 'Coals'
            WHEN top_seal_lithology = 'ARGILLACEOUS_DOLOMITE' THEN 'Argillaceous dolomite'
            WHEN top_seal_lithology = 'ARGILLACEOUS_LIMESTONE' THEN 'Argillaceous limestone'
            WHEN top_seal_lithology = 'CARBONATE' THEN 'Carbonate'
            ELSE top_seal_lithology_other
            END
WHERE top_seal_lithology IN (
                           'SALT', 'EVAPORITES', 'SHALE', 'CEMENTED_SANDSTONE',
                           'NOT_PROVIDED', 'CLAYSTONE', 'COALS', 'ARGILLACEOUS_DOLOMITE',
                           'ARGILLACEOUS_LIMESTONE', 'CARBONATE'
    );

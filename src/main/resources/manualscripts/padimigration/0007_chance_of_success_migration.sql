DO $$
DECLARE

    chance_of_success_rec RECORD;

BEGIN

    FOR chance_of_success_rec IN (
        SELECT prdc.*, sd.id sd_id
        FROM remi.padi_res_data_cos prdc
        JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdc.resdat_id
    )
    LOOP

        INSERT INTO remi.chance_of_success(
            id,
            submission_detail_id,
            play_source_percentage,
            play_source_comments,
            play_reservoir_percentage,
            play_reservoir_comments,
            play_top_seal_percentage,
            play_top_seal_comments,
            prospect_specific_source_percentage,
            prospect_specific_source_comments,
            prospect_specific_reservoir_percentage,
            prospect_specific_reservoir_comments,
            prospect_specific_top_seal_percentage,
            prospect_specific_top_seal_comments,
            prospect_specific_trap_percentage,
            prospect_specific_trap_comments,
            prospect_specific_migration_percentage,
            prospect_specific_migration_comments,
            geophysically_modified,
            geophysically_modified_percentage,
            geophysically_modified_comments,
            related_to_specific_well_location,
            well_modified_percentage,
            well_modified_comments
        ) VALUES (
            gen_random_uuid(),
            chance_of_success_rec.sd_id,
            chance_of_success_rec.play_source,
            chance_of_success_rec.play_source_comment,
            chance_of_success_rec.play_reservoir,
            chance_of_success_rec.play_reservoir_comment,
            chance_of_success_rec.play_seal,
            chance_of_success_rec.play_seal_comment,
            chance_of_success_rec.source_presence,
            chance_of_success_rec.source_presence_comment,
            chance_of_success_rec.reservoir_presence,
            chance_of_success_rec.reservoir_presence_comment,
            NULL, --is prospect specific top seal captured in PaDI?
            NULL,
            chance_of_success_rec.trap_presence,
            chance_of_success_rec.trap_presence_comment,
            NULL, --is prospect specific migration captured in PaDI?
            NULL,
            CASE WHEN chance_of_success_rec.geophysical_cos IS NOT NULL THEN TRUE ELSE FALSE END,
            chance_of_success_rec.geophysical_cos,
            chance_of_success_rec.geophysical_cos_comment,
            CASE WHEN chance_of_success_rec.well_location_cos IS NOT NULL THEN TRUE ELSE FALSE END,
            chance_of_success_rec.well_location_cos,
            chance_of_success_rec.well_location_cos_comment
        );

    END LOOP;

END;

$$;
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
            play_total,
            prospect_specific_trap_presence,
            prospect_specific_trap_presence_comments,
            prospect_specific_trap_effectiveness,
            prospect_specific_trap_effectiveness_comments,
            prospect_specific_source_presence,
            prospect_specific_source_presence_comments,
            prospect_specific_source_effectiveness,
            prospect_specific_source_effectiveness_comments,
            prospect_specific_reservoir_presence,
            prospect_specific_reservoir_presence_comments,
            prospect_specific_reservoir_effectiveness,
            prospect_specific_reservoir_effectiveness_comments,
            prospect_specific_total,
            overall_total,
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
            chance_of_success_rec.play_cos,
            chance_of_success_rec.trap_presence,
            chance_of_success_rec.trap_presence_comment,
            chance_of_success_rec.trap_effectiveness,
            chance_of_success_rec.trap_effectiveness_comment,
            chance_of_success_rec.source_presence,
            chance_of_success_rec.source_presence_comment,
            chance_of_success_rec.source_effectiveness,
            chance_of_success_rec.source_effectiveness_comment,
            chance_of_success_rec.reservoir_presence,
            chance_of_success_rec.reservoir_presence_comment,
            chance_of_success_rec.reservoir_effectiveness,
            chance_of_success_rec.reservoir_effectiveness_comm,
            chance_of_success_rec.prospect_cos,
            chance_of_success_rec.overall_cos,
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
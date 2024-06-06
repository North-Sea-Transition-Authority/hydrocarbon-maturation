DO $$
DECLARE

    opportunity_rec RECORD;

BEGIN

    FOR opportunity_rec IN (
        SELECT
            prdh.opportunity_type,
            prdh.opportunity_comment,
            regexp_replace(prdn.key_evaluation_comment, E'<[^>]+>', '', 'gi') key_evaluation_comm,
            regexp_replace(prdn.key_work_comment, E'<[^>]+>', '', 'gi') key_work_comm,
            sd.id sd_id
        FROM remi.padi_res_data_header prdh
        JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdh.resdat_id
        LEFT JOIN remi.padi_res_data_notes prdn ON prdn.resdat_id = prdh.resdat_id
    )
    LOOP

        INSERT INTO remi.opportunity_details(
            id,
            submission_detail_id,
            opportunity_type,
            other_type_of_opportunity,
            opportunity_comments,
            key_evaluation_work_carried_out,
            key_work_required_for_further_maturation
        ) VALUES (
            gen_random_uuid(),
            opportunity_rec.sd_id,
            CASE opportunity_rec.opportunity_type
                WHEN 'Discovery' THEN 'DISCOVERY'
                WHEN 'Drill-ready Prospect' THEN 'DRILL_READY_PROSPECT'
                WHEN 'Dry Prospect (for post-well analysis)' THEN 'DRY_WELL'
                WHEN 'Lead' THEN 'LEAD'
                WHEN 'Prospect' THEN 'PROSPECT'
                ELSE 'OTHER'
            END,
            CASE
                WHEN opportunity_rec.opportunity_type NOT IN ('Discovery', 'Drill-ready Prospect', 'Dry Prospect (for post-well analysis)', 'Lead', 'Prospect')
                THEN opportunity_rec.opportunity_type -- Is it correct to migrate the other opportunity types as "Other"?
            END,
            opportunity_rec.opportunity_comment,
            opportunity_rec.key_evaluation_comm,
            opportunity_rec.key_work_comm
        );

    END LOOP;

END;

$$;
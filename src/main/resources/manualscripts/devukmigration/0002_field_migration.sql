DO $$
DECLARE

    field_rec RECORD;
    remi_res_id UUID;
    remi_geographical_area TEXT;
    remi_responsible_team TEXT;
    remi_field_identifier INTEGER;

BEGIN

    -- Create fields
    FOR field_rec IN (
      SELECT
        df.field_identifier
      , df.name
      , df.status
      , df.sub_type
      , df.infrastructure_host_flag
      , df.on_offshore
      , df.median_line_flag
      , df.parent_field_identifier
      , df.geographic_area_mnem
      , df.field_prt_number
      , df.consulting_authorities
      FROM remi.devuk_field df
    )
    LOOP

      INSERT INTO remi.resources (
        id
      , type
      , created_timestamp
      , archived
      )
      VALUES (
        gen_random_uuid()
      , 'FIELD'
      , NOW()
      , CASE WHEN field_rec.status BETWEEN 200 AND 900 THEN FALSE ELSE TRUE END
      )
      RETURNING id INTO remi_res_id;

      INSERT INTO remi.fields (
        field_identifier
      , resource_id
      , legal_field_name
      , corrected_legal_field_name
      , common_name_for_field
      , onshore_offshore
      , field_number
      , infrastructure_host
      , determination_status
      , parent_determined_field_id
      , has_consulting_authorities
      , consulting_authorities
      , field_located_in_ukcs
      , field_located_in_england
      )
      VALUES (
        field_rec.field_identifier
      , remi_res_id
      , field_rec.name
      , field_rec.name
      , field_rec.name
      , CASE field_rec.on_offshore WHEN 'L' THEN 'ONSHORE' WHEN 'O' THEN 'OFFSHORE' END
      , field_rec.field_prt_number
      , CASE field_rec.infrastructure_host_flag WHEN 'true' THEN TRUE ELSE FALSE END
      , CASE field_rec.sub_type WHEN 'D' THEN 'DETERMINED' WHEN 'U' THEN 'UNDETERMINED' WHEN 'S' THEN 'SUB_AREA_OF_DETERMINED_FIELD' END
      , field_rec.parent_field_identifier
      , CASE WHEN field_rec.consulting_authorities IS NOT NULL THEN TRUE END
      , CASE field_rec.consulting_authorities
          WHEN 'E' THEN 'ENGLISH_AUTHORITIES'
          WHEN 'S' THEN 'SCOTTISH_AUTHORITIES'
          WHEN 'N' THEN 'NORWEGIAN_AUTHORITIES'
        END
      , CASE field_rec.median_line_flag WHEN 'true' THEN FALSE ELSE TRUE END
      , CASE field_rec.median_line_flag WHEN 'true' THEN FALSE ELSE TRUE END
      )
      RETURNING field_identifier INTO remi_field_identifier;

      IF field_rec.status BETWEEN 200 AND 900 THEN
          INSERT into remi.field_status_history (
            id
          , field_identifier
          , field_status
          , status_date
          )
          VALUES (
            gen_random_uuid()
          , remi_field_identifier
          , 'STATUS_' || field_rec.status
          , NOW()
          );
      END IF;

      IF field_rec.on_offshore = 'L' THEN
        remi_geographical_area := 'ONSHORE';
        remi_responsible_team := 'ONSHORE';
      ELSE
        IF field_rec.geographic_area_mnem = 'NNS' THEN
          remi_geographical_area := 'NORTHERN_NORTH_SEA';
          remi_responsible_team := 'NORTHERN_NORTH_SEA_WEST_OF_SHETLAND';
        ELSIF field_rec.geographic_area_mnem = 'WoS' OR field_rec.geographic_area_mnem IS NULL THEN
          remi_geographical_area := 'WEST_OF_SHETLAND';
          remi_responsible_team := 'NORTHERN_NORTH_SEA_WEST_OF_SHETLAND';
        ELSIF field_rec.geographic_area_mnem = 'CNS' THEN
          remi_geographical_area := 'CENTRAL_NORTH_SEA';
          remi_responsible_team := 'CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA';
        ELSIF field_rec.geographic_area_mnem = 'SNS' THEN
          remi_geographical_area := 'SOUTHERN_NORTH_SEA';
          remi_responsible_team := 'CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA';
        ELSIF field_rec.geographic_area_mnem = 'IS' THEN
          remi_geographical_area := 'EAST_IRISH_SEA';
          remi_responsible_team := 'CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA';
        END IF;
      END IF;

      INSERT INTO remi.regulator_managed_information (
        id
      , resource_id
      , responsible_team
      , geographical_area
      )
      VALUES (
        gen_random_uuid()
      , remi_res_id
      , remi_responsible_team
      , remi_geographical_area
      );

    END LOOP;

END;
$$;


ALTER TABLE timetable
    ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE timetable
    ADD COLUMN active_user_id BIGINT GENERATED ALWAYS AS (
        CASE WHEN is_active THEN user_id END
        ) VIRTUAL;

CREATE UNIQUE INDEX uq_user_active_timetable
    ON timetable (active_user_id);
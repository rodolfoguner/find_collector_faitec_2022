ALTER TABLE collects
    ADD COLUMN status VARCHAR(20);

UPDATE collects
SET status = CASE
    WHEN collected IS TRUE THEN 'COMPLETED'
    WHEN accept IS TRUE THEN 'ACCEPTED'
    ELSE 'PENDING'
END;

ALTER TABLE collects
    ALTER COLUMN status SET NOT NULL,
    ADD CONSTRAINT collects_status_check
        CHECK (status IN ('PENDING', 'ACCEPTED', 'COMPLETED', 'CANCELLED')),
    DROP COLUMN accept,
    DROP COLUMN collected;

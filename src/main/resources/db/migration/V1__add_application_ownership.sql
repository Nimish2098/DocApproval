-- Normalize the legacy table name. The previous mapping contained two trailing spaces.
DO $$
BEGIN
    IF to_regclass('"application_tracker  "') IS NOT NULL
       AND to_regclass('application_tracker') IS NULL THEN
        EXECUTE 'ALTER TABLE "application_tracker  " RENAME TO application_tracker';
    END IF;
END $$;

-- This definition also lets a new database run the migration before Hibernate creates entities.
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    email VARCHAR(255),
    password VARCHAR(255),
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS application_tracker (
    uuid UUID PRIMARY KEY,
    position VARCHAR(255),
    company VARCHAR(255),
    application_status VARCHAR(50),
    local_date_time TIMESTAMP,
    url VARCHAR(255)
);

ALTER TABLE application_tracker
    ADD COLUMN IF NOT EXISTS user_id UUID;

-- Legacy rows had no owner. Assign them to the first registered user so they remain visible
-- without inventing ownership when there is no user to assign them to.
DO $$
DECLARE
    owner_id UUID;
BEGIN
    SELECT id INTO owner_id FROM users ORDER BY id LIMIT 1;

    IF EXISTS (SELECT 1 FROM application_tracker WHERE user_id IS NULL) THEN
        IF owner_id IS NULL THEN
            RAISE EXCEPTION 'Cannot migrate application_tracker: legacy rows exist but users is empty';
        END IF;

        UPDATE application_tracker
        SET user_id = owner_id
        WHERE user_id IS NULL;
    END IF;
END $$;

ALTER TABLE application_tracker
    ALTER COLUMN user_id SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_application_tracker_user'
    ) THEN
        ALTER TABLE application_tracker
            ADD CONSTRAINT fk_application_tracker_user
            FOREIGN KEY (user_id) REFERENCES users(id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_application_tracker_user_id
    ON application_tracker(user_id);
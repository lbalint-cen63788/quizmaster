-- Add SUCCESS and FAILED statuses to attempt table
ALTER TABLE attempt DROP CONSTRAINT chk_status;
ALTER TABLE attempt ADD CONSTRAINT chk_status CHECK (status IN ('FINISHED', 'IN_PROGRESS', 'TIMEOUT', 'SUCCESS', 'FAILED'));

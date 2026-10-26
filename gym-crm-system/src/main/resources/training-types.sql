-- Constant list of training types. The application never modifies this table;
-- the script only inserts missing rows, so running it on every start is safe.
-- Adjust the table/column names to match your schema.
INSERT INTO training_type (name)
SELECT 'YOGA' WHERE NOT EXISTS (SELECT 1 FROM training_type WHERE name = 'YOGA');

INSERT INTO training_type (name)
SELECT 'LIFTING' WHERE NOT EXISTS (SELECT 1 FROM training_type WHERE name = 'LIFTING');

INSERT INTO training_type (name)
SELECT 'AEROBICA' WHERE NOT EXISTS (SELECT 1 FROM training_type WHERE name = 'AEROBICA');

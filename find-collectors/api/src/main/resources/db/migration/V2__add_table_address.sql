ALTER TABLE persons RENAME COLUMN address TO street;
ALTER TABLE collects RENAME COLUMN address TO street;

ALTER TABLE persons
    ADD COLUMN city VARCHAR(100),
    ADD COLUMN state VARCHAR(50);
    
ALTER TABLE collects
    ADD COLUMN city VARCHAR(100),
    ADD COLUMN state VARCHAR(50);
    
UPDATE persons p
SET city = c.name,
    state = s.name
FROM cities c
JOIN states s ON c.state_id = s.id
WHERE p.city_id = c.id;

UPDATE collects co
SET city = c.name,
    state = s.name
FROM cities c
JOIN states s ON c.state_id = s.id
WHERE co.city_id = c.id;

ALTER TABLE persons DROP CONSTRAINT IF EXISTS persons_city_id_fkey;
ALTER TABLE collects DROP CONSTRAINT IF EXISTS collects_city_id_fkey;

ALTER TABLE persons DROP COLUMN IF EXISTS city_id;
ALTER TABLE collects DROP COLUMN IF EXISTS city_id;

DROP TABLE IF EXISTS cities;
DROP TABLE IF EXISTS states;
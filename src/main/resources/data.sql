DELETE FROM bookings;
DELETE FROM items;
DELETE FROM requests;
DELETE FROM users;

ALTER TABLE users
    ALTER COLUMN id RESTART WITH 1;

ALTER TABLE requests
    ALTER COLUMN id RESTART WITH 1;

ALTER TABLE items
    ALTER COLUMN id RESTART WITH 1;

ALTER TABLE bookings
    ALTER COLUMN id RESTART WITH 1;

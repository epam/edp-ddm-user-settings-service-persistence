CREATE TABLE settings
(
    settings_id uuid primary key,
    keycloak_id uuid,
    email text,
    phone text,
    communication_is_allowed boolean
);

INSERT INTO settings VALUES
(
    '321e7654-e89b-12d3-a456-426655441111',
    '496fd2fd-3497-4391-9ead-41410522d06f',
    'settings@gmail.com',
    '0951111111',
    false
);

SELECT 'CREATE DATABASE settings'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'settings')\gexec

\c settings

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TABLE IF NOT EXISTS settings (
    settings_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    keycloak_id UUID UNIQUE NOT NULL,
    email TEXT,
    phone TEXT,
    communication_is_allowed BOOLEAN
);
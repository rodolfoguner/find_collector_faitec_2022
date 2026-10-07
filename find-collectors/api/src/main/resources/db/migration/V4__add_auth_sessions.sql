CREATE TABLE auth_sessions (
    id UUID PRIMARY KEY,
    person_id BIGINT NOT NULL REFERENCES persons(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT auth_sessions_lifetime_check CHECK (expires_at > created_at)
);

CREATE INDEX auth_sessions_person_id_idx ON auth_sessions(person_id);
CREATE INDEX auth_sessions_expires_at_idx ON auth_sessions(expires_at);

CREATE TABLE auth_refresh_tokens (
    token_hash VARCHAR(64) PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES auth_sessions(id) ON DELETE CASCADE,
    consumed_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX auth_refresh_tokens_session_id_idx ON auth_refresh_tokens(session_id);

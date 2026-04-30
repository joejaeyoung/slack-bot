CREATE TABLE users (
    id                   BIGSERIAL PRIMARY KEY,
    display_name         VARCHAR(50)  NOT NULL,
    slack_user_id        VARCHAR(50)  UNIQUE NOT NULL,
    jira_account_id      VARCHAR(100) UNIQUE NOT NULL,
    google_calendar_id   VARCHAR(255) NOT NULL,
    google_refresh_token TEXT         NOT NULL,
    created_at           TIMESTAMPTZ  DEFAULT NOW(),
    updated_at           TIMESTAMPTZ  DEFAULT NOW()
);

CREATE TABLE scrum_entries (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT      NOT NULL REFERENCES users(id),
    slack_ts    VARCHAR(50) UNIQUE NOT NULL,
    text        TEXT        NOT NULL,
    jira_keys   TEXT[],
    posted_date DATE        NOT NULL,
    created_at  TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_scrum_entries_posted_date ON scrum_entries(posted_date);
CREATE INDEX idx_scrum_entries_user_id     ON scrum_entries(user_id);

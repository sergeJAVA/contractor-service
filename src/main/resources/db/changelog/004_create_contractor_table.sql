CREATE TABLE IF NOT EXISTS contractor (
    id VARCHAR(12) PRIMARY KEY NOT NULL,
    parent_id VARCHAR(12),
    name TEXT NOT NULL,
    name_full TEXT,
    inn TEXT,
    ogrn TEXT,
    country TEXT,
    industry INT,
    org_form INT,
    create_date TIMESTAMP NOT NULL DEFAULT NOW(),
    modify_date TIMESTAMP,
    create_user_id TEXT,
    modify_user_id TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    FOREIGN KEY (parent_id) REFERENCES contractor(id),
    FOREIGN KEY (country) REFERENCES country(id),
    FOREIGN KEY (industry) REFERENCES industry(id),
    FOREIGN KEY (org_form) REFERENCES org_form(id)
);
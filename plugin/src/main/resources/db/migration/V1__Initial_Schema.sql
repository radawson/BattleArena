-- BattleArena player backup data
CREATE TABLE ${tablePrefix}player_data (
    uuid VARCHAR(36) NOT NULL PRIMARY KEY,
    player_name VARCHAR(16),
    experience INTEGER,
    health DOUBLE PRECISION,
    healthp DOUBLE PRECISION,
    hunger INTEGER,
    magic INTEGER,
    magicp DOUBLE PRECISION,
    items TEXT,
    match_items TEXT,
    gamemode VARCHAR(16),
    godmode BOOLEAN,
    location TEXT,
    effects TEXT,
    flight BOOLEAN,
    arena_class VARCHAR(64),
    old_team VARCHAR(64),
    scoreboard TEXT,
    money DOUBLE PRECISION,
    stored_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_${tablePrefix}player_data_player_name ON ${tablePrefix}player_data(player_name);

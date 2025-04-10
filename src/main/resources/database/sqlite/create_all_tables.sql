CREATE TABLE IF NOT EXISTS barrels
(
    origin_x       INTEGER,
    origin_y       INTEGER,
    origin_z       INTEGER,
    unique_x       INTEGER,
    unique_y       INTEGER,
    unique_z       INTEGER,
    world_uuid     BINARY(16),
    transformation TEXT,
    format         TEXT,
    barrel_type    TEXT,
    size           INTEGER,
    PRIMARY KEY (unique_x, unique_y, unique_z, world_uuid)
);

CREATE TABLE IF NOT EXISTS barrel_brews
(
    unique_x   INTEGER,
    unique_y   INTEGER,
    unique_z   INTEGER,
    world_uuid BINARY(16),
    pos        INTEGER,
    brew       JSON,
    FOREIGN KEY (unique_x, unique_y, unique_z, world_uuid)
        REFERENCES barrels (unique_x, unique_y, unique_z, world_uuid)
        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS cauldrons
(
    cauldron_x INTEGER,
    cauldron_y INTEGER,
    cauldron_z INTEGER,
    world_uuid BINARY(16),
    brew       JSON,
    PRIMARY KEY (cauldron_x, cauldron_y, cauldron_z, world_uuid)
);

CREATE TABLE IF NOT EXISTS distilleries
(
    origin_x       INTEGER,
    origin_y       INTEGER,
    origin_z       INTEGER,
    unique_x       INTEGER,
    unique_y       INTEGER,
    unique_z       INTEGER,
    world_uuid     BINARY(16),
    transformation TEXT,
    format         TEXT,
    start_time     INTEGER,
    PRIMARY KEY (unique_x, unique_y, unique_z, world_uuid)
);

CREATE TABLE IF NOT EXISTS distillery_brews
(
    unique_x      INTEGER,
    unique_y      INTEGER,
    unique_z      INTEGER,
    world_uuid    BINARY(16),
    pos           INTEGER,
    is_distillate INTEGER,
    brew          JSON,
    FOREIGN KEY (unique_x, unique_y, unique_z, world_uuid)
        REFERENCES distilleries (unique_x, unique_y, unique_z, world_uuid)
        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS version
(
    version INTEGER DEFAULT -1
);
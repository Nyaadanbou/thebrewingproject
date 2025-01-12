CREATE TABLE IF NOT EXISTS barrels
(
    origin_x       INTEGER,
    origin_y       INTEGER,
    origin_z       INTEGER,
    sign_x         INTEGER,
    sign_y         INTEGER,
    sign_z         INTEGER,
    world_uuid     BINARY(16),
    transformation TEXT,
    format         TEXT,
    barrel_type    TEXT,
    size           INTEGER,
    PRIMARY KEY (sign_x, sign_y, sign_z, world_uuid)
);

CREATE TABLE IF NOT EXISTS barrel_brews
(
    sign_x           INTEGER,
    sign_y           INTEGER,
    sign_z           INTEGER,
    world_uuid       BINARY(16),
    pos              INTEGER,
    barrel_type      TEXT,
    cauldron_type    TEXT,
    brew_time        INTEGER,
    aging_start      INTEGER,
    ingredients_json TEXT,
    FOREIGN KEY (sign_x, sign_y, sign_z, world_uuid)
        REFERENCES barrels (sign_x, sign_y, sign_z, world_uuid)
        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS cauldrons
(
    cauldron_x       INTEGER,
    cauldron_y       INTEGER,
    cauldron_z       INTEGER,
    world_uuid       BINARY(16),
    brew_start       INTEGER,
    ingredients_json TEXT,
    PRIMARY KEY (cauldron_x, cauldron_y, cauldron_z, world_uuid)
);

CREATE TABLE IF NOT EXISTS distilleries
(
    x          INTEGER,
    y          INTEGER,
    z          INTEGER,
    world_uuid BINARY(16),
    PRIMARY KEY (x, y, z, world_uuid)
);

CREATE TABLE IF NOT EXISTS version
(
    version INTEGER
);

INSERT INTO version
VALUES (-1);
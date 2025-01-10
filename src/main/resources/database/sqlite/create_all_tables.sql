CREATE TABLE IF NOT EXISTS barrels (
    origin_x INTEGER,
    origin_y INTEGER,
    origin_z INTEGER,
    sign_x INTEGER,
    sign_y INTEGER,
    sign_z INTEGER,
    quaternion_a DOUBLE,
    quaternion_b DOUBLE,
    quaternion_c DOUBLE,
    quaternion_d DOUBLE,
    flip INTEGER,
    format TEXT,
    barrel_type TEXT,
    PRIMARY KEY(sign_x, sign_y, sign_z)
);

CREATE TABLE IF NOT EXISTS barrel_brews(
    sign_x INTEGER,
    sign_y INTEGER,
    sign_z INTEGER,
    pos INTEGER,
    barrel_type TEXT,
    cauldron_type TEXT,
    brew_time INTEGER,
    aging_start INTEGER,
    ingredients_json TEXT,
    FOREIGN KEY (sign_x, sign_y, sign_z)
        REFERENCES barrel_data(sign_x, sign_y, sign_z)
        ON UPDATE CASCADE ON DELETE CASCADE,
);

CREATE TABLE IF NOT EXISTS cauldrons(
    cauldron_x INTEGER,
    cauldron_y INTEGER,
    cauldron_z INTEGER,
    cauldron_type TEXT,
    brew_start INTEGER,
    ingredients_json TEXT
    PRIMARY KEY(cauldron_x, cauldron_y, cauldron_z)
);

CREATE TABLE IF NOT EXISTS distilleries(
    x INTEGER,
    y INTEGER,
    z INTEGER,
    PRIMARY KEY(x, y, z)
);

CREATE TABLE IF NOT EXISTS version(
    version INTEGER
);

INSERT INTO AppVersion VALUES(-1);
UPDATE cauldrons
SET brew_start    = ?,
    ingredients_json = ?
WHERE cauldron_x = ?
  AND cauldron_y = ?
  AND cauldron_z = ?
  AND world_uuid = ?;
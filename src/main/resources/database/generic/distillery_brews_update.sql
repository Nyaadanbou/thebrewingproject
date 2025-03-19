UPDATE distillery_brews
SET cauldron_type    = ?,
    brew_time        = ?,
    distillery_runs  = ?,
    ingredients_json = ?
WHERE unique_x = ?
  AND unique_y = ?
  AND unique_z = ?
  AND world_uuid = ?
  AND pos = ?
  AND is_distillate = ?;
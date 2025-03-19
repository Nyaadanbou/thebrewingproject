UPDATE barrel_brews
SET barrel_type      = ?,
    cauldron_type    = ?,
    brew_time        = ?,
    aging_start      = ?,
    ingredients_json = ?
    distillery_runs  = ?
WHERE sign_x = ?
  AND sign_y = ?
  AND sign_z = ?
  AND world_uuid = ?
  AND pos = ?
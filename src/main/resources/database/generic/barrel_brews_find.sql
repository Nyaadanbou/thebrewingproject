SELECT brew
FROM barrel_brews
WHERE sign_x = ?
  AND sign_y = ?
  AND sign_z = ?
  AND world_uuid = ?;
UPDATE distilleries
SET start_time    = ?
WHERE x = ?
  AND y = ?
  AND z = ?
  AND world_uuid = ?;
-- Product seed for local/demo. Users are created by DataInitializer with PasswordEncoder.
INSERT INTO products (name, brand, description, price_cents, stock, specs_json, image_path, active)
VALUES
  (
    'Nova X1 128GB',
    'Nova',
    'Смартфон с OLED-экраном и быстрой зарядкой.',
    4999000,
    25,
    '{"display":"6.1 OLED","storage":"128GB","ram":"8GB","color":"Graphite"}',
    NULL,
    TRUE
  ),
  (
    'Pulse Pro 256GB',
    'Pulse',
    'Флагман с тройной камерой и защитой IP68.',
    8999000,
    12,
    '{"display":"6.7 AMOLED","storage":"256GB","ram":"12GB","color":"Ocean"}',
    NULL,
    TRUE
  );

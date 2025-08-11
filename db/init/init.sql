-- Crear tabla product
CREATE TABLE IF NOT EXISTS product (
  id VARCHAR(255) NOT NULL,
  name VARCHAR(255) DEFAULT NULL,
  price BIGINT DEFAULT NULL,
  PRIMARY KEY (id)
);

-- Crear tabla inventory
CREATE TABLE IF NOT EXISTS inventory (
  product_id VARCHAR(255) NOT NULL,
  quantity INT DEFAULT NULL,
  PRIMARY KEY (product_id)
);

-- Insertar datos de prueba
INSERT INTO product (id, name, price) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'Laptop Dell XPS 13', 1200),
('550e8400-e29b-41d4-a716-446655440001', 'Mouse Logitech MX Master 3', 999),
('550e8400-e29b-41d4-a716-446655440002', 'Teclado Mecánico Keychron K2', 895);

INSERT INTO inventory (product_id, quantity) VALUES
('550e8400-e29b-41d4-a716-446655440000', 10),
('550e8400-e29b-41d4-a716-446655440001', 25),
('550e8400-e29b-41d4-a716-446655440002', 15);

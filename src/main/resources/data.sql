
-- DATOS INICIALES PARA DEMO Y DESARROLLO
-- Spring Boot ejecuta este archivo automáticamente al arrancar,
-- después de que Hibernate crea las tablas.

-- Categorías iniciales
INSERT INTO categoria (id, nombre) VALUES (1, 'Gastronomía');
INSERT INTO categoria (id, nombre) VALUES (2, 'Hospedaje');
INSERT INTO categoria (id, nombre) VALUES (3, 'Zona Turística');
INSERT INTO categoria (id, nombre) VALUES (4, 'Artesanía');
INSERT INTO categoria (id, nombre) VALUES (5, 'Otro');

-- Coordenadas obtenidas de Google Maps
INSERT INTO ruta (id, nombre, latitud_centro, longitud_centro) VALUES
    (1, 'Marcahuasi', -11.7581, -76.5961);

INSERT INTO ruta (id, nombre, latitud_centro, longitud_centro) VALUES
    (2, 'Cascada de Palacala (Santa Eulalia)', -11.9119, -76.4270);

INSERT INTO ruta (id, nombre, latitud_centro, longitud_centro) VALUES
    (3, 'Lomas de Lucumo', -12.2129, -76.7507);

INSERT INTO ruta (id, nombre, latitud_centro, longitud_centro) VALUES
    (4, 'Rupac', -11.3249, -76.8047);

INSERT INTO ruta (id, nombre, latitud_centro, longitud_centro) VALUES
    (5, 'Cataratas de Antankallo', -11.8542, -76.3711);
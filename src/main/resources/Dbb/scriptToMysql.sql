-- =========================================
-- CATEGORÍAS
-- =========================================
INSERT INTO dccat_categorias (dcca_id, dcca_nombre) VALUES
                                                        (1,'ARMA'),
                                                        (2,'SERVICIO'),
                                                        (3,'ESPECIALISTA'),
                                                        (4,'VARIOS'),
                                                        (5,'ÚNICA DOCTRINA MILITAR CONJUNTA'),
                                                        (6,'ÚNICA LITERATURA MILITAR'),
                                                        (7,'ÚNICA DOCUMENTOS DE INVESTIGACIÓN'),
                                                        (8,'LEYES'),
                                                        (9,'REGLAMENTOS'),
                                                        (10,'COLECCIÓN DEL EJÉRCITO'),
                                                        (11,'AUTORIDADES MILITARES'),
                                                        (12,'VARIOS');

-- =========================================
-- SUBCATEGORÍAS
-- =========================================
INSERT INTO dmsub_subcategorias (dmsb_id, dmsb_nombre, dccat_categorias_dcca_id) VALUES
-- ARMA
(1,'Infantería', 1),
(2,'Caballería', 1),
(3,'Artillería', 1),
(4,'Ingeniería', 1),
(5,'Comunicaciones', 1),
(6,'Inteligencia', 1),
(7,'Aviación del Ejército', 1),

-- SERVICIO
(8,'Intendencia', 2),
(9,'Material de guerra', 2),
(10,'Transportes', 2),

-- ESPECIALISTA
(11,'Sanidad', 3),
(12,'Administración general', 3),
(13,'Músicos', 3),
(14,'Veterinaria', 3),

-- VARIOS
(15,'Ejército', 4),
(16,'Fuerzas especiales', 4),
(17,'SIS', 4),
(18,'Personal', 4),
(19,'Otros', 4);

-- =========================================
-- TIPOS
-- =========================================
INSERT INTO dmtyp_tipos (dmty_id, dmty_codigo, dmty_nombre_tipo, dmty_estado) VALUES
                                                                                  (1, 1,'MANUALES FUNDAMENTALES DEL EJÉRCITO',1),
                                                                                  (2, 2,'MANUALES FUNDAMENTALES DE REFERENCIA DEL EJÉRCITO',1),
                                                                                  (3, 3,'MANUALES DE CAMPAÑA DEL EJÉRCITO',1),
                                                                                  (4, 4,'MANUALES DE TÉCNICAS DEL EJÉRCITO',1),
                                                                                  (5, 5,'SIN TIPO',1);

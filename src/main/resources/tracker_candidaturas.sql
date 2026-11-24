CREATE DATABASE IF NOT EXISTS tracker_candidaturas
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE tracker_candidaturas;

CREATE TABLE IF NOT EXISTS EMPRESA (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    sector VARCHAR(100),
    web VARCHAR(255),
    fechaRegistro DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS OFERTA (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_empresa BIGINT NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT,
    estado VARCHAR(30) NOT NULL,
    fechaPostulacion DATE NOT NULL,
    enlace VARCHAR(255),
    salario DECIMAL(10, 2),
    notas TEXT,
    CONSTRAINT fk_oferta_empresa
        FOREIGN KEY (id_empresa) REFERENCES EMPRESA(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS ENTREVISTA (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_oferta BIGINT NOT NULL,
    fecha DATE NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    notas TEXT,
    resultado VARCHAR(30) NOT NULL,
    CONSTRAINT fk_entrevista_oferta
        FOREIGN KEY (id_oferta) REFERENCES OFERTA(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
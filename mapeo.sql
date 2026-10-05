CREATE DATABASE IF NOT EXISTS code_n_school;
USE code_n_school;

CREATE TABLE empleado (
    id_empleado INT AUTO_INCREMENT PRIMARY KEY,
    dpi VARCHAR(13) UNIQUE NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    puesto VARCHAR(50) NOT NULL,
    fecha_contratacion DATE NOT NULL,
    salario DECIMAL(10,2) NOT NULL,
    estado BOOLEAN NOT NULL
);

CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    correo VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol ENUM('SUPER_ADMIN', 'ADMINISTRADOR', 'SECRETARIA', 'MAESTRO', 'BIBLIOTECARIO', 'ESTUDIANTE') NOT NULL,
    estado BOOLEAN NOT NULL,
    id_empleado INT NULL,
    id_estudiante INT NULL,
    FOREIGN KEY (id_empleado) REFERENCES empleado(id_empleado)
    --De momento no coloco el id del estudiante como llave foranea
);

CREATE TABLE ciclo_lectivo (
    id_ciclo INT AUTO_INCREMENT PRIMARY KEY,
    anio INT UNIQUE NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado ENUM('ABIERTO', 'CERRADO') NOT NULL
);

CREATE TABLE nivel_educativo (
    id_nivel INT AUTO_INCREMENT PRIMARY KEY,
    nivel ENUM('PRE_PRIMARIA', 'PRIMARIA', 'BASICO', 'DIVERSIFICADO') UNIQUE NOT NULL
);

CREATE TABLE carrera (
    id_carrera INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    duracion_anios INT NOT NULL,
    estado BOOLEAN NOT NULL
);

CREATE TABLE grado (
    id_grado INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    id_nivel INT NOT NULL,
    id_carrera INT NULL,
    FOREIGN KEY (id_nivel) REFERENCES nivel_educativo(id_nivel),
    FOREIGN KEY (id_carrera) REFERENCES carrera(id_carrera)
);

CREATE TABLE seccion (
    id_seccion INT AUTO_INCREMENT PRIMARY KEY,
    identificador VARCHAR(10) NOT NULL,
    cupo INT NOT NULL,
    id_grado INT NOT NULL,
    FOREIGN KEY (id_grado) REFERENCES grado(id_grado)
);

CREATE TABLE curso (
    id_curso INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    estado BOOLEAN NOT NULL
);

CREATE TABLE curriculo (
    id_curriculo INT AUTO_INCREMENT PRIMARY KEY,
    id_grado INT NOT NULL,
    id_ciclo INT NOT NULL,
    id_curso INT NOT NULL,
    FOREIGN KEY (id_grado) REFERENCES grado(id_grado),
    FOREIGN KEY (id_ciclo) REFERENCES ciclo_lectivo(id_ciclo),
    FOREIGN KEY (id_curso) REFERENCES curso(id_curso)
);



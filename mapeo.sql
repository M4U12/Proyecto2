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

CREATE TABLE estudiante (
    id_estudiante INT AUTO_INCREMENT PRIMARY KEY,
    codigo_personal VARCHAR(20) UNIQUE NOT NULL, 
    nombres VARCHAR(100) NOT NULL,
    apelliARCHAR(100) NOT NULL,
    fecha_nacdos Vimiento DATE NOT NULL,
    informacion_medica TEXT NULL, 
    nombre_encargado VARCHAR(150) NOT NULL,
    contacto_encargado VARCHAR(100) NOT NULL,
    estado ENUM('ACTIVO', 'INACTIVO', 'GRADUADO') NOT NULL DEFAULT 'ACTIVO'
);

CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    correo VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol ENUM('SUPER_ADMIN', 'ADMINISTRADOR', 'SECRETARIA', 'MAESTRO', 'BIBLIOTECARIO', 'ESTUDIANTE') NOT NULL,
    estado BOOLEAN NOT NULL,
    id_empleado INT NULL,
    id_estudiante INT NULL,
    FOREIGN KEY (id_empleado) REFERENCES empleado(id_empleado),
    FOREIGN KEY (id_estudiante) REFERENCES estudiante(id_estudiante)
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
    descripcion VARCHAR(150) NULL,        
    estado BOOLEAN NOT NULL
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
    id_ciclo INT NOT NULL,
    FOREIGN KEY (id_ciclo) REFERENCES ciclo_lectivo(id_ciclo)
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

CREATE TABLE inscripcion (
    id_inscripcion INT AUTO_INCREMENT PRIMARY KEY,
    id_estudiante INT NOT NULL,
    id_grado INT NOT NULL,
    id_seccion INT NOT NULL,
    id_ciclo INT NOT NULL,
    fecha_inscripcion DATE NOT NULL,
    FOREIGN KEY (id_estudiante) REFERENCES estudiante(id_estudiante),
    FOREIGN KEY (id_grado) REFERENCES grado(id_grado),
    FOREIGN KEY (id_seccion) REFERENCES seccion(id_seccion),
    FOREIGN KEY (id_ciclo) REFERENCES ciclo_lectivo(id_ciclo),
    UNIQUE (id_estudiante, id_ciclo) 
);

CREATE TABLE asignacion_maestro (
    id_asignacion INT AUTO_INCREMENT PRIMARY KEY,
    id_empleado INT NOT NULL, 
    id_curso INT NOT NULL,
    id_seccion INT NOT NULL,
    id_ciclo INT NOT NULL,
    FOREIGN KEY (id_empleado) REFERENCES empleado(id_empleado),
    FOREIGN KEY (id_curso) REFERENCES curso(id_curso),
    FOREIGN KEY (id_seccion) REFERENCES seccion(id_seccion),
    FOREIGN KEY (id_ciclo) REFERENCES ciclo_lectivo(id_ciclo),
    UNIQUE (id_curso, id_seccion, id_ciclo)
);

CREATE TABLE asistencia (
    id_asistencia INT AUTO_INCREMENT PRIMARY KEY,
    id_estudiante INT NOT NULL,
    id_asignacion INT NOT NULL, -- se vincula a la clase especifica del maestro
    fecha DATE NOT NULL,
    estado ENUM('PRESENTE', 'AUSENTE', 'JUSTIFICADO') NOT NULL,
    FOREIGN KEY (id_estudiante) REFERENCES estudiante(id_estudiante),
    FOREIGN KEY (id_asignacion) REFERENCES asignacion_maestro(id_asignacion),
    UNIQUE (id_estudiante, id_asignacion, fecha)
);

-- ZONAS Y PONDERACIONES 
CREATE TABLE actividad (
    id_actividad INT AUTO_INCREMENT PRIMARY KEY,
    id_asignacion INT NOT NULL, -- se vincula a la clase del maestro
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255) NULL,
    ponderacion DECIMAL(5,2) NOT NULL, 
    FOREIGN KEY (id_asignacion) REFERENCES asignacion_maestro(id_asignacion)
);

CREATE TABLE nota (
    id_nota INT AUTO_INCREMENT PRIMARY KEY,
    id_actividad INT NOT NULL,
    id_estudiante INT NOT NULL,
    punteo_obtenido DECIMAL(5,2) NOT NULL,
    FOREIGN KEY (id_actividad) REFERENCES actividad(id_actividad),
    FOREIGN KEY (id_estudiante) REFERENCES estudiante(id_estudiante),
    -- alumno solo tiene una nota final por cada actividad
    UNIQUE (id_actividad, id_estudiante)
);

CREATE TABLE libro (
    id_libro INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    autor VARCHAR(150) NOT NULL,
    editorial VARCHAR(100) NULL,
    copias_totales INT NOT NULL,
    copias_disponibles INT NOT NULL,
    imagen LONGTEXT NULL, 
    estado ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO' 
);

CREATE TABLE prestamo (
    id_prestamo INT AUTO_INCREMENT PRIMARY KEY,
    id_libro INT NOT NULL,
    id_estudiante INT NULL, 
    id_empleado INT NULL,   
    fecha_solicitud DATE NOT NULL,
    fecha_prestamo DATE NULL, 
    fecha_limite DATE NOT NULL,
    fecha_devolucion DATE NULL,
    estado ENUM('SOLICITADO', 'ACTIVO', 'DEVUELTO', 'RECHAZADO') NOT NULL,
    renovaciones INT NOT NULL DEFAULT 0, 
    multa_generada DECIMAL(10,2) NOT NULL DEFAULT 0.00, 
    multa_pagada BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (id_libro) REFERENCES libro(id_libro),
    FOREIGN KEY (id_estudiante) REFERENCES estudiante(id_estudiante),
    FOREIGN KEY (id_empleado) REFERENCES empleado(id_empleado),
    CHECK (id_estudiante IS NOT NULL OR id_empleado IS NOT NULL)
);


CREATE TABLE pago (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    numero_factura VARCHAR(50) UNIQUE NOT NULL,
    id_estudiante INT NULL,
    id_empleado INT NULL
    id_ciclo INT NOT NULL,   
    concepto ENUM('INSCRIPCION', 'COLEGIATURA', 'EXTRACURRICULAR', 'MULTA_BIBLIOTECA') NOT NULL,
    mes_colegiatura INT NULL,  
    descripcion VARCHAR(255) NULL, 
    monto DECIMAL(10,2) NOT NULL,
    fecha_pago DATE NOT NULL,
    estado ENUM('ACTIVO', 'ANULADO') NOT NULL DEFAULT 'ACTIVO',
    motivo_anulacion VARCHAR(255) NULL,
    id_prestamo INT NULL, 
    FOREIGN KEY (id_estudiante) REFERENCES estudiante(id_estudiante),
    FOREIGN KEY (id_empleado) REFERENCES empleado(id_empleado),
    FOREIGN KEY (id_ciclo) REFERENCES ciclo_lectivo(id_ciclo),
    FOREIGN KEY (id_prestamo) REFERENCES prestamo(id_prestamo),
    CHECK (id_estudiante iS NOT NULL OR id_empleado IS NOT NULL)
);



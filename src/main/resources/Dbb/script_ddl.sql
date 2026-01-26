-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2025-01-10 20:26:44.703

-- tables
-- Table: DCCAT_CATEGORIAS
CREATE TABLE DCCAT_CATEGORIAS (
                                  DCCA_ID number  DEFAULT as NOT NULL,
                                  DCCA_NOMBRE varchar2(150)  NOT NULL,
                                  CONSTRAINT PK_DCCAT_CATE PRIMARY KEY (DCCA_ID)
) ;

-- Table: DCGRD_GRADOS
CREATE TABLE DCGRD_GRADOS (
                              DCGR_ID number  DEFAULT as NOT NULL,
                              DCGR_CODIGO number  NOT NULL,
                              DCGR_NOMBRE varchar2(10)  NOT NULL,
                              CONSTRAINT PK_DCGRD_GRAD PRIMARY KEY (DCGR_ID)
) ;

-- Table: DCMCM_COMITE_MANUAL
CREATE TABLE DCMCM_COMITE_MANUAL (
                                     DCMM_ID number  DEFAULT as NOT NULL,
                                     DCMM_MIEMBRO_ID number  NOT NULL,
                                     DCMM_MANUAL_ID number  NOT NULL,
                                     DCMM_TIPO_COMITE_ID number  NOT NULL,
                                     CONSTRAINT PK_DCMCM_COMAN PRIMARY KEY (DCMM_ID)
) ;

-- Table: DCMEM_MIEMBROS
CREATE TABLE DCMEM_MIEMBROS (
                                DCMB_ID number  DEFAULT as NOT NULL,
                                DCMB_GRADO_ID number  NOT NULL,
                                DCMB_NOMBRE_COMPLETO varchar2(255)  NOT NULL,
                                DCMB_IDENTIFICACION varchar2(10)  NOT NULL,
                                DCMB_ESTADO char(1)  NOT NULL,
                                CONSTRAINT PK_DCMEM_MIEM PRIMARY KEY (DCMB_ID)
) ;

-- Table: DCSUB_SUBFASES
CREATE TABLE DCSUB_SUBFASES (
                                DCSB_ID number  DEFAULT as NOT NULL,
                                DCSB_NOMBRE varchar2(150)  NOT NULL,
                                DCSB_ESTADO char(1)  NOT NULL,
                                DCSB_FASE_ID number  NOT NULL,
                                CONSTRAINT PK_DCSUB_SUBF PRIMARY KEY (DCSB_ID)
) ;

-- Table: DCTIP_COMITE
CREATE TABLE DCTIP_COMITE (
                              DCTP_ID number  DEFAULT as NOT NULL,
                              DCTP_CODIGO number  NOT NULL,
                              DCTP_NOMBRE varchar2(150)  NOT NULL,
                              CONSTRAINT PK_DCTIP_COM PRIMARY KEY (DCTP_ID)
) ;

-- Table: DMAN_MANUALES
CREATE TABLE DMAN_MANUALES (
                               DMAN_ID number  DEFAULT as NOT NULL,
                               DMAN_NOMBRE varchar2(100)  NOT NULL,
                               DMAN_CODIGO varchar2(20)  NULL,
                               DMAN_OBSERVACIONES varchar2(255)  NULL,
                               DMAN_DESCRIPCION CLOB  NULL,
                               DMAN_ANIO_PUB date  NULL,
                               DMAN_ESTADO char(1)  NOT NULL,
                               DMAN_PUBLICADO char(1)  NOT NULL,
                               DMAN_URL_IMAGEN varchar2(250)  NULL,
                               DMAN_ENLACE_DESC varchar2(50)  NULL,
                               DMAN_FASE_ID number  NOT NULL,
                               DMAN_TIPO_ID number  NOT NULL,
                               DMSUB_SUBCATEGORIAS_DMSB_ID number  NOT NULL,
                               CONSTRAINT PK_DMAN_MANUAL PRIMARY KEY (DMAN_ID)
) ;

-- Table: DMDOW_DESCARGAS
CREATE TABLE DMDOW_DESCARGAS (
                                 DMDW_ID number  DEFAULT as NOT NULL,
                                 DMDW_FECHA timestamp  NOT NULL,
                                 DMDW_IP varchar2(10)  NOT NULL,
                                 DMDW_USUARIO_ID varchar2(10)  NULL,
                                 DMDW_MANUAL_ID number  NOT NULL,
                                 CONSTRAINT PK_DMDOW_DESC PRIMARY KEY (DMDW_ID)
) ;

-- Table: DMMIL_UNIDADES
CREATE TABLE DMMIL_UNIDADES (
                                DMMU_ID number  DEFAULT as NOT NULL,
                                DMMU_NOMBRE varchar2(50)  NOT NULL,
                                DMMU_SIGLAS varchar2(10)  NOT NULL,
                                DMMU_ESTADO char(1)  NOT NULL,
                                CONSTRAINT PK_DMMIL_UNID PRIMARY KEY (DMMU_ID)
) ;

-- Table: DMMIL_UNIDADES_MILITARES
CREATE TABLE DMMIL_UNIDADES_MILITARES (
                                          DMMU_ID number  DEFAULT as NOT NULL,
                                          DMMU_MANUAL_ID number  NOT NULL,
                                          DMMU_TIPO_COMITE_ID number  NOT NULL,
                                          DMMU_UNIDAD_MIL_ID number  NOT NULL,
                                          CONSTRAINT PK_DMMIL_UNIM PRIMARY KEY (DMMU_ID)
) ;

-- Table: DMPHS_FASES
CREATE TABLE DMPHS_FASES (
                             DMPH_ID number  DEFAULT as NOT NULL,
                             DMPH_CODIGO number  NOT NULL,
                             DMPH_NOMBRE_FASE number  NOT NULL,
                             DMPH_ESTADO number(1)  NOT NULL,
                             CONSTRAINT PK_DMPHS_FASE PRIMARY KEY (DMPH_ID)
) ;

-- Table: DMPHS_FASE_SUBFASES
CREATE TABLE DMPHS_FASE_SUBFASES (
                                     DMPH_ID number  DEFAULT as NOT NULL,
                                     DMPH_COMPLETADO char(1)  NOT NULL,
                                     DMPH_FECHA_COMP date  NOT NULL,
                                     DMPH_MANUAL_ID number  NOT NULL,
                                     DMPH_SUBFASE_ID number  NOT NULL,
                                     DMPH_FASE_ID number  NOT NULL,
                                     CONSTRAINT PK_DMPHS_FASUB PRIMARY KEY (DMPH_ID)
) ;

-- Table: DMSUB_SUBCATEGORIAS
CREATE TABLE DMSUB_SUBCATEGORIAS (
                                     DMSB_ID number  DEFAULT as NOT NULL,
                                     DMSB_NOMBRE varchar2(150)  NOT NULL,
                                     DCCAT_CATEGORIAS_DCCA_ID number  NOT NULL,
                                     CONSTRAINT PK_DMSUB_SUBC PRIMARY KEY (DMSB_ID)
) ;

-- Table: DMTYP_TIPOS
CREATE TABLE DMTYP_TIPOS (
                             DMTY_ID number  DEFAULT as NOT NULL,
                             DMTY_CODIGO varchar2(10)  NOT NULL,
                             DMTY_NOMBRE_TIPO varchar2(50)  NOT NULL,
                             DMTY_ESTADO char(1)  NOT NULL,
                             CONSTRAINT PK_DMTYP_TIPO PRIMARY KEY (DMTY_ID)
) ;

-- foreign keys
-- Reference: DMAN_MAN_DMSUB_SUBCAT (table: DMAN_MANUALES)
ALTER TABLE DMAN_MANUALES ADD CONSTRAINT DMAN_MAN_DMSUB_SUBCAT
    FOREIGN KEY (DMSUB_SUBCATEGORIAS_DMSB_ID)
        REFERENCES DMSUB_SUBCATEGORIAS (DMSB_ID);

-- Reference: DMSUB_SUBCAT_DCCAT_CAT (table: DMSUB_SUBCATEGORIAS)
ALTER TABLE DMSUB_SUBCATEGORIAS ADD CONSTRAINT DMSUB_SUBCAT_DCCAT_CAT
    FOREIGN KEY (DCCAT_CATEGORIAS_DCCA_ID)
        REFERENCES DCCAT_CATEGORIAS (DCCA_ID);

-- Reference: FK_DCMCM_MANUAL (table: DCMCM_COMITE_MANUAL)
ALTER TABLE DCMCM_COMITE_MANUAL ADD CONSTRAINT FK_DCMCM_MANUAL
    FOREIGN KEY (DCMM_MANUAL_ID)
        REFERENCES DMAN_MANUALES (DMAN_ID);

-- Reference: FK_DCMCM_MIEMBRO (table: DCMCM_COMITE_MANUAL)
ALTER TABLE DCMCM_COMITE_MANUAL ADD CONSTRAINT FK_DCMCM_MIEMBRO
    FOREIGN KEY (DCMM_MIEMBRO_ID)
        REFERENCES DCMEM_MIEMBROS (DCMB_ID);

-- Reference: FK_DCMCM_TIPO (table: DCMCM_COMITE_MANUAL)
ALTER TABLE DCMCM_COMITE_MANUAL ADD CONSTRAINT FK_DCMCM_TIPO
    FOREIGN KEY (DCMM_TIPO_COMITE_ID)
        REFERENCES DCTIP_COMITE (DCTP_ID);

-- Reference: FK_DCMEM_GRADO (table: DCMEM_MIEMBROS)
ALTER TABLE DCMEM_MIEMBROS ADD CONSTRAINT FK_DCMEM_GRADO
    FOREIGN KEY (DCMB_GRADO_ID)
        REFERENCES DCGRD_GRADOS (DCGR_ID);

-- Reference: FK_DCSUB_FASE (table: DCSUB_SUBFASES)
ALTER TABLE DCSUB_SUBFASES ADD CONSTRAINT FK_DCSUB_FASE
    FOREIGN KEY (DCSB_FASE_ID)
        REFERENCES DMPHS_FASES (DMPH_ID);

-- Reference: FK_DMAN_FASE (table: DMAN_MANUALES)
ALTER TABLE DMAN_MANUALES ADD CONSTRAINT FK_DMAN_FASE
    FOREIGN KEY (DMAN_FASE_ID)
        REFERENCES DMPHS_FASES (DMPH_ID);

-- Reference: FK_DMAN_TIPO (table: DMAN_MANUALES)
ALTER TABLE DMAN_MANUALES ADD CONSTRAINT FK_DMAN_TIPO
    FOREIGN KEY (DMAN_TIPO_ID)
        REFERENCES DMTYP_TIPOS (DMTY_ID);

-- Reference: FK_DMDOW_MANUAL (table: DMDOW_DESCARGAS)
ALTER TABLE DMDOW_DESCARGAS ADD CONSTRAINT FK_DMDOW_MANUAL
    FOREIGN KEY (DMDW_MANUAL_ID)
        REFERENCES DMAN_MANUALES (DMAN_ID);

-- Reference: FK_DMMIL_MANUAL (table: DMMIL_UNIDADES_MILITARES)
ALTER TABLE DMMIL_UNIDADES_MILITARES ADD CONSTRAINT FK_DMMIL_MANUAL
    FOREIGN KEY (DMMU_MANUAL_ID)
        REFERENCES DMAN_MANUALES (DMAN_ID);

-- Reference: FK_DMMIL_TIPO (table: DMMIL_UNIDADES_MILITARES)
ALTER TABLE DMMIL_UNIDADES_MILITARES ADD CONSTRAINT FK_DMMIL_TIPO
    FOREIGN KEY (DMMU_TIPO_COMITE_ID)
        REFERENCES DCTIP_COMITE (DCTP_ID);

-- Reference: FK_DMMIL_UNIDAD (table: DMMIL_UNIDADES_MILITARES)
ALTER TABLE DMMIL_UNIDADES_MILITARES ADD CONSTRAINT FK_DMMIL_UNIDAD
    FOREIGN KEY (DMMU_UNIDAD_MIL_ID)
        REFERENCES DMMIL_UNIDADES (DMMU_ID);

-- Reference: FK_DMPHS_FASE (table: DMPHS_FASE_SUBFASES)
ALTER TABLE DMPHS_FASE_SUBFASES ADD CONSTRAINT FK_DMPHS_FASE
    FOREIGN KEY (DMPH_FASE_ID)
        REFERENCES DMPHS_FASES (DMPH_ID);

-- Reference: FK_DMPHS_MANUAL (table: DMPHS_FASE_SUBFASES)
ALTER TABLE DMPHS_FASE_SUBFASES ADD CONSTRAINT FK_DMPHS_MANUAL
    FOREIGN KEY (DMPH_MANUAL_ID)
        REFERENCES DMAN_MANUALES (DMAN_ID);

-- Reference: FK_DMPHS_SUBFASE (table: DMPHS_FASE_SUBFASES)
ALTER TABLE DMPHS_FASE_SUBFASES ADD CONSTRAINT FK_DMPHS_SUBFASE
    FOREIGN KEY (DMPH_SUBFASE_ID)
        REFERENCES DCSUB_SUBFASES (DCSB_ID);

-- End of file.


package com.example.tracker.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;



public class Oferta {

    /**
     * Representa una oferta de empleo dentro del sistema de seguimiento.
     * Almacena información detallada sobre el puesto, la empresa, el estado de la postulación,
     * la fecha, el enlace de interés y notas adicionales.
     *
     * @author Davvid Lorca
     * @version 1.0
     */

    private long id;
    private String titulo;
    private String empresa;
    private String estado;
    private LocalDate fechaPostulacion;
    private String enlace;
    private String notas;


    /**
     * Crea una nueva instancia de {@code Oferta} con todos sus atributos.
     *
     * @param id Identificador único de la oferta.
     * @param titulo Título o puesto de trabajo.
     * @param empresa Nombre de la empresa.
     * @param estado Estado actual del proceso.
     * @param fechaPostulacion Fecha de la postulación.
     * @param enlace URL o enlace de la oferta.
     * @param notas Notas adicionales u observaciones.
     */
    public Oferta(long id, String titulo, String empresa, String estado, LocalDate fechaPostulacion, String enlace, String notas) {
        this.id = id;
        this.titulo = titulo;
        this.empresa = empresa;
        this.estado = estado;
        this.fechaPostulacion = fechaPostulacion;
        this.enlace = enlace;
        this.notas = notas;
    }

    public long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getEmpresa() { return empresa; }
    public String getEstado() { return estado; }
    public LocalDate getFechaPostulacion() { return fechaPostulacion; }
    public String getEnlace() { return enlace; }
    public String getNotas() { return notas; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format("[%d] %s en %s | Estado: %s | Fecha: %s | URL: %s | Notas: %s",
                id, titulo, empresa, estado, fechaPostulacion.format(fmt),
                (enlace != null ? enlace : "N/A"), (notas != null ? notas : "N/A"));
    }
}
package com.example.tracker.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Oferta {
    private long id;
    private String titulo;
    private String empresa;
    private String estado;
    private LocalDate fechaPostulacion;
    private String enlace;
    private String notas;

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
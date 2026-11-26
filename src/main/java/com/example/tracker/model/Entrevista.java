package com.example.tracker.model;

import java.time.LocalDate;

/**
 * Entrevista que pega a una candidatura, con los datos de la oferta y la empresa
 * que se muestran en la pantalla de entrevistas.
 */
public class Entrevista {

    private final long id;
    private final String titulo;
    private final String empresa;
    private final LocalDate fecha;
    private final String tipo;
    private final String notas;
    private final String resultado;

    public Entrevista(long id, String titulo, String empresa, LocalDate fecha,
                      String tipo, String notas, String resultado) {
        this.id = id;
        this.titulo = titulo;
        this.empresa = empresa;
        this.fecha = fecha;
        this.tipo = tipo;
        this.notas = notas;
        this.resultado = resultado;
    }

    public long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getEmpresa() { return empresa; }
    public LocalDate getFecha() { return fecha; }
    public String getTipo() { return tipo; }
    public String getNotas() { return notas; }
    public String getResultado() { return resultado; }
}

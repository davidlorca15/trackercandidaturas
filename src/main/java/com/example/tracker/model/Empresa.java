package com.example.tracker.model;

/**
 * Empresa a la que se envían candidaturas.
 */
public class Empresa {

    private final long id;
    private final String nombre;
    private final String sector;
    private final String web;
    private final int candidaturasActivas;

    public Empresa(long id, String nombre, String sector, String web, int candidaturasActivas) {
        this.id = id;
        this.nombre = nombre;
        this.sector = sector;
        this.web = web;
        this.candidaturasActivas = candidaturasActivas;
    }

    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getSector() { return sector; }
    public String getWeb() { return web; }
    public int getCandidaturasActivas() { return candidaturasActivas; }
}
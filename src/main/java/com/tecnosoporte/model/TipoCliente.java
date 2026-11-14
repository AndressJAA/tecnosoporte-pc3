package com.tecnosoporte.model;

public enum TipoCliente {

    NUEVO("Nuevo", "Primera atención"),
    FRECUENTE("Frecuente", "Cliente recurrente"),
    INSTITUCIONAL("Institucional", "Empresa u organización");

    private final String nombre;
    private final String descripcion;

    TipoCliente(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

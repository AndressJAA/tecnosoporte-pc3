package com.tecnosoporte.model;

import java.math.BigDecimal;

public enum ServicioTecnico {

    DIAGNOSTICO("Diagnóstico de computadora", "Evaluación integral de hardware y software", "40.00"),
    MANTENIMIENTO("Mantenimiento preventivo", "Limpieza, optimización y revisión del equipo", "70.00"),
    INSTALACION_SOFTWARE("Instalación de software", "Instalación y configuración del programa", "50.00");

    private final String nombre;
    private final String descripcion;
    private final BigDecimal precioUnitario;

    ServicioTecnico(String nombre, String descripcion, String precioUnitario) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioUnitario = new BigDecimal(precioUnitario);
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
}

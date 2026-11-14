package com.tecnosoporte.model;

import java.math.BigDecimal;

public record Cotizacion(
        String nombreCliente,
        ServicioTecnico servicio,
        int cantidad,
        TipoCliente tipoCliente,
        BigDecimal precioUnitario,
        BigDecimal subtotal,
        BigDecimal porcentajeDescuento,
        BigDecimal importeDescuento,
        BigDecimal total) {
}

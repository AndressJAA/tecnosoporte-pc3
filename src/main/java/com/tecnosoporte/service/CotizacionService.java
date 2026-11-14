package com.tecnosoporte.service;

import com.tecnosoporte.model.Cotizacion;
import com.tecnosoporte.model.SolicitudCotizacion;
import com.tecnosoporte.model.TipoCliente;
import com.tecnosoporte.strategy.EstrategiaDescuento;
import com.tecnosoporte.strategy.SelectorEstrategiaDescuento;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

@Service
public class CotizacionService {

    private static final BigDecimal CIEN = new BigDecimal("100");

    private final SelectorEstrategiaDescuento selectorEstrategia;

    public CotizacionService(SelectorEstrategiaDescuento selectorEstrategia) {
        this.selectorEstrategia = selectorEstrategia;
    }

    public Map<TipoCliente, BigDecimal> obtenerPorcentajesDisponibles() {
        EnumMap<TipoCliente, BigDecimal> porcentajes = new EnumMap<>(TipoCliente.class);
        for (TipoCliente tipoCliente : TipoCliente.values()) {
            BigDecimal porcentaje = selectorEstrategia.seleccionar(tipoCliente)
                    .getTasaDescuento()
                    .multiply(CIEN)
                    .setScale(0, RoundingMode.HALF_UP);
            porcentajes.put(tipoCliente, porcentaje);
        }
        return Map.copyOf(porcentajes);
    }

    public Cotizacion calcular(SolicitudCotizacion solicitud) {
        Objects.requireNonNull(solicitud, "La solicitud es obligatoria");
        Objects.requireNonNull(solicitud.getServicio(), "El servicio es obligatorio");
        Objects.requireNonNull(solicitud.getCantidad(), "La cantidad es obligatoria");
        Objects.requireNonNull(solicitud.getTipoCliente(), "El tipo de cliente es obligatorio");

        BigDecimal precioUnitario = solicitud.getServicio().getPrecioUnitario();
        BigDecimal subtotal = precioUnitario
                .multiply(BigDecimal.valueOf(solicitud.getCantidad()))
                .setScale(2, RoundingMode.HALF_UP);

        EstrategiaDescuento estrategia = selectorEstrategia.seleccionar(solicitud.getTipoCliente());
        BigDecimal importeDescuento = estrategia.calcularDescuento(subtotal);
        BigDecimal porcentajeDescuento = estrategia.getTasaDescuento()
                .multiply(CIEN)
                .setScale(0, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.subtract(importeDescuento).setScale(2, RoundingMode.HALF_UP);

        return new Cotizacion(
                solicitud.getNombreCliente().strip(),
                solicitud.getServicio(),
                solicitud.getCantidad(),
                solicitud.getTipoCliente(),
                precioUnitario.setScale(2, RoundingMode.HALF_UP),
                subtotal,
                porcentajeDescuento,
                importeDescuento,
                total);
    }
}

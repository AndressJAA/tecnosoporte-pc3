package com.tecnosoporte.strategy;

import com.tecnosoporte.model.TipoCliente;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DescuentoClienteFrecuente implements EstrategiaDescuento {

    private static final BigDecimal TASA = new BigDecimal("0.10");

    @Override
    public TipoCliente getTipoCliente() {
        return TipoCliente.FRECUENTE;
    }

    @Override
    public BigDecimal getTasaDescuento() {
        return TASA;
    }
}

package com.tecnosoporte.strategy;

import com.tecnosoporte.model.TipoCliente;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DescuentoClienteInstitucional implements EstrategiaDescuento {

    private static final BigDecimal TASA = new BigDecimal("0.15");

    @Override
    public TipoCliente getTipoCliente() {
        return TipoCliente.INSTITUCIONAL;
    }

    @Override
    public BigDecimal getTasaDescuento() {
        return TASA;
    }
}

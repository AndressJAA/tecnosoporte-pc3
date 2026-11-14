package com.tecnosoporte.strategy;

import com.tecnosoporte.model.TipoCliente;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DescuentoClienteNuevo implements EstrategiaDescuento {

    private static final BigDecimal TASA = new BigDecimal("0.00");

    @Override
    public TipoCliente getTipoCliente() {
        return TipoCliente.NUEVO;
    }

    @Override
    public BigDecimal getTasaDescuento() {
        return TASA;
    }
}

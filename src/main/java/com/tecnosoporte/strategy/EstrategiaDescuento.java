package com.tecnosoporte.strategy;

import com.tecnosoporte.model.TipoCliente;

import java.math.BigDecimal;
import java.math.RoundingMode;

public interface EstrategiaDescuento {

    TipoCliente getTipoCliente();

    BigDecimal getTasaDescuento();

    default BigDecimal calcularDescuento(BigDecimal subtotal) {
        return subtotal.multiply(getTasaDescuento()).setScale(2, RoundingMode.HALF_UP);
    }
}

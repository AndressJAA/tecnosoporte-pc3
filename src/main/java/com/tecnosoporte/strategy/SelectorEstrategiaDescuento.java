package com.tecnosoporte.strategy;

import com.tecnosoporte.model.TipoCliente;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class SelectorEstrategiaDescuento {

    private final Map<TipoCliente, EstrategiaDescuento> estrategias;

    public SelectorEstrategiaDescuento(List<EstrategiaDescuento> estrategiasDisponibles) {
        EnumMap<TipoCliente, EstrategiaDescuento> estrategiasPorCliente = new EnumMap<>(TipoCliente.class);
        for (EstrategiaDescuento estrategia : estrategiasDisponibles) {
            EstrategiaDescuento repetida = estrategiasPorCliente.put(estrategia.getTipoCliente(), estrategia);
            if (repetida != null) {
                throw new IllegalStateException(
                        "Existe más de una estrategia para " + estrategia.getTipoCliente());
            }
        }

        for (TipoCliente tipoCliente : TipoCliente.values()) {
            if (!estrategiasPorCliente.containsKey(tipoCliente)) {
                throw new IllegalStateException("No existe una estrategia para " + tipoCliente);
            }
        }

        this.estrategias = Map.copyOf(estrategiasPorCliente);
    }

    public EstrategiaDescuento seleccionar(TipoCliente tipoCliente) {
        EstrategiaDescuento estrategia = estrategias.get(tipoCliente);
        if (estrategia == null) {
            throw new IllegalArgumentException("Tipo de cliente no soportado: " + tipoCliente);
        }
        return estrategia;
    }
}

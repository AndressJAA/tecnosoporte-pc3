package com.tecnosoporte.strategy;

import com.tecnosoporte.model.TipoCliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SelectorEstrategiaDescuentoTest {

    private SelectorEstrategiaDescuento selector;

    @BeforeEach
    void setUp() {
        selector = new SelectorEstrategiaDescuento(List.of(
                new DescuentoClienteNuevo(),
                new DescuentoClienteFrecuente(),
                new DescuentoClienteInstitucional()));
    }

    @Test
    @DisplayName("Selecciona una estrategia intercambiable para cada tipo de cliente")
    void seleccionaEstrategiaPorTipoCliente() {
        assertThat(selector.seleccionar(TipoCliente.NUEVO))
                .isInstanceOf(DescuentoClienteNuevo.class);
        assertThat(selector.seleccionar(TipoCliente.FRECUENTE))
                .isInstanceOf(DescuentoClienteFrecuente.class);
        assertThat(selector.seleccionar(TipoCliente.INSTITUCIONAL))
                .isInstanceOf(DescuentoClienteInstitucional.class);
    }

    @Test
    @DisplayName("Cada estrategia calcula solamente su propio porcentaje")
    void cadaEstrategiaCalculaSuDescuento() {
        BigDecimal subtotal = new BigDecimal("200.00");

        assertThat(selector.seleccionar(TipoCliente.NUEVO).calcularDescuento(subtotal))
                .isEqualByComparingTo("0.00");
        assertThat(selector.seleccionar(TipoCliente.FRECUENTE).calcularDescuento(subtotal))
                .isEqualByComparingTo("20.00");
        assertThat(selector.seleccionar(TipoCliente.INSTITUCIONAL).calcularDescuento(subtotal))
                .isEqualByComparingTo("30.00");
    }
}

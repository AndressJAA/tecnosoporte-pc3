package com.tecnosoporte.service;

import com.tecnosoporte.model.Cotizacion;
import com.tecnosoporte.model.ServicioTecnico;
import com.tecnosoporte.model.SolicitudCotizacion;
import com.tecnosoporte.model.TipoCliente;
import com.tecnosoporte.strategy.DescuentoClienteFrecuente;
import com.tecnosoporte.strategy.DescuentoClienteInstitucional;
import com.tecnosoporte.strategy.DescuentoClienteNuevo;
import com.tecnosoporte.strategy.SelectorEstrategiaDescuento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CotizacionServiceTest {

    private CotizacionService cotizacionService;

    @BeforeEach
    void setUp() {
        SelectorEstrategiaDescuento selector = new SelectorEstrategiaDescuento(List.of(
                new DescuentoClienteNuevo(),
                new DescuentoClienteFrecuente(),
                new DescuentoClienteInstitucional()));
        cotizacionService = new CotizacionService(selector);
    }

    @DisplayName("Calcula correctamente los tres casos obligatorios")
    @ParameterizedTest(name = "{0} x {1} para cliente {2} da S/ {3}")
    @MethodSource("casosObligatorios")
    void calculaCasosObligatorios(
            ServicioTecnico servicio,
            int cantidad,
            TipoCliente tipoCliente,
            String totalEsperado,
            String subtotalEsperado,
            String porcentajeEsperado,
            String descuentoEsperado) {

        SolicitudCotizacion solicitud = crearSolicitud(servicio, cantidad, tipoCliente);

        Cotizacion cotizacion = cotizacionService.calcular(solicitud);

        assertThat(cotizacion.total()).isEqualByComparingTo(totalEsperado);
        assertThat(cotizacion.subtotal()).isEqualByComparingTo(subtotalEsperado);
        assertThat(cotizacion.porcentajeDescuento()).isEqualByComparingTo(porcentajeEsperado);
        assertThat(cotizacion.importeDescuento()).isEqualByComparingTo(descuentoEsperado);
    }

    static Stream<Arguments> casosObligatorios() {
        return Stream.of(
                Arguments.of(
                        ServicioTecnico.DIAGNOSTICO, 2, TipoCliente.NUEVO,
                        "80.00", "80.00", "0", "0.00"),
                Arguments.of(
                        ServicioTecnico.MANTENIMIENTO, 2, TipoCliente.FRECUENTE,
                        "126.00", "140.00", "10", "14.00"),
                Arguments.of(
                        ServicioTecnico.INSTALACION_SOFTWARE, 3, TipoCliente.INSTITUCIONAL,
                        "127.50", "150.00", "15", "22.50"));
    }

    @Test
    @DisplayName("Conserva dos decimales en todos los importes monetarios")
    void conservaEscalaMonetaria() {
        Cotizacion cotizacion = cotizacionService.calcular(
                crearSolicitud(ServicioTecnico.INSTALACION_SOFTWARE, 3, TipoCliente.INSTITUCIONAL));

        assertThat(cotizacion.precioUnitario().scale()).isEqualTo(2);
        assertThat(cotizacion.subtotal().scale()).isEqualTo(2);
        assertThat(cotizacion.importeDescuento().scale()).isEqualTo(2);
        assertThat(cotizacion.total().scale()).isEqualTo(2);
    }

    @Test
    @DisplayName("Expone los porcentajes desde las estrategias configuradas")
    void obtienePorcentajesDesdeLasEstrategias() {
        assertThat(cotizacionService.obtenerPorcentajesDisponibles())
                .containsEntry(TipoCliente.NUEVO, new BigDecimal("0"))
                .containsEntry(TipoCliente.FRECUENTE, new BigDecimal("10"))
                .containsEntry(TipoCliente.INSTITUCIONAL, new BigDecimal("15"));
    }

    private SolicitudCotizacion crearSolicitud(
            ServicioTecnico servicio,
            int cantidad,
            TipoCliente tipoCliente) {
        SolicitudCotizacion solicitud = new SolicitudCotizacion();
        solicitud.setNombreCliente("Cliente de prueba");
        solicitud.setServicio(servicio);
        solicitud.setCantidad(cantidad);
        solicitud.setTipoCliente(tipoCliente);
        return solicitud;
    }
}

package com.tecnosoporte.controller;

import com.tecnosoporte.model.Cotizacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
class CotizacionControllerTest {

    @Autowired
    private WebApplicationContext applicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(applicationContext).build();
    }

    @Test
    @DisplayName("GET muestra el formulario con catálogos y porcentajes reales")
    void muestraFormularioConPorcentajes() throws Exception {
        MvcResult resultado = mockMvc.perform(get("/cotizaciones/nueva"))
                .andExpect(status().isOk())
                .andExpect(view().name("cotizaciones/formulario"))
                .andExpect(model().attributeExists(
                        "solicitudCotizacion", "servicios", "tiposCliente", "descuentosPorCliente"))
                .andExpect(content().string(containsString("Crea tu cotización")))
                .andExpect(content().string(not(containsString("null%"))))
                .andReturn();

        assertThat(resultado.getResponse().getContentAsString())
                .containsPattern("(?s)value=\"NUEVO\".*?<b>0%</b>")
                .containsPattern("(?s)value=\"FRECUENTE\".*?<b>10%</b>")
                .containsPattern("(?s)value=\"INSTITUCIONAL\".*?<b>15%</b>");
    }

    @DisplayName("POST válido mantiene los tres resultados obligatorios en la vista")
    @ParameterizedTest(name = "{0} x {1} para {2} muestra S/ {3}")
    @MethodSource("cotizacionesValidas")
    void procesaCotizacionValida(
            String servicio,
            String cantidad,
            String tipoCliente,
            String totalEsperado) throws Exception {

        MvcResult resultado = mockMvc.perform(post("/cotizaciones")
                        .param("nombreCliente", "Comercial Andina")
                        .param("servicio", servicio)
                        .param("cantidad", cantidad)
                        .param("tipoCliente", tipoCliente))
                .andExpect(status().isOk())
                .andExpect(view().name("cotizaciones/resultado"))
                .andExpect(model().attributeExists("cotizacion"))
                .andExpect(content().string(containsString("S/ " + totalEsperado)))
                .andExpect(content().string(containsString("Nueva cotización")))
                .andReturn();

        Cotizacion cotizacion = (Cotizacion) resultado.getModelAndView()
                .getModel()
                .get("cotizacion");
        assertThat(cotizacion.total()).isEqualByComparingTo(totalEsperado);
    }

    static Stream<Arguments> cotizacionesValidas() {
        return Stream.of(
                Arguments.of("DIAGNOSTICO", "2", "NUEVO", "80.00"),
                Arguments.of("MANTENIMIENTO", "2", "FRECUENTE", "126.00"),
                Arguments.of("INSTALACION_SOFTWARE", "3", "INSTITUCIONAL", "127.50"));
    }

    @Test
    @DisplayName("Nombre vacío y cantidad cero retornan errores y conservan la selección")
    void rechazaNombreVacioYCantidadCero() throws Exception {
        MvcResult resultado = mockMvc.perform(post("/cotizaciones")
                        .param("nombreCliente", "   ")
                        .param("servicio", "DIAGNOSTICO")
                        .param("cantidad", "0")
                        .param("tipoCliente", "NUEVO"))
                .andExpect(status().isOk())
                .andExpect(view().name("cotizaciones/formulario"))
                .andExpect(model().attributeHasFieldErrors(
                        "solicitudCotizacion", "nombreCliente", "cantidad"))
                .andExpect(content().string(containsString("Ingresa el nombre del cliente.")))
                .andExpect(content().string(containsString("La cantidad mínima es 1.")))
                .andExpect(content().string(not(containsString("null%"))))
                .andReturn();

        assertThat(resultado.getResponse().getContentAsString())
                .containsPattern("<input(?=[^>]*value=\"NUEVO\")(?=[^>]*checked=\"checked\")[^>]*>")
                .containsPattern("(?s)value=\"NUEVO\".*?<b>0%</b>")
                .containsPattern("(?s)value=\"FRECUENTE\".*?<b>10%</b>")
                .containsPattern("(?s)value=\"INSTITUCIONAL\".*?<b>15%</b>");
    }

    @Test
    @DisplayName("Cantidad no entera retorna un mensaje de validación")
    void rechazaCantidadNoEntera() throws Exception {
        mockMvc.perform(post("/cotizaciones")
                        .param("nombreCliente", "Cliente válido")
                        .param("servicio", "DIAGNOSTICO")
                        .param("cantidad", "1.5")
                        .param("tipoCliente", "NUEVO"))
                .andExpect(status().isOk())
                .andExpect(view().name("cotizaciones/formulario"))
                .andExpect(model().attributeHasFieldErrors("solicitudCotizacion", "cantidad"))
                .andExpect(content().string(containsString(
                        "Ingresa una cantidad entera entre 1 y 5.")));
    }
}

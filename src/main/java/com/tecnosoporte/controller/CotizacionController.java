package com.tecnosoporte.controller;

import com.tecnosoporte.model.Cotizacion;
import com.tecnosoporte.model.ServicioTecnico;
import com.tecnosoporte.model.SolicitudCotizacion;
import com.tecnosoporte.model.TipoCliente;
import com.tecnosoporte.service.CotizacionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.util.Map;

@Controller
public class CotizacionController {

    private final CotizacionService cotizacionService;

    public CotizacionController(CotizacionService cotizacionService) {
        this.cotizacionService = cotizacionService;
    }

    @ModelAttribute("servicios")
    public ServicioTecnico[] servicios() {
        return ServicioTecnico.values();
    }

    @ModelAttribute("tiposCliente")
    public TipoCliente[] tiposCliente() {
        return TipoCliente.values();
    }

    @ModelAttribute("descuentosPorCliente")
    public Map<TipoCliente, BigDecimal> descuentosPorCliente() {
        return cotizacionService.obtenerPorcentajesDisponibles();
    }

    @GetMapping({"/", "/cotizaciones", "/cotizaciones/nueva"})
    public String mostrarFormulario(Model model) {
        if (!model.containsAttribute("solicitudCotizacion")) {
            model.addAttribute("solicitudCotizacion", new SolicitudCotizacion());
        }
        return "cotizaciones/formulario";
    }

    @PostMapping("/cotizaciones")
    public String generarCotizacion(
            @Valid @ModelAttribute("solicitudCotizacion") SolicitudCotizacion solicitud,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "cotizaciones/formulario";
        }

        Cotizacion cotizacion = cotizacionService.calcular(solicitud);
        model.addAttribute("cotizacion", cotizacion);
        return "cotizaciones/resultado";
    }
}

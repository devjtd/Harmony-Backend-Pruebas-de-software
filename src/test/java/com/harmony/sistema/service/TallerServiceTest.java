package com.harmony.sistema.service;

import com.harmony.sistema.model.Taller;
import com.harmony.sistema.repository.TallerRepository;
import com.harmony.sistema.service.config.ConfiguradorDefaultsTaller;
import com.harmony.sistema.service.validation.ValidadorTaller;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TallerServiceTest {

    @Mock
    private TallerRepository tallerRepository;
    @Mock
    private ValidadorTaller validadorTaller;
    @Mock
    private ConfiguradorDefaultsTaller configuradorDefaults;

    @InjectMocks
    private TallerService tallerService;

    @Test
    @DisplayName("CP-TALLER-01: Creación Exitosa de Taller")
    void crearTallerExitoso() {
        
        // 1. Arrange: Preparar el taller de entrada
        Taller tallerEntrada = new Taller();
        tallerEntrada.setNombre("Guitarra Eléctrica");
        tallerEntrada.setPrecio(java.math.BigDecimal.valueOf(510.0));
        
        // Simular que el repositorio devuelve el taller con un ID asignado
        Taller tallerGuardado = new Taller();
        tallerGuardado.setId(1L);
        tallerGuardado.setNombre("Guitarra Eléctrica");
        tallerGuardado.setActivo(true);
        when(tallerRepository.save(any(Taller.class))).thenReturn(tallerGuardado);
        
        // 2. Act: Ejecutar el método
        Taller resultado = tallerService.crearTallerSolo(tallerEntrada);
        
        // 3. Assert: Verificaciones
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Guitarra Eléctrica", resultado.getNombre());
        
        // Verificar que se llamaron a los componentes de validación y configuración
        verify(validadorTaller, times(1)).validarParaCreacion(tallerEntrada);
        verify(configuradorDefaults, times(1)).aplicarValoresPorDefecto(tallerEntrada);
        verify(tallerRepository, times(1)).save(tallerEntrada);
    }
}

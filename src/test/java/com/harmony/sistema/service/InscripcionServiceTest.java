package com.harmony.sistema.service;

import com.harmony.sistema.model.Cliente;
import com.harmony.sistema.model.Horario;
import com.harmony.sistema.model.Inscripcion;
import com.harmony.sistema.repository.InscripcionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InscripcionServiceTest {

    @Mock
    private InscripcionRepository inscripcionRepository;
    @Mock
    private ServicioVacantes servicioVacantes;

    @InjectMocks
    private InscripcionService inscripcionService;

    @Test
    @DisplayName("CP-INS-01: Inscripción Exitosa de Cliente")
    void crearInscripcionExitosa() {
        // 1. Arrange (Preparar datos)
        Cliente cliente = new Cliente();
        cliente.setId(10L);
        Horario horario = new Horario();
        horario.setId(5L);
        // Simular que NO existe una inscripción previa
        when(inscripcionRepository.findByClienteIdAndHorarioId(10L, 5L))
                .thenReturn(Optional.empty());

        // 2. Act (Ejecutar la acción)
        assertDoesNotThrow(() -> {
            inscripcionService.crearInscripcion(cliente, horario);
        });

        // 3. Assert (Verificar interacciones críticas)
        // Verificar que se llamó al servicio para descontar la vacante
        verify(servicioVacantes, times(1)).reservarVacante(horario);
        // Verificar que se intentó guardar la nueva inscripción
        verify(inscripcionRepository, times(1)).save(any(Inscripcion.class));
    }
}

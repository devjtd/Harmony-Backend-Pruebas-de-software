package com.harmony.sistema.service;

import com.harmony.sistema.model.Horario;
import com.harmony.sistema.model.Profesor;
import com.harmony.sistema.model.Taller;
import com.harmony.sistema.repository.HorarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HorarioServiceTest {

    @Mock
    private HorarioRepository horarioRepository;
    @Mock
    private TallerService tallerService;
    @Mock
    private ProfesorService profesorService;
    
    @InjectMocks
    private HorarioService horarioService;

    @Test
    @DisplayName("CP-HOR-01: Creación de Horario con Cálculo de Fecha Fin")
    void crearHorarioConCalculoFechaFin() {
        // 1. Arrange: Configurar Taller de 4 semanas y Profesor
        Long tallerId = 1L;
        Long profesorId = 2L;
        LocalDate fechaInicio = LocalDate.of(2026, 5, 1);

        Taller tallerMock = new Taller();
        tallerMock.setId(tallerId);
        tallerMock.setDuracionSemanas(4);

        Profesor profesorMock = new Profesor();
        profesorMock.setId(profesorId);

        // Configurar comportamiento de Mocks
        when(tallerService.obtenerTallerPorId(tallerId)).thenReturn(tallerMock);
        when(profesorService.obtenerProfesorPorId(profesorId)).thenReturn(profesorMock);
        when(horarioRepository.findByTallerAndProfesorAndDiasDeClaseAndHoraInicioAndHoraFin(
                any(), any(), anyString(), any(), any())).thenReturn(Optional.empty());

        // Simular guardado
        when(horarioRepository.save(any(Horario.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. Act: Ejecutar creación
        Horario resultado = horarioService.crearHorario(
                tallerId, profesorId, "Lunes y Miércoles",
                LocalTime.of(10, 0), LocalTime.of(12, 0),
                fechaInicio, 15
        );

        // 3. Assert: Verificar que la fecha fin sea inicio + 28 días
        assertNotNull(resultado);
        LocalDate fechaFinEsperada = fechaInicio.plusDays(28);
        assertEquals(fechaFinEsperada, resultado.getFechaFin(), "La fecha fin no fue calculada correctamente");

        verify(horarioRepository, times(1)).save(any(Horario.class));
    }
}

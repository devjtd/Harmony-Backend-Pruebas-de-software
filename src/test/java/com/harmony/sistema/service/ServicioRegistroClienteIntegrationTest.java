package com.harmony.sistema.service;

import com.harmony.sistema.dto.CredencialesDTO;
import com.harmony.sistema.dto.InscripcionFormDTO;
import com.harmony.sistema.model.Cliente;
import com.harmony.sistema.model.Horario;
import com.harmony.sistema.model.Inscripcion;
import com.harmony.sistema.model.Taller;
import com.harmony.sistema.model.User;
import com.harmony.sistema.repository.ClienteRepository;
import com.harmony.sistema.repository.HorarioRepository;
import com.harmony.sistema.repository.InscripcionRepository;
import com.harmony.sistema.repository.TallerRepository;
import com.harmony.sistema.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
class ServicioRegistroClienteIntegrationTest {

    @Autowired
    private InscripcionService inscripcionService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TallerRepository tallerRepository;

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @MockBean
    private ServicioNotificacion servicioNotificacion;

    @Test
    @DisplayName("Prueba integración: Registrar cliente nuevo con inscripcion")
    void registrarClienteNuevoConInscripcion() {
        // 1. Buscar un taller existente en los datos iniciales.
        Taller taller = tallerRepository.findByNombre("Piano")
                .orElseThrow(() -> new IllegalStateException("No se encontro el taller Piano en los datos iniciales"));

        // 2. Tomar un horario del taller que tenga vacantes disponibles.
        Horario horario = taller.getHorarios().stream()
                .filter(h -> h.getVacantesDisponibles() > 0)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No se encontro un horario disponible para Piano"));

        // 3. Preparar datos de entrada únicos para no chocar con registros previos.
        String email = "chipa.robladillo@gmail.com";
        String nombre = "Jeanpierre Chipa";
        String telefono = "921340064";

        // 4. Construir el DTO que simula lo que enviaría el formulario.
        InscripcionFormDTO dto = new InscripcionFormDTO();
        dto.setNombre(nombre);
        dto.setEmail(email);
        dto.setTelefono(telefono);

        // 5. Guardar cuántas vacantes tenía el horario antes de ejecutar la prueba.
        int vacantesIniciales = horario.getVacantesDisponibles();

        // 6. Ejecutar el flujo completo de inscripción y registro del cliente.
        CredencialesDTO credenciales = inscripcionService.procesarInscripcionCompleta(dto, Map.of(taller.getId(), horario.getId()));

        // 7. Verificar la respuesta del servicio.
        assertNotNull(credenciales);
        assertEquals(email, credenciales.getCorreo());
        assertNotNull(credenciales.getContrasenaTemporal());
        assertTrue(credenciales.getContrasenaTemporal().startsWith("temporal-"));

        // 8. Confirmar que el cliente fue persistido en la base de datos.
        Cliente clientePersistido = clienteRepository.findByCorreo(email)
                .orElseThrow(() -> new IllegalStateException("El cliente no fue persistido en la base de datos"));
        assertEquals(nombre, clientePersistido.getNombreCompleto());
        assertEquals(telefono, clientePersistido.getTelefono());

        // 9. Confirmar que también se creó el usuario asociado para autenticación.
        User userPersistido = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("El usuario no fue persistido en la base de datos"));
        assertTrue(userPersistido.isEnabled());
        assertTrue(userPersistido.getRoles().stream().anyMatch(role -> "ROLE_CLIENTE".equals(role.getName())));

        // 10. Confirmar que se creó la inscripción y quedó vinculada al horario.
        List<Inscripcion> inscripciones = inscripcionRepository.findByClienteId(clientePersistido.getId());
        assertEquals(1, inscripciones.size());
        assertEquals(horario.getId(), inscripciones.get(0).getHorario().getId());

        // 11. Verificar que la vacante del horario disminuyó en una unidad.
        Horario horarioActualizado = horarioRepository.findById(horario.getId())
                .orElseThrow(() -> new IllegalStateException("No se encontro el horario despues del registro"));
        assertEquals(vacantesIniciales - 1, horarioActualizado.getVacantesDisponibles());

        // 12. Verificar que el servicio de notificación fue invocado, pero sin enviar correo real.
        verify(servicioNotificacion, times(1))
                .enviarCredenciales(email, nombre, credenciales.getContrasenaTemporal());
    }
}

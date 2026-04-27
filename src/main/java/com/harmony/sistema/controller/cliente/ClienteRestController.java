package com.harmony.sistema.controller.cliente;

import com.harmony.sistema.dto.CambioClaveRequest;
import com.harmony.sistema.dto.HorarioClienteDTO;
import com.harmony.sistema.model.Cliente;
import com.harmony.sistema.model.Horario;
import com.harmony.sistema.model.User;
import com.harmony.sistema.repository.ClienteRepository;
import com.harmony.sistema.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cliente")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteRestController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Endpoint para obtener los horarios del cliente autenticado.
     * GET /api/cliente/horarios
     */
    @GetMapping("/horarios")
    public ResponseEntity<List<HorarioClienteDTO>> getHorarios(Authentication authentication) {
        String email = authentication.getName();
        System.out.println("[INFO] [CLIENTE REST] GET /api/cliente/horarios - Usuario autenticado: " + email);

        Cliente cliente = clienteRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        System.out.println("[SUCCESS] [CLIENTE REST] Cliente seleccionado encontrado. ID: " + cliente.getId());
        System.out.println("[INFO] [CLIENTE REST] Datos del cliente - Nombre: " + cliente.getNombreCompleto()
                + ", Correo: " + cliente.getCorreo() + ", Teléfono: " + cliente.getTelefono());

        List<HorarioClienteDTO> horarios = cliente.getInscripciones().stream()
                .map(inscripcion -> {
                    Horario h = inscripcion.getHorario();
                    HorarioClienteDTO dto = new HorarioClienteDTO();
                    dto.setId(h.getId());
                    dto.setDiasDeClase(h.getDiasDeClase());
                    dto.setHoraInicio(h.getHoraInicio().toString());
                    dto.setHoraFin(h.getHoraFin().toString());
                    dto.setFinalizado(h.getFinalizado());
                    dto.setFechaFin(h.getFechaFin() != null ? h.getFechaFin().toString() : null);

                    // Usamos los DTOs anidados
                    dto.setTaller(new HorarioClienteDTO.TallerSimpleDTO(h.getTaller().getNombre()));
                    dto.setProfesor(new HorarioClienteDTO.ProfesorSimpleDTO(h.getProfesor().getNombreCompleto()));

                    // Mapear cancelaciones
                    if (h.getCancelaciones() != null) {
                        List<com.harmony.sistema.dto.ClaseCanceladaDTO> cancelacionesDTO = h.getCancelaciones().stream()
                                .map(c -> new com.harmony.sistema.dto.ClaseCanceladaDTO(
                                        c.getId(),
                                        c.getFecha(),
                                        c.getMotivo(),
                                        c.getAccion()))
                                .collect(Collectors.toList());
                        dto.setCancelaciones(cancelacionesDTO);
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        System.out.println("[INFO] [CLIENTE REST] Horarios resultantes obtenidos: " + horarios.size());
        return ResponseEntity.ok(horarios);
    }

    /**
     * Endpoint para cambiar la contraseña del cliente.
     * POST /api/cliente/cambiar-clave
     */
    @PostMapping("/cambiar-clave")
    public ResponseEntity<String> cambiarClave(
            @RequestBody CambioClaveRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        System.out.println("[INFO] [CLIENTE REST] POST /api/cliente/cambiar-clave - Usuario: " + email);

        if (!request.getNuevaContrasena().equals(request.getConfirmarContrasena())) {
            System.out.println("[WARN] [CLIENTE REST] Las contraseñas no coinciden para: " + email);
            return ResponseEntity.badRequest().body("Las contraseñas no coinciden");
        }

        if (request.getNuevaContrasena().length() < 6) {
            System.out.println("[WARN] [CLIENTE REST] Contraseña demasiado corta para: " + email);
            return ResponseEntity.badRequest().body("La contraseña debe tener al menos 6 caracteres");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        System.out.println("[INFO] [CLIENTE REST] Usuario encontrado. ID: " + user.getId());
        System.out.println("[INFO] [CLIENTE REST] Preparando actualización de contraseña para el usuario.");

        user.setPassword(passwordEncoder.encode(request.getNuevaContrasena()));
        userRepository.save(user);
        System.out.println("[INFO] [CLIENTE REST] Datos resultantes - Contraseña actualizada correctamente.");
        System.out.println("[SUCCESS] [CLIENTE REST] Contraseña actualizada y guardada para: " + email);

        return ResponseEntity.ok("Contraseña cambiada exitosamente");
    }
}

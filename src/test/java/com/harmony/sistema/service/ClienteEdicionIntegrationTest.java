package com.harmony.sistema.service;

import com.harmony.sistema.model.Cliente;
import com.harmony.sistema.model.Role;
import com.harmony.sistema.model.User;
import com.harmony.sistema.repository.ClienteRepository;
import com.harmony.sistema.repository.RoleRepository;
import com.harmony.sistema.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class ClienteEdicionIntegrationTest {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Prueba integración: Editar cliente con nombre y telefono")
    void editarClienteSoloNombreYTelefono() {
        // 1. Crear un cliente base directamente en la base de datos de prueba.
        Cliente cliente = crearClienteBase();

        // 2. Definir los nuevos valores que se van a actualizar.
        String nombreActualizado = "Cliente Editado";
        String telefonoActualizado = "955444333";

        // 3. Ejecutar la actualización del cliente usando el servicio real.
        Cliente clienteEditado = clienteService.actualizarCliente(
                cliente.getId(),
                nombreActualizado,
                cliente.getCorreo(),
                telefonoActualizado,
                cliente.getCorreo());

        // 4. Verificar que el objeto devuelto por el servicio contenga los datos nuevos.
        assertNotNull(clienteEditado);
        assertEquals(nombreActualizado, clienteEditado.getNombreCompleto());
        assertEquals(telefonoActualizado, clienteEditado.getTelefono());
        assertEquals(cliente.getCorreo(), clienteEditado.getCorreo());

        // 5. Confirmar que el cambio quedó persistido en la base de datos.
        Cliente clientePersistido = clienteRepository.findById(cliente.getId())
                .orElseThrow(() -> new IllegalStateException("No se encontro el cliente editado"));
        assertEquals(nombreActualizado, clientePersistido.getNombreCompleto());
        assertEquals(telefonoActualizado, clientePersistido.getTelefono());
        assertEquals(cliente.getCorreo(), clientePersistido.getCorreo());

        // 6. Confirmar que el usuario asociado sigue existiendo y conserva el correo.
        User userPersistido = userRepository.findByEmail(cliente.getCorreo())
                .orElseThrow(() -> new IllegalStateException("No se encontro el usuario asociado"));
        assertEquals(cliente.getCorreo(), userPersistido.getEmail());
    }

    private Cliente crearClienteBase() {
        // 1. Buscar el rol cliente ya creado por el inicializador.
        Role roleCliente = roleRepository.findByName("ROLE_CLIENTE")
                .orElseThrow(() -> new IllegalStateException("No se encontro el rol ROLE_CLIENTE"));

        // 2. Generar un correo único para evitar colisiones entre pruebas.
        String email = "cliente.edicion." + UUID.randomUUID() + "@harmony.com";

        // 3. Crear y guardar el usuario asociado al cliente.
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode("password123"))
                .enabled(true)
                .roles(new HashSet<>(java.util.Set.of(roleCliente)))
                .build();
        userRepository.save(user);

        // 4. Crear y guardar el cliente vinculado al usuario.
        Cliente cliente = Cliente.builder()
                .nombreCompleto("Cliente Original")
                .correo(email)
                .telefono("999888777")
                .user(user)
                .build();

        // 5. Retornar el cliente persistido para usarlo en la prueba.
        return clienteRepository.save(cliente);
    }
}

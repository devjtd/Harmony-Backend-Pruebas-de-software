package com.harmony.sistema.service;

import com.harmony.sistema.dto.AuthResponse;
import com.harmony.sistema.dto.LoginRequest;
import com.harmony.sistema.model.Cliente;
import com.harmony.sistema.model.User;
import com.harmony.sistema.repository.ClienteRepository;
import com.harmony.sistema.repository.UserRepository;
import com.harmony.sistema.security.JwtService;
import java.util.HashSet;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("CP-AUTH-01: Login Exitoso de Cliente")
    void loginExitosoCliente() {
        // 1. Datos de entrada (Arrage)
        LoginRequest request = new LoginRequest("daves@ejemplo.com", "123");
        User user = User.builder().email("daves@ejemplo.com").roles(new HashSet<>()).build();
        Cliente cliente = new Cliente();
        cliente.setNombreCompleto("Daves Malpartida");
        // 2. Simular comportamientos (Mocking)
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(clienteRepository.findByUser(user)).thenReturn(Optional.of(cliente));
        when(jwtService.generateToken(user)).thenReturn("token-abc-123");
        // 3. Ejecutar (Act)
        AuthResponse response = authService.login(request);
        // 4. Verificaciones (Assert)
        assertNotNull(response);
        assertEquals("Daves Malpartida", response.getNombreCompleto());
        assertEquals("token-abc-123", response.getToken());
        verify(authenticationManager).authenticate(any());
    }
}

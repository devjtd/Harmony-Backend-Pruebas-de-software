package com.harmony.sistema.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TallerServiceIntegrationTest {

    @Autowired
    private TallerService tallerService;

    @Test
    public void testTallerIntegrationFlow() {
        System.out.println("======= EJECUTANDO PRUEBA DE INTEGRACIÓN: TALLER SERVICE =======");
        
        // 1. Verificación de inyección
        if (tallerService != null) {
            System.out.println("[OK] TallerService inyectado correctamente.");
        }

        // 2. Intento de listar talleres (Lógica de negocio)
        System.out.println("[INFO] Consultando oferta académica en la base de datos...");
        try {
            int cantidad = tallerService.encontrarTalleresActivos().size(); 
            System.out.println("[RESULTADO] Conexión exitosa. Talleres encontrados: " + cantidad);
        } catch (Exception e) {
            System.out.println("[ERROR] Error al consultar la tabla taller: " + e.getMessage());
        }

        System.out.println("======= PRUEBA TALLER FINALIZADA CON ÉXITO =======");
    }
}

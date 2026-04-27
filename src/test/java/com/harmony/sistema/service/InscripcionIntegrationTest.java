package com.harmony.sistema.service;

import com.harmony.sistema.repository.InscripcionRepository; // Asegúrate que el nombre sea correcto
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class InscripcionIntegrationTest {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Test
    public void testConexionBaseDeDatos() {
        // Esta prueba verifica que puedes comunicarte con la tabla 'inscripcion' de tu phpMyAdmin
        long cantidad = inscripcionRepository.count();
        System.out.println("--- PRUEBA EXITOSA ---");
        System.out.println("Se detectaron " + cantidad + " registros en la tabla inscripcion.");
    }
}
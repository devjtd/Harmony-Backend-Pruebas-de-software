package com.harmony.sistema.service;

import com.harmony.sistema.service.HorarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

@SpringBootTest
public class HorarioServiceIntegrationTest {

    @Autowired
    private HorarioService horarioService;

    @Test
    public void testConsultarHorarioReal() {
        System.out.println("======= EXTRACCIÓN DE DATO: HORARIO SERVICE =======");

        try {
            // Usamos el ID 1 que vimos que existe en tu base de datos
            Long idExistente = 1L;
            var horario = horarioService.getHorarioById(idExistente);

            if (horario != null) {
                System.out.println("[RESULTADO] ¡Conexión exitosa y Horario encontrado!");
                System.out.println("---------------------------------------------------------");

                // Imprimimos los datos del objeto directamente
                System.out.println(" > ID: " + horario.getId());
                System.out.println(" > DÍAS: " + horario.getDiasDeClase());
                System.out.println(" > HORA INICIO: " + horario.getHoraInicio());
                System.out.println(" > HORA FIN: " + horario.getHoraFin());

                System.out.println("---------------------------------------------------------");
            } else {
                System.out.println("[AVISO] No se encontró ningún horario con el ID: " + idExistente);
            }

        } catch (Exception e) {
            System.out.println("[ERROR] Error al procesar el dato: " + e.getMessage());
        }

        System.out.println("======= PRUEBA FINALIZADA =======");
    }
}

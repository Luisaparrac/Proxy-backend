package com.luisaparra.aiproxy.service.ai;

import com.luisaparra.aiproxy.dto.GenerationRequest;
import com.luisaparra.aiproxy.dto.GenerationResponse;
import com.luisaparra.aiproxy.service.TokenEstimator;

import java.time.Instant;
import java.util.List;
import java.util.Random;

/**
 * Real subject of the Proxy pattern. Simulates a text generation model:
 * waits 1200 ms and returns one of several predefined texts at random.
 */
public class MockAIGenerationService implements AIGenerationService {

    private static final String MODEL_NAME = "mock-text-gen-1";
    private static final long SIMULATED_LATENCY_MS = 1200;

    private static final List<String> PREDEFINED_RESPONSES = List.of(
            "¡Buena pregunta! En términos simples, la clave está en dividir el problema en partes pequeñas, "
                    + "resolver cada una por separado y luego unir las soluciones.",
            "Aquí tienes una idea: empieza por definir claramente el objetivo, identifica las restricciones "
                    + "y propone al menos dos alternativas. Compararlas te ayudará a tomar una mejor decisión.",
            "El patrón Proxy permite controlar el acceso a un objeto real. En esta plataforma se usan dos proxies "
                    + "encadenados: uno limita las solicitudes por minuto y otro controla la cuota mensual de tokens.",
            "Según mi análisis, hay tres puntos importantes: 1) la calidad de los datos de entrada, "
                    + "2) la claridad de los requisitos y 3) la validación constante de los resultados con el usuario.",
            "Una forma práctica de abordarlo es iterar: construye una primera versión sencilla, pruébala con "
                    + "usuarios reales, recoge comentarios y mejora. Repetir este ciclo reduce el riesgo y acelera el aprendizaje.",
            "Te propongo este resumen: el rate limiting protege al servicio de abusos en periodos cortos, "
                    + "mientras que las cuotas controlan el consumo total en periodos largos, como un mes.",
            "Interesante planteamiento. Desde el punto de vista del diseño de software, conviene mantener cada "
                    + "clase con una sola responsabilidad. Así el código es más fácil de probar, entender y extender.",
            "Respuesta generada: la inteligencia artificial generativa produce texto prediciendo la siguiente "
                    + "palabra más probable. Por eso, un buen prompt con contexto claro suele dar mejores resultados."
    );

    private final TokenEstimator tokenEstimator;
    private final Random random = new Random();

    public MockAIGenerationService(TokenEstimator tokenEstimator) {
        this.tokenEstimator = tokenEstimator;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        long start = System.currentTimeMillis();
        simulateLatency();

        String text = PREDEFINED_RESPONSES.get(random.nextInt(PREDEFINED_RESPONSES.size()));
        int promptTokens = tokenEstimator.estimate(request.prompt());
        int completionTokens = tokenEstimator.estimate(text);

        return new GenerationResponse(
                text,
                MODEL_NAME,
                promptTokens,
                completionTokens,
                promptTokens + completionTokens,
                System.currentTimeMillis() - start,
                Instant.now()
        );
    }

    private void simulateLatency() {
        try {
            Thread.sleep(SIMULATED_LATENCY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("La generación de texto fue interrumpida.", e);
        }
    }
}

package com.luisaparra.aiproxy.service.ai;

import com.luisaparra.aiproxy.dto.GenerationRequest;
import com.luisaparra.aiproxy.dto.GenerationResponse;

/**
 * Subject of the Proxy pattern: the real service and every proxy implement it,
 * so callers cannot tell whether they talk to a proxy or to the real service.
 */
public interface AIGenerationService {

    GenerationResponse generate(GenerationRequest request);
}

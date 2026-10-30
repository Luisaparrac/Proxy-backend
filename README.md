# Proxy Backend — Plataforma de consumo de IA con rate limiting y cuotas

API en **Spring Boot 3 (Java 21)** que expone un servicio de IA simulado protegido por dos proxies encadenados (patrón **Proxy**):

```
Cliente ──► RateLimitProxyService ──► QuotaProxyService ──► MockAIGenerationService
              (429 Too Many Requests)   (402 Payment Required)   (Thread.sleep 1200 ms)
```


## Planes

| Plan | Requests por minuto | Tokens por mes | Precio simulado |
|---|---|---|---|
| FREE | 10 | 50.000 | USD 0 |
| PRO | 60 | 500.000 | USD 19,99 |
| ENTERPRISE | Ilimitado | Ilimitado | USD 99,99 |

## Estructura

```
src/main/java/com/luisaparra/aiproxy
├── AiProxyApplication.java        # Punto de entrada (@EnableScheduling)
├── config/                        # Cadena de proxies, CORS y reloj (zona horaria)
├── controller/                    # Endpoints REST
├── dto/                           # Requests y responses (records)
├── exception/                     # Excepciones y manejador global (429, 402, 404, 400)
├── model/                         # Plan, UserAccount, DailyUsage
├── repository/                    # Almacenamiento en memoria (mapas concurrentes)
├── scheduler/                     # Reset por minuto y reset mensual
├── seed/                          # Usuarios demo e historial inicial
└── service/
    ├── QuotaService.java          # Estado, historial y upgrade
    ├── TokenEstimator.java        # ~4 caracteres = 1 token
    └── ai/
        ├── AIGenerationService.java      # Interfaz (sujeto del patrón)
        ├── MockAIGenerationService.java  # Servicio real simulado
        ├── RateLimitProxyService.java    # Proxy 1
        └── QuotaProxyService.java        # Proxy 2
```

## Endpoints

Todas las peticiones (excepto `/api/users` y `/api/health`) requieren el header `X-User-Id`.

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/ai/generate` | Genera texto pasando por toda la cadena de proxies. Body: `{ "prompt": "..." }` |
| GET | `/api/quota/status` | Tokens usados, restantes, fecha de reset, plan y requests del minuto actual |
| GET | `/api/quota/history` | Uso diario de los últimos 7 días |
| POST | `/api/quota/upgrade` | Upgrade FREE → PRO con pago simulado. Body: `{ "targetPlan": "PRO", "cardHolder": "...", "cardLastFour": "4242" }` |
| GET | `/api/users` | Usuarios demo para el selector del frontend |
| GET | `/api/health` | Health check |

Respuestas de error (mensajes en español):

- **429** cuando se supera el límite por minuto, con el header `Retry-After` (segundos).
- **402** cuando se agota la cuota mensual de tokens.

## Usuarios demo

| Id | Nombre | Plan | Nota |
|---|---|---|---|
| `free-user` | Ana Gómez | FREE | Uso normal |
| `free-limit-user` | Carlos Ruiz | FREE | Casi sin tokens, ideal para probar el 402 y el upgrade |
| `pro-user` | Sofía Martínez | PRO | |
| `enterprise-user` | Acme Corp | ENTERPRISE | Sin límites |

Los datos viven en memoria: se reinician cada vez que el servidor se reinicia o se redespliega.

## Tareas programadas

- Cada minuto (segundo 0): se reinician los contadores de requests por minuto.
- El día 1 de cada mes a medianoche: se reinicia la cuota mensual de tokens.

Ambas usan la zona horaria `America/Bogota` (configurable con `APP_TIME_ZONE`).

## Ejecutar en local

```bash
mvn spring-boot:run
```

La API queda en `http://localhost:8080`. Para correr las pruebas: `mvn test`.

## Variables de entorno

| Variable | Valor por defecto | Uso |
|---|---|---|
| `PORT` | `8080` | Railway la asigna automáticamente |
| `ALLOWED_ORIGINS` | `http://localhost:5173,https://*.vercel.app` | Orígenes permitidos por CORS (separados por coma) |
| `APP_TIME_ZONE` | `America/Bogota` | Zona horaria de fechas y tareas programadas |

## Despliegue en Railway

1. En Railway: **New Project → Deploy from GitHub repo** y elige `Proxy-backend`.
2. Railway detecta el `Dockerfile` y `railway.json` automáticamente.
3. En **Settings → Networking** pulsa **Generate Domain** y copia la URL pública.
4. (Opcional) En **Variables** define `ALLOWED_ORIGINS` con la URL exacta de tu frontend en Vercel.
5. Verifica abriendo `https://TU-DOMINIO.up.railway.app/api/health`.

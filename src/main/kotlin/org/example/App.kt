package org.example

import com.typesafe.config.ConfigFactory
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.jakarta.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.instrumentation.ktor.v3_0.client.KtorClientTracing
import okhttp3.ConnectionPool


const val ApiPortKey = "ktor.deployment.port"

fun main() {
    val env = applicationEnvironment {
        config = HoconApplicationConfig(ConfigFactory.load())
    }

    embeddedServer(Jetty, env, configure = {
        connector {
            port = env.config.property(ApiPortKey).getString().toInt()
        }
    }).start(true)
}

fun Application.mainModule() {
    val openTelemetry = GlobalOpenTelemetry.get()

    val httpClient = HttpClient(OkHttp) {
        engine {
            config {
                connectionPool(ConnectionPool())
                followRedirects(false)
            }
        }

        install(KtorClientTracing) {
            setOpenTelemetry(openTelemetry)
        }
    }

    routing {
        get("/") {
            try {
                val httpResponse = httpClient.get("https://postman-echo.com/get") {
                    parameter("foo1", "bar1")
                    parameter("foo2", "bar2")
                }
                this.call.respond(httpResponse.status, httpResponse.bodyAsText())
            } catch (ex: Exception) {
                ex.printStackTrace()
                this.call.respond(HttpStatusCode.InternalServerError, ex.message ?: "oh dear!")
            }

        }
    }



}
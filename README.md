# otel-ktor3-httpclient-issue

Sample app to reproduce the issue when using `KtorClientTracing` that causes  
```
class io.opentelemetry.javaagent.instrumentation.opentelemetryapi.context.AgentContextWrapper cannot be cast to class io.opentelemetry.javaagent.shaded.io.opentelemetry.context.Context (io.opentelemetry.javaagent.instrumentation.opentelemetryapi.context.AgentContextWrapper is in unnamed module of loader 'app'; io.opentelemetry.javaagent.shaded.io.opentelemetry.context.Context is in unnamed module of loader 'bootstrap')
```

To build the sample app:
```
./gradlew copyAgent shadowJar
```

To run the app with the OTEL agent enabled:
```
export OTEL_JAVAAGENT_ENABLED=true
java -jar -javaagent:build/agents/opentelemetry-javaagent.jar build/libs/app.jar
```

To run the app with the OTEL agent disabled:
```
export OTEL_JAVAAGENT_ENABLED=false
java -jar -javaagent:build/agents/opentelemetry-javaagent.jar build/libs/app.jar
```

To reproduce the issue open your browser and go to `http://localhost:8080` with the agent disabled you should get something like
```
{
  "args": {
    "foo1": "bar1",
    "foo2": "bar2"
  },
  "headers": {
    "host": "postman-echo.com",
    "x-request-start": "t=1731935403.167",
    "connection": "close",
    "x-forwarded-proto": "https",
    "x-forwarded-port": "443",
    "x-amzn-trace-id": "Root=1-673b3cab-7afa4d980946957d376cb98f",
    "accept-charset": "UTF-8",
    "accept": "*/*",
    "user-agent": "ktor-client",
    "accept-encoding": "gzip"
  },
  "url": "https://postman-echo.com/get?foo1=bar1&foo2=bar2"
}
```

If the agent is enabled, the call throws the exception
```
class io.opentelemetry.javaagent.instrumentation.opentelemetryapi.context.AgentContextWrapper cannot be cast to class io.opentelemetry.javaagent.shaded.io.opentelemetry.context.Context (io.opentelemetry.javaagent.instrumentation.opentelemetryapi.context.AgentContextWrapper is in unnamed module of loader 'app'; io.opentelemetry.javaagent.shaded.io.opentelemetry.context.Context is in unnamed module of loader 'bootstrap')
```
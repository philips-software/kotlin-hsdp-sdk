# kotlin-hsdp-sdk

[![Build](https://github.com/philips-software/kotlin-hsdp-api/actions/workflows/gradle.yml/badge.svg?branch=main)](https://github.com/philips-software/kotlin-hsdp-api/actions/workflows/gradle.yml?query=workflow%3Agradle)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

This SDK provides a simple interface to features offered by the various services of HSDP.

The output of the project is a JAR-file that can be used in either a Java or Kotlin backend service 
or an Android application.

## Table of contents

- [Using in your projects](#using-in-your-projects)
  - [Gradle Kotlin DSL](#gradle-kotlin-dsl)
  - [Gradle](#gradle)
  - [Maven](#maven)
- [Basic usage](#basic-usage)
  - [Android](#android)
  - [Kotlin backend](#kotlin-backend)
  - [Java backend](#java-backend)
- [Supported APIs](#supported-apis)
- [Logging implementation examples](#logging-implementation-examples)
  - [Timber logger for Android](#timber-logger-for-android)
  - [Log4j2 logger for Kotlin backend applications](#log4j2-logger-for-kotlin-backend-applications)
  - [Simple logger for Java backend applications](#simple-logger-for-java-backend-applications)
- [Todo](#todo)
- [Issues](#issues)
- [Contact / Getting help](#contact--geting-help)
- [License](#license)


## Using in your projects

The library dependency can be included in your projects in different ways.

### Gradle Kotlin DSL

```kotlin
repositories {
    // Other repos...
    maven { setUrl("https://jitpack.io") }
}

dependencies {
  // Other dependencies...
  implementation("com.github.philips-software:kotlin-hsdp-sdk:0.2.0")
}
```

### Gradle

```groovy

repositories {
    // Other repos...
    maven { url 'https://jitpack.io' }
}

dependencies {
    // Other dependencies...
    implementation "com.github.philips-software:kotlin-hsdp-sdk:0.2.0"
}
```

### Maven

```maven
<repositories>
    ...
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
<dependencies>
    ...
    <dependency>
        <groupId>com.github.philips-software</groupId>
        <artifactId>kotlin-hsdp-sdk</artifactId>
        <version>0.2.0</version>
    </dependency>
</dependencies>
```


## Basic usage

Simple examples for Android and backend (Kotlin & Java) applications are provided to give you a quick start.

The library externalized the choice of a logging system by providing an abstract logger factory and a logger interface.
It is up to the application developer to provide implementations of both, and to register the concrete logger factory
to the logger manager before instantiating any SDK services.

### Android

Next code example shows how to use the library in an Android project written in Kotlin.

```kotlin
class MainActivity : AppCompatActivity() {

  private val scope = CoroutineScope(Dispatchers.IO)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // First register the concrete logger factory, before creating any of the services!
    // This will allow (future) versions of the SDK services to also log something in their construction phase.
    PlatformLoggerFactory.registerConcreteFactory(TimberLoggerFactory)
    logger = PlatformLoggerFactory.create("Main", javaClass)

    val httpClient = HttpClient()
    val iamOAuth2 = IamOAuth2(iamUrl, basicAuthUsername, basicAuthPassword, httpClient, initialToken)
    val tdr = TDR(tdrUrl, httpClient)
    scope.launch {
      try {
        if (iamOAuth2.token.isNotValid) {
          logger.error { "no valid token" }
          val token = iamOAuth2.login(username, password)
        }
        val contracts = tdr.getContracts(
          ContractQuery(
            organizationQuery = OrganizationQuery(tenant),
            countQuery = CountQuery(10)
          )
        )
        logger.info { "Getting contracts for $tenant returned page with ${contracts.data.size} items" }
        logger.info { "revoke token" }
        val revokedToken = iamOAuth2.revokeToken()
      } catch (ex: IOException) {
        logger.error { ex.toString() }
      } catch (httpEx: HttpException) {
        logger.error { httpEx.toString() }
      } catch (serializationException: SerializationException) {
        logger.error { serializationException.toString() }
      }
    }
  }
}
```

Notes:
- The example registers a `TimberLoggerFactory` to the `PlatformLoggerFactory`. Former will be detailed in the 
  logging section.
- `iamUrl`, `basicAuthUsername`, `basicAuthPassword`, `tdrUrl`, `tenant`, `username` and `password` are somehow 
  provided by the application.
- `initialToken` can be provided, to allow the application to store the last obtained token somewhere safely, so that 
  upon reopening of the application there is no need for logging in again when the token is still valid.

### Kotlin backend

Next code example shows how to use the library in a Kotlin backend project.

```kotlin
import some.sdk.packages

fun main() {
  // First register the concrete logger factory, before creating any of the services!
  // This will allow (future) versions of the SDK services to also log something in their construction phase.
  PlatformLoggerFactory.registerConcreteFactory(Log4j2LoggerFactory)
  
  val httpClient = HttpClient()
  val iamOAuth2 = IamOAuth2(iamUrl, basicAuthUsername, basicAuthPassword, httpClient)
  val tdr = TDR(tdrUrl, httpClient)
  runBlocking {
    iamOAuth2.login(username, password)
    val dataItems = tdr.getDataItems(
      query = DataItemQuery(organizationQuery = OrganizationQuery(tenant))
    )
    println(dataItems)
    iamOAuth2.revokeToken()
  }
}
```

Notes:
- The example registers a `Log4j2LoggerFactory` to the `PlatformLoggerFactory`. Former will be detailed in the
  logging section.
- `iamUrl`, `basicAuthUsername`, `basicAuthPassword`, `tdrUrl`, `tenant`, `username` and `password` are somehow
  provided by the application.

### Java backend

As mentioned in the design decisions, the Java application must instantiate the service proxies instead of
the services themselves.

Next code example shows how to use the library in a Java Spring Boot backend project.

```java
@SpringBootApplication
public class SimpleApplication {

    public static void main(String[] args) {
        PlatformLoggerFactory.registerConcreteFactory(new SimpleLoggerFactory());
        SpringApplication.run(SimpleApplication.class, args);
    }

    @Bean
    HttpClient getHttpClient() {
        return new HttpClient();
    }

    @Bean
    IamOAuth2JavaProxy getIamOAuth2() {
        return new IamOAuth2JavaProxy(
                iamUrl,
                basicAuthUsername,
                basicAuthPassword,
                getHttpClient()
        );
    }

    @Bean
    TDRJavaProxy getTDR() {
        return new TDRJavaProxy(
                tdrUrl,
                getHttpClient()
        );
    }
}
```

An example REST controller that performs calls to the SDK services for IAM and TDR:

```java
@RestController
@RequestMapping("/tdr")
class TdrController {
    private final HttpClient httpClient;
    private final IamOAuth2JavaProxy iamOAuth2;
    private final TDRJavaProxy tdr;
  
    public TdrController(HttpClient httpClient, IamOAuth2JavaProxy iamOAuth2, TDRJavaProxy tdr) {
        this.httpClient = httpClient;
        this.iamOAuth2 = iamOAuth2;
        this.tdr = tdr;
    }
  
    @GetMapping("contracts")
    public CompletableFuture<ContractsDto> getContracts() {
        return ascertainToken()
                .thenCompose(s -> {
                    ContractQuery contractQuery = new ContractQueryBuilder()
                        .organizationQuery(new OrganizationQuery(tenant))
                        .countQuery(new CountQuery(10))
                        .offsetQuery(new OffsetQuery(0))
                        .build();
                    return tdr.getContracts(contractQuery);
                });
    }
    
    private CompletableFuture<Void> ascertainToken() {
        if (!httpClient.getToken().isValid()) {
            return iamOAuth2.login(username, password)
                  .thenAccept(token -> System.out.println(token.getAccessToken()));
        }
        return CompletableFuture.completedFuture(null);
    }
}
```

Notes:
- The example registers a `SimpleLoggerFactory` to the `PlatformLoggerFactory`. Former will be detailed in the
  logging section.
- `iamUrl`, `basicAuthUsername`, `basicAuthPassword`, `tdrUrl`, `tenant`, `username` and `password` are somehow
  provided by the application.


## Supported APIs

The current implementation covers only a subset of HSDP APIs. Additional functionality is built as needed.

- [x] IAM Identity and Access Management (IAM)
  - [ ] Access Management
    - [ ] Federation
    - [x] OAuth2
      - [x] OAuth2 Authorization
      - [x] OAuth2 Token Revocation
      - [x] OpenID Connect UserInfo
      - [x] Introspect
      - [ ] Session (refresh, terminate)
      - [ ] OpenID (configuration, JWKS)
    - [ ] Token
  - [x] Identity Management
    - [x] User
      - [x] Search user account
      - [x] Register a user account
      - [x] Delete a user account
      - [x] Set password for user account
      - [x] Change user password
      - [x] Reset password service with kba validation
      - [x] Enables user password to be reset by admin
      - [x] Resend account activation email to the user
      - [x] Unlock user account
      - [x] Retrieve saved kba challenges questions for a user
      - [x] Get effective password policy for a user
      - [x] Send verification code (OTP) to secondary auth factors like email and SMS
      - [x] Verify the code (OTP) sent to secondary auth factors like email and SMS
      - [x] Remove OTP device registration
      - [x] Update user's login ID
      - [ ] Enables a user to delegate access to another user to act on its behalf
      - [ ] Revoke delegation granted to given delegatee
  - [ ] Policy Management
- [x] Clinical Data Repository (CDR)
  - [x] Read
  - [x] VRead (versioned read)
  - [x] Create
  - [x] Update
  - [x] Patch
  - [x] Delete
  - [ ] Get History
  - [ ] Batch operation
  - [ ] Get Capabilities
  - [ ] Pagination
- [x] Telemetry Data Repository (TDR)
  - [x] Contracts
  - [x] Data Items
- [x] Provisioning
  - [ ] Provisioning
    - [ ] Provision
    - [x] Create Identity
  - [ ] Reprovision
  - [ ] Unprovisioning
  - [ ] Reset
  - [ ] Identity Certificate
  - [ ] Task

Other services will follow later.


## Logging implementation examples

### Timber logger for Android

A possible implementation for logging with Timber:

```kotlin
object TimberLoggerFactory: AbstractPlatformLoggerFactory {
    init {
        Timber.plant(Timber.DebugTree())
    }

    override fun create(tag: String, ofClass: Class<*>): PlatformLogger = TimberLogger(tag)
}

class TimberLogger(private val tag: String): PlatformLogger {
    private val traceLevel = 1

    override fun fatal(message: () -> String) {
        Timber.tag(tag).wtf(message())
    }

    override fun error(message: () ->String) {
        Timber.tag(tag).e(message())
    }

    override fun warn(message: () -> String) {
        Timber.tag(tag).w(message())
    }

    override fun info(message: () -> String) {
        Timber.tag(tag).i(message())
    }

    override fun debug(message: () -> String) {
        Timber.tag(tag).d(message())
    }

    override fun trace(message: () -> String) {
        Timber.tag(tag).log(traceLevel, message())
    }
}
```

### Log4j2 logger for Kotlin backend applications

A possible implementation for logging with Log4j2:

```kotlin
import com.philips.hsdp.apis.support.logging.AbstractPlatformLoggerFactory
import com.philips.hsdp.apis.support.logging.PlatformLogger
import org.apache.logging.log4j.kotlin.loggerOf


object Log4j2LoggerFactory: AbstractPlatformLoggerFactory {
    override fun create(tag: String, ofClass: Class<*>): PlatformLogger = Log4j2Logger(tag, ofClass)
}

class Log4j2Logger(private val tag: String, ofClass: Class<*>) : PlatformLogger {
    private val logger = loggerOf(ofClass)
    override fun fatal(message: () -> String) {
        logger.fatal(message)
    }

    override fun error(message: () -> String) {
        logger.error(message)
    }

    override fun warn(message: () -> String) {
        logger.warn(message)
    }

    override fun info(message: () -> String) {
        logger.info(message)
    }

    override fun debug(message: () -> String) {
        logger.debug(message)
    }

    override fun trace(message: () -> String) {
        logger.trace(message)
    }
}
```

### Simple logger for Java backend applications

A possible implementation for logging with just println:

```java
public class SimpleLoggerFactory implements AbstractPlatformLoggerFactory {
    @NotNull
    @Override
    public PlatformLogger create(@NotNull String tag, @NotNull Class<?> ofClass) {
        return new SimpleLogger(tag, ofClass);
    }
}

class SimpleLogger implements PlatformLogger {
  private final String tag;
  private final Class<?> ofClass;

  public SimpleLogger(String tag, Class<?> ofClass) {
    this.tag = tag;
    this.ofClass = ofClass;
  }

  @Override
  public void debug(@NotNull Function0<String> message) {
    System.out.println(buildMessage("DEBUG", message));
  }

  @Override
  public void error(@NotNull Function0<String> message) {
    System.out.println(buildMessage("ERROR", message));
  }

  @Override
  public void fatal(@NotNull Function0<String> message) {
    System.out.println(buildMessage("FATAL", message));
  }

  @Override
  public void info(@NotNull Function0<String> message) {
    System.out.println(buildMessage("INFO", message));
  }

  @Override
  public void trace(@NotNull Function0<String> message) {
    System.out.println(buildMessage("TRACE", message));
  }

  @Override
  public void warn(@NotNull Function0<String> message) {
    System.out.println(buildMessage("WARN", message));
  }

  private String buildMessage(String level, Function0<String> message) {
    return level + " " + ofClass.getSimpleName() + " " + message.invoke();
  }
}
```


## Todo

- Implement more HSDP API calls


## Issues

- If you have an issue: report it on the [issue tracker](https://github.com/philips-software/kotlin-hsdp-api/issues)


## Contact / Getting help

Aad Rijnberg (<aad.rijnberg@philips.com>) \
Meindert Schuitema (<meindert.schuitema@philips.com>) \
Martijn van Welie (<martijn.van.welie@philips.com>)

## License

See [LICENSE.md](LICENSE.md).

# kotlin-hsdp-api

This SDK provides a simple interface to features offered by the various services of HSDP.

The output of the project is a JAR-file that can be used in either a Java or Kotlin backend service 
or an Android application.

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
      - [ ] Register a user account
      - [ ] Delete a user account
      - [ ] Set password for user account
      - [ ] Change user password
      - [ ] Reset password service with kba validation
      - [ ] Enables user password to be reset by admin
      - [ ] Resend account activation email to the user
      - [ ] Unlock user account
      - [ ] Retrieve saved kba challenges questions for a user
      - [ ] Get effective password policy for a user
      - [ ] Send verification code (OTP) to secondary auth factors like email and SMS
      - [ ] Verify the code (OTP) sent to secondary auth factors like email and SMS
      - [ ] Remove OTP device registration
      - [ ] Update user's login ID
      - [ ] Enables a user to delegate access to another user to act on its behalf
      - [ ] Revoke delegation granted to given delegatee
  - [ ] Policy Management
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

## Design decisions

### Resource usage

A core component of the SDK is a REST client that performs calls to HSDP APIs. Typical applications perform 
REST calls in a blocking fashion, where one thread is used per request. With many requests at the same time 
(typical in a backend application), this can become an issue (threads are expensive resources). It would lead 
to spinning up extra servers to cope with certain load, just because the number of threads is exhausted.

The Kotlin eco-system provides a feature called coroutines that can solve this problem. Instead of using a
thread per call we can use a coroutine per call. Coroutines are cheap resources; creating millions of them is 
no problem. The main characteristics of a coroutine is that it runs *suspendable* functions, which means that 
functions will suspend when they can not progress (e.g. wait for response from an HTTP server), and once they 
can progress again (e.g. get the response) they will continue where they left off. When they are suspended,
other coroutines that use the same dispatcher (backed by a single thread or a thread pool) get the chance
to progress.
 
The SDK is also targeted for use in Java, which does not support coroutines. Fortunately, Kotlin coroutines 
provide a means to convert a coroutine into a CompletableFuture, which IS understood by Java. This means that
the methods to be called from a Kotlin application will be suspending functions that return a data structure `D`,
and the methods to be called from a Java application will be regular (non-suspending) functions that return a
`CompletableFuture<D>`.  

Possible approaches:
1. Put both the Kotlin and Java implementation in a single class. \
The Java and Kotlin methods have same method parameters but different return types and latter are suspending.
So method overloading cannot be used. Instead, different method names are required to differentiate between
the methods for Java and Kotlin, each with their own implementation.
2. Introduce a separate proxy class for each SDK service. \
The proxy contains a reference to the Kotlin implementation and exposes same methods with different return
type. Each method delegates the work to the Kotlin implementation and wraps the coroutine in a CompletableFuture.

Latter approach was selected, as that leads to a cleaner SDK service interface for both Kotlin and Java. 
The only difference between a Java application and a Kotlin application is that former instantiates a 
XxxJavaProxy and latter just a Xxx, and of course the way it handles the returned results.


### Logging framework flexibility

The SDK must allow for a flexible logging implementation, as Android and backend applications use quite different
logging implementations. Backend applications typically use a log4j2 implementation, whereas Android applications 
often choose for a Timber implementation.

The SDK exposes an abstract logger factory interface:
```kotlin
interface AbstractPlatformLoggerFactory {
    fun create(tag: String, ofClass: Class<*>): PlatformLogger
}
```

The application is responsible for a concrete logger factory that implements this interface.

A logger factory creates logger instances that adhere to following interface:
```kotlin
interface PlatformLogger {
    fun fatal(message: () -> String)
    fun error(message: () -> String)
    fun warn(message: () -> String)
    fun info(message: () -> String)
    fun debug(message: () -> String)
    fun trace(message: () -> String)
}
```

The application is responsible for a concrete logger that implements this interface.

*Typically, a logger has many more variations in method input parameters for each log level, but here the choice
was to start simple and add more interfaces if needed.*

A singleton logger factory "manager" (`PlatformLoggerFactory`) is used for registering the desired concrete
logger factory to use in the SDK. The application must perform this registration BEFORE any logging is performed.


## Code examples

### Kotlin backend application

Next code example shows how to use the library in a Kotlin project.

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
- Above example assumes that a concrete logger factory `Log4j2LoggerFactory` is provided by the application developer.
- `iamUrl`, `basicAuthUsername`, `basicAuthPassword`, `tdrUrl` and `tenant` are to be provided by the application somehow

The logger could be implemented as follows:

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

### Java backend application

As mentioned in the design decisions, the Java application must instantiate the service proxies instead of 
the services themselves.

Next example shows how to use the SDK in a simple Java Spring Boot application:
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
    IamOAuth2JavaProxy getIamOAuth2(IamConfig config) {
        return new IamOAuth2JavaProxy(
                config.getIamUrl(),
                config.getBasicAuthUsername(),
                config.getBasicAuthPassword(),
                getHttpClient()
        );
    }

    @Bean
    TDRJavaProxy getTDR(TdrConfig config) {
        return new TDRJavaProxy(
                config.getUrl(),
                getHttpClient()
        );
    }
}
```

IamConfig and TdrConfig are classes that capture the configuration values in application.properties, 
and are not depicted here.

An example REST controller that performs calls to the SDK services for IAM and TDR:
```java
@RestController
@RequestMapping("/tdr")
class TdrController {
    private final HttpClient httpClient;
    private final IamOAuth2JavaProxy iamOAuth2;
    private final TDRJavaProxy tdr;
  
    @Value("${tdr.tenant}")
    private String tenant;
    @Value("${iam.username}")
    private String username;
    @Value("${iam.password}")
    private String password;
  
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
  
    @GetMapping("data-items")
    public CompletableFuture<DataItemsDto> getDataItems() {
        return ascertainToken()
                .thenCompose(s -> {
                    DataItemQuery dataItemQuery = new DataItemQueryBuilder()
                        .organizationQuery(new OrganizationQuery(tenant))
                        .countQuery(new CountQuery(5))
                        .offsetQuery(new OffsetQuery(20))
                        .build();
                    return tdr.getDataItems(dataItemQuery);
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

And the logger implementation could look like:
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

### Kotlin Android application

A simple Android application that shows the basics of how to use the SDK:

```kotlin
class MainActivity : AppCompatActivity() {

  private val scope = CoroutineScope(Dispatchers.IO)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Initialize logging
    PlatformLoggerFactory.registerConcreteFactory(TimberLoggerFactory)
    logger = PlatformLoggerFactory.create("Main", javaClass)

    val httpClient = HttpClient()
    val iamOAuth2 = IamOAuth2(iamUrl, clientId, clientSecret, httpClient, initialToken)
    val tdr = TDR(tdrUrl, httpClient)
    scope.launch {
      try {
        if (iamOAuth2.token.isNotValid) {
          logger.error { "no valid token" }
          val token = doLogin(iamOAuth2, propertiesService)
          storeToken(token)
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

The Timber logging implementation could look like:

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


## Todo

- Implement more HSDP API calls


## Issues

- If you have an issue: report it on the [issue tracker](https://github.com/philips-software/kotlin-hsdp-api/issues)


## Contact / Getting help

Aad Rijnberg (<aad.rijnberg@philips.com>) \
Meindert Schuitema (<meindert.schuitema@philips.com>) \
Martijn van Welie (<martijn.van.welie@philips.com>)

## License

License is MIT. See [LICENSE file](LICENSE.md)
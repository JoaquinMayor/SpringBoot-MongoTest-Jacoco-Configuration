# Java Nivel 2 — Pruebas y calidad de código con Spring Boot

Proyecto de ejemplo del curso, organizado por clase:

- **Clase 13 — Pruebas de integración con Spring Boot y análisis estático:** `@SpringBootTest`, `@DataMongoTest`, `@WebMvcTest`, Testcontainers con MongoDB real, `@DynamicPropertySource`, SonarQube y refactorización.
- **Clase 14 — Automatización de pruebas con Copilot y JUnit:** JUnit 5, aserciones, buenas prácticas, Mockito, GitHub Copilot, cobertura con JaCoCo, SonarQube en Maven y tests en GitHub Actions.

---

## Índice

- **[Estructura del proyecto](#estructura-del-proyecto)**
- **[Requisitos y cómo ejecutar los tests](#requisitos-y-cómo-ejecutar-los-tests)**
- **[Clase 13 — Pruebas de integración con Spring Boot y análisis estático](#clase-13--pruebas-de-integración-con-spring-boot-y-análisis-estático)**
  - [13.1 ¿Qué son las pruebas de integración?](#131-qué-son-las-pruebas-de-integración)
  - [13.2 Tipos de tests en Spring](#132-tipos-de-tests-en-spring)
  - [13.3 @SpringBootTest](#133-springboottest)
  - [13.4 @DataMongoTest](#134-datamongotest)
  - [13.5 @WebMvcTest, MockMvc y @MockitoBean](#135-webmvctest-mockmvc-y-mockitobean)
  - [13.6 @AutoConfigureMockMvc: probar un endpoint de punta a punta](#136-autoconfiguremockmvc-probar-un-endpoint-de-punta-a-punta)
  - [13.7 Testcontainers](#137-testcontainers)
  - [13.8 @Testcontainers y @Container](#138-testcontainers-y-container)
  - [13.9 @DynamicPropertySource](#139-dynamicpropertysource)
  - [13.10 @Document y MongoRepository](#1310-document-y-mongorepository)
  - [13.11 Tests rápidos vs. pruebas de integración en Maven (Surefire y Failsafe)](#1311-tests-rápidos-vs-pruebas-de-integración-en-maven-surefire-y-failsafe)
  - [13.12 Análisis estático con SonarQube](#1312-análisis-estático-con-sonarqube)
  - [13.13 Refactorización guiada por análisis estático](#1313-refactorización-guiada-por-análisis-estático)
  - [13.14 Copilot + JUnit](#1314-copilot--junit)
  - [13.15 Errores comunes y buenas prácticas](#1315-errores-comunes-y-buenas-prácticas)
  - [13.16 Ejercicio práctico](#1316-ejercicio-práctico)
  - [13.17 Resumen](#1317-resumen)
- **[Clase 14 — Automatización de pruebas con Copilot y JUnit](#clase-14--automatización-de-pruebas-con-copilot-y-junit)**
  - [14.1 Archivos de esta clase](#141-archivos-de-esta-clase)
  - [14.2 Pruebas unitarias con JUnit 5](#142-pruebas-unitarias-con-junit-5)
  - [14.3 ¿Dónde se escriben los tests?](#143-dónde-se-escriben-los-tests)
  - [14.4 Aserciones comunes en JUnit 5](#144-aserciones-comunes-en-junit-5)
  - [14.5 Buenas prácticas en pruebas unitarias](#145-buenas-prácticas-en-pruebas-unitarias)
  - [14.6 Tests negativos o de errores](#146-tests-negativos-o-de-errores)
  - [14.7 Tests parametrizados](#147-tests-parametrizados)
  - [14.8 Tests con dependencias externas: Mockito](#148-tests-con-dependencias-externas-mockito)
  - [14.9 GitHub Copilot + JUnit](#149-github-copilot--junit)
  - [14.10 Buenas prácticas al usar Copilot para testing](#1410-buenas-prácticas-al-usar-copilot-para-testing)
  - [14.11 Cobertura de código con JaCoCo](#1411-cobertura-de-código-con-jacoco)
  - [14.12 Cómo leer el reporte de JaCoCo](#1412-cómo-leer-el-reporte-de-jacoco)
  - [14.13 SonarQube + JaCoCo: cómo trabajan juntos](#1413-sonarqube--jacoco-cómo-trabajan-juntos)
  - [14.14 Configuración de SonarQube en Maven](#1414-configuración-de-sonarqube-en-maven)
  - [14.15 Cómo leer el informe de SonarQube](#1415-cómo-leer-el-informe-de-sonarqube)
  - [14.16 Tests en CI/CD con GitHub Actions](#1416-tests-en-cicd-con-github-actions)
  - [14.17 Errores comunes](#1417-errores-comunes)
  - [14.18 Ejercicio práctico](#1418-ejercicio-práctico)
  - [14.19 Resumen](#1419-resumen)

---

## Estructura del proyecto

```
src/main/java/com/app/back/springbootmongotest
├── SpringBootMongoTestApplication.java      -> clase principal (@SpringBootApplication)
├── clase13/
│   ├── model/Producto.java                  -> documento de MongoDB (@Document)
│   ├── repository/ProductoRepository.java   -> acceso a datos (MongoRepository)
│   ├── service/ProductoService.java         -> lógica de negocio
│   ├── controller/ProductoController.java   -> API REST /productos
│   └── exception/ProductoNoEncontradoException -> se traduce en HTTP 404
└── clase14/
    ├── Calculadora.java                     -> lógica pura para practicar JUnit 5
    ├── UsuarioService.java                  -> servicio con una dependencia externa
    ├── Notificador.java                     -> interfaz de la dependencia (se mockea)
    └── NotificadorConsola.java              -> implementación real (solo escribe en el log)

src/test/java/com/app/back/springbootmongotest
├── clase13/
│   ├── unit/ProductoServiceTest.java            -> @Test unitario con Mockito (sin Spring)
│   ├── datamongo/ProductoRepositoryTest.java    -> @DataMongoTest + Testcontainers
│   ├── webmvc/ProductoControllerWebMvcTest.java -> @WebMvcTest + MockMvc + @MockitoBean
│   └── integration/
│       ├── ProductoServiceIT.java               -> @SpringBootTest + Testcontainers
│       └── ProductoControllerIT.java            -> @SpringBootTest + @AutoConfigureMockMvc + Testcontainers
└── clase14/
    ├── CalculadoraTest.java                     -> JUnit 5: aserciones, AAA, tests negativos y parametrizados
    ├── UsuarioServiceTest.java                  -> Mockito con mock() y verify()
    └── UsuarioServiceMockitoExtensionTest.java  -> Mockito con @Mock e @InjectMocks

.github/workflows/ci.yml                         -> pipeline de GitHub Actions             [Clase 14]
```

El código y los tests de cada clase del curso van en su propia carpeta (`clase13/`, `clase14/`, ...), tanto en `src/main` como en `src/test`. Todas tienen que quedar **dentro** del paquete `com.app.back.springbootmongotest`, donde está la clase principal:
- `@SpringBootApplication` solo detecta componentes (`@Service`, `@RestController`, repositorios) en **su paquete y los subpaquetes**. Si una carpeta quedara afuera, Spring no encontraría sus beans.
- `@SpringBootTest`, `@DataMongoTest` y `@WebMvcTest` buscan la clase principal "hacia arriba" en los paquetes del test.

Cada archivo tiene comentarios explicando qué hace cada anotación, método y parámetro.

## Requisitos y cómo ejecutar los tests

- **Java 21**
- **Docker** en ejecución (Docker Desktop en Windows/Mac). Lo necesitan los tests de la Clase 13 que usan Testcontainers; los de la Clase 14 no.
- Maven no hace falta instalarlo: se usa el wrapper `mvnw` incluido.

```bash
# Solo tests rápidos (*Test.java): unitarios, @DataMongoTest, @WebMvcTest (clases 13 y 14)
./mvnw test

# Todo: tests rápidos + pruebas de integración (*IT.java) + reporte de cobertura
./mvnw verify
```

En Windows (cmd/PowerShell) usar `mvnw.cmd` en lugar de `./mvnw`.

> Si Docker **no** está corriendo, los tests con Testcontainers aparecen como **omitidos (skipped)** en lugar de fallar. Eso es gracias a `@Testcontainers(disabledWithoutDocker = true)`.

Para levantar la aplicación localmente: `./mvnw spring-boot:run`. El archivo `compose.yaml` y la dependencia `spring-boot-docker-compose` hacen que Spring Boot arranque un MongoDB con Docker Compose de forma automática.

---

## Clase 13 — Pruebas de integración con Spring Boot y análisis estático

**Objetivos:**
- Crear pruebas de integración con Spring Boot.
- Probar controladores, servicios y repositorios con Testcontainers.
- Usar SonarQube para analizar y mejorar la calidad del código.
- Aplicar refactorizaciones guiadas por análisis estático.

Ejemplo usado: un pequeño **inventario de productos** (entidad `Producto` con `id`, `nombre` y `precio`) expuesto con una API REST y guardado en MongoDB.

Para ejecutar solo los tests de esta clase (necesita Docker):

```bash
./mvnw verify -Dtest="**/clase13/**/*Test" -Dit.test="**/clase13/**/*IT"
```

- `-Dtest` filtra los tests rápidos que corre Surefire (`*Test`).
- `-Dit.test` filtra las pruebas de integración que corre Failsafe (`*IT`).

Se recomienda leer los tests en este orden: **unitario → @DataMongoTest → @WebMvcTest → @SpringBootTest**, que va de lo más chico y aislado a lo más completo.

### Endpoints de la API

| Método | Ruta                            | Respuesta                                  |
|--------|---------------------------------|--------------------------------------------|
| POST   | `/productos`                    | 201 con el producto creado                 |
| GET    | `/productos`                    | 200 con la lista de productos              |
| GET    | `/productos/{id}`               | 200 con el producto, o 404 si no existe    |
| GET    | `/productos/buscar?nombre=...`  | 200 con el producto, o 404 si no existe    |

### 13.1 ¿Qué son las pruebas de integración?

Una **prueba unitaria** verifica una pieza de código **sola**: una clase o un método, reemplazando sus dependencias por objetos falsos (*mocks*).

Una **prueba de integración** verifica que **varios componentes colaboran correctamente** entre sí: por ejemplo, que el servicio llama al repositorio, el repositorio arma la consulta correcta y MongoDB guarda y devuelve los datos esperados.

**¿Para qué sirven?**
- Detectan errores que los unitarios no ven: una consulta mal escrita, un mapeo de campos incorrecto, una configuración de Spring errónea, un JSON que no se convierte bien, un status HTTP equivocado.
- Simulan el **flujo real** de la aplicación (controlador → servicio → repositorio → base de datos).

**¿Qué tener en cuenta?**
- Son **más lentas** que las unitarias, porque levantan Spring y, a veces, una base de datos.
- Por eso se escriben **menos** pruebas de integración que unitarias, enfocadas en los recorridos importantes.
- Cada test tiene que ser **independiente**: no debe depender de datos que dejó otro test (por eso en el proyecto se borra la colección en `@BeforeEach`).

---

### 13.2 Tipos de tests en Spring

| Tipo de test        | ¿Qué cubre?                                        | ¿Cuándo usarlo?                               | ¿Necesita Docker? |
|---------------------|----------------------------------------------------|-----------------------------------------------|-------------------|
| `@Test` (unitario)  | Lógica interna de un método/clase                  | Validar reglas de negocio sin dependencias    | No                |
| `@DataMongoTest`    | Repositorios con MongoDB real                      | Probar consultas y persistencia aislada       | Sí (Mongo real)   |
| `@WebMvcTest`       | Controladores (sin servicios ni repositorios)      | Testear rutas, JSON y respuestas HTTP         | No                |
| `@SpringBootTest`   | Toda la aplicación (end-to-end)                    | Validar la integración completa               | Sí (Mongo real)   |

`@DataMongoTest` y `@WebMvcTest` se conocen como **test slices** ("rebanadas"): cargan solo **una capa** de la aplicación. Son un punto medio entre el test unitario y el `@SpringBootTest` completo: más realistas que un unitario y más rápidas que levantar todo.

**Regla práctica:** usar la anotación **más chica** que alcance para probar lo que se quiere probar.

---

### 13.3 @SpringBootTest

**¿Qué hace?** Carga **todo el contexto de Spring Boot**, igual que cuando se ejecuta la aplicación: controladores, servicios, repositorios, configuración y conexión a la base. Permite probar **varias capas al mismo tiempo**.

**¿Cómo funciona?** Busca la clase anotada con `@SpringBootApplication`, crea todos los beans y permite inyectarlos en el test con `@Autowired`.

```java
@SpringBootTest
class ProductoServiceIT {

    @Autowired
    ProductoService productoService;   // el servicio REAL, con el repositorio REAL

    @Test
    void debeGuardarProducto() {
        Producto guardado = productoService.save(new Producto("Mouse", 3500.0));
        assertNotNull(guardado.getId());   // creación -> guardado -> verificación
    }
}
```

**Parámetros útiles:**
- `webEnvironment`: cómo se levanta la parte web.
  - `MOCK` (por defecto): no abre un puerto real; se combina con `MockMvc`.
  - `RANDOM_PORT`: levanta un servidor real en un puerto libre (para llamarlo con un cliente HTTP real).
  - `NONE`: sin parte web.
- `properties`: propiedades extra solo para ese test, por ejemplo `@SpringBootTest(properties = "app.feature=true")`.
- `classes`: qué clases de configuración cargar, si no se quiere usar la principal.

**A tener en cuenta:**
- Es la opción **más lenta**: úsala para los recorridos importantes, no para cada detalle.
- Spring **reutiliza (cachea) el contexto** entre clases de test con la misma configuración. Si cada test cambia la configuración, se crean contextos nuevos y todo se vuelve más lento.
- Si se necesita un Mongo real, hay que proveerlo (en este proyecto, con Testcontainers).

Ejemplos: [ProductoServiceIT.java](src/test/java/com/app/back/springbootmongotest/clase13/integration/ProductoServiceIT.java) y [ProductoControllerIT.java](src/test/java/com/app/back/springbootmongotest/clase13/integration/ProductoControllerIT.java).

---

### 13.4 @DataMongoTest

**¿Qué hace?** Levanta **solo la capa de persistencia de MongoDB**: repositorios, `MongoTemplate` y la conexión. **No** carga controladores, servicios ni el servidor web.

**¿Para qué sirve?** Para **pruebas rápidas y aisladas** de los repositorios: comprobar que `save`, `findById` y las consultas propias (como `findByNombre`) funcionan contra una base real.

```java
@DataMongoTest
@Testcontainers(disabledWithoutDocker = true)
class ProductoRepositoryTest {

    @Container
    static MongoDBContainer mongoDB = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", mongoDB::getReplicaSetUrl);
    }

    @Autowired
    ProductoRepository productoRepository;

    @Test
    void guardarYLeerProducto() {
        Producto p = new Producto("Mouse", 3500.0);
        productoRepository.save(p);
        assertTrue(productoRepository.findById(p.getId()).isPresent());
    }
}
```

**A tener en cuenta:**
- Como no carga servicios, **no se puede** hacer `@Autowired` de un `@Service` dentro de un `@DataMongoTest` (falla porque ese bean no existe en el contexto).
- Necesita un MongoDB disponible: se lo damos con Testcontainers + `@DynamicPropertySource`.
- En Spring Boot 4 se importa desde `org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest` (en Spring Boot 3 el paquete era `org.springframework.boot.test.autoconfigure.data.mongo`).

Ejemplo: [ProductoRepositoryTest.java](src/test/java/com/app/back/springbootmongotest/clase13/datamongo/ProductoRepositoryTest.java).

---

### 13.5 @WebMvcTest, MockMvc y @MockitoBean

#### @WebMvcTest

**¿Qué hace?** Levanta **solo la capa web**: controladores, conversión de JSON, validaciones y manejo de errores. **No** carga servicios ni repositorios, y **no necesita base de datos**.

**¿Para qué sirve?** Para testear **rutas y respuestas HTTP**: que la URL exista, que el JSON de entrada se lea bien, que la respuesta tenga el status y el cuerpo correctos.

```java
@WebMvcTest(ProductoController.class)   // cargar solo este controlador
class ProductoControllerWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ProductoService productoService;    // el servicio es falso

    @Test
    void debeCrearProducto() throws Exception {
        when(productoService.save(any(Producto.class))).thenReturn(new Producto("Mouse", 3500.0));

        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Mouse\",\"precio\":3500}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Mouse"));
    }
}
```

- El parámetro `ProductoController.class` indica **qué controlador** cargar. Sin parámetro, carga todos.
- En Spring Boot 4 se importa desde `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`.

#### MockMvc

Simula **peticiones HTTP sin levantar un servidor real** (no abre ningún puerto). Sus partes principales:

| Método                         | Para qué sirve                                                     |
|--------------------------------|--------------------------------------------------------------------|
| `perform(...)`                 | Ejecuta la petición simulada                                       |
| `post("/ruta")`, `get(...)`    | Indican el método HTTP y la URL                                    |
| `.contentType(...)`            | Define el header `Content-Type` (por ejemplo JSON)                 |
| `.content(json)`               | El cuerpo de la petición                                           |
| `.param("nombre", "Mouse")`    | Agrega un parámetro a la URL (`?nombre=Mouse`)                     |
| `.andExpect(status().isOk())`  | Verifica el código HTTP (`isCreated()` = 201, `isNotFound()` = 404) |
| `.andExpect(jsonPath("$.x"))`  | Verifica un campo del JSON de respuesta (`$` es la raíz)           |

#### @MockitoBean

Crea un **mock de Mockito** y lo registra como bean en el contexto de Spring, para que el controlador lo reciba en lugar del servicio real. Con `when(...).thenReturn(...)` se define qué debe devolver.

**A tener en cuenta:**
- Si el controlador necesita un bean que `@WebMvcTest` no carga (como el servicio) y no se lo mockea, el contexto **no arranca**.
- `@MockitoBean` reemplaza al antiguo `@MockBean`, que fue eliminado en Spring Boot 4. Se importa desde `org.springframework.test.context.bean.override.mockito.MockitoBean`.
- Este test **no** prueba que el dato llegue a la base: eso lo cubre la prueba de integración completa.

Ejemplo: [ProductoControllerWebMvcTest.java](src/test/java/com/app/back/springbootmongotest/clase13/webmvc/ProductoControllerWebMvcTest.java).

---

### 13.6 @AutoConfigureMockMvc: probar un endpoint de punta a punta

`@WebMvcTest` trae `MockMvc` automáticamente, pero `@SpringBootTest` no. Con `@AutoConfigureMockMvc` se agrega un `MockMvc` al **contexto completo**, y así se puede probar un endpoint REST recorriendo **todas** las capas reales:

```
Petición HTTP simulada -> Controller -> Service -> Repository -> MongoDB (contenedor)
```

```java
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class ProductoControllerIT {
    // ... contenedor + @DynamicPropertySource ...

    @Autowired MockMvc mockMvc;

    @Test
    void debeCrearProducto() throws Exception {
        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Mouse\",\"precio\":3500}"))
                .andExpect(status().isCreated());
    }
}
```

Este test verifica:
- que el endpoint **existe**,
- que **recibe los datos** correctamente,
- que **responde como se espera**,
- y que el producto quedó **realmente guardado** en MongoDB.

Ejemplo: [ProductoControllerIT.java](src/test/java/com/app/back/springbootmongotest/clase13/integration/ProductoControllerIT.java).

---

### 13.7 Testcontainers

**¿Qué es?** Una librería que **levanta contenedores Docker reales** (MongoDB, PostgreSQL, Kafka, etc.) desde el código de los tests, y los **destruye al terminar**.

**¿Para qué sirve?** Para probar contra la **misma tecnología que en producción**, en lugar de una base "en memoria" o simulada. Reproduce condiciones muy similares a las reales y garantiza **entornos de prueba realistas y repetibles**: cada ejecución arranca con una base nueva y vacía, en cualquier máquina o pipeline de CI que tenga Docker.

**¿Cómo funciona?**
1. Antes de los tests, Testcontainers le pide a Docker que descargue (la primera vez) y arranque la imagen indicada, por ejemplo `mongo:7.0`.
2. El contenedor expone su puerto en un **puerto aleatorio** de la máquina, para no chocar con otros servicios.
3. Los tests se conectan a ese contenedor.
4. Al terminar, el contenedor se detiene y se elimina (un contenedor auxiliar llamado *Ryuk* se encarga de limpiar aunque el test se corte a la mitad).

#### Dependencias necesarias

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>          <!-- núcleo -->
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers-mongodb</artifactId>  <!-- trae MongoDBContainer -->
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers-junit-jupiter</artifactId> <!-- trae @Testcontainers y @Container -->
    <scope>test</scope>
</dependency>
```

- `scope test`: solo se usan en los tests, no viajan con la aplicación.
- No llevan `<version>`: la administra el parent de Spring Boot, que garantiza versiones compatibles.
- **Ojo con la versión:** en **Testcontainers 2.x** (la que usa Spring Boot 4) los artefactos se llaman `testcontainers-mongodb` y `testcontainers-junit-jupiter`, y la clase es `org.testcontainers.mongodb.MongoDBContainer`. En Testcontainers 1.x se llamaban `mongodb` y `junit-jupiter`, y la clase estaba en `org.testcontainers.containers.MongoDBContainer`. Muchos tutoriales en internet todavía muestran los nombres viejos.

#### MongoDBContainer

```java
static MongoDBContainer mongoDB = new MongoDBContainer("mongo:7.0");
```

- El parámetro es la **imagen de Docker** con su versión (`nombre:etiqueta`).
- Conviene usar **la misma versión que en producción** y **nunca `latest`**: con `latest` los tests pueden empezar a fallar el día que sale una versión nueva.
- `getReplicaSetUrl()` devuelve la cadena de conexión al contenedor, por ejemplo `mongodb://localhost:54321/test`. Mongo se levanta como *replica set* para que también funcionen las transacciones.

**A tener en cuenta:**
- **Docker tiene que estar corriendo.** Es el error más común.
- La **primera** ejecución tarda más porque descarga la imagen.

---

### 13.8 @Testcontainers y @Container

```java
@Testcontainers(disabledWithoutDocker = true)
class ProductoRepositoryTest {

    @Container
    static MongoDBContainer mongoDB = new MongoDBContainer("mongo:7.0");
}
```

- **`@Testcontainers`** (sobre la clase): activa la extensión de JUnit 5 que busca los campos `@Container` y maneja su ciclo de vida (arrancarlos y detenerlos).
  - `disabledWithoutDocker = true`: si no hay Docker disponible, los tests se **omiten** en vez de fallar. Útil para que el proyecto compile y corra en máquinas sin Docker.
- **`@Container`** (sobre el campo): marca el contenedor que hay que administrar.
  - Campo **`static`** → se crea **un solo contenedor para toda la clase**; todos los tests lo comparten. Es lo recomendado.
  - Campo **no static** → se crea **un contenedor nuevo para cada test**. Más aislado, pero mucho más lento.

Como el contenedor se comparte, los datos que guarda un test los ve el siguiente. Por eso se limpia la base antes de cada test:

```java
@BeforeEach
void limpiarBase() {
    productoRepository.deleteAll();
}
```

---

### 13.9 @DynamicPropertySource

**El problema:** el contenedor de Mongo arranca en un **puerto aleatorio** que recién se conoce en tiempo de ejecución. No se puede escribir en `application.properties`.

**La solución:** `@DynamicPropertySource` permite **agregar propiedades de configuración dinámicamente**, justo antes de que Spring cree el contexto.

```java
@DynamicPropertySource
static void mongoProps(DynamicPropertyRegistry registry) {
    registry.add("spring.mongodb.uri", mongoDB::getReplicaSetUrl);
}
```

- El método tiene que ser **`static`** y recibir un **`DynamicPropertyRegistry`**.
- `registry.add(nombre, proveedor)`:
  - `nombre`: la propiedad de Spring que se quiere definir.
  - `proveedor`: un *Supplier* (acá la referencia a método `mongoDB::getReplicaSetUrl`) que se evalúa cuando Spring necesita el valor, es decir, cuando el contenedor ya arrancó.
- **Resultado:** la URI del contenedor se inyecta automáticamente y Spring se conecta a ese Mongo, **sin modificar** `application.properties` / `application.yml`.

**A tener en cuenta:**
- **Spring Boot 4 cambió el nombre de la propiedad:** ahora es **`spring.mongodb.uri`**. La anterior, `spring.data.mongodb.uri` (Spring Boot 2 y 3), quedó obsoleta. Si se usa el nombre viejo, Spring ignora la URI y trata de conectarse a `localhost:27017`.
- Las propiedades dinámicas forman parte de la "clave" del contexto cacheado: clases con contenedores distintos tendrán contextos distintos.

#### Alternativa moderna: @ServiceConnection

Desde Spring Boot 3.1 existe una forma más corta. Agregando la dependencia `spring-boot-testcontainers`, se puede reemplazar el `@DynamicPropertySource` completo por:

```java
@Container
@ServiceConnection
static MongoDBContainer mongoDB = new MongoDBContainer("mongo:7.0");
```

Spring detecta el tipo de contenedor y configura la conexión solo. En este proyecto se usa `@DynamicPropertySource` porque muestra **explícitamente** qué está pasando y sirve para cualquier propiedad, no solo para conexiones.

---

### 13.10 @Document y MongoRepository

#### @Document

```java
@Document(collection = "productos")
public class Producto {
    @Id
    private String id;
    private String nombre;
    private Double precio;
}
```

- **`@Document`**: indica que la clase se guarda como un **documento** de MongoDB. `collection` es el nombre de la colección (el equivalente a una tabla). Si se omite, se usa el nombre de la clase.
- **`@Id`**: el identificador único (`_id` en Mongo). Si es `null` al guardar, Mongo genera uno automáticamente.
- La clase necesita un **constructor vacío** (o uno que Spring Data pueda usar) para reconstruir el objeto al leerlo.

#### MongoRepository

```java
public interface ProductoRepository extends MongoRepository<Producto, String> {
    Optional<Producto> findByNombre(String nombre);
    List<Producto> findByPrecioLessThan(Double precio);
}
```

- Los parámetros genéricos son **el tipo de documento** (`Producto`) y **el tipo del id** (`String`).
- Trae listos `save`, `findById`, `findAll`, `deleteById`, `count`, etc. **No hay que implementar la interfaz**: Spring lo hace en tiempo de ejecución.
- **Consultas derivadas:** Spring lee el **nombre del método** y arma la consulta. `findByNombre` busca por igualdad; `findByPrecioLessThan` busca precios menores. Si el nombre tiene un error (un campo que no existe), la aplicación falla al arrancar.

Justamente estas consultas son las que conviene probar con `@DataMongoTest` contra un Mongo real.

---

### 13.11 Tests rápidos vs. pruebas de integración en Maven (Surefire y Failsafe)

Maven tiene dos plugins para ejecutar tests:

| Plugin       | Ejecuta las clases que terminan en | Fase de Maven | Comando          |
|--------------|-----------------------------------|---------------|------------------|
| **Surefire** | `*Test`, `*Tests`                 | `test`        | `./mvnw test`    |
| **Failsafe** | `*IT` (Integration Test)          | `verify`      | `./mvnw verify`  |

La convención del sufijo **`IT`** permite separar los tests rápidos (que conviene correr a cada rato) de las pruebas de integración más lentas (que se corren antes de subir cambios o en el pipeline de CI/CD). Failsafe está configurado en el `pom.xml` del proyecto.

> Si una clase termina en `IT` y Failsafe **no** está configurado, Maven simplemente **no la ejecuta**, y no avisa.

---

### 13.12 Análisis estático con SonarQube

#### ¿Qué es el análisis estático?

Es revisar el **código fuente sin ejecutarlo**, buscando problemas a partir de reglas conocidas. Es como tener un revisor automático que lee todo el código cada vez que cambia.

#### ¿Qué es SonarQube?

Una plataforma que analiza el código y muestra un tablero con su calidad. Detecta:

| Concepto             | Qué significa                                                                                     |
|----------------------|---------------------------------------------------------------------------------------------------|
| **Bugs**             | Código que probablemente funciona mal (posible `NullPointerException`, condiciones siempre verdaderas). |
| **Vulnerabilidades** | Problemas de seguridad (contraseñas en el código, inyecciones, datos sensibles en logs).          |
| **Code smells**      | Código que funciona pero es difícil de mantener (métodos larguísimos, nombres poco claros, variables sin usar). |
| **Duplicaciones**    | Bloques de código copiados y pegados en varios lugares.                                           |
| **Cobertura**        | Qué porcentaje del código se ejecuta durante los tests. Baja cobertura = código sin probar.       |
| **Complejidad**      | Cuántos caminos posibles tiene un método (muchos `if`/`for` anidados = difícil de entender y probar). |
| **Quality Gate**     | El conjunto de condiciones mínimas (por ejemplo "0 bugs nuevos y cobertura ≥ 80%") que el código debe cumplir para considerarse aprobado. |

#### Cobertura con JaCoCo

SonarQube **no ejecuta** los tests: lee un reporte de cobertura. Ese reporte lo genera **JaCoCo**, ya configurado en el `pom.xml`. (JaCoCo y la configuración de SonarQube en Maven se ven en detalle en la Clase 14.) Al correr `./mvnw verify` queda en `target/site/jacoco/` (abrir `index.html` para verlo en el navegador; `jacoco.xml` es el que lee SonarQube).

#### Cómo ejecutar un análisis local

1. Levantar SonarQube con Docker:
   ```bash
   docker run -d --name sonarqube -p 9000:9000 sonarqube:community
   ```
2. Entrar a http://localhost:9000 (usuario y contraseña iniciales: `admin` / `admin`), crear un proyecto local y generar un **token**.
3. Ejecutar el análisis desde el proyecto:
   ```bash
   ./mvnw clean verify sonar:sonar \
     -Dsonar.host.url=http://localhost:9000 \
     -Dsonar.token=TU_TOKEN
   ```
4. Revisar el tablero: bugs, code smells, duplicaciones, cobertura y estado del Quality Gate.

**A tener en cuenta:**
- Ejecutar siempre `verify` **antes** de `sonar:sonar`, para que exista el reporte de cobertura.
- **No subir el token al repositorio**: pasarlo por parámetro o variable de entorno (en CI, como *secret*).
- No todo lo que marca SonarQube es grave: hay que leer la explicación de cada regla y priorizar por severidad.
- También existe **SonarLint** (SonarQube for IDE), un plugin para IntelliJ/VS Code que marca los problemas mientras se escribe.

---

### 13.13 Refactorización guiada por análisis estático

**Refactorizar** es **mejorar la estructura interna del código sin cambiar lo que hace**. El objetivo es reducir complejidad y errores futuros, y que el código sea más fácil de leer y mantener.

**Por qué los tests son clave:** los tests son la red de seguridad. Si después de refactorizar todos los tests siguen en verde, el comportamiento no se rompió.

**Flujo de trabajo:**
1. Correr los tests (todo en verde).
2. Analizar con SonarQube y elegir un problema.
3. Aplicar el cambio.
4. Volver a correr los tests y el análisis.

#### Ejemplos típicos de sugerencias y cómo resolverlas

**Inyección por campo → inyección por constructor**
```java
// Antes: SonarQube sugiere evitar @Autowired en campos
@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;
}

// Después: dependencia final, explícita y fácil de reemplazar en un test unitario
@Service
public class ProductoService {
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }
}
```

**`Optional.get()` sin comprobar → `orElseThrow`**
```java
// Antes: si no existe, lanza NoSuchElementException (posible bug)
return productoRepository.findById(id).get();

// Después: error claro y controlado (en la API se traduce a 404)
return productoRepository.findById(id)
        .orElseThrow(() -> new ProductoNoEncontradoException(id));
```

**Números o textos "mágicos" → constantes con nombre**
```java
// Antes
if (producto.getPrecio() > 100000) { ... }

// Después
private static final double PRECIO_MAXIMO = 100_000;
if (producto.getPrecio() > PRECIO_MAXIMO) { ... }
```

**Código duplicado → extraer un método**
Si el mismo bloque de validación aparece en `save` y en `update`, se extrae a un método privado `validarPrecio(producto)` y se llama desde ambos.

**Método muy complejo → dividirlo**
Un método con muchos `if` anidados se divide en métodos más pequeños con nombres descriptivos, o se reemplazan los `if` por *retornos tempranos* (*guard clauses*).

---

### 13.14 Copilot + JUnit

Los asistentes de IA como **GitHub Copilot** ayudan a **acelerar la escritura de tests**: generan el esqueleto de una clase de test, sugieren casos (camino feliz, valores nulos, negativos, bordes) y completan `assert`s repetitivos.

**A tener en cuenta:**
- El test generado hay que **leerlo y entenderlo**: puede probar algo incorrecto o verificar el comportamiento actual aunque esté mal.
- Un test que **nunca falla** no sirve. Conviene romper el código a propósito una vez para comprobar que el test lo detecta.
- La IA no reemplaza decidir **qué** hay que probar: eso sale de las reglas de negocio.

Usado así, fomenta una **cultura de testing continuo**: escribir tests deja de ser una tarea pesada y pasa a ser parte natural del desarrollo.

Este tema se desarrolla en detalle en la [Clase 14](#clase-14--automatización-de-pruebas-con-copilot-y-junit).

---

### 13.15 Errores comunes y buenas prácticas

| Problema                                                        | Causa / solución                                                                                  |
|-----------------------------------------------------------------|---------------------------------------------------------------------------------------------------|
| `Could not find a valid Docker environment`                     | Docker no está corriendo. Abrir Docker Desktop.                                                   |
| Los tests intentan conectarse a `localhost:27017`               | Se usó `spring.data.mongodb.uri`; en Spring Boot 4 es `spring.mongodb.uri`.                       |
| `No qualifying bean of type ...Service` en `@WebMvcTest`        | El servicio no se carga en esa slice: agregar `@MockitoBean`.                                      |
| `No qualifying bean of type ...Service` en `@DataMongoTest`     | `@DataMongoTest` solo carga repositorios: usar `@SpringBootTest` si se necesita el servicio.        |
| Un test pasa solo y falla junto a los demás                     | Datos compartidos en el contenedor: limpiar en `@BeforeEach`.                                     |
| Las clases `*IT` no se ejecutan                                 | Falta el plugin Failsafe o se corrió `mvn test` en lugar de `mvn verify`.                         |
| Los tests empiezan a fallar "solos" un día                      | Imagen con `latest`: fijar una versión concreta (`mongo:7.0`).                                    |
| `cannot find symbol MongoDBContainer` / `MockBean`              | Imports de versiones viejas: ver los paquetes nuevos en las secciones 13.4, 13.5 y 13.7.                    |

**Buenas prácticas:**
- Usar la anotación de test **más chica** que alcance.
- Contenedores **`static`** para compartirlos dentro de la clase.
- Tests **independientes** entre sí y con nombres que describan el comportamiento (`debeResponder404SiNoExiste`).
- Estructura **Arrange / Act / Assert** (preparar, ejecutar, verificar) en cada test.
- Integrar tests + análisis estático en el pipeline de CI/CD, para que se ejecuten en cada cambio.

---

### 13.16 Ejercicio práctico

**Contexto:** se está desarrollando una app de inventario. Hay que validar el repositorio de productos contra una base MongoDB real en un contenedor y mejorar la calidad del código con SonarQube.

**Consigna:**
1. Crear la entidad `Producto` con los campos `id`, `nombre` y `precio`, anotada con `@Document`.
2. Crear `ProductoRepository` extendiendo `MongoRepository`, con el método `findByNombre`.
3. Configurar Testcontainers con `MongoDBContainer` y conectar Spring con `@DynamicPropertySource`.
4. Escribir un test de integración que guarde un producto y lo busque por nombre.
5. Ejecutar y comprobar que el contenedor se levanta y se conecta (`docker ps` mientras corre el test muestra el contenedor de Mongo).
6. Ejecutar el análisis con SonarQube y revisar los resultados.
7. Refactorizar aplicando al menos una mejora sugerida por SonarQube y volver a correr los tests.

La solución de los pasos 1 a 5 está en este proyecto.

**Para seguir practicando:**
- Agregar `findByNombreContainingIgnoreCase` al repositorio y probarlo con `@DataMongoTest`.
- Agregar un endpoint `DELETE /productos/{id}` y probarlo con `@WebMvcTest` y con `ProductoControllerIT`.
- Pasar el contenedor a una clase base abstracta para no repetirlo en cada test.

---

### 13.17 Resumen

- Las **pruebas de integración** verifican que los componentes colaboran correctamente, simulando el flujo real de la aplicación.
- **`@SpringBootTest`** levanta toda la aplicación para pruebas realistas de punta a punta.
- **`@DataMongoTest`** prueba de forma rápida y aislada la capa de persistencia.
- **`@WebMvcTest`** prueba los controladores y las respuestas HTTP sin base de datos, usando `MockMvc` y `@MockitoBean`.
- **Testcontainers** levanta bases reales como MongoDB en Docker, con condiciones similares a producción.
- **`@Container`** define el contenedor y **`@DynamicPropertySource`** conecta Spring a él sin tocar la configuración.
- **SonarQube** detecta bugs, vulnerabilidades, duplicaciones, code smells y baja cobertura sin ejecutar la aplicación.
- **Refactorizar** con los tests como red de seguridad reduce la complejidad y facilita el mantenimiento.
- **Copilot + JUnit** aceleran la escritura de tests, siempre revisando lo generado.
- Integrar pruebas, análisis y mejora constante es la clave para un software de calidad profesional.

---

## Clase 14 — Automatización de pruebas con Copilot y JUnit

**Objetivos:**
- Comprender cómo GitHub Copilot puede asistir en la creación de pruebas unitarias.
- Aplicar buenas prácticas en los tests generados con Copilot.
- Usar JUnit 5 para validar el comportamiento esperado de métodos Java.
- Medir y acelerar la cobertura de pruebas en clases de servicio y lógica de negocio.
- Integrar las pruebas unitarias en el flujo de desarrollo continuo (CI/CD).

### 14.1 Archivos de esta clase

| Archivo | Qué muestra |
|---|---|
| [Calculadora.java](src/main/java/com/app/back/springbootmongotest/clase14/Calculadora.java) | Clase de lógica pura (sumar, restar, dividir, potencia) para practicar |
| [UsuarioService.java](src/main/java/com/app/back/springbootmongotest/clase14/UsuarioService.java) | Servicio con reglas de negocio y una dependencia externa (`Notificador`) |
| [CalculadoraTest.java](src/test/java/com/app/back/springbootmongotest/clase14/CalculadoraTest.java) | JUnit 5: `@Test`, `@BeforeEach`, AAA, tests negativos, `@ParameterizedTest`, `@DisplayName` |
| [UsuarioServiceTest.java](src/test/java/com/app/back/springbootmongotest/clase14/UsuarioServiceTest.java) | Todas las aserciones comunes + Mockito con `mock()` y `verify()` |
| [UsuarioServiceMockitoExtensionTest.java](src/test/java/com/app/back/springbootmongotest/clase14/UsuarioServiceMockitoExtensionTest.java) | Mockito con anotaciones: `@Mock`, `@InjectMocks` |
| [pom.xml](pom.xml) | Plugins de JaCoCo y SonarQube, y propiedades `sonar.*` |
| [.github/workflows/ci.yml](.github/workflows/ci.yml) | Pipeline de GitHub Actions que ejecuta los tests en cada push |

Para ejecutar solo los tests de esta clase:

```bash
./mvnw test -Dtest="**/clase14/**/*Test"
```

> Estos tests **no** necesitan Docker ni MongoDB: son unitarios puros.

---

### 14.2 Pruebas unitarias con JUnit 5

**¿Qué es JUnit 5?** El framework estándar para escribir y ejecutar tests en Java. Cada método anotado con `@Test` es un test independiente que **pasa** (verde) si todas sus aserciones se cumplen, o **falla** (rojo) si alguna no se cumple o se lanza una excepción inesperada.

**¿Qué es una prueba unitaria?** Un test que verifica **una sola unidad de código** (un método, una clase) de forma **aislada**: sin base de datos, sin red, sin Spring. Por eso son muy rápidas (milisegundos) y se pueden ejecutar cientos de veces por día.

```java
class CalculadoraTest {

    private Calculadora calc;

    @BeforeEach                 // se ejecuta antes de CADA test
    void setUp() {
        calc = new Calculadora();
    }

    @Test                       // marca el método como un test
    void testSumar() {
        assertEquals(5, calc.sumar(2, 3));
    }
}
```

**Anotaciones básicas:**

| Anotación | Para qué sirve |
|---|---|
| `@Test` | Marca un método como test. No debe ser `private` ni devolver nada (`void`). |
| `@BeforeEach` | Se ejecuta antes de cada test. Ideal para crear los objetos comunes. |
| `@AfterEach` | Se ejecuta después de cada test (por ejemplo, para limpiar recursos). |
| `@BeforeAll` / `@AfterAll` | Se ejecutan una sola vez, antes/después de todos los tests de la clase (métodos `static`). |
| `@DisplayName("...")` | Nombre legible que se muestra en el IDE y en los reportes. |
| `@Disabled("motivo")` | Desactiva un test temporalmente (siempre indicando el motivo). |
| `@ParameterizedTest` | Ejecuta el mismo test con distintos datos (ver 14.7). |

**A tener en cuenta:**
- En JUnit 5 la clase de test y sus métodos **no necesitan ser `public`**.
- El orden en que se ejecutan los tests **no está garantizado**: ningún test puede depender de otro.

---

### 14.3 ¿Dónde se escriben los tests?

En un proyecto Maven el código de producción va en `src/main/java` y los tests en `src/test/java`. **La clase de test replica el mismo package que la clase que prueba**:

```
src/main/java/com/app/back/springbootmongotest/clase14/Calculadora.java
src/test/java/com/app/back/springbootmongotest/clase14/CalculadoraTest.java
                 └──────────── mismo package ────────────┘
```

**¿Por qué?**
- **Organización:** se encuentra el test de cada clase de inmediato.
- **Acceso a métodos package-private:** un método sin modificador (`void validarExponente(...)`) solo es visible dentro de su package. Como el test está en el mismo package (aunque en otra carpeta), puede llamarlo directamente:

```java
// Calculadora.java (src/main)
void validarExponente(int exponente) { ... }   // package-private

// CalculadoraTest.java (src/test) - mismo package
@Test
void validarExponenteAceptaCero() {
    assertDoesNotThrow(() -> calc.validarExponente(0));
}
```

**Convención de nombres:** la clase de test se llama igual que la clase probada con el sufijo `Test` (`Calculadora` → `CalculadoraTest`). Además de ser una convención, Maven Surefire solo ejecuta automáticamente las clases que terminan en `Test` o `Tests`.

---

### 14.4 Aserciones comunes en JUnit 5

Las **aserciones** son las verificaciones del test. Se importan de forma estática desde `org.junit.jupiter.api.Assertions`.

| Aserción | Uso | Ejemplo en el proyecto |
|---|---|---|
| `assertEquals(esperado, actual)` | Compara el valor esperado contra el real | `assertEquals(5, calc.sumar(2, 3))` |
| `assertTrue(condicion)` | Verifica que la condición sea verdadera | `assertTrue(service.esMayorDeEdad(20))` |
| `assertFalse(condicion)` | Verifica que la condición sea falsa | `assertFalse(service.esMayorDeEdad(17))` |
| `assertNull(valor)` | Verifica que el valor sea `null` | `assertNull(service.normalizarEmail(null))` |
| `assertNotNull(valor)` | Verifica que el valor no sea `null` | `assertNotNull(resultado)` |
| `assertThrows(Excepcion.class, () -> ...)` | Verifica que se lance una excepción | `assertThrows(ArithmeticException.class, () -> calc.dividir(10, 0))` |
| `assertDoesNotThrow(() -> ...)` | Verifica que **no** se lance ninguna excepción | `assertDoesNotThrow(() -> calc.validarExponente(0))` |
| `assertAll(() -> ..., () -> ...)` | Ejecuta varias aserciones y reporta **todas** las que fallen juntas | Ver `normalizarEmailQuitaEspaciosYPasaAMinusculas` |

**A tener en cuenta:**
- En `assertEquals` el **primer parámetro es el esperado** y el segundo el real. Si se invierten, el test funciona igual, pero el mensaje de error confunde ("se esperaba 7 pero fue 5" al revés).
- Todas aceptan un **último parámetro opcional** con un mensaje para cuando fallan: `assertEquals(5, resultado, "La suma de 2 + 3 debería ser 5")`.
- Se pueden combinar varias aserciones en un test si tiene sentido, **pero con foco**: todas deben verificar el mismo comportamiento.

---

### 14.5 Buenas prácticas en pruebas unitarias

**Patrón AAA (Arrange – Act – Assert):** organizar cada test en tres bloques.

```java
@Test
void testSumaConNumerosNegativos() {
    // Arrange: preparar los datos y las dependencias
    int a = -4;
    int b = -6;

    // Act: ejecutar la lógica que se quiere probar
    int resultado = calc.sumar(a, b);

    // Assert: verificar el resultado
    assertEquals(-10, resultado);
}
```

**Recomendaciones:**
- ✔️ **Una sola lógica por test.** Si falla, se sabe exactamente qué se rompió.
- ✔️ **Nombres descriptivos** que expliquen el comportamiento: `deberiaRechazarEdadNegativa()`, `testSumaConNumerosNegativos()`. El nombre debería leerse como una frase.
- ✔️ **`@BeforeEach`** para inicializar los objetos comunes en lugar de repetirlos en cada test.

**Evitar:**
- 🚫 **Duplicar la lógica del método real** en el test. Si el código tiene un error, el test tendrá el mismo error y pasará igual:
  ```java
  // MAL: repite la cuenta que hace el método
  assertEquals(a + b, calc.sumar(a, b));
  // BIEN: valor esperado concreto, calculado "a mano"
  assertEquals(5, calc.sumar(2, 3));
  ```
- 🚫 **Tests que dependen entre sí** (que uno necesite que otro se ejecute antes).
- 🚫 **Aserciones ambiguas**, que no verifican nada concreto:
  ```java
  assertNotNull(calc.sumar(2, 3));   // un int nunca es null: este test no prueba nada
  assertTrue(resultado > 0);         // ¿5? ¿1000? demasiado amplio
  ```

---

### 14.6 Tests negativos o de errores

No alcanza con probar lo que "funciona bien" (el *camino feliz*). También hay que probar:
- **Errores esperados:** entradas inválidas que deben lanzar una excepción.
- **Casos límite:** los valores justo en el borde de una condición (0, 18, el máximo, el mínimo, `null`, vacío).

```java
@Test
void testDivisionPorCero() {
    ArithmeticException error =
            assertThrows(ArithmeticException.class, () -> calc.dividir(10, 0));

    assertEquals("No se puede dividir por cero", error.getMessage());
}

@Test
void deberiaConsiderarMayorDeEdadAQuienTieneExactamente18() {
    assertTrue(service.esMayorDeEdad(18));   // el borde de "edad >= 18"
}
```

**¿Cómo funciona `assertThrows`?** El segundo parámetro es una **lambda** (`() -> codigo`). JUnit la ejecuta y el test pasa **solo si** se lanza esa excepción (o una subclase). Además devuelve la excepción, lo que permite verificar su mensaje.

**A tener en cuenta:** los casos límite son justamente donde más se equivocan los programadores (`>` en lugar de `>=`) y los que Copilot más suele omitir.

---

### 14.7 Tests parametrizados

Cuando hay que probar el mismo comportamiento con muchos datos distintos, en lugar de copiar y pegar el test se usa `@ParameterizedTest`:

```java
@ParameterizedTest(name = "{0} elevado a {1} = {2}")
@CsvSource({
        "2, 0, 1",
        "2, 1, 2",
        "2, 10, 1024",
        "-3, 3, -27"
})
void testPotencia(int base, int exponente, int esperado) {
    assertEquals(esperado, calc.potencia(base, exponente));
}
```

- `@CsvSource`: cada string es una fila de datos separados por coma; JUnit los convierte a los tipos de los parámetros del método.
- `name`: cómo se muestra cada ejecución en el reporte (`{0}`, `{1}`... son los parámetros).
- El test se ejecuta **una vez por fila** y cada fila aparece como un test separado.

Es una forma muy rápida de **subir la cobertura de ramas**. Otras fuentes de datos: `@ValueSource` (un solo parámetro), `@MethodSource` (datos generados por un método).

---

### 14.8 Tests con dependencias externas: Mockito

Muchas clases dependen de otras: un servicio que envía emails, que consulta una API, que guarda en una base. En un test unitario **no queremos** que esas dependencias se ejecuten de verdad. La solución es reemplazarlas por un **mock**: un objeto simulado que no hace nada, pero **registra cómo fue llamado**.

```java
class UsuarioService {
    private final Notificador notificador;

    UsuarioService(Notificador notificador) {     // la dependencia entra por el constructor
        this.notificador = notificador;
    }

    public void enviarAlerta(String email) {
        notificador.enviar(normalizarEmail(email), "Alerta enviada");
    }
}

@Test
void testEnviarAlerta() {
    Notificador mock = mock(Notificador.class);          // 1) crear el objeto simulado
    UsuarioService service = new UsuarioService(mock);   // 2) dárselo al servicio

    service.enviarAlerta("persona@banco.com");            // 3) ejecutar

    verify(mock).enviar("persona@banco.com", "Alerta enviada");  // 4) ¿se llamó con esos parámetros?
}
```

**Métodos principales de Mockito:**

| Método | Para qué sirve |
|---|---|
| `mock(Clase.class)` | Crea el objeto simulado |
| `when(mock.metodo()).thenReturn(valor)` | Define qué devuelve el mock cuando lo llaman |
| `when(...).thenThrow(excepcion)` | Hace que el mock lance una excepción |
| `verify(mock).metodo(params)` | Verifica que se llamó al método con esos parámetros |
| `verify(mock, times(n))` | Verifica que se llamó exactamente `n` veces |
| `verify(mock, never())` | Verifica que **nunca** se llamó |
| `verifyNoInteractions(mock)` | Verifica que el mock no se usó en absoluto |
| `any()`, `anyString()` | Comodines: "cualquier valor" en `when` o `verify` |

**Con anotaciones** (el estilo que Copilot suele generar):

```java
@ExtendWith(MockitoExtension.class)   // activa Mockito en JUnit 5
class UsuarioServiceMockitoExtensionTest {

    @Mock
    Notificador notificador;          // equivale a mock(Notificador.class)

    @InjectMocks
    UsuarioService service;           // equivale a new UsuarioService(notificador)
}
```

**A tener en cuenta:**
- El **aislamiento de dependencias** es posible porque `UsuarioService` recibe el `Notificador` por el **constructor** y depende de una **interfaz**. Si hiciera `new NotificadorEmail()` adentro, no se podría reemplazar.
- Mockear solo las **dependencias externas**, nunca la clase que se está probando.
- No abusar de `verify`: verificar las interacciones **importantes** para el negocio, no cada llamada interna.

---

### 14.9 GitHub Copilot + JUnit

**¿Qué es?** GitHub Copilot es un asistente de IA integrado en el IDE (IntelliJ, VS Code, Eclipse). Analiza el código de la clase y el contexto del proyecto, y **sugiere automáticamente** el cuerpo de los tests usando patrones comunes.

**¿Cómo se generan tests con Copilot?**
1. Crear la clase de prueba (`CalculadoraTest.java`) en el mismo package que la clase a probar.
2. Escribir solo la firma del test y dejar el cursor dentro del método:
   ```java
   @Test
   void testSumar() {
       |   <- cursor acá
   }
   ```
3. Copilot sugiere el cuerpo en gris:
   ```java
   Calculadora calc = new Calculadora();
   assertEquals(5, calc.sumar(2, 3));
   ```
4. Aceptar con **Tab** (o descartar con **Esc**) y **ajustar si es necesario**.
5. Ejecutar el test con JUnit para verificar que pase.

**Otras formas de usarlo:**
- **Copilot Chat:** seleccionar un método y pedir "generá tests unitarios con JUnit 5 para este método, incluyendo casos límite y errores", o usar el comando `/tests`.
- **Escribir un comentario descriptivo** antes del test (`// debería lanzar excepción si el divisor es cero`): Copilot usa ese comentario como guía.

**Ejemplo real:**

```java
// Clase bajo prueba
public boolean esMayorDeEdad(int edad) {
    return edad >= 18;
}

// Test sugerido por Copilot
@Test
void testEsMayorDeEdad() {
    assertTrue(service.esMayorDeEdad(20));
    assertFalse(service.esMayorDeEdad(17));
}
```

La sugerencia es correcta, pero **incompleta**: no prueba el caso límite `18` ni una edad negativa. Esos tests los agregamos nosotros (ver `UsuarioServiceTest`).

---

### 14.10 Buenas prácticas al usar Copilot para testing

- 🧠 **Revisar siempre las sugerencias:** Copilot puede equivocarse o asumir una lógica que el código no tiene.
- 🧪 **Agregar casos límite y negativos:** no quedarse solo con los ejemplos "felices".
- 📝 **Usar nombres de métodos descriptivos:** `testSumaConNumerosNegativos()` en lugar de `test1()`.
- 🔍 **Validar los valores esperados:** nunca confiar 100% en el `assertEquals` sugerido; calcular el resultado a mano.
- 🔄 **Completar la cobertura** con lo que Copilot no sugiere: validaciones de `null`, errores esperados, ramas del `if` que faltan.
- ♻️ **Ejecutar los tests regularmente** en el flujo de CI/CD para que todo se mantenga en verde.
- 🚫 **No usar Copilot como reemplazo del pensamiento:** es un asistente, no el autor final. Quien decide **qué** probar es el desarrollador, a partir de las reglas de negocio.
- ✅ **No aceptar tests redundantes o triviales:** evaluar si cada test aporta valor.

**Sugerencias típicas que hay que corregir:**

```java
// 1) Valor esperado incorrecto: Copilot "adivina" el resultado
assertEquals(6, calc.restar(3, 2));          // ❌ 3 - 2 es 1

// 2) Repite la lógica del método: si el método está mal, el test también
assertEquals(a - b, calc.restar(a, b));      // ❌

// 3) Test trivial que no prueba nada
assertNotNull(new Calculadora());            // ❌

// 4) Asume un comportamiento que el código no tiene
assertEquals(0, calc.dividir(10, 0));        // ❌ el método lanza ArithmeticException
```

**Truco para saber si un test sirve:** romper el código a propósito (por ejemplo, cambiar `>=` por `>`) y verificar que el test **falle**. Si sigue en verde, el test no está protegiendo nada.

---

### 14.11 Cobertura de código con JaCoCo

**¿Qué es?** JaCoCo (*Java Code Coverage*) es una herramienta que **mide qué partes del código se ejecutan durante los tests**: líneas, ramas (`if`/`else`/`switch`), métodos y clases. Genera reportes en **HTML** (para leer) y **XML/CSV** (para otras herramientas, como SonarQube). Se ejecuta como plugin de Maven o Gradle.

**Configuración en el `pom.xml`** (ya incluida en el proyecto):

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.15</version>
    <executions>
        <execution>
            <id>prepare-agent</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>verify</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

**¿Cómo funciona?**
1. **`prepare-agent`**: antes de ejecutar los tests, engancha un *agente* a la JVM que va anotando qué instrucciones se ejecutan. El resultado crudo queda en `target/jacoco.exec`.
2. **`report`** (en la fase `verify`): convierte ese archivo en reportes legibles dentro de `target/site/jacoco/`.

**Cómo acceder al reporte:**
1. Ejecutar en la terminal:
   ```bash
   ./mvnw clean verify jacoco:report
   ```
   (`jacoco:report` es opcional en este proyecto, porque el reporte ya se genera en `verify`.)
2. En la carpeta `target` aparecen:
   - `jacoco.exec`: los datos crudos de la ejecución.
   - `site/jacoco/`: el reporte.
3. Abrir `target/site/jacoco/index.html` en el navegador (en el IDE: clic derecho sobre el archivo → *Open in → Browser*, o *Reveal in Explorer* y doble clic).

**A tener en cuenta:**
- La versión del plugin tiene que soportar la versión de Java del proyecto (JaCoCo 0.8.11+ para Java 21). Con una versión vieja el reporte sale vacío o falla.
- Si se ejecuta solo `mvn test`, se genera `jacoco.exec` pero **no** el reporte HTML (el `report` está atado a `verify`).

---

### 14.12 Cómo leer el reporte de JaCoCo

El reporte muestra una tabla por package → clase → método. Cada columna significa:

| Columna | Significado |
|---|---|
| **Missed Instructions** | Instrucciones (*bytecode*) que no se ejecutaron en los tests. Menos es mejor. |
| **Cov. (Instructions)** | Porcentaje de instrucciones cubiertas. Es la medida de cobertura general. |
| **Missed Branches** | Decisiones de control de flujo (`if`, `else`, `switch`, `? :`) que no se recorrieron. |
| **Cov. (Branches)** | Cobertura de decisiones: indica si los tests exploran **todas las rutas posibles**. |
| **Missed Cxty** | *Complejidad ciclomática* no cubierta: cantidad de caminos lógicos sin probar. A mayor complejidad, más tests hacen falta. |
| **Missed Lines** | Líneas de código fuente que no se ejecutaron. Es lo más fácil de ver para el desarrollador. |
| **Missed Methods** | Métodos que ningún test llamó. |
| **Missed Classes** | Clases que no se ejecutaron en absoluto: un riesgo potencial. |

Al hacer clic en una clase se ve el código coloreado:
- 🟩 **Verde:** línea ejecutada por los tests.
- 🟨 **Amarillo:** rama parcialmente cubierta (por ejemplo, se probó el `if` pero no el `else`).
- 🟥 **Rojo:** línea que nunca se ejecutó.

**Ejemplo en el proyecto:** en `Calculadora` el método `multiplicar` **no tiene test a propósito**. En el reporte aparece en rojo, con 1 *Missed Method* y 1 *Missed Line*. Escribir su test y regenerar el reporte es un buen ejercicio para ver cómo cambia.

**A tener en cuenta:**
- **100% de cobertura no significa que no haya bugs:** solo indica que el código se *ejecutó*, no que se haya *verificado* correctamente. Un test sin aserciones también suma cobertura.
- La **cobertura de ramas** es más exigente y más útil que la de líneas.
- Usar la cobertura para encontrar **qué falta probar**, no como un número a alcanzar a cualquier costo.

---

### 14.13 SonarQube + JaCoCo: cómo trabajan juntos

SonarQube hace el **análisis estático** (revisa el código **sin ejecutarlo**) y detecta:
- **Bugs:** errores lógicos que pueden causar fallos en tiempo de ejecución.
- **Code Smells:** problemas de diseño o estilo que no son bugs, pero dificultan el mantenimiento.
- **Duplicaciones:** código repetido que aumenta el esfuerzo de mantenimiento.
- **Cobertura de tests:** porcentaje del código ejecutado por las pruebas.

SonarQube **no ejecuta los tests**, así que la cobertura la obtiene de JaCoCo:

```
mvn verify  ──>  JaCoCo genera target/site/jacoco/jacoco.xml
mvn sonar:sonar  ──>  SonarQube lee ese XML y muestra la cobertura junto al resto del análisis
```

(Los conceptos generales de SonarQube están en la [sección 13.12](#1312-análisis-estático-con-sonarqube) de la Clase 13.)

---

### 14.14 Configuración de SonarQube en Maven

**1) Elegir dónde corre SonarQube:**
- **Servidor local:** con Docker, `docker run -d --name sonarqube -p 9000:9000 sonarqube:community`, y entrar a http://localhost:9000.
- **SonarQube Cloud** (https://sonarcloud.io): registrarse con la cuenta de GitHub y crear una **organización** vinculada a GitHub. Es gratis para repositorios públicos.

**2) Agregar el plugin en el `pom.xml`** (ya incluido):

```xml
<plugin>
    <groupId>org.sonarsource.scanner.maven</groupId>
    <artifactId>sonar-maven-plugin</artifactId>
    <version>5.8.0.7211</version>
</plugin>
```

Agrega el comando `mvn sonar:sonar`. No se ejecuta solo: no cambia nada al correr `mvn test` o `mvn verify`.

**3) Agregar las propiedades en `<properties>` del `pom.xml`** (ya incluidas):

```xml
<sonar.projectKey>SpringBoot-Mongo-Test</sonar.projectKey>
<sonar.projectName>SpringBoot-Mongo-Test</sonar.projectName>
<sonar.java.binaries>target/classes</sonar.java.binaries>
<sonar.sourceEncoding>UTF-8</sonar.sourceEncoding>
<sonar.coverage.jacoco.xmlReportPaths>target/site/jacoco/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
```

| Propiedad | Para qué sirve |
|---|---|
| `sonar.projectKey` | Identificador único del proyecto en SonarQube |
| `sonar.projectName` | Nombre que se muestra en el panel |
| `sonar.java.binaries` | Dónde están las clases compiladas (algunas reglas de Java las necesitan) |
| `sonar.sourceEncoding` | Codificación de los archivos fuente |
| `sonar.coverage.jacoco.xmlReportPaths` | Ruta del reporte XML de JaCoCo para importar la cobertura |

**4) Crear un token:** en SonarQube, **Mi cuenta → Seguridad → Generar tokens**, ingresar un nombre y copiar el token (se muestra una sola vez).

**5) Ejecutar el análisis:**

```bash
# Servidor local
./mvnw clean verify sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token=TU_TOKEN

# SonarQube Cloud
./mvnw clean verify sonar:sonar -Dsonar.host.url=https://sonarcloud.io -Dsonar.organization=mi-organizacion -Dsonar.token=TU_TOKEN
```

En PowerShell conviene poner cada `-D...` entre comillas: `"-Dsonar.token=TU_TOKEN"`.

**6) Acceder al reporte:** al terminar, Maven muestra el link al panel del proyecto.

**A tener en cuenta:**
- `sonar.organization` solo se usa en **SonarQube Cloud**.
- En versiones nuevas el parámetro es **`sonar.token`**; `sonar.login` quedó obsoleto.
- Correr siempre `verify` **antes** de `sonar:sonar` (en el mismo comando, como arriba). Si no, no hay reporte de JaCoCo y la cobertura aparece en 0%.
- **Nunca** guardar el token en el `pom.xml` ni subirlo al repositorio.

---

### 14.15 Cómo leer el informe de SonarQube

Lo más importante del panel:

| Métrica | Qué indica |
|---|---|
| **Bugs / Reliability** | Errores que impactan en el funcionamiento |
| **Code Smells / Maintainability** | Malas prácticas o diseño mejorable |
| **Coverage** | % del código cubierto por tests (viene de JaCoCo) |
| **Duplications** | % de código repetido innecesariamente |
| **Security Hotspots** | Código sensible que una persona debe revisar |

- Cada problema tiene una **severidad** (de *Blocker* / *High* a *Info* / *Low*) y una explicación de **por qué** es un problema y **cómo** solucionarlo.
- El **Quality Gate** indica si el proyecto aprueba (*Passed*) o no (*Failed*) las condiciones mínimas de calidad.
- SonarQube distingue **New Code** (lo que cambió recientemente) de **Overall Code** (todo el proyecto). La idea es que el código nuevo siempre esté limpio, aunque el viejo tenga deuda.

🔍 **Consejo:** usar el informe para **priorizar mejoras**. No todos los *smells* son críticos, pero todos deben ser revisados. Empezar por bugs y vulnerabilidades, luego cobertura, luego code smells.

---

### 14.16 Tests en CI/CD con GitHub Actions

Para que los tests no dependan de que alguien se acuerde de ejecutarlos, se integran en el **pipeline de CI/CD**. El proyecto incluye [.github/workflows/ci.yml](.github/workflows/ci.yml):

```yaml
name: Java CI
on:
  push:
  pull_request:

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4          # descarga el código
      - name: Configurar Java
        uses: actions/setup-java@v4        # instala Java
        with:
          distribution: temurin
          java-version: '21'
          cache: maven
      - name: Ejecutar tests
        run: |
          chmod +x mvnw
          ./mvnw -B verify
```

| Elemento | Para qué sirve |
|---|---|
| `on` | Qué eventos disparan el pipeline (cada `push` y cada Pull Request) |
| `runs-on` | La máquina virtual donde corre (Ubuntu ya trae Docker, así que Testcontainers funciona) |
| `actions/checkout` | Descarga el código del repositorio |
| `actions/setup-java` | Instala la versión de Java indicada; `distribution` es obligatorio y `cache: maven` acelera las siguientes ejecuciones |
| `./mvnw -B verify` | Compila y ejecuta todos los tests (`-B` = modo sin interacción, para logs limpios) |

📌 **Si un test falla, el pipeline se detiene** y queda en rojo.
🔒 Esto **protege la calidad del código antes de hacer merge**: configurando *branch protection* en GitHub se puede impedir que se mezcle un Pull Request con el pipeline en rojo.

El workflow tiene además un paso **opcional** que envía el análisis a SonarQube Cloud, solo si se configuró el secret `SONAR_TOKEN` en el repositorio (*Settings → Secrets and variables → Actions*).

**A tener en cuenta:**
- La versión de Java del pipeline tiene que coincidir con la del `pom.xml` (21 en este proyecto).
- Usar `verify` y no solo `test` si se quieren ejecutar también las pruebas de integración (`*IT`).
- Usar versiones actuales de las *actions* (`@v4`): las versiones viejas como `@v2` dejaron de recibir soporte.

---

### 14.17 Errores comunes

| Problema | Causa / solución |
|---|---|
| El test no aparece / no se ejecuta | El método es `private` o no tiene `@Test`, o la clase no termina en `Test`. |
| `assertEquals` muestra el mensaje "al revés" | Se invirtió el orden: primero va el **esperado**, después el **real**. |
| `NullPointerException` en el test | Falta inicializar el objeto: usar `@BeforeEach`. |
| `@Mock` / `@InjectMocks` quedan en `null` | Falta `@ExtendWith(MockitoExtension.class)` en la clase. |
| `UnnecessaryStubbingException` | Se definió un `when(...)` que el test nunca usa: borrarlo. |
| No se puede llamar a un método package-private | El test no está en el mismo package que la clase. |
| El reporte de JaCoCo no existe | Se ejecutó `mvn test` en lugar de `mvn verify`. |
| Cobertura en 0% en SonarQube | No se ejecutó `verify` antes de `sonar:sonar`, o la ruta de `sonar.coverage.jacoco.xmlReportPaths` es incorrecta. |
| `Not authorized` en SonarQube | Token incorrecto o vencido; generar uno nuevo. |

---

### 14.18 Ejercicio práctico

**Contexto:** en el equipo se está impulsando la automatización de tareas repetitivas para ganar tiempo en el desarrollo. Una de las estrategias es usar GitHub Copilot para generar pruebas unitarias con JUnit de manera más rápida y estructurada.

**Consigna:** usar Copilot para generar una prueba unitaria de un método simple, verificar la lógica propuesta, adaptarla si es necesario y ejecutar el test para asegurarse de que pase.

**Paso a paso:**
1. Crear la clase `Calculadora` con un método `int restar(int a, int b)`.
2. Crear la clase `CalculadoraTest` en el mismo package, dentro de `src/test/java`.
3. Escribir `@Test void testRestar()` y posicionar el cursor dentro del método.
4. Copilot sugerirá algo como:
   ```java
   Calculadora calc = new Calculadora();
   assertEquals(1, calc.restar(3, 2));
   ```
5. Aceptar o ajustar el test.
6. Agregar un segundo test con números negativos o ceros.
7. Ejecutar los tests y verificar los resultados.

La solución está en [CalculadoraTest.java](src/test/java/com/app/back/springbootmongotest/clase14/CalculadoraTest.java).

**Para seguir practicando:**
- Escribir con Copilot el test de `multiplicar` (incluyendo multiplicar por cero y por negativos), ejecutar `./mvnw verify` y comprobar en el reporte de JaCoCo que `Calculadora` pasó a 100%.
- Pedirle a Copilot Chat tests para `UsuarioService.normalizarEmail` y comparar con los del proyecto: ¿qué casos sugirió y cuáles faltaron?
- Romper a propósito `esMayorDeEdad` (cambiar `>=` por `>`) y ver qué test lo detecta.
- Subir el proyecto a GitHub y ver ejecutarse el pipeline de GitHub Actions.

---

### 14.19 Resumen

- **JUnit 5** permite escribir y ejecutar pruebas unitarias reales que validan el comportamiento de los métodos.
- Los tests van en `src/test/java`, **en el mismo package** que la clase que prueban.
- Las **aserciones** (`assertEquals`, `assertTrue`, `assertNull`, `assertThrows`, etc.) son las verificaciones del test.
- **Buenas prácticas:** patrón AAA, una lógica por test, nombres descriptivos, `@BeforeEach`, casos positivos **y** negativos.
- **Mockito** aísla las dependencias externas con `mock()` y verifica las interacciones con `verify()`.
- **GitHub Copilot** asiste en la generación de tests, pero sus sugerencias **siempre** se revisan, se validan y se completan.
- **JaCoCo** mide la cobertura y muestra qué código no fue probado; **SonarQube** la integra a su análisis de calidad.
- **GitHub Actions** ejecuta los tests automáticamente en cada cambio y detiene el pipeline si algo falla.

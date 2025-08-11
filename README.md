# 🏪 Store App

Este proyecto está compuesto por **dos microservicios**:

```text
| Microservicio         | Descripción                 |
|-----------------------|-----------------------------|
| **product-service**   | Gestión de productos        |
| **inventary-service** | Gestión de inventario       |
```

Ambos están contenidos dentro de un proyecto **multi-módulo Maven** llamado `store`.

---

## 📂 Estructura del proyecto

```text
store/
├── pom.xml               # POM padre (configuración y dependencias comunes)
├── product-service/      # Servicio de productos
│   └── pom.xml
└── inventary-service/    # Servicio de inventario
    └── pom.xml
```

---

## 🛠 Tecnologías utilizadas

- **Java** 21  
- **Spring Boot** 3.3.4  
- **Spring Data JPA**  
- **Spring Web MVC**  
- **Spring Cloud** 2023.0.3  
- **Feign Client** para comunicación entre microservicios  
- **MySQL** como base de datos  
- **Lombok** para reducir código *boilerplate*  
- **Spring HATEOAS JSON:API** para hipermedia  
- **SpringDoc OpenAPI** para documentación de API  
- **JUnit 5**, **Mockito**, **Testcontainers**, **WireMock** para pruebas  

---

## 📦 Módulos

```text
| Módulo                | Descripción                                                                                                                                      |
|-----------------------|--------------------------------------------------------------------------------------------------------------------------------------------------|
| store (POM padre)     | Define versiones, dependencias y configuración común de Spring Boot y Spring Cloud. Incluye JPA, Web, MySQL, HATEOAS, OpenAPI y plugins comunes. |
| product-service       | Microservicio REST para gestión de productos. Hereda configuración del POM padre.                                                                |
| inventary-service     | Microservicio REST para gestión de inventario. Se comunica con product-service vía Feign Client.                                                 |
```

---

# ▶️ Ejecución en modo local


#### Clonar repositorio
```bash
git clone https://github.com/felipevasquez74/store.git
cd store
```

#### Compilar y empaquetar
```bash
mvn clean install
```

#### Levantar base de datos MySQL en Docker
```bash
docker run --name mi-mysql \
  -e MYSQL_ROOT_PASSWORD=admin \
  -e MYSQL_USER=admin \
  -e MYSQL_PASSWORD=admin \
  -e MYSQL_DATABASE=store_db \
  -p 3306:3306 \
  -d mysql:8.0
```

#### Ejecutar microservicios
```bash
cd product-service
mvn spring-boot:run
```

```bash
cd ../inventary-service
mvn spring-boot:run
```

# ▶️ Ejecución con Docker Compose

#### Clonar repositorio
```bash
git clone https://github.com/felipevasquez74/store.git
cd store
```

#### Ejecutar docker-compose.yml
```bash
docker compose up --build

docker ps

```

Debes ver algo asi 

```text
CONTAINER ID   IMAGE                 COMMAND                  STATUS         PORTS
xxxxx          product-service       "java -jar app.jar"      Up 2 minutes   0.0.0.0:8080->8080/tcp
yyyyy          inventary-service     "java -jar app.jar"      Up 2 minutes   0.0.0.0:8081->8080/tcp
zzzzz          mysql:8.0             "docker-entrypoint.s…"   Up 2 minutes   0.0.0.0:3306->3306/tcp
```
Acceder a los microservicios

Product Service → http://localhost:8080
Inventary Service → http://localhost:8081

## 📖 Documentación de APIs (Swagger)

Cada microservicio expone su propia documentación interactiva con **Swagger UI**.

- **Product Service**  
  URL: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)  
  Puerto: `8080`

- **Inventory Service**  
  URL: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)  
  Puerto: `8081`

> 💡 Puedes abrirlos en el navegador mientras el servicio esté ejecutándose para probar los endpoints directamente.

## 📬 Colección de Postman

Para facilitar las pruebas de los endpoints, se incluye una colección de Postman.

- Archivo: [`product_inventory_collection.json`](docs/postman/product_inventory_collection.json)
- Cómo usar:
  1. Abrir Postman.
  2. Ir a **Import**.
  3. Seleccionar el archivo `.json` de la colección.
  4. Probar los endpoints usando los entornos adecuados.

> 💡 Incluye tanto el **Product Service** como el **Inventory Service** con las rutas documentadas en Swagger.



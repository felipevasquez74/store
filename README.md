## Decisión sobre la Base de Datos

Para esta solución, se eligió una base de datos relacional SQL, específicamente **MYSQL**, para ambos microservicios.  

### Justificación de la elección

1. Relación clara entre entidades
   - El microservicio de Inventario depende de `producto_id`, que referencia a un producto en el microservicio de Productos.  
   - Aunque las tablas sean independientes, el modelo relacional se adapta perfectamente a este tipo de relación y asegura integridad lógica.

2. Consultas estructuradas y transacciones
   - Se requiere paginación eficiente, consultas por ID y actualizaciones atómicas.  
   - MYSQL ofrece soporte robusto para transacciones y operaciones seguras en entornos concurrentes.

3. Madurez y ecosistema 
   - MYSQL cuenta con un ecosistema maduro, integración fluida con ORMs (Hibernate, JPA, etc.) y soporte completo para migraciones.
   - Fácil de contenerizar con Docker y compatible con entornos CI/CD.

4. Escalabilidad y mantenibilidad
   - MYSQL permite escalar horizontal y verticalmente, soportando réplicas, particionamiento y optimización de consultas.
   - Ideal para crecer con el sistema manteniendo consistencia fuerte.

5. Alternativas evaluadas 
   - **NoSQL** (MongoDB): más adecuadas para datos no estructurados o esquemas variables. Aquí el esquema es fijo y relacional.  
   - **En memoria** (Redis): excelente para *cache* o pruebas rápidas, pero no garantiza persistencia como almacenamiento primario.

---

**Arquitectura propuesta:**

- **Microservicio de Productos** → MYSQL (`product`: `id`, `name`, `price`)
- **Microservicio de Inventario** → MYSQL (`inventary`: `product_id`, `quantity`)
- Comunicación entre microservicios vía **HTTP JSON API** con autenticación por API Key.
- Integridad de datos validada a nivel de aplicación.


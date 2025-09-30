# **Person-Service Documentation**

The **Person-Service** provides a RESTful API for managing **Person** entities, including their associated **Address** records. It leverages **Hibernate** for persistence, supports **multi-tenancy**, and integrates **auditing** to ensure data integrity and traceability.

---

## **1. Data Model**

### **Person DTO**

```java
public record Person (
    @Nullable String id,
    @Nullable Long version,
    @NotNull @Size(min = 3, max = 255) String firstName,
    @NotNull @Size(min = 3, max = 255) String lastName,
    List<Address> address
) {}
```

### **Address DTO**

```java
public record Address (
    @Nullable String id,
    @Nullable Long version,
    @NotNull @Size(min = 3, max = 255) 
    @Pattern(regexp = "[a-zA-Z0-9.\\s]*") String street,
    @NotNull @Size(min = 3, max = 255) String city
) {}
```

* **Validation** is applied using annotations like `@NotNull`, `@Size`, and `@Pattern`.
* **Entities** are mapped to the underlying database via Hibernate.
* **Auditing** ensures `id` and `version` are tracked automatically.

---

## **2. Controller Endpoints**

The controller exposes several REST endpoints to interact with `Person` resources:

| **HTTP Method** | **Endpoint**     | **Description**                                        | **Parameters**                                  |
| --------------- | ---------------- | ------------------------------------------------------ | ----------------------------------------------- |
| `GET`           | `/person/{id}`   | Retrieve a person by their unique identifier.          | `id` (PathVariable)                             |
| `GET`           | `/person`        | Search persons based on filter criteria.               | `PersonSearch` (ModelAttribute), `page`, `size` |
| `GET`           | `/person/street` | Find persons by street name.                           | `street`, `page`, `size`                        |
| `POST`          | `/person`        | Save a new or updated person record.                   | Request body: `Person` (validated DTO)          |
| `GET`           | `/person/name`   | Retrieve a person using the custom "sayMyName" method. | `name` (RequestParam)                           |

---

## **3. Features**

### **Multi-Tenancy**

* Each request is executed within a **tenant context**, ensuring data isolation.
* Tenant resolution is typically managed via **headers** or **security context**.

### **Auditing**

* Every entity includes:

    * **`id`**: unique identifier.
    * **`version`**: version number for optimistic locking.
* Audit fields (e.g., createdBy, createdDate, lastModifiedBy, lastModifiedDate) are maintained automatically.

### **Hibernate Integration**

* Entities mirror the DTO structure (`Person`, `Address`).
* Schema evolves automatically through **Hibernate ORM**.
* **Indexes** should be defined on searchable attributes (e.g., `firstName`, `lastName`, `street`).

### **REST Calls**

* The service communicates over **JSON**.
* Consumes and produces `application/json` media type.
* Compatible with **standard REST clients** (e.g., Postman, curl, frontend apps).

---

## **4. Usage Notes**

* ⚠️ **Indexing**: Ensure all searchable fields are indexed for performance.
* 🛡️ **Validation**: Input DTOs are validated before persistence.
* 🔄 **Optimistic Locking**: The `version` field prevents concurrent write conflicts.
* 🌍 **Extensibility**: Can be extended with additional search criteria or projections.



## 🎬 MovieList Backend API

Backend REST desarrollado con Java 21 + Spring Boot + MongoDB Atlas, que permite a los usuarios gestionar listas de películas (pendientes y vistas) utilizando datos obtenidos desde The Movie Database (TMDB).

La aplicación está desplegada en la nube mediante Render y utiliza autenticación JWT (Bearer Token).

## 🌍 URL de Producción

https://movie-list-bcknd-spring.onrender.com

## 🧱 Tecnologías Utilizadas

- Java 21

- Spring Boot 3.5

- Spring Security (JWT)

- MongoDB Atlas

- Docker (multi-stage build)

- Render (deploy en la nube)

- The Movie Database API (TMDB)

## 🛠️ Arquitectura
Cliente (Postman / Frontend)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;↓
        
Spring Boot API<br>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;↓
        
JWT Security Filter<br>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;↓
        
MongoDB Atlas (users + movies_cache)<br>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;↓
        
TMDB API (solo cuando la película no está en caché)


El sistema implementa un mecanismo de caché (movies_cache) para evitar llamadas repetidas a TMDB.

## 🔐 Autenticación

La API utiliza JWT (Bearer Token).

Flujo de autenticación

1. Realizar login:
POST /auth/login

2. Copiar el token recibido en la respuesta.

3. Incluirlo en cada petición protegida:

Authorization: Bearer <TOKEN>

## 📌 Endpoints Principales
## 🔓 Públicos

- Health Check
- GET /health

- Login
- POST /auth/login

- Buscar películas en TMDB
- GET /tmdb/search?query=matrix&page=1

## 🔐 Requieren autenticación

- Obtener lista pendiente
- GET /users/me/pending?page=0&size=5

- Obtener lista vistas
- GET /users/me/watched?page=0&size=5

- Añadir película a pendientes
- POST /users/me/pending

Body:
{
"tmdbId": 603
}

- Marcar película como vista
- PATCH /users/me/watched/{tmdbId}

## 👑 Solo ADMIN

- Crear película manualmente en caché
- POST /movies

- Actualizar película
- PUT /movies/{id}

- Eliminar película
- DELETE /movies/{id}

Nota: No se permite eliminar una película si está referenciada en alguna lista de usuario.

## 🗄️ Modelo de Datos

- ## Usuario

* id
* username
* password
* role (USER / ADMIN)
* pendingMovies
* watchedMovies

- ## movies_cache

* id
* tmdbId
* title
* overview
* releaseDate
* genres
* posterPath
* createdAt
* updatedAt

## 🔄 Flujo Funcional

1. Usuario inicia sesión.
2. Usuario busca películas usando TMDB.
3. Usuario selecciona una película.
4. Si la película no existe en movies_cache, se obtiene desde TMDB y se guarda automáticamente.
5. La película se añade a la lista del usuario (pending o watched).
6. Las futuras consultas usan la caché en MongoDB.

## ⚙️ Variables de Entorno

La aplicación requiere las siguientes variables:

- MONGODB_URI
- JWT_SECRET
- TMDB_API_KEY
- JWT_EXPIRATION_MINUTES
- TMDB_BASE_URL

No se incluyen valores reales en el repositorio por motivos de seguridad.

## 🐳 Ejecución en Local con Docker

1. Construir imagen:

- docker build -t movielist-api .
  
2. Ejecutar contenedor:
   
- docker run -p 8080:8080
-e MONGODB_URI=...
-e JWT_SECRET=...
-e TMDB_API_KEY=...
movielist-api

La aplicación estará disponible en:

- http://localhost:8080

## ☁️ Despliegue en Render

- Tipo: Web Service
- Runtime: Docker
- Health Check Path: /health
- Variables de entorno configuradas en el panel de Render

## 📦 Estructura del Proyecto

- auth/
- movies/
- users/
- security/
- config/

Separación clara por capas:

- Controller
- Service
- Repository
- Security
- Configuración

## 🛡️ Seguridad

- Autenticación stateless (JWT)
- Roles USER y ADMIN
- Endpoints protegidos por rol
- Variables sensibles gestionadas por entorno

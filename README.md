# RetroGameHub - Tu Tienda De Videojuegos Retro

## Descripción del Proyecto
**RetroGameHub** es una plataforma online dedicada a la venta y comparación de videojuegos retro. Incluye un sistema de compra con filtros avanzados, inicio de sesión con Google y pasarela de pago con Stripe.

---

## Características Principales

### 🛒 Sistema de Venta de Videojuegos Retro
- Catálogo de videojuegos retro organizado por:
  - **Género**
- Sistema de **carrito de compra** con cálculo automático del precio total
- **Sistema de pago** integrado con Stripe para procesar transacciones de forma segura

### 🔍 Filtros y Búsqueda Avanzados
- **Filtros por género** (acción, aventura, RPG, deportes, etc.)
- **Filtros por rango de precio** (mínimo y máximo)
- **Búsqueda por nombre** para encontrar títulos específicos rápidamente

### 🔐 Sistema de Autenticación
- **Inicio de sesión con Google** para que los usuarios accedan de forma rápida y segura
- Gestión de sesión y datos de usuario almacenados en la base de datos

---

## 🛠 Tecnologías Utilizadas
- **Base de datos**: postgresql   
- **Frontend**: Next.js  
- **Backend**: Kotlin (Ktor)  
- **Autenticación**: OAuth 2.0 (Google)  
- **Pasarela de pago**: Stripe  
- **Dockerización**: Podman Desktop / Docker Compose  
- **Control de versiones**: GitHub  

---

## 🌐 Enlaces de Despliegue (Render)
- **Backend (API)**: https://api-ktor.onrender.com  
- **Frontend**: https://next-js-mjsb.onrender.com  

Puedes acceder a la aplicación directamente desde estos enlaces sin necesidad de montar nada en local.

---

## 📌 Ejecución en Local

1. **Descargar y descomprimir el ZIP del proyecto**  
   - Asegúrate de tener el repositorio comprimido en un archivo `.zip` o clonado desde GitHub.  
   - Descomprímelo en la ubicación deseada de tu máquina.

2. **Navegar a la carpeta `compose`**  
   - Dentro de la carpeta principal del proyecto, localiza la carpeta llamada `compose`.

3. **Ejecutar con Docker Compose**  
   - Abre una terminal o consola y navega hasta la carpeta `compose` (donde está el `docker-compose.yml`).
   - Ejecuta:
     ```bash
     docker compose up -d --build
     ```
   - Esto levantará los contenedores de:
     - **SostgreSQL** (base de datos)
     - **Backend (Ktor)**
     - **Frontend (Next.js)**

4. **Verificar levantamiento de contenedores**  
   - Comprueba con:
     ```bash
     docker ps
     ```
   - Debes ver tres contenedores activos: uno para la base de datos, otro para el backend y otro para el frontend.

5. **Acceder en el navegador**  
   - Abre tu navegador y visita:
     ```
     http://localhost
     ```
   - El sistema de enrutamiento dentro de los contenedores hará que el frontend esté disponible en el puerto 80 (o el que hayas configurado). Si tienes otro puerto mapeado, ajústalo (por ejemplo, `http://localhost:3000`).

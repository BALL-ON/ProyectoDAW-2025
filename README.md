# Nombre de Vuestro Proyecto

## Tecnologías
* **Backend:** Java (Spring Boot)
* **Frontend:** HTML5, CSS3, JavaScript (Vanilla)
* **Base de datos:** MySQL

## Instalación y Puesta en Marcha

### 1. Clonar el repositorio
git clone [(https://github.com/BALL-ON/ProyectoDAW-2025.git)]

### 2. Base de Datos
Crea una base de datos en MySQL llamada `bd_ballon`.

### 3. Backend
1.  Entra en la carpeta `/backend`.
2.  Abre el proyecto en IntelliJ/Eclipse.
3.  Edita `application.properties` con tu usuario/pass de MySQL.
4.  Haz click derecho en la carpeta  `backend > Run as > Spring Boot App`. Dará un error, pero no te alarmes, es normal, esta primera ejecución sirve para que se inicialice la preconfiguración necesaria. 
5.  Ahora debe ir a `Run > Run Configurations... > Spring Boot App > backend-BackendApplication > Enviroment`. 
6.  Una vez allí pulsar en `Add...` y añadir las siguientes variables:
                    
                    MAIL_PASSWORD valor: ogicejcpwyicbiyn
                    MAIL_USERNAME valor: ballontfg2026@gmail.com

7.  Una vez creadas las variables se debe pulsar en `Apply > Close ` y volver a hacer el recorrido de `backend > Run as > Spring Boot App`. El servidor iniciará correctamente.

### 4. Frontend
1.  Entra en la carpeta `/frontend`.
2.  Ejecuta el comando `npm install`
3.  Ejecuta el comando `ng serve`
# BALL-ON

## Tecnologías
* **Backend:** Java (Spring Boot)
* **Frontend:** HTML5, CSS3, Typescript (ANGULAR)
* **Base de datos:** MySQL

## Instalación y Puesta en Marcha

### 1. Clonar el repositorio
git clone [(https://github.com/BALL-ON/ProyectoDAW-2025.git)]

### 2. Base de Datos
Crea una base de datos en MySQL llamada `bd_ballon`.
Crear un usuario llamado `usuario` con contraseña `usuario` con `ALL PRIVILEGES`.

### 3. Backend
1.  Abre SpringToolSuite.

2.  Entra en la carpeta `ProyectoDAW-2025`.

3.  Puedes utilizar directamente el usuario creado anteriormente en la base de datos o editar el `application.properties` con tu usuario/pass de MySQL (este debe tener `ALL PRIVILEGES`).

4.  Si al abrir el proyecto no aparece la carpeta `backend` se le debe dar a [click derecho en Package Explorer → Import... → Existing Projects into Workspace → Next → Browse... → backend]

5.  Debemos comprobar que tenemos instalado el jdk-17, ya que el proyecto está desarrollado en el mismo, y sería óptimo que el Spring Boot también lo tenga. Para comprobarlo debemos ir a [`Window → Preferences... → Java → Installed JREs`], y ahí seleccionar `jdk-17`, y en el caso de que no lo tenga instalado, podrá encontrar este en la base del proyecto, deberá ir a a[`Add... → Standard VM → Next → Directory...`], y entonces deberá seleccionar la carpeta jdk-17 de la base del proyecto.

6.  Haz click derecho en la carpeta  [`backend → Run as → Spring Boot App`].
7.  Si da un error de ejecución y por consola aparece lo siguiente al final:

`***************************
APPLICATION FAILED TO START
***************************

Description:

Parameter 3 of constructor in com.ballon.backend.services.UsuarioService required a bean of type 'com.ballon.backend.mapper.UsuarioMapper' that could not be found.

Action:

Consider defining a bean of type 'com.ballon.backend.mapper.UsuarioMapper' in your configuration.`

7.  No te preocupes, este error suele darse cuando el SpringToolSuite instalado en el ordenador es demasiado moderno, sigue los siguientes pasos:

    7.1. [`Clic derecho en el proyecto → Run As → Maven build... → en "Goals" escribe lo siguiente: 'clean install -DskipTests' → Run`]
    
    7.2. Forzar actualización del proyecto: [`Clic derecho en el proyecto → Maven → Update Project → marca "Force Update of Snapshots/Releases" → OK`]


8.  En el caso de que dé un error, no te alarmes, es normal, significa que las variables de entorno no se han configurado. Se debe hacer lo de los pasos 6., 7. y 8.; si no te ha dado el error y se ha ejecutado correctamente no es necesario seguir estos pasos.
9.  Ahora debe ir a `Run → Run Configurations... → Spring Boot App → backend-BackendApplication → Enviroment`. 
10.  Una vez allí pulsar en `Add...` y añadir las siguientes variables:
                    
                                            MAIL_PASSWORD valor: ogicejcpwyicbiyn
                                            MAIL_USERNAME valor: ballontfg2026@gmail.com

8.  Una vez creadas las variables se debe pulsar en `Apply → Close ` y volver a hacer el recorrido de `backend → Run as → Spring Boot App`. El servidor iniciará correctamente.

### 4. Frontend
1. Abre Visual Studio Code.
2.  Se debe tener instalado previamente node.js, si no está instalado, lo puedes descargar desde el siguiente enlace: https://nodejs.org/es/download (docker y npm).
3.  Navega a la carpeta `/frontend` desde VSC.
4.  Ejecuta el comando `npm install`, si te da el siguiente error ejecuta el paso 5.; sino, puedes saltártelo: 


    npm : No se puede cargar el archivo C:\Program Files\nodejs\npm.ps1 porque la ejecución de scripts está deshabilitada en este sistema. Para obtener más información, consulta el tema 

    about_Execution_Policies en https:/go.microsoft.com/fwlink/?LinkID=135170.

    En línea: 1 Carácter: 1

    + npm install

    + ~~~

        + CategoryInfo          : SecurityError: (:) [], PSSecurityException

        + FullyQualifiedErrorId : UnauthorizedAccess

    
5. Abre el PowerShell como Administrador y ejecuta el siguiente comando:

                                Set-ExecutionPolicy RemoteSigned -Scope CurrentUser

6.  Ejecuta el comando `ng serve`
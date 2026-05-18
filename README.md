# BALL-ON

## Tecnologías
* **Backend:** Java (Spring Boot)
* **Frontend:** HTML5, CSS3, Typescript (ANGULAR)
* **Base de datos:** MySQL

## Instalación y Puesta en Marcha

### 1. Clonar el repositorio
git clone https://github.com/BALL-ON/ProyectoDAW-2025.git (Si tienes un archivo .zip con el proyecto, este paso no es necesario)

### 2. Base de Datos
Crea una base de datos en MySQL llamada `bd_ballon`.
Si quieres puedes importar la base de datos llamada "bd_ballon_final.sql" a `bd_ballon`, la cual ya tiene cosas creadas por el equipo.
Crear un usuario llamado `usuario` con contraseña `usuario` con `ALL PRIVILEGES`.

### 3. Backend
1.  Abre SpringToolSuite.

2.  Entra en la carpeta [`ProyectoDAW-2025`].

3.  Si al abrir el proyecto no aparece la carpeta [`backend`] se le debe dar a [`click derecho en Package Explorer → Import... → Existing Projects into Workspace → Next → Browse... → backend`]

4.  Debemos comprobar que tenemos instalado el jdk-17, ya que el proyecto está desarrollado en el mismo, y sería óptimo que el Spring Boot también lo tenga. Para comprobarlo debemos ir a [`Window → Preferences... → Java → Installed JREs`], y ahí seleccionar `jdk-17`, y en el caso de que no lo tenga instalado, podrá descargarlo desde el siguiente enlace: (https://drive.google.com/file/d/1alk02BUQ1V6Cl5CaMyGJs2CXLaHhIZka/view?usp=sharing) (tenía subidp el que utilizo yo en mi entorno a drive, con instalar jdk-17 de cualquier sitio valdría, pero ya que lo tenía subido lo comparto), deberá ir a a[`Add... → Standard VM → Next → Directory...`], y entonces deberá seleccionar la carpeta jdk-17 de la base del proyecto.

5. Debemos comprobar si las variables de entorno están configuradas correctamente. Para ello sigue los siguientes pasos:

        5.1 Debemos ir a [`Run → Run Configurations... → Spring Boot App → backend-BackendApplication → Enviroment`]. 

        5.2 Una vez allí debemos ver si están creadas las siguientes variables: pulsar en `Add...` y añadir las siguientes variables:

                                            MAIL_PASSWORD valor: ogicejcpwyicbiyn
                                            MAIL_USERNAME valor: ballontfg2026@gmail.com

        5.3 Si no están creadas pulsa en `Add...` y añádelas.

        5.4 Una vez creadas haz [`Apply → Close `]

6.  Haz [`click derecho en la carpeta → backend → Run as → Spring Boot App`].
7.  Si da un error de ejecución y por consola aparece lo siguiente al final sigue los pasos 7.1, 7.2 y 7.3:

                        `***************************
                        APPLICATION FAILED TO START
                        ***************************

                        Description:

                        Parameter 3 of constructor in com.ballon.backend.services.UsuarioService required a bean of type 'com.ballon.backend.mapper.UsuarioMapper' that could not be found.

                        Action:

                        Consider defining a bean of type 'com.ballon.backend.mapper.UsuarioMapper' in your configuration.`

    7.1 [`Clic derecho en el proyecto → Run As → Maven build... → en "Goals" escribe lo siguiente: 'clean install -DskipTests' → Run`]
    
    7.2 Forzar actualización del proyecto: [`Clic derecho en el proyecto → Maven → Update Project → marca "Force Update of Snapshots/Releases" → OK`]

    7.3 Y con esta configuración ya debería funcionar todo correctamente.

8. Si te da un error de ejecución y en la consola aparece lo siguiente, sigue los pasos :

                            `java.lang.ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag :: UNKNOWN`

    8.1 Este error sale porque te has saltado el paso anterior de tener instalado el jdk-17. Realiza el paso 5.

    8.2 Entra en [`Clic derecho en el proyecto → Run As → Maven build... → en "Goals" escribe lo siguiente: 'clean install -DskipTests' → Run`]

    8.3 Y con esto ejecuta otra vez [`Clic derecho en el proyecto → backend → Run as → Spring Boot App`]


### 4. Frontend
1. Abre Visual Studio Code.

2.  Se debe tener instalado previamente node.js, si no está instalado, lo puedes descargar desde el siguiente enlace: https://nodejs.org/es/download (docker y npm).

3.  Navega a la carpeta `/frontend` desde VSC.

4.  Ejecuta el comando [`npm install`], si te da el siguiente error ejecuta el paso 5.; sino, puedes saltártelo: 


    `npm : No se puede cargar el archivo C:\Program Files\nodejs\npm.ps1 porque la ejecución de scripts está deshabilitada en este sistema. Para obtener más información, consulta el tema 

    about_Execution_Policies en https:/go.microsoft.com/fwlink/?LinkID=135170.

    En línea: 1 Carácter: 1

    + npm install

    + ~~~

        + CategoryInfo          : SecurityError: (:) [], PSSecurityException

        + FullyQualifiedErrorId : UnauthorizedAccess`

    
5. Abre el PowerShell como Administrador y ejecuta el siguiente comando:

                                Set-ExecutionPolicy RemoteSigned -Scope CurrentUser

6.  Ejecuta el comando [`ng serve`]. Si ejecutando sale el siguiente error es porque no tienes descargado Angular, así que ejecuta el comando del paso 7: 

        `ng : El término 'ng' no se reconoce como nombre de un cmdlet, función, archivo de script o programa ejecutable. Compruebe si escribió correctamente el nombre o, si incluyó una ruta de acceso, compruebe que dicha ruta es correcta e inténtelo de nuevo.
        En línea: 1 Carácter: 1
        
        + ng serve
        
        + ~~
            + CategoryInfo          : ObjectNotFound: (ng:String) [], CommandNotFoundException
            + FullyQualifiedErrorId : CommandNotFoundException`

7.  [`npm install -g @angular/cli`]


# EN EL CASO DE QUE QUIERAS DESPLEGAR LA APLICACIÓN

1.  Instala Docker Desktop.

2.  Abrir la raíz del proyecto desde VSC.

3.  Ejecutar el comando [`docker compose up --build -d`].

4.  Comprobar con el comando [`docker compose ps`], si tanto backend, frontend, bd y phpmyadmin aparecen como Up todo está perfecto.

5.  Si aparece el siguiente error realizar el paso 6. y 7.: 

        target backend: failed to solve: failed to compute cache key: failed to calculate checksum of ref lpdxx0zhbewjgbneifmk2psl4::twt6249q9mpl69dvoq90d3t2v: "/target/backend-0.0.1-SNAPSHOT.jar": not found

6.  Haz el comando [`cd backend`].
7.  Ejecuta el siguiente comando: [`./mvnw clean package -DskipTests`]
8.  Vuelve a la raíz de tu proyecto con [`cd ..`] y vuelve a realizar el paso 3.
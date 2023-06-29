# unq-pds-app-university-api

## Frameworks utilizados
Para el proyecto decidimos utilizar en el backend Kotlin junto con SpringBoot ya que son una de las herramientas utilizadas en el dia de la fecha.
Por este motivo, se cuenta con mucha documentación en caso de investigación y de pruebas y error. 

Para la base de datos optamos por MySql

Para el frontend decidimos usar React junto con JavaScript, ya que al igual que Angular es otra herramienta con la que se cuenta con mucha información en cuanto a documentación.

Otro de los motivos a utilizarlos es que son herramientas ya trabajadas en cuatrimestres anteriores

Hay que actualizar esto

## Utilización
 1 - Primero deben realizar un clone del repositorio o bajarse el .zip del mismo
 2 - Si utilizan Docker compose bastará con situarse dentro de la carpeta y utilizar el comando  `docker compose up --build`
        donde se inicializará la aplicación con datos precargados (Exceptuando el repositorio ya que trae una cantidad abismal de información).
        Para cargar el repositorio intente agregar al proyecto cargado el siguiente: unq-pds-app-university-api
 3 - Si por casualidad por la consola les visualiza: 
         `not foundapp  | /waitingDatabase.sh: line 2: springboot-app  | /waitingDatabase.sh: line 8: syntax error: unexpected end of file (expecting "done")springboot-app exited with code 2` solo les bastará con ir al archivo y eliminar la última linea.
        Por alguna razón Github agrega una linea extra por defecto

 4 - En caso de no usar docker compose puede ejecutar la aplicacion normalmente por el IDE pero acuerdese de agregar las siguientes variables de entorno:
          
         `ENCRYPT_PASSWORD`
         `DISABLED_TEST` => Solo en caso que no quiera que cuando se buildee corran todos los tests agrege cualquier dato


## Usuarios cargados
                    Mail        |  Paswword
    Student : german@gmail.com  | funciona
    Student : lucas@gmail.com   | funciona
    Teacher : maxi@gmail.com    | funciona
    Teacher : gustavo@gmail.com | funciona
    Admin   : admin@gmail.com   | funciona
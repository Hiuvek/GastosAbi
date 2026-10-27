# GastosAbi

Aplicacion Android de control de gastos con Kotlin, Jetpack Compose y SQLiteOpenHelper.

## Estructura

- `data/local`: helper SQLite y preferencias visuales de iconos.
- `data/repository`: implementacion del repositorio; las once funciones se ejecutan con `Dispatchers.IO`.
- `domain/model`: Categoria, Gasto, ResumenCategoria y validacion de montos.
- `domain/usecase`: alta, modificacion y eliminacion.
- `ui/screens`: Inicio, Categorias y Gastos; formularios de alta y edicion.
- `ui/components`, `ui/theme`, `ui/util`: controles, paleta y formato monetario.

## Tablas y relacion

Base local `gastos.db`, version 2.

| Tabla | Campos |
| --- | --- |
| categorias | id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL UNIQUE |
| gastos | id INTEGER PRIMARY KEY AUTOINCREMENT, categoria_id INTEGER NOT NULL, descripcion TEXT opcional, monto DOUBLE NOT NULL CHECK(monto > 0) |

Una categoria tiene muchos gastos. `gastos.categoria_id` referencia `categorias.id`, con claves foraneas activas y `ON DELETE RESTRICT`. No se puede eliminar una categoria que tenga gastos. Los nombres se validan y se recortan antes de guardarse. Las inserciones/actualizaciones usan ContentValues y todos los cursores se cierran con `use`.

## Decisiones

- El PDF pide DOUBLE/Double, aunque el ejemplo adjunto usa INTEGER/Long y la tabla del PDF tambien menciona centavos. Se interpreta el monto en unidades monetarias con dos decimales, conforme a los ejemplos mostrados: 85.50 se guarda como 85.50. Se redondea HALF_UP al guardar; se acepta punto o coma decimal sin separadores de miles. Cero, negativos y valores no finitos se rechazan.
- La version 1 de esta aplicacion mostraba los enteros como pesos. La migracion mantiene ese valor: 85 pasa a 85.00. Conserva categorias, IDs, descripciones, relaciones y la secuencia autoincremental. Copia los gastos a una tabla con el nuevo tipo dentro de la transaccion de SQLiteOpenHelper antes de sustituir la tabla antigua; no vacia la base. Si una base externa contiene centavos como enteros, requiere una conversion especifica antes de importarla.
- `resumenCategorias()` obtiene los totales con LEFT JOIN, SUM, GROUP BY y COALESCE, redondeados a dos decimales. Inicio y Categorias usan ese resultado e incluyen categorias sin gastos. El total general suma este resumen.
- El nombre de cada categoria se resuelve relacionando las consultas por ID; no se duplica en la tabla gastos. `leeGasto()` y `leeCategoria()` cargan la edicion desde SQLite.
- La descripcion es opcional. Sin categorias se bloquea el guardado y se ofrece Crear categoria.
- Las operaciones de BD se suspenden en el repositorio y se ejecutan en IO. Compose actualiza sus listas y el resumen tras cada operacion exitosa. La navegacion usa estado y los formularios rememberSaveable.
- Los iconos se guardan en SharedPreferences por ID, sin agregar columnas; su valor predeterminado es el carrito. La paleta principal es #804CB4.

## Ejecucion

Abrir el proyecto en Android Studio con SDK Android 37 y el JDK requerido por Gradle. Configurar `local.properties` con `sdk.dir` de la instalacion local. Sincronizar y ejecutar `app` en un dispositivo/emulador API 24 o superior.

```sh
./gradlew :app:assembleDebug :app:testDebugUnitTest
./gradlew :app:connectedDebugAndroidTest
```

El segundo comando instala y ejecuta las pruebas en los dispositivos conectados; usar solamente un dispositivo de pruebas. Con dependencias ya descargadas se puede agregar `--offline`.

## Pruebas y entrega

Ver [matriz de pruebas](docs/pruebas.md) y [wireframes y flujo](docs/wireframes.md).

Las pruebas instrumentadas usan una base con nombre exclusivo y la eliminan al terminar; no borran `gastos.db`. Cubren las once funciones, los importes del PDF, restricciones, descripcion vacia, reapertura del helper y migracion. Las pruebas Compose verifican formularios y resumen; las unitarias validan decimales, redondeo y formato.

## Limitaciones

DOUBLE tiene la precision binaria propia de punto flotante; se redondean importes y resumen a dos decimales. No hay fechas, ingresos, cuentas ni sincronizacion remota. Los iconos son preferencias locales y no forman parte de una copia de SQLite. Las pruebas de reapertura del helper no sustituyen una prueba manual de cierre completo del proceso. Los wireframes documentan las pantallas y el flujo; la presentacion inicial al profesor requiere evidencia externa.

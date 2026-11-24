# Guía para pasar los cambios de la versión final a Astah

Los colores de las imágenes significan lo mismo que en la versión dos:

- **Verde**: es nuevo, hay que crearlo.
- **Naranja**: ya existe pero cambia.
- **Rojo tachado**: se quita.
- **Negro / amarillo**: no cambia, solo está para ubicarse. "..." quiere decir "lo demás sigue igual".

La lista completa, sacada del código, está en `atributos_y_metodos_v3.txt`. Si tienen dudas sobre un tipo o un parámetro, revisen ahí.

Consejo: primero creen el paquete **dto** y las interfaces, porque las clases las usan como tipos.

---

## 0. Paquetes (`0_paquetes.png`)

1. Creen el paquete **dto** al lado de domain.
2. Pongan dependencias «use»: presentation → domain, presentation → dto, domain → dto y tests → domain.
3. domain no debe tener ninguna flecha hacia presentation.

## 1a. domain: interfaces y polimorfismo (`1a_clases_domain_interfaces.png`)

1. Creen las 5 interfaces nuevas: **Dibujable**, **Interactuable**, **Pateable**, **PoderEspecial** y **RepositorioPartida**, con los métodos de la imagen. Movible y EstrategiaMaquina ya existen.
2. Pongan las realizaciones (flecha punteada con triángulo vacío):
   - Cancha → Dibujable
   - Arco → Dibujable e Interactuable
   - Balon → Dibujable y Pateable (Balon → Movible ya existe)
   - Jugador → Dibujable, Interactuable y Pateable (Jugador → Movible ya existe)
   - Personaje → PoderEspecial
   - PartidoDAO → RepositorioPartida
3. En **Cancha**, **Arco**, **Balon**, **Jugador**, **Personaje**, **Maquina** y en los 4 países, agreguen o borren los miembros de color.
4. **PartidoDAO** pasa a ser Singleton: borren el constructor público, agreguen el privado, `instancia` (Static) y `getInstance()` (Static).
5. En **Partido**, borren las 6 constantes, `cabecear` y `verificarGol`. Agreguen `registrarGol`, `getInteractuables`, `getDibujables` y `getGolesMaximos` (protegido, #).
6. Creen **ConstantesJuego** con sus 7 constantes (Static) y su constructor privado. Pongan una dependencia «use» Partido → ConstantesJuego.
7. En **PartidoPorTiempo**, borren `muerteSubita` y agreguen `seAcaboElTiempo()`.

## 1b. domain y dto: DTO, fachada, excepciones y enums (`1b_clases_domain_dto_excepciones.png`)

1. Dentro del paquete dto creen **ComandoDibujo** y **EstadoJugador**. Todos sus atributos son públicos y finales: márquenlos `{readOnly}`.
2. Creen el enum interno **ComandoDibujo::Forma** (CANCHA, ARCO, BALON, JUGADOR), anidado (línea con círculo y cruz), y la asociación `- forma` hacia él.
3. En **DOPOSoccer**:
   - borren `- dao : PartidoDAO` y su asociación;
   - agreguen `- repositorio : RepositorioPartida` con su asociación (multiplicidad 1);
   - borren `getJugador`, `getGanador`, `getBalon` y `getCancha`;
   - agreguen el `iniciarPartido` de 6 parámetros, `getModalidad`, `getEstadoJugador`, `getNumeroGanador` y `getComandosDibujo`;
   - pongan las dependencias «use» hacia ComandoDibujo, EstadoJugador y Modalidad.
4. Creen **PersistenciaException** y **FormatoArchivoException**, las dos con generalización hacia DOPOSoccerException. En DOPOSoccerException agreguen el constructor `(message, causa)`.
5. En **Log**: el constructor pasa a privado, y agreguen `LOGGER`, `preparado` y `preparar()` (Static). Pongan «use» PersistenciaException → Log.
6. Creen el enum **Modalidad** en domain (sale de PanelConfiguracion).
7. En **Pais**, cambien el constructor y agreguen `fabrica : Supplier<Personaje>` y `crearPersonaje()`. Pongan «create» Pais → Personaje.
8. En **Estadisticas**, agreguen `copiar()`.

## 2. presentation (`2_clases_presentation.png`)

1. Creen la interfaz **VistaPrincipal** y la realización DOPOSoccerGUI → VistaPrincipal.
2. Creen **CicloJuego**, con las asociaciones `- vista` (CicloJuego → VistaPrincipal) y `- ciclo` (DOPOSoccerGUI → CicloJuego).
3. En **DOPOSoccerGUI**, borren `MILISEGUNDOS`, `timer`, `siguienteAction`, `iniciarAction`, `cicloAction` y `sonidoAction`, y agreguen lo verde. En **Pantalla**, agreguen INSTRUCCIONES.
4. Creen **PanelDegradado** (hereda de JPanel). PanelConfiguracion, PanelJugadores y la nueva **PanelInstrucciones** heredan de PanelDegradado (antes PanelConfiguracion y PanelJugadores heredaban de JPanel).
5. Pongan `- vista : VistaPrincipal` en PanelInicio, PanelConfiguracion, PanelJugadores y PanelInstrucciones, con su asociación hacia VistaPrincipal.
6. Borren el enum **PanelConfiguracion::Modalidad**. Ahora el combo usa domain::Modalidad.
7. Creen **RenderizadorElementos** (todos sus métodos son Static) y las dependencias «use» desde PanelCancha y PanelInicio.
8. En **PanelCancha**, **PanelInicio**, **PanelPausa**, **PanelConfiguracion**, **PanelJugadores** y **Estilo**, agreguen o borren los miembros de color.

---

## Diagramas de secuencia

Para los que ya existen, abran el de Astah y apliquen solo lo que está en color.

### 3. iniciarPartido (`3_sec_iniciarPartido.png`)
1. Cambien el mensaje 1 por la firma de 6 parámetros (con `modalidad : Modalidad`).
2. Cambien la guarda del primer opt a `[modo == null || modalidad == null]`.
3. Agreguen la línea de vida **modalidad : Modalidad**, el mensaje `tieneMaquina()` y el opt `[m && perfil == null]` con el throw PERFIL_INVALIDO.
4. Cambien la guarda del opt de la máquina a `[m]`.

### 4. patearJugador (`4_sec_patearJugador.png`)
1. Agreguen `getRival(j)` y la línea de vida **pt : Pateable**.
2. Dentro de `patear`, reemplacen el if del balón y del rival por un loop sobre `{balon, r}` con `estaAlAlcanceDe(this)` y, en un opt, `serPateado(this)`.

### 5. actualizar (`5_sec_actualizar.png`)
1. Borren el loop de `cabecear` y la llamada `verificarGol()`.
2. Agreguen la línea de vida **it : Interactuable** y el loop sobre `getInteractuables()` con `interactuar(balon, this)`.

### 6. guardarPartida (`6_sec_guardarPartida.png`)
1. DOPOSoccer ya no llama directamente al DAO: agreguen la línea de vida **r : RepositorioPartida**.
2. En los dos errores, cambien el throw por el create de **PersistenciaException**, que llama a `Log.record(this)`. Borren el `Log.record(e)` que hacía el DAO.

### 7. abrirPartida (`7_sec_abrirPartida.png`)
1. Agreguen **r : RepositorioPartida**.
2. Si no se puede leer el archivo, se crea una **PersistenciaException**. Si el formato está mal, se usa `invalido(datos, razon)`, que crea una **FormatoArchivoException**. El resto no cambia.

### 8. getComandosDibujo (`8_sec_getComandosDibujo.png`): diagrama nuevo
Créenlo completo. Muestra cómo PanelCancha pide los ComandoDibujo a la fachada (que se los pide a cada Dibujable) y se los pasa a RenderizadorElementos.

### 9. cicloJuego (`9_sec_cicloJuego.png`): diagrama nuevo
Créenlo completo. Muestra el ciclo de paso fijo: CicloJuego llama a `pasoDeJuego(1/60)` varias veces si hace falta, y después llama a `refrescar()` una sola vez.

Los diagramas de **moverJugador**, **jugarMaquina** y **cambiarEstrategia** no cambian.

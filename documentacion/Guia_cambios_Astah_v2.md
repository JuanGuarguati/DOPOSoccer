# Guía para pasar los cambios de la versión dos a Astah

En todas las imágenes los colores significan:

- **Verde**: es nuevo, hay que crearlo.
- **Naranja**: ya existe pero cambia (se renombra, cambia el tipo o los parámetros).
- **Rojo tachado**: se quita.
- **Negro**: no cambia, solo está para ubicarse.

La lista completa de atributos y métodos, sacada del código, está en `atributos_y_metodos_v2.txt`. Si tienen dudas sobre un tipo o un parámetro, revisen ahí.

Consejo: hagan primero todos los enums e interfaces, porque las clases los usan como tipos.

---

## 1a. Clases de domain: Strategy y Movible (`1a_clases_domain_strategy_movible.png`)

1. Creen la interfaz **Movible** con `mover`, `reiniciar`, `getX`, `getY`, `getVelocidad` y `getRadio`.
2. Pongan una realización (flecha punteada con triángulo vacío) de **Jugador → Movible** y otra de **Balon → Movible**.
3. En **Balon**, agreguen `+ getVelocidad() : double`.
4. En **Jugador**, renombren `actualizar(...)` a `mover(segundos : double, cancha : Cancha)`. Agreguen `jugar(partido)`, `esMaquina()`, `setGoles(goles)`, `getVelocidad()` y `estaEnSuelo()`, y cambien `getPais()` para que devuelva `Pais`.
5. Creen el enum **Accion** (QUEDARSE, MOVER_IZQUIERDA, MOVER_DERECHA, SALTAR, PATEAR).
6. Creen la interfaz **EstrategiaMaquina** con `decidirAccion(maquina : Maquina, partido : Partido) : Accion` y `getPerfil() : PerfilMaquina`.
7. En **Maquina**: quiten la marca Abstract. Agreguen `+ TOLERANCIA` (Static) y `- estrategia : EstrategiaMaquina`. El constructor recibe ahora también `estrategia`. Agreguen `jugar`, `ejecutar`, `accionHacia`, `getSentidoAtaque`, `miraHaciaAtaque`, `accionHaciaAtaque`, `setEstrategia`, `getEstrategia` y `esMaquina`.
8. Pongan una asociación **Maquina → EstrategiaMaquina** con rol `- estrategia` y multiplicidad 1.
9. **MaquinaTorpe, MaquinaOfensiva, MaquinaDefensiva y MaquinaExperta** ya no heredan de Maquina. Borren esas generalizaciones y pongan en su lugar realizaciones hacia **EstrategiaMaquina**. Agreguen sus atributos y métodos como salen en la imagen.
10. Creen la clase nueva **MaquinaEquilibrada** (atributos `ataque` y `defensa`, método `vaGanando`), también con realización hacia EstrategiaMaquina.
11. Pongan las asociaciones entre estrategias: Experta → Ofensiva (`ataque`), Experta → Defensiva (`defensa`), Defensiva → Ofensiva (`despeje`), Equilibrada → Ofensiva (`ataque`) y Equilibrada → Defensiva (`defensa`).
12. Creen el enum **PerfilMaquina** (TORPE, OFENSIVA, DEFENSIVA, EXPERTA, EQUILIBRADA) con `crearEstrategia()`, `desdeTexto(texto)` (Static), `getNombre()` y `toString()`. Pongan una dependencia «create» hacia EstrategiaMaquina.

## 1b. Clases de domain: enums y DAO (`1b_clases_domain_enums_dao.png`)

1. Creen los enums **Pais**, **Direccion** y **Estadistica** con sus valores, atributos y métodos.
2. En **Personaje**, cambien `pais : String` por `pais : Pais`, y `crear(pais : String)` por `crear(pais : Pais)`. Pongan una asociación Personaje → Pais.
3. En **Camerun, CoreaDelSur, Nigeria y EstadosUnidos**, borren `+ PAIS : String`.
4. En **Estadisticas**, agreguen el constructor de 4 enteros, `mejorar(estadistica : Estadistica)`, `getValor(estadistica : Estadistica)` y `getMejorasCompradas()`.
5. En **Partido**: agreguen `ALTURA_SAQUE` (Static), y el constructor recibe `(pais1 : Pais, pais2 : Pais)`. `moverJugador` recibe `Direccion`. Agreguen `ponerMaquina`, `getModo` (Abstract), `getValor` (Abstract), `restaurar`, `getMovibles`, `getRival` y `getMaquina`.
6. Creen el enum interno **Partido::ModoJuego** (POR_TIEMPO, POR_GOLES), anidado dentro de Partido (la línea con el círculo y la cruz).
7. En **PartidoPorGoles y PartidoPorTiempo**, cambien el constructor para que reciba `Pais` y agreguen `getModo()` y `getValor()`.
8. Creen la clase **PartidoDAO** con sus constantes y métodos. Pongan una asociación **DOPOSoccer → PartidoDAO** con rol `- dao`.
9. En **DOPOSoccer**: borren `POR_TIEMPO`, `POR_GOLES` y `PAISES`, y agreguen `- dao`. Cambien `iniciarPartido`, `mejorarEstadistica` y `moverJugador` a los nuevos tipos. Agreguen `cambiarEstrategia`, `esContraMaquina`, `getPerfilMaquina`, `guardar`, `abrir`, `getPartido` (~), `restaurar` (~) y `prepararJugadores`.
10. En **DOPOSoccerException**, agreguen las 6 constantes nuevas: PERFIL_INVALIDO, NO_HAY_MAQUINA, ESTADISTICA_INVALIDA, ARCHIVO_NO_GUARDADO, ARCHIVO_NO_ABIERTO y ARCHIVO_INVALIDO.

## 2. Clases de presentation (`2_clases_presentation.png`)

1. Creen la clase **Estilo** (final). Lleva todos los colores y fuentes como constantes Static, y los métodos de ayuda (`crearBoton`, `estilizarCombo`, `agregarHover`, etc.).
2. Borren de **PanelCancha, PanelPausa y PanelInicio** las constantes de colores que salen en rojo. Ahora están en Estilo.
3. Creen **BotonRedondeado** (hereda de JButton) y **PanelTarjeta** (hereda de JPanel).
4. Donde había `JButton`, cambien el tipo a `BotonRedondeado` (naranja), y pongan las asociaciones hacia BotonRedondeado que salen en la imagen.
5. En **PanelConfiguracion**: `comboTipo` pasa a `comboModo`. Creen los enums internos **Modalidad** y **Resolucion** y agreguen `getModo()` y `getPerfil()`.
6. En **PanelJugadores**: los combos ahora son de `Pais`. Agreguen `tarjetas : PanelTarjeta[]` (asociación de multiplicidad 2) y `setContraMaquina`.
7. En **PanelPausa**: agreguen `botonAbrir`, `botonEstrategia`, `prepararBoton` y `setContraMaquina`.
8. En **DOPOSoccerGUI**: creen el enum interno **Pantalla** y agreguen `TAMANO_MENU`, `itemEstrategia`, `selectorArchivo`, `mostrarCancha`, `guardarAction`, `abrirAction` y `cambiarEstrategiaAction`. Borren `proximamenteAction`.
9. Pongan las dependencias «use» hacia **Estilo**.

---

## Diagramas de secuencia

Para cada diagrama abran el que ya tienen en Astah y apliquen solo lo que está en color.

### 3. iniciarPartido (`3_sec_iniciarPartido.png`)
1. Cambien el mensaje 1 por la firma nueva: `pais1 : Pais, pais2 : Pais, modo : ModoJuego, valor : int, perfil : PerfilMaquina`.
2. Cambien la guarda del opt a `[modo == null || valor <= 0]`.
3. Borren el alt de tiempo o goles que estaba en DOPOSoccer.
4. Agreguen la línea de vida **modo : ModoJuego** con `crearPartido(...)`, y dentro de ella el alt que crea PartidoPorTiempo o PartidoPorGoles.
5. Agreguen el opt `[perfil != null]` con `crearEstrategia()` en **pm : PerfilMaquina**, `ponerMaquina(e)` en Partido y el create de **m : Maquina**.
6. Agreguen `prepararJugadores(nuevo)` y metan sumarPuntos y setEstadisticas en un loop `[numero = 1..2]`. Borren los 4 mensajes sueltos viejos.

### 4. moverJugador (`4_sec_moverJugador.png`)
1. Cambien el parámetro `direccion : String` por `direccion : Direccion` en los mensajes 1 y 4.
2. Agreguen el opt `[direccion == null]` con el throw DIRECCION_INVALIDA.
3. Cambien las guardas del alt a `Direccion.IZQUIERDA` y `[else]`. Borren el tercer operando con el throw.

### 5. actualizar (`5_sec_actualizar.png`)
1. Agreguen el loop `jugar(this)` sobre cada jugador.
2. Agreguen el loop sobre `getMovibles()` con `mover(segundos, cancha)` hacia **mv : Movible**.
3. Borren los mensajes sueltos `mover` a Balon y `actualizar` a j1 y j2.
4. Metan `cabecear` en un loop sobre cada jugador.

### 6. jugarMaquina (`6_sec_jugarMaquina.png`), diagrama nuevo
Créenlo completo, como sale en la imagen. Muestra cómo la Maquina le pide la acción a su estrategia (`decidirAccion`) y luego la ejecuta en el alt.

### 7. cambiarEstrategia (`7_sec_cambiarEstrategia.png`), diagrama nuevo
Créenlo completo. Muestra cómo se cambia la estrategia en plena partida con `setEstrategia(e)`.

### 8. guardarPartida (`8_sec_guardarPartida.png`), diagrama nuevo
Créenlo completo: DOPOSoccer → PartidoDAO, el archivo se escribe, y si hay error se llama a `Log.record` y se lanza ARCHIVO_NO_GUARDADO.

### 9. abrirPartida (`9_sec_abrirPartida.png`), diagrama nuevo
Créenlo completo: primero se lee y valida el archivo, luego se arma el partido y por último DOPOSoccer lo restaura en pausa.

El diagrama de **patearJugador** no cambia.

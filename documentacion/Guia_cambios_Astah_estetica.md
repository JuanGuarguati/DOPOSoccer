# Guía para pasar a Astah los cambios de estética y física

Esta guía va **después** de `Guia_cambios_Astah_final.md` (poderes y música). Los colores son los de siempre:

- **Verde**: es nuevo.
- **Naranja**: cambia.
- **Rojo tachado**: se quita.
- "...": lo demás sigue igual.

La lista completa sacada del código está en `atributos_y_metodos_final2.txt`.

---

## 1d. domain: física (`1d_clases_domain_fisica.png`)

1. En **Jugador**:
   - agreguen `CUELLO`, `LARGO_PIE`, `ALTO_PIE` y `REBOTE_CUERPO` (Static);
   - agreguen `chocarConCabeza` (privado), `chocarConCuerpo` (público) y `chocarConRectangulo` (privado).
2. En **Balon** agreguen `tirosLanzados`, `rebotarEn(...)` y `getTirosLanzados()`.
3. Pongan una dependencia «use» Jugador → Balon (rebotarEn).
4. En **DOPOSoccer** agreguen `getTirosDePoder() : int`.
5. **Arco** no cambia en el diagrama (solo por dentro).

## 2c. presentation y dto (`2c_clases_presentation_estetica.png`)

1. En **RenderizadorElementos**:
   - borren `PIE_ANCHO`, `PIE_ALTO`, `RED_ARCO`, `AURA` y los 4 métodos en rojo;
   - agreguen las constantes y los métodos verdes (todos Static).
   - `dibujar(g, c)` se queda, y se agrega `dibujar(g, c, tiempo)`.
2. En **PanelCancha**:
   - borren `PODER`, `PODER_LLENO` y el `dibujarInfoJugador` viejo;
   - agreguen los miembros verdes. `colorPoder` y `mezclar` son Static.
3. En **CicloJuego** agreguen `DURACION_CAMARA_LENTA`, `FACTOR_CAMARA_LENTA` (Static), `tiempoLento` y sus 3 métodos.
4. En **DOPOSoccerGUI** agreguen `tirosVistos`.
5. En **PanelInstrucciones** borren `agregarLineas` y agreguen `crearTarjeta`.
6. Creen **IconoInstruccion**:
   - hereda de JComponent;
   - lleva sus constantes (Static), `tipo` y sus métodos;
   - dentro va el enum anidado **Tipo** con 6 valores (círculo con + desde Tipo hacia IconoInstruccion).
   - Pongan una asociación PanelInstrucciones → IconoInstruccion con multiplicidad 6 y un «use» IconoInstruccion → RenderizadorElementos.
7. En **ComandoDibujo** borren `pateando` y agreguen `avancePatada : double`. Cambien el constructor largo por el nuevo.
8. En **EstadoJugador** agreguen `colores : Color[]` y cambien el constructor por el de 12 parámetros (colores va tercero).

## 12. Secuencia cámara lenta (`12_sec_camaraLenta.png`)

Diagrama nuevo:

1. El ciclo cuenta el tiempo más despacio con `tiempoDeJuego`.
2. En cada paso, la GUI revisa `getTirosDePoder()`; si hay un tiro nuevo, llama `iniciarCamaraLenta()`.
3. Al refrescar, la GUI pasa `getCamaraLenta()` a `setOscurecer(n)` del panel.

## 13. Secuencia de los choques del jugador (`13_sec_choquesJugador.png`)

Diagrama nuevo de `Jugador.interactuar`:

1. Primero va el choque con la cabeza (cabezazo, o tiro de poder si tiene el aura).
2. Si no tocó la cabeza, va el choque con el cuerpo y con el pie (`chocarConRectangulo` + `rebotarEn`).

---

Los demás diagramas quedan igual.

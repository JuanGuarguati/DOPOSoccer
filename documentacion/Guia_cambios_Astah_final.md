# Guía para pasar los poderes y la música a Astah (versión final)

Esta guía va **después** de la de la versión tres (`Guia_cambios_Astah_v3.md`). Primero hagan esos cambios y luego estos.

Los colores significan lo mismo de siempre:

- **Verde**: es nuevo, hay que crearlo.
- **Naranja**: ya existe pero cambia.
- **Rojo tachado**: se quita.
- **Negro / amarillo**: no cambia, solo está para ubicarse. "..." quiere decir "lo demás sigue igual".

La lista completa sacada del código está en `atributos_y_metodos_final.txt`. Si tienen dudas sobre un tipo o un parámetro, revisen ahí.

---

## 1c. domain: poderes especiales (`1c_clases_domain_poderes.png`)

1. Creen el enum **Efecto** con sus 5 valores (NINGUNO, EMPUJE, ELECTROCUTADO, INCONSCIENTE, CONFUSION), sus 5 atributos privados, el constructor privado y sus 5 métodos.
2. En **PoderEspecial** y en **Personaje** borren `tiroDePoder(balon : Balon, haciaDerecha : boolean)` y agreguen `tiroDePoder(balon : Balon, tirador : Jugador)`. En Personaje queda {abstract}.
3. En **Camerun**, **CoreaDelSur**, **Nigeria** y **EstadosUnidos** hagan el mismo cambio de `tiroDePoder` y agreguen `NOMBRE_TIRO` y `VELOCIDAD_TIRO` (Static). EstadosUnidos también tiene `BALONES_FALSOS` (Static).
4. En **Jugador** agreguen las 4 constantes (Static), los 4 atributos (`barraPoder`, `conPoder`, `efecto`, `tiempoEfecto`) y los 11 métodos verdes. `haciaMiArco()` es privado.
5. En **Balon** agreguen las 2 constantes (Static), los 7 atributos y los 10 métodos verdes. `moverSenuelos` es privado.
6. Creen **BalonFalso** con su atributo, su constructor y sus 2 métodos. Pongan una generalización BalonFalso → Balon.
7. Asociaciones nuevas:
   - Jugador → Efecto, rol `- efecto`, multiplicidad 1.
   - Balon → Jugador, rol `- tirador`, multiplicidad 0..1.
   - Balon → BalonFalso, rol `- senuelos`, multiplicidad 0..*.
   - BalonFalso → Balon, rol `- original`, multiplicidad 1.
   - Dependencia «use» Personaje → Efecto.
8. En **Partido**, agreguen `activarPoder(numero : int)` y `getBalonPara(jugador : Jugador) : Balon`. Si todavía tienen `verificarGol()`, bórrenlo.
9. En **DOPOSoccer** agreguen `activarPoder(numero : int)`.
10. **Maquina** y las estrategias no cambian en el diagrama de clases (solo cambia lo que hacen por dentro).

## 2b. presentation y dto: música y dibujo de los poderes (`2b_clases_presentation_musica.png`)

1. Creen **Musica** con sus 13 constantes (Static; BOMBO, CAJA, PLATILLO, BAJO y MELODIA son privadas), `secuenciador` y `activa`, y sus 10 métodos (`crearCancion`, `agregarNota` y `evento` son privados).
2. En **DOPOSoccerGUI**:
   - agreguen `itemMusica` y `musica`;
   - borren `teclaPresionadaAction(codigo : int)` y agreguen `teclaPresionadaAction(codigo : int, ubicacion : int)`;
   - agreguen `musicaAction()` y `actualizarMusica()` (privados).
   - Pongan la asociación DOPOSoccerGUI → Musica, rol `- musica`, multiplicidad 1.
3. En **PanelPausa** agreguen `botonMusica`, `setMusica(activa, disponible)` y `getBotonMusica()`.
4. En **PanelCancha** cambien el valor de `ALTO_INFORMACION` a 100 y agreguen `PODER`, `PODER_LLENO` (Static) y `textoPoder(j)`.
5. En **RenderizadorElementos** agreguen las 7 constantes (Static; `BRILLO` es pública) y `dibujarTiro(g, c)` (Static, privado).
6. En **ComandoDibujo** agreguen `aura` y `efecto` {readOnly} y el constructor del balón con `tiro : String`. Borren el constructor largo viejo y pongan el nuevo, que termina en `aura : boolean, efecto : String`.
7. En **EstadoJugador** agreguen `barraPoder`, `conPoder` y `efecto` {readOnly}. Cambien el constructor por el de 11 parámetros.
8. PanelInstrucciones, PanelJugadores y PanelInicio no cambian en el diagrama (solo cambian textos por dentro).

## 10. Secuencia activarPoder (`10_sec_activarPoder.png`)

Diagrama nuevo. Usuario → DOPOSoccerGUI (tecla Q o Ctrl derecho) → DOPOSoccer.activarPoder → Partido.activarPoder → Jugador.activarPoder, con el `opt [!pausado]` y el `alt` de la condición de la barra.

## 11. Secuencia tiroDePoder (`11_sec_tiroDePoder.png`)

Diagrama nuevo. Muestra el tiro (usarPoder → tiroDePoder → lanzarTiro, y crearSenuelos solo en Estados Unidos) y lo que pasa cuando el tiro golpea al rival (recibirTiro → recibirDanio → aplicarEfecto → recibirEfecto → terminarTiro).

---

Los demás diagramas de la versión tres (0 a 9) quedan igual.

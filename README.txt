Práctica 1 - Matemáticas Computacionales
---------------------------------------
Carlos Ernesto López Solano
A01633683
16/09/19

--------------------------------------------------------------------------------------------------------------------

Buscador de Archivos con expresiones regulares

----------------------Sintaxis:-------------------------------------------------------------------------------------

Las operaciones posibles a ingresar en el programa y sus respectivas representaciones son las siguientes:

Cerradura - * (asterisco)
Cerradura Positiva - + (símbolo de suma)
Unión - o (letra O minúscula)

La concatenación se manejara de manera automática al concatenar paréntesis (por ejemplo (ab)(cd)) y al concatenar
símbolos dentro de paréntesis (por ejemplo (abcd)).

Es *ABSOLUTAMENTE* necesario utilizar paréntesis para diferenciar las diferentes operaciones, excepto concatenación.
Las operaciones que requieren el uso de un símbolo diferenciador u operador deberán tener el siguiente formato:

    ([símbolos concatenados])[operador]

Por ejemplo:
Si quiera buscar la cadena "Hola" (que es una concatenación de los cuatro símbolos H, O, L y A) bajo la cerradura,
debo ingresar:

    (Hola)*

donde la cadena esta dentro de los paréntesis y el operador le sigue al paréntesis de cierre.


Un ejemplo de una expresión regular compleja que utilice todos los operadores podría ser:

    (H)*(o)o(a)o(O)(la)+

donde se buscará la letra 'H' 0 o más veces, el caracter 'o' ó 'a' ó 'O', y por ultimo 'la' 1 o mas veces.

NOTA: al día de hoy, el programa no acepta el uso de la palabra vacía o de paréntesis anidados. Sin embargo,
los paréntesis concatenados pueden ser usados con libertad, por lo que puede tener un gran numero de cerraduras,
uniones o cerraduras positivas concatenadas.


----------------------Instrucciones de uso:-------------------------------------------------------------------------

1) Al ejecutar el archivo Regex.java, aparecerá un prompt donde deberá ingresar la expresión regular a buscar.

NOTA: como se dijo antes, se debe tener cuidado introducir los paréntesis necesarios para la búsqueda correcta
de la expresión regular. El mal uso de dicha sintaxis puede llevar a errores en la búsqueda o a un rechazo
de la expresión regular por parte del programa.

2) Después, aparecerá otro prompt donde deberá ingresar el path al directorio donde se desea buscar.

NOTA: debe ser introducido un path válido, de lo contrario podrían ocurrir errores en la búsqueda o un rechazo
del path por parte del programa.

3) Al introducir ambas variables, la búsqueda comenzará. En la consola se imprimirán los directorios y
archivos sobre los cuales esta buscando.

4) Al finalizar la búsqueda, se imprimirá en consola si la búsqueda fue exitosa así como sus resultados, o si la
búsqueda no encontró ninguna incidencia.

5) Para volver a buscar, es necesario ejecutar de nuevo el programa.

----------------------Funcionamiento:-------------------------------------------------------------------------------

El programa se encarga de analizar la expresión regular brindada para verificar su validez. Al mismo tiempo que hace
esto, almacena las cadenas compuestas de símbolos concatenados dentro de "contenedores" y les asigna el operador
brindado. Estos contenedores serán almacenados en una lista para su fácil acceso.
El programa cuenta con la función de almacenar contenedores de manera recursiva para permitir la búsqueda de
paréntesis anidados. Lamentablemente, la función no fue completada y quedará pendiente para configuración futura.

Después, verificará que el path brindado sea válido y sea un directorio sobre el cual pueda buscar.

Al comenzar la búsqueda, el programa irá recorriendo de manera recursiva los directorios dentro del brindado hasta
dar con archivos, sobre los cuales se aplicarán dos métodos de búsqueda:

    Sobre su título/nombre
    Sobre su contenido al leerlo

Cada vez que el programa acceda a un directorio o archivo su ruta y la acción sobre este serán impresos en la
consola, mientras que los resultados serán almacenados en una lista.

NOTA: Si un archivo no puede ser leído (lo cual es probable para muchos tipos de archivo) simplemente se pasará
al siguiente archivo y se imprimirá el evento sucedido.

Al terminar la búsqueda, se imprimirán en pantalla todos los resultados obtenidos con el siguiente formato

    [Cadena encontrada] [ruta del archivo] [sección donde fue encontrada: título ó en el texto]



Carlos López, 2019
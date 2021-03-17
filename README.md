# LauncherVideoInnovation

# Detalles 

Esta aplicacion tipo launcher usa las siguientes librerias : 

- Leanback
- Glide
- Gson
- Retrofit
- ViewModel
- LiveData
- Lottie
- Webkit
- Exoplayer

El launcher consume de la API los objetos los cuales se clasifican entre:

- Proyectos: Contiene un Apk con opcion a descarga.
- Noticias: Te da opcion a abrir un WebView para ver una noticia relacionada.
- Videos: Usando ExoPlayer puedes reproducir un video.

Todos estos objetos salen representados en la pantalla con el diseño de la libreria Leanback con un menu lateral para dividirlos por categorias.
Cada objeto contiene:

- Id (Int)
- Titulo (String)
- Descripcion (String)
- Categoria (String)
- Video de Presentacion (String)
- Video entero (String)
- Foto (String)
- Accion (String)

Cada Categoria contiene: 
- Id (Int)
- Nombre (String)
- Icono (String)

Al Seleccionar cada Objeto el video de fondo cambia poniendo un corto de cada elemento y al pulsarlo entras en una pantalla de informacion
en la cual puedes leer sobre cada objeto tambien te da la opcion de pulsar un boton para accionar el objeto (Descargar el Apk, Ver la noticia, Ver el video).


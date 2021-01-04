package guinea.diego.launchervideoinnovation.data.models

import android.graphics.drawable.Drawable
import java.io.Serializable

class PhotoItem(
    val title: String,
    val description: String,
    val photo: Drawable?) {
}
data class Categoria(
    var id: Int,
    var name: String
): Serializable
data class Proyectos(
    var id: Int,
    var titulo: String,
    var descripcion: String,
    var categoria: String,
    var VideoPresentacion: String,
    var VideoEntero: String,
    var foto: String
): Serializable

data class Values(var categorias: List<Categoria>,var proyectos: List<Proyectos>)

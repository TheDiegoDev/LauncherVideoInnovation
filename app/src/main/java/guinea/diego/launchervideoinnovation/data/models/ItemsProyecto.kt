package guinea.diego.launchervideoinnovation.data.models

import java.io.Serializable

data class Categoria(
    var id: Int,
    var name: String,
    var icon: String
): Serializable
data class Proyectos(
    var id: Int,
    var titulo: String?,
    var descripcion: String?,
    var categoria: String?,
    var VideoPresentacion: String?,
    var VideoEntero: String?,
    var foto: String?,
    var accion: String?
): Serializable

data class Values(var categorias: List<Categoria>,var proyectos: List<Proyectos>)

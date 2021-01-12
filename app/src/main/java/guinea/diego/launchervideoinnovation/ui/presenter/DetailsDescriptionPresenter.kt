package guinea.diego.launchervideoinnovation.ui.presenter

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import guinea.diego.launchervideoinnovation.data.models.Proyectos

class DetailsDescriptionPresenter: AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(viewHolder: ViewHolder, item: Any?) {
        if(item is Proyectos) {
            viewHolder.title.text = item.titulo
            viewHolder.subtitle.text = item.categoria
            viewHolder.body.text = item.descripcion
        }
    }

}
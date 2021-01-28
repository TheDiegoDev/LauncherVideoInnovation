package guinea.diego.launchervideoinnovation.ui.presenter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.RowHeaderPresenter
import com.bumptech.glide.Glide
import guinea.diego.launchervideoinnovation.R
import guinea.diego.launchervideoinnovation.data.models.Categoria
import kotlin.properties.Delegates

class IconHeaderItemPresenter(private val categoria: ArrayList<Categoria>): RowHeaderPresenter() {

    private var unselectedAlpha: Float by Delegates.notNull()
    private var contador = 0

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        unselectedAlpha = parent.resources
            .getFraction(R.fraction.lb_browse_header_unselect_alpha, 1, 1)
        val inflater = parent.context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = inflater.inflate(R.layout.item_icon_header, null)
        view.alpha = unselectedAlpha

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any, payloads: MutableList<Any>) {
        super.onBindViewHolder(viewHolder, item, payloads)

        val rootView = viewHolder.view
        val iconView = rootView.findViewById<ImageView>(R.id.header_icon)
        val label = rootView.findViewById<TextView>(R.id.header_label)
        rootView.isFocusable = true

        val headerItem = (item as ListRow).headerItem
        label.text = headerItem.name

        val num = getInt(headerItem.name)

        Glide.with(iconView.context)
            .load(categoria[num].icon)
            .into(iconView)
    }

    private fun getInt(name: String?): Int {
      var respuesta = 0
      categoria.forEach {
            if (categoria[contador].name == name){
                respuesta = contador
            }
          contador++
        }
        return respuesta
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        super.onUnbindViewHolder(viewHolder)
    }

    override fun onSelectLevelChanged(holder: ViewHolder) {
        holder.view.alpha = unselectedAlpha + holder.selectLevel *
                (1.0f - unselectedAlpha)
    }
}
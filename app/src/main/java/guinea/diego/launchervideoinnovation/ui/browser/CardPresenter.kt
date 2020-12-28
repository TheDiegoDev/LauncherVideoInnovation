package guinea.diego.launchervideoinnovation.ui.browser

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ViewTarget
import guinea.diego.launchervideoinnovation.R
import guinea.diego.launchervideoinnovation.data.models.Categoria
import guinea.diego.launchervideoinnovation.data.models.PhotoItem
import guinea.diego.launchervideoinnovation.data.models.Proyectos
import okhttp3.internal.notifyAll



private const val TAG = "CardPresenter"

class CardPresenter : Presenter() {

    private var proyectosData: ArrayList<Proyectos> = arrayListOf()
    private var categoriasData: ArrayList<Categoria> = arrayListOf()


     fun setData(proyectos: ArrayList<Proyectos>, categoria: ArrayList<Categoria>){
        proyectosData.clear()
        categoriasData.clear()
        proyectosData.addAll(proyectos)
        categoriasData.addAll(categoria)
        notifyAll()
    }

    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder? {
        Log.d(TAG, "onCreateViewHolder")
        val imageCardView = ImageCardView(parent?.context)
        imageCardView.focusable = View.FOCUSABLE
        imageCardView.isFocusableInTouchMode = true
        imageCardView.findViewById<TextView>(R.id.content_text).setTextColor(Color.BLUE)
        return MyViewHolder(imageCardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, photoItem: Any?) {
        photoItem as PhotoItem
        viewHolder as MyViewHolder
        viewHolder.bind(photoItem)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        Log.d(TAG, "onUnbindViewHolder");
    }

    class MyViewHolder(val imageCardView: ImageCardView) : ViewHolder(imageCardView) {
        private val CARD_WIDTH = 250
        private val CARD_HEIGHT = 150
        fun bind(photoItem: PhotoItem) = with(imageCardView) {
            titleText = photoItem.title
            contentText = photoItem.description

            setMainImageDimensions(CARD_WIDTH * 2, CARD_HEIGHT * 2)
            setUpImage(photoItem)

        }

        private fun setUpImage(photoItem: PhotoItem): ViewTarget<ImageView, Drawable> {
            return Glide.with(view.context)
                .load(photoItem.photo)
                .into(imageCardView.mainImageView)
        }


    }


}
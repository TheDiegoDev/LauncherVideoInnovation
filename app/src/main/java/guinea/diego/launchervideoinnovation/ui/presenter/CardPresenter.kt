package guinea.diego.launchervideoinnovation.ui.presenter


import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.method.ScrollingMovementMethod
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

import guinea.diego.launchervideoinnovation.data.models.Proyectos




private const val TAG = "CardPresenter"

class CardPresenter : Presenter() {

    private var proyectosData: ArrayList<Proyectos> = arrayListOf()
    private var categoriasData: ArrayList<Categoria> = arrayListOf()


     fun setData(proyectos: ArrayList<Proyectos>, categoria: ArrayList<Categoria>){
        proyectosData.clear()
        categoriasData.clear()
        proyectosData.addAll(proyectos)
        categoriasData.addAll(categoria)

    }


    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder? {
        Log.d(TAG, "onCreateViewHolder")
        val imageCardView = ImageCardView(parent?.context)
        imageCardView.focusable = View.FOCUSABLE
        imageCardView.isFocusableInTouchMode = true
        imageCardView.findViewById<TextView>(R.id.content_text).horizontalScrollbarTrackDrawable
        imageCardView.findViewById<TextView>(R.id.content_text).setTextColor(Color.WHITE)
        //Scroll del content

        return MyViewHolder(imageCardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        item as Proyectos
        viewHolder as MyViewHolder
        viewHolder.bind(item)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        Log.d(TAG, "onUnbindViewHolder");
    }

    class MyViewHolder(val imageCardView: ImageCardView) : ViewHolder(imageCardView) {

        private var CARD_WIDTH = 250
        private var CARD_HEIGHT = 150


        fun bind(photoItem: Proyectos) = with(imageCardView) {
            titleText = photoItem.titulo
            contentText = photoItem.descripcion

            if(photoItem.categoria == "Noticias"){
                CARD_WIDTH = 200
                CARD_HEIGHT = 100
                setMainImageDimensions(CARD_WIDTH * 2, CARD_HEIGHT * 2)
                setUpImage(photoItem)
            }else if(photoItem.categoria == "Proyectos"){
                CARD_WIDTH = 250
                CARD_HEIGHT = 250
                setMainImageDimensions(CARD_WIDTH * 2, CARD_HEIGHT * 2)
                setUpImage(photoItem)
            }else{
                setMainImageDimensions(CARD_WIDTH * 2, CARD_HEIGHT * 2)
                setUpImage(photoItem)
            }
        }

        private fun setUpImage(photoItem: Proyectos): ViewTarget<ImageView, Drawable> {
            return Glide.with(view.context)
                .load(photoItem.foto)
                .into(imageCardView.mainImageView)
        }
    }
}
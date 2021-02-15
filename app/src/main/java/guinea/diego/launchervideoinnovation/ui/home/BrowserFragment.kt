package guinea.diego.launchervideoinnovation.ui.home


import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.VideoView
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import guinea.diego.launchervideoinnovation.R
import guinea.diego.launchervideoinnovation.utils.showLoadingDialog
import guinea.diego.launchervideoinnovation.data.models.Categoria
import guinea.diego.launchervideoinnovation.data.models.Proyectos
import guinea.diego.launchervideoinnovation.data.models.Values
import guinea.diego.launchervideoinnovation.ui.detail.DetailActivity
import guinea.diego.launchervideoinnovation.ui.presenter.CardPresenter
import guinea.diego.launchervideoinnovation.ui.presenter.IconHeaderItemPresenter
import guinea.diego.launchervideoinnovation.utils.Constants.TITLE_BROWSER
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.exoplayerview.*

import org.koin.android.ext.android.inject


class BrowserFragment : BrowseSupportFragment() {

    companion object {
        const val TAG = "BrowserFragment"
    }

    private lateinit var backgroundManager: BackgroundManager
    private var defaultBackground: Drawable? = null
    private lateinit var metrics: DisplayMetrics

//    private lateinit var mediaPlayer: MediaPlayer
//    private var mCurrentVideoPosition: Int = 0
//  //  private val mediaController = MediaController(context)
  //  private val videoView = activity?.findViewById<ExoPlayerView>(R.id.videoDeFondo)
    var values: Values? = null
    private var mRowsAdapter: ArrayObjectAdapter? = null
    private val viewModel by inject<BrowserFragmentViewModel>()
    private val proyectos: ArrayList<Proyectos> = arrayListOf()
    private val categorias: ArrayList<Categoria> = arrayListOf()
    private var loadingDialog: Dialog? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllData()
        ObserverMLD()
        setUpBrowser()

        showDialog()
    }
    private fun showDialog() {
        hideLoading()
        loadingDialog = activity?.showLoadingDialog()
    }
    private fun hideLoading() {
        loadingDialog?.let { if (it.isShowing) it.cancel() }
    }
    private fun stopAnimacion() {
        Handler().postDelayed({
            hideLoading()
        }, 1)
    }

    private fun ObserverMLD() {
        viewModel.valuesViewMLD.observe( viewLifecycleOwner , Observer {
            Respuesta(it)
        })
    }

    private fun Respuesta(respuesta: Values) {
        stopAnimacion()
        values = respuesta
        proyectos.addAll(respuesta.proyectos)
        categorias.addAll(respuesta.categorias)
        CardPresenter().setData(respuesta.proyectos as ArrayList<Proyectos>,
            respuesta.categorias as ArrayList<Categoria>)
        setupRows()
    }

    private fun setUpBrowser() {
        title = TITLE_BROWSER;
        badgeDrawable = activity?.resources?.getDrawable(R.drawable.speed);
        prepareBackgroundManager()
        setHeaderPresenterSelector(object: PresenterSelector(){
            override fun getPresenter(item: Any?): Presenter {
                return IconHeaderItemPresenter(categorias)
            }
        })
    }

    private fun setupRows() {
        val lrp = ListRowPresenter()
        mRowsAdapter = ArrayObjectAdapter(lrp)

        var i = 0
        val categorias = getCategories()
        Log.d(TAG, "Ctegorias: $categorias")

        categorias.forEach{ category ->
            val listRowAdapter = ArrayObjectAdapter(CardPresenter()).apply {
                val proyectsFromCategory = category?.let { getProyectsByCategory(it) }
                Log.d(TAG, "moviesFromCategory: $proyectsFromCategory")
                addAll(0,proyectsFromCategory)
            }
            HeaderItem(i.toLong(),category).also { header ->
                mRowsAdapter?.add(ListRow(header, listRowAdapter))
            }
            i++

        }
        adapter = mRowsAdapter
        onItemViewClickedListener = ItemViewClickedListener()
        onItemViewSelectedListener = ItemViewSelectedListener()
    }
    private fun getCategories() = proyectos.distinctBy { it.categoria }.map { it.categoria }
    private fun getProyectsByCategory(category: String) = proyectos.filter { it.categoria == category }

    private inner class ItemViewClickedListener: OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder?,
            item: Any?,
            rowViewHolder: RowPresenter.ViewHolder?,
            row: Row?) {

            if (item is Proyectos) {
                val intent = Intent(activity, DetailActivity::class.java)
                intent.putExtra("id", item.id)
                intent.putExtra("titulo", item.titulo)
                intent.putExtra("descripcion", item.descripcion)
                intent.putExtra("categoria", item.categoria)
                intent.putExtra("foto", item.foto)
                intent.putExtra("videoP", item.VideoPresentacion)
                intent.putExtra("videoE", item.VideoEntero)
                intent.putExtra("accion", item.accion)
                startActivity(intent)
            }
        }
    }
    override fun onPause() {
        super.onPause()
        activity?.videoDeFondo?.onPause()
    }

    override fun onResume() {
        super.onResume()
        activity?.videoDeFondo?.onResume()
    }

    private fun prepareBackgroundManager() {
       // activity?.textDescripcion?.text = ""
    //  activity?.videoDeFondo?.prepare(Uri.parse("https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4"))
//        backgroundManager = BackgroundManager.getInstance(activity)
////        backgroundManager.attach(activity?.window)
////        defaultBackground = activity?.let { ContextCompat.getDrawable(it, R.drawable.background_title) }
////        metrics = DisplayMetrics()
////        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
    }
    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(
            itemViewHolder: Presenter.ViewHolder?,
            item: Any?,
            rowViewHolder: RowPresenter.ViewHolder?,
            row: Row?) {
            if(item is Proyectos) {
                val backgroundImageUrl = Uri.parse(item.VideoPresentacion)
                updateBackground(backgroundImageUrl.toString())
            }

        }
    }
    private fun updateBackground(uri: String) {
        activity?.videoDeFondo?.prepare(Uri.parse("$uri"))
    }
}
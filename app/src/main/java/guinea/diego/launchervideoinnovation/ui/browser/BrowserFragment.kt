package guinea.diego.launchervideoinnovation.ui.browser

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.lifecycle.Observer
import guinea.diego.launchervideoinnovation.R
import guinea.diego.launchervideoinnovation.data.models.Categoria
import guinea.diego.launchervideoinnovation.data.models.PhotoItem
import guinea.diego.launchervideoinnovation.data.models.Proyectos
import guinea.diego.launchervideoinnovation.data.models.Values
import guinea.diego.launchervideoinnovation.utils.Constants.NUM_ROWS
import guinea.diego.launchervideoinnovation.utils.Constants.TITLE_BROWSER
import org.koin.android.ext.android.inject


class BrowserFragment : BrowseSupportFragment() {
    private var mRowsAdapter: ArrayObjectAdapter? = null
    private val viewModel by inject<BrowserFragmentViewModel>()
    //private val viewModel = BrowserFragmentViewModel()
    private val proyectos: ArrayList<Proyectos> = arrayListOf()
    private val categorias: ArrayList<Categoria> = arrayListOf()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllData()
        ObserverMLD()
        //set the title and badgeDrawable
        setUpBrowser()
        //set the rows, categories and content
        setupRows()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun ObserverMLD() {
        viewModel.valuesViewMLD.observe( this, Observer {
            Respuesta(it)
        })
    }

    private fun Respuesta(respuesta: Values) {
        proyectos.addAll(respuesta.proyectos)
        categorias.addAll(respuesta.categorias)
        CardPresenter().setData(respuesta.proyectos as ArrayList<Proyectos>,
            respuesta.categorias as ArrayList<Categoria>)
    }

    private fun setUpBrowser() {
        //set the title
        title = TITLE_BROWSER;
        badgeDrawable = activity?.resources?.getDrawable(R.mipmap.telefonica);


    }

    private fun setupRows() {
        val lrp = ListRowPresenter()

        mRowsAdapter = ArrayObjectAdapter(lrp)
        // For good performance, it's important to use a single instance of
        // a card presenter for all rows using that presenter.
        val cardPresenter = CardPresenter()

        for (i in 0 until NUM_ROWS) {
            val listRowAdapter = ArrayObjectAdapter(cardPresenter)
//            listRowAdapter.add(Proyectos(proyectos[i].id, "${proyectos[i].titulo}","${proyectos[i].descripcion}",
//                "${proyectos[i].categoria}","${proyectos[i].VideoPresentacion}","${proyectos[i].VideoEntero}",
//                "${proyectos[i].foto}"))
            listRowAdapter.add(PhotoItem("Testing 2", "anything here 2", ContextCompat.getDrawable(requireContext(),R.drawable.splashscreen)))
            //listRowAdapter.add(PhotoItem("Testing 3", "anything here 3", ContextCompat.getDrawable(requireContext(),R.drawable.movie)))

            val header = HeaderItem(i.toLong(), "categoria $i")

            mRowsAdapter!!.add(ListRow(header, listRowAdapter))
        }
        adapter = mRowsAdapter
    }

}
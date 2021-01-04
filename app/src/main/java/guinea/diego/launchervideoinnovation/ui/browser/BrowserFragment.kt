package guinea.diego.launchervideoinnovation.ui.browser


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.lifecycle.Observer
import guinea.diego.launchervideoinnovation.R
import guinea.diego.launchervideoinnovation.data.models.Categoria
import guinea.diego.launchervideoinnovation.data.models.Proyectos
import guinea.diego.launchervideoinnovation.data.models.Values
import guinea.diego.launchervideoinnovation.utils.Constants.TITLE_BROWSER
import org.koin.android.ext.android.inject


class BrowserFragment : BrowseSupportFragment() {

    companion object {
        const val TAG = "BrowserFragment"
    }
    var values: Values? = null
    private var mRowsAdapter: ArrayObjectAdapter? = null
    private val viewModel by inject<BrowserFragmentViewModel>()
    private val proyectos: ArrayList<Proyectos> = arrayListOf()
    private val categorias: ArrayList<Categoria> = arrayListOf()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllData()
        ObserverMLD()
        //set the title and badgeDrawable
        setUpBrowser()


    }

    private fun ObserverMLD() {
        viewModel.valuesViewMLD.observe( viewLifecycleOwner , Observer {
            Respuesta(it)
        })

    }

    private fun Respuesta(respuesta: Values) {
        values = respuesta
        proyectos.addAll(respuesta.proyectos)
        categorias.addAll(respuesta.categorias)
        CardPresenter().setData(respuesta.proyectos as ArrayList<Proyectos>,
            respuesta.categorias as ArrayList<Categoria>)
        setupRows()
    }

    private fun setUpBrowser() {
        //set the title
        title = TITLE_BROWSER;
        badgeDrawable = activity?.resources?.getDrawable(R.mipmap.telefonica);


    }

    private fun setupRows() {
        val lrp = ListRowPresenter()
        mRowsAdapter = ArrayObjectAdapter(lrp)

        var i = 0
        val categorias = getCategories()
        Log.d(TAG, "Ctegorias: $categorias")

        categorias.forEach{ category ->
            val listRowAdapter = ArrayObjectAdapter(CardPresenter()).apply {
                val proyectsFromCategory = getProyectsByCategory(category)
                Log.d(TAG, "moviesFromCategory: $proyectsFromCategory")
                addAll(0,proyectsFromCategory)
            }
            HeaderItem(i.toLong(),category).also { header ->
                mRowsAdapter?.add(ListRow(header, listRowAdapter))
            }
            i++

        }
        adapter = mRowsAdapter
    }
    private fun getCategories() = proyectos.distinctBy { it.categoria }.map { it.categoria }
    private fun getProyectsByCategory(category: String) = proyectos.filter { it.categoria == category }
}
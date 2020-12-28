package guinea.diego.launchervideoinnovation.ui.browser

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import guinea.diego.launchervideoinnovation.data.ValuesTVRepository
import guinea.diego.launchervideoinnovation.data.models.Values
import guinea.diego.launchervideoinnovation.utils.BaseCallback

class BrowserFragmentViewModel: ViewModel() {
    private val repositorio = Single.tvRepository()
    val valuesViewMLD = MutableLiveData<Values>()
    val errorViewMLD = MutableLiveData<Error>()

    fun getAllData(){
        repositorio.getValues(object : BaseCallback<Values> {
            override fun onResult(result: Values) {
                valuesViewMLD.value = result
            }
            override fun onError(error: Error) {
                errorViewMLD.value = error
            }
        })
    }
}
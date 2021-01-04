package guinea.diego.launchervideoinnovation.data

import guinea.diego.launchervideoinnovation.data.remoto.RetrofitInitializer
import guinea.diego.launchervideoinnovation.data.models.Values
import guinea.diego.launchervideoinnovation.utils.BaseCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ValuesTVRepository() {

    private val valuesService = RetrofitInitializer(PrincipalRepo).ValuesService()

    fun getValues(callback: BaseCallback<Values>){
        valuesService.callProyect().enqueue(object : Callback<Values>{
            override fun onResponse(call: Call<Values>, response: Response<Values>) {
                if (response.body() == null) {
                    callback.onError(Error("NO HAY DATOS"))
                } else {
                    callback.onResult(response.body()!!)
                }
            }
            override fun onFailure(call: Call<Values>, t: Throwable) {
                callback.onError(Error(t))
            }
        })
    }
}
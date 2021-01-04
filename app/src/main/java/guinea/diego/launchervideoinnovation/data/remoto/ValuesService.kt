

import guinea.diego.launchervideoinnovation.data.models.Values
import retrofit2.Call
import retrofit2.http.*


interface ValuesService {
    @GET("prueba1")
    fun callProyect() : Call<Values>
}


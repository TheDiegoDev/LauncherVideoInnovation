

import guinea.diego.launchervideoinnovation.data.models.Values
import retrofit2.Call
import retrofit2.http.*


interface ValuesService {
    @GET("dy6182femd4fbju/prueba2.json?dl=1")
    fun callProyect() : Call<Values>
}
//prueba1.json?dl=1
//a9b5c42c-8036-484f-87e5-dbd2515e4ba7


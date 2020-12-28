

import guinea.diego.launchervideoinnovation.data.models.Values
import retrofit2.Call
import retrofit2.http.*


interface ValuesService {
    @GET("725f24ab-05e1-4134-a34c-19154b00f018")
    fun callProyect() : Call<Values>
}


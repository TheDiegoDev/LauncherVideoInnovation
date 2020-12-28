
import com.ihsanbal.logging.LoggingInterceptor
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInitializer(repo: String) {
    private fun getLoggingInterceptor(): LoggingInterceptor {
        return LoggingInterceptor.Builder()
            .log(Platform.INFO)
            .request("Request")
            .response("Response")
            .build()
    }

    private fun getOkClient(): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(getLoggingInterceptor()).build()
    }


    private val retrofit = Retrofit.Builder()
        .baseUrl(repo)
        .client(getOkClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun ValuesService() = retrofit.create(ValuesService::class.java)

}
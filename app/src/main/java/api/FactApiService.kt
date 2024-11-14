
package api

import com.example.funfactgenerator.models.FactResponse
import retrofit2.Call
import retrofit2.http.GET

interface FactApiService {
    @GET("random.json?language=en")
    fun getRandomFact(): Call<FactResponse>
}
package com.eskisehir.eventapp.data.remote

import com.eskisehir.eventapp.data.model.InteractionRequest
import com.eskisehir.eventapp.data.model.InteractionType
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InteractionApiIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var interactionApi: InteractionApi
    private val gson = Gson()

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        interactionApi = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(InteractionApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testLogInteractionSuccess() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"interactionId":42}""")
        )

        val response = interactionApi.logInteraction(
            InteractionRequest(
                userId = 1L,
                poiId = 2L,
                interactionType = InteractionType.SHARE,
                comment = "rating=5"
            )
        )

        assert(response.interactionId == 42L)

        val request = mockWebServer.takeRequest()
        assert(request.path == "/interactions")
        assert(request.method == "POST")
        assert(request.body.readUtf8().contains("SHARE"))
    }
}
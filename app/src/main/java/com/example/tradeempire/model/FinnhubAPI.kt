package com.example.tradeempire.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface FinnhubApi {
    @GET("quote")
    suspend fun getQuote(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): QuoteResponse

    @GET("stock/symbol")
    suspend fun getSymbols(
        @Query("exchange") exchange: String,
        @Query("token") token: String
    ): List<SymbolInfo>
}
interface TwelveDataApi{
    @GET("time_series")
    suspend fun getTwelveDataCandles(
        @Query("symbol") symbol: String,
        @Query("interval") interval: String,
        @Query("outputsize") outputsize: Int,
        @Query("apikey") apiKey: String
    ): TwelveDataResponse

    //key: 71f3398c2ea94691ac223325bc5ae3ba
}

object ApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://finnhub.io/api/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: FinnhubApi = retrofit.create(FinnhubApi::class.java)

    private val retrofitTD = Retrofit.Builder()
        .baseUrl("https://api.twelvedata.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val serviceTD: TwelveDataApi = retrofitTD.create(TwelveDataApi::class.java)
}


data class QuoteResponse(
    val c: Double = 0.0,
    val dp: Double = 0.0,
    val h: Double = 0.0,
    val l: Double = 0.0,
    val o: Double = 0.0,
    val pc: Double = 0.0,
    val t: Int = 0
)
data class SymbolInfo(
    val currency: String = "",
    val description: String = "",
    val displaySymbol: String = "",
    val symbol: String = ""
)
data class TwelveDataResponse(
    val values: List<CandleValue>,
    val status: String
)

data class CandleValue(
    val datetime: String,
    val open: String,
    val high: String,val low: String,
    val close: String
)

package com.example.tradeempire.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tradeempire.R
import com.example.tradeempire.model.ApiClient
import com.example.tradeempire.model.AppDatabase
import com.example.tradeempire.model.InstrumentAdapter
import com.example.tradeempire.model.QuoteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomepageFragment : Fragment() {

    private val adapter = InstrumentAdapter(arrayListOf())
    private var lastPriceUpdate: Long = 0

    private val updateCooldown: Long = 60 * 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_homepage, container, false)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerList)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(requireContext())

        return view
    }

    override fun onResume() {
        super.onResume()
        getPrices()
    }

    private fun getPrices() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val instruments: ArrayList<Array<String>> = arrayListOf()
            val db = AppDatabase.getDatabase(requireContext())
            val quoteDao = db.quoteDao()

            try {
                val currentTime = System.currentTimeMillis()
                val subscribedList = quoteDao.getSubscribedSymbols()

                if (currentTime - lastPriceUpdate < updateCooldown && lastPriceUpdate != 0L) {
                    Log.d("HomepageFragment", "Loading from DB (Cache)")

                    for (subEntity in subscribedList) {
                        val cachedQuote = quoteDao.getQuoteBySymbol(subEntity.symbol)
                        if (cachedQuote != null) {
                            instruments.add(
                                arrayOf(
                                    cachedQuote.symbol,
                                    cachedQuote.dp.toString(),
                                    cachedQuote.currentPrice.toString()
                                )
                            )
                        }
                    }
                } else {
                    Log.d("HomepageFragment", "Fetching from API")

                    for (subEntity in subscribedList) {
                        val symbolStr = subEntity.symbol
                        try {
                            val quoteData = ApiClient.service.getQuote(
                                symbolStr,
                                "d4ivrtpr01queuakr160d4ivrtpr01queuakr16g"
                            )

                            val price = quoteData.c
                            val percentChange = quoteData.dp

                            val quoteEntity = QuoteEntity(
                                symbol = symbolStr,
                                currentPrice = price,
                                highPrice = quoteData.h,
                                lowPrice = quoteData.l,
                                openPrice = quoteData.o,
                                dp = quoteData.dp,
                                timestamp = quoteData.t,
                                cachedAt = System.currentTimeMillis()
                            )

                            quoteDao.insertQuote(quoteEntity)

                            instruments.add(
                                arrayOf(
                                    symbolStr,
                                    percentChange.toString(),
                                    price.toString()
                                )
                            )
                        } catch (e: Exception) {
                            Log.e("HomepageFragment", "Failed to fetch/save $symbolStr: ${e.message}")
                        }
                    }
                    lastPriceUpdate = System.currentTimeMillis()
                }

                withContext(Dispatchers.Main) {
                    adapter.updateData(instruments)
                }
            } catch (e: Exception) {
                Log.e("HomepageFragment", "Error in getPrices: ${e.message}")
            }
        }
    }

}
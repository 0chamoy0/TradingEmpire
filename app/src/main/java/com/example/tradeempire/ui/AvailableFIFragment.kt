package com.example.tradeempire.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tradeempire.R
import com.example.tradeempire.model.AddInstrumentAdapter
import com.example.tradeempire.model.ApiClient
import com.example.tradeempire.model.AppDatabase
import com.example.tradeempire.model.AddInstrumentData
import com.example.tradeempire.model.SubSymbolEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AvailableFIFragment : Fragment() {
    private var fullSymbolsList: List<AddInstrumentData> = arrayListOf()
    private val adapter = AddInstrumentAdapter(arrayListOf()) { symbolToAdd ->
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(requireContext())
            db.quoteDao().insertSubscribedSymbol(SubSymbolEntity(symbol = symbolToAdd))
            Log.d("AvailableFI", "Subscribed to $symbolToAdd")
            getAllSymbols()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerSecondList)
        val searchView = view.findViewById<androidx.appcompat.widget.SearchView>(R.id.searchView)

        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })

        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(requireContext())
        getAllSymbols()

        return view
    }

    private fun getAllSymbols() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(requireContext())
            val quoteDao = db.quoteDao()

            val instruments: ArrayList<AddInstrumentData> = arrayListOf()

            try {
                var allSymbols = quoteDao.getAllSymbols()
                val subscribedSymbols = quoteDao.getSubscribedSymbols()
                val subscribedSet = subscribedSymbols.map { it.symbol }.toHashSet()

                if (allSymbols.isEmpty()) {
                    Log.d("AvailableFIFragment", "DB empty, fetching symbols from API...")
                    try {
                        val apiSymbols = ApiClient.service.getSymbols("US", "d4ivrtpr01queuakr160d4ivrtpr01queuakr16g")
                        val symbolEntities = apiSymbols.map {
                            com.example.tradeempire.model.SymbolEntity(
                                symbol = it.symbol,
                                currency = it.currency,
                                description = it.description,
                                displaySymbol = it.displaySymbol
                            )
                        }
                        quoteDao.insertAllSymbols(symbolEntities)

                        allSymbols = quoteDao.getAllSymbols()
                    } catch (e: Exception) {
                        Log.e("AvailableFIFragment", "API Error: ${e.message}")
                    }
                }
                for (symbolEntity in allSymbols) {
                    val isSubscribed = subscribedSet.contains(symbolEntity.symbol)
                    instruments.add(
                        AddInstrumentData(
                            addSymbolName = symbolEntity.symbol,
                            isSubscribed = isSubscribed
                        )
                    )
                }
                withContext(Dispatchers.Main) {
                    fullSymbolsList = instruments
                    adapter.updateData(instruments)
                }

            } catch (e: Exception) {
                Log.e("AvailableFIFragment", "Error fetching symbols: ${e.message}")
            }
        }
    }
    private fun filterList(query: String?) {
        val filteredList = if (query.isNullOrBlank()) {
            fullSymbolsList // Show everything if search is empty
        } else {
            fullSymbolsList.filter {
                it.addSymbolName.contains(query, ignoreCase = true)
            }
        }
        adapter.updateData(ArrayList(filteredList))
    }
}




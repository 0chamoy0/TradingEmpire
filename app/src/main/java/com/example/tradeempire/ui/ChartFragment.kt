package com.example.tradeempire.ui
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tradeempire.R
import com.example.tradeempire.model.ApiClient
import com.example.tradeempire.model.AppDatabase
import com.example.tradeempire.model.TwelveDataResponse
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ChartFragment : Fragment() {
    private lateinit var chart: CandleStickChart
    private lateinit var symbolTitle: TextView
    private var currentSymbol: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chart, container, false)
        chart = view.findViewById(R.id.candleStickChart)
        symbolTitle = view.findViewById(R.id.chartSymbolTitle)

        symbolTitle.text = "Loading..."
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSelectedSymbol()
    }

    private fun loadSelectedSymbol() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(requireContext())
            val savedSymbol = db.quoteDao().getChartSymbol()?.symbol
            withContext(Dispatchers.Main) {
                if (savedSymbol != null) {
                    updateUI(savedSymbol)
                } else {
                    symbolTitle.text = "No symbol selected"
                }
            }
        }
    }

    private fun updateUI(symbol: String) {
        this.currentSymbol = symbol
        symbolTitle.text = symbol
        fetchCandleData(symbol)
    }
    private fun fetchCandleData(symbol: String) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val apiKey = "71f3398c2ea94691ac223325bc5ae3ba"

                val response = ApiClient.serviceTD.getTwelveDataCandles(
                    symbol,
                    "1day",
                    50,
                    apiKey
                )

                if (response.status == "ok") {
                    withContext(Dispatchers.Main) {
                        setupChart(response, symbol)
                    }
                }
            } catch (e: Exception) {
                Log.e("ChartFragment", "Error: ${e.message}")
            }
        }
    }
    private fun setupChart(data: TwelveDataResponse, symbol: String) {
        val entries = ArrayList<CandleEntry>()
        val reversedList = data.values.reversed()

        for (i in reversedList.indices) {
            val candle = reversedList[i]
            entries.add(CandleEntry(
                i.toFloat(),
                candle.high.toFloat(),
                candle.low.toFloat(),
                candle.open.toFloat(),
                candle.close.toFloat()
            ))
        }

        val dataSet = CandleDataSet(entries, "$symbol Data").apply {
            color = Color.rgb(80, 80, 80)
            shadowColor = Color.DKGRAY
            shadowWidth = 0.7f
            decreasingColor = ContextCompat.getColor(requireContext(), R.color.red)
            decreasingPaintStyle = Paint.Style.FILL
            increasingColor = ContextCompat.getColor(requireContext(), R.color.green)
            increasingPaintStyle = Paint.Style.FILL
            neutralColor = Color.LTGRAY
            setDrawValues(false)
        }

        chart.apply {
            this.data = CandleData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            requestDisallowInterceptTouchEvent(true)
            invalidate()
        }
    }

}
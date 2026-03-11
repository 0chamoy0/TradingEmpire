package com.example.tradeempire.model
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.tradeempire.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InstrumentAdapter(private val dataSet: ArrayList<Array<String>>) :
    RecyclerView.Adapter<InstrumentAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val symbolView: TextView = view.findViewById(R.id.symbolName)
        val percentageView: TextView = view.findViewById(R.id.percentage)
        val priceView: TextView = view.findViewById(R.id.price)
        val viewChartButton: Button = view.findViewById(R.id.viewChartButton)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.instrument_list_element, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val context = viewHolder.itemView.context
        viewHolder.symbolView.text = dataSet[position][0]

        val change = dataSet[position][1].toDoubleOrNull()
        val symbol = dataSet[position][0]

        if (change != null) {
            viewHolder.percentageView.text = String.format("%.2f", change) + " %"

            if (change >= 0) {
                viewHolder.percentageView.setTextColor(
                    ContextCompat.getColor(context, R.color.green)
                )
            } else {
                viewHolder.percentageView.setTextColor(
                    ContextCompat.getColor(context, R.color.red)
                )
            }
        }
        viewHolder.priceView.text = "$" + dataSet[position][2]
        viewHolder.viewChartButton.setOnClickListener {
            val activity = viewHolder.itemView.context as? androidx.appcompat.app.AppCompatActivity
            activity?.lifecycleScope?.launch(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(activity)
                db.quoteDao().clearChartSymbols()
                db.quoteDao().insertChartSymbol(ChartSymbolEntity(symbol = symbol))
                launch(Dispatchers.Main) {
                    val bottomNav = activity.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)
                    bottomNav?.selectedItemId = R.id.nav_chart
                }
            }

        }
    }
    override fun getItemCount() = dataSet.size
    fun updateData(newItems: ArrayList<Array<String>>) {
        dataSet.clear()
        dataSet.addAll(newItems)
        notifyDataSetChanged()
    }

}
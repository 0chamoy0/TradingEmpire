package com.example.tradeempire.model
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tradeempire.R

class InstrumentAdapter(private val dataSet: ArrayList<Array<String>>) :
    RecyclerView.Adapter<InstrumentAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val symbolView: TextView = view.findViewById(R.id.symbolName)
        val upView: TextView = view.findViewById(R.id.upPercentage)
        val downView: TextView = view.findViewById(R.id.downPercentage)
        val priceView: TextView = view.findViewById(R.id.price)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.instrument_list_element, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.symbolView.text = dataSet[position][0]
        val change = dataSet[position][1].toDoubleOrNull()

        if (change!==null && change>=0){
            viewHolder.upView.text = String.format("%.2f", change) + " %"
            viewHolder.downView.visibility = View.INVISIBLE
        } else {
            viewHolder.downView.text = String.format("%.2f", change) + " %"
            viewHolder.upView.visibility = View.INVISIBLE
        }
        viewHolder.priceView.text = "$" + dataSet[position][2]
    }
    override fun getItemCount() = dataSet.size
    fun updateData(newItems: ArrayList<Array<String>>) {
        dataSet.clear()
        dataSet.addAll(newItems)
        notifyDataSetChanged()
    }

}
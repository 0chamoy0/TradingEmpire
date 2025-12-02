package com.example.tradeempire.model
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tradeempire.R

data class AddInstrumentData(
    val addSymbolName: String,
    val isSubscribed: Boolean
)
class AddInstrumentAdapter(private val dataSet: ArrayList<AddInstrumentData>, private val onAddClick: (String) -> Unit ) :
    RecyclerView.Adapter<AddInstrumentAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val symbolView: TextView = view.findViewById(R.id.addSymbolName)
        val addButton: Button = view.findViewById(R.id.addButton)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.instrument_list_addelement, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val currentItem = dataSet[position]
        viewHolder.symbolView.text = dataSet[position].addSymbolName
        viewHolder.addButton.visibility = if (dataSet[position].isSubscribed) View.INVISIBLE else View.VISIBLE
        viewHolder.addButton.setOnClickListener {
            onAddClick(currentItem.addSymbolName)
            viewHolder.addButton.visibility = View.INVISIBLE
        }
    }
    override fun getItemCount() = dataSet.size
    fun updateData(newItems: ArrayList<AddInstrumentData>) {
        dataSet.clear()
        dataSet.addAll(newItems)
        notifyDataSetChanged()
    }

}
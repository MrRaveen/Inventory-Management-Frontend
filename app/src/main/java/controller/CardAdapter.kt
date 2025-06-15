package controller

import Model.Folders
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.R

class CardAdapter(private val inventoryList : List<Folders>) :
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view){
       val cardItem: CardView = view.findViewById(R.id.cardItem)
        val mainHeader: TextView = itemView.findViewById(R.id.mainHeader)
        val description: TextView = itemView.findViewById(R.id.description)
        val removeBtn: Button = itemView.findViewById(R.id.buttonRemove)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CardAdapter.CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_component,parent,false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardAdapter.CardViewHolder, position: Int) {
        val item = inventoryList[position]
        holder.mainHeader.text = item.name
        holder.description.text = item.description
        val context = holder.itemView.context
        holder.removeBtn.setOnClickListener {
        //Toast.makeText(context, "clicked : ${item.name}", Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int {
       return inventoryList.size
    }
}
package controller

import Model.Folders
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.R
import android.content.res.Resources
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlin.getValue

class CardAdapter(private val inventoryList : List<Folders>, private val folderOperationsObj : folderOperations, private var resources: Resources) :
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view){
       val cardItem: CardView = view.findViewById(R.id.cardItem)
        val mainHeader: TextView = itemView.findViewById(R.id.mainHeader)
        val description: TextView = itemView.findViewById(R.id.description)
        val removeBtn: Button = itemView.findViewById(R.id.buttonRemove)
        val viewButton: Button = itemView.findViewById(R.id.buttonView)
        val updateButton: Button = itemView.findViewById(R.id.buttonUpdate)
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
        // Get credentials
        val PREF_NAME = "prefs"
        val KEY_NAME = "userid"
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val savedUserID = sharedPref.getString(KEY_NAME, null) ?: "0" //userID
        val token = decript_access().accessToken(context) ?: "" //token
        holder.removeBtn.setOnClickListener {
            //send the remove request with the ID
            val dialog = AlertDialog.Builder(context)
                .setTitle("Confirmation")
                .setMessage("Do you want to proceed?")
                .setPositiveButton("Yes") { _, _ ->
                    (context as? LifecycleOwner)?.lifecycleScope?.launch {
                        try {
                            val loadingDialog = Dialog(context)
                            loadingDialog.setContentView(R.layout.custom_dialog)
                            loadingDialog.window?.setLayout(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            loadingDialog.setCancelable(false)
                            loadingDialog.show()
                            val removeResult = folderOperationsObj.handleRemove(item.folderID, resources, token)
                            if (removeResult) {
                                Toast.makeText(context, "Removed successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Removal failed", Toast.LENGTH_SHORT).show()
                            }
                            loadingDialog.dismiss()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                .setNegativeButton("No") { _, _ ->

                }
                .create()
            dialog.show()
        }
        holder.viewButton.setOnClickListener {

        }
        holder.updateButton.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
       return inventoryList.size
    }
}
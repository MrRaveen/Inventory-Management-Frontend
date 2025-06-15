package controller

import Model.Folders
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.gallery.MainActivity
import com.example.gallery.homeMenu
import com.example.gallery.updateFolder
import kotlinx.coroutines.launch
import kotlin.getValue
import kotlin.jvm.java

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
                                refreshFragment(context)
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
            try{
                val intent = Intent(context, updateFolder::class.java).apply {
                    putExtra("folderID", item.folderID)
                }
                context.startActivity(intent)

            }catch(e : Exception){
                Log.e("TAG","ERROR!!! : ${e.toString()}")
                throw Exception("${e.toString()}")
            }
        }
    }
    fun refreshFragment(context: Context?) {
        context?.let {
            val fragmentManager = (context as? AppCompatActivity)?.supportFragmentManager
            fragmentManager?.let {
                val currentFragment = fragmentManager.findFragmentById(R.id.mainHomeSection)
                currentFragment?.let {
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.detach(it)
                    fragmentTransaction.attach(it)
                    fragmentTransaction.commit()
                }
            }
        }
    }
    override fun getItemCount(): Int {
       return inventoryList.size
    }
}
package com.example.gallery

import Model.Folders
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import controller.CardAdapter
import controller.decript_access
import controller.folderOperations
import controller.getAllFolders
import controller.handleLogin
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [homePart.newInstance] factory method to
 * create an instance of this fragment.
 */
class homePart : Fragment() {
    val PREF_NAME = "prefs"
    val KEY_NAME = "userid"
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //executes the normal code here

        //loading dialog
        val context = requireContext()
        val loadingDialog = Dialog(context)
        loadingDialog.setContentView(R.layout.custom_dialog)
        loadingDialog.window?.setLayout(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        loadingDialog.setCancelable(false)
        loadingDialog.show()
        //send the request
        val requestObj : getAllFolders by viewModels()
        var result2 : List<Folders>?
        lifecycleScope.launch {
            try {
                // Get credentials
                val sharedPref = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                val savedUserID = sharedPref.getString(KEY_NAME, null) ?: "0"
                val token = decript_access().accessToken(requireContext()) ?: ""

                // Fetch folders
                val requestObj: getAllFolders by viewModels()
                val folders = requestObj.handleFolderGet(
                    savedUserID.toInt(),
                    token,
                    resources
                )

                // Process results
                folders?.let {
                    Log.d("homePart", "Received ${it.size} folders")

                    // TODO: Update your UI here (e.g., set to RecyclerView adapter)
                    // adapter.submitList(it)
                    val recyclerViewOut = view.findViewById<RecyclerView>(R.id.recycleviewHomePart)
                    recyclerViewOut.layoutManager = LinearLayoutManager(context)
                    val folderOperationObj: folderOperations by viewModels() //in here
                    recyclerViewOut.adapter = CardAdapter(folders,folderOperationObj, resources)
                    // Show success message
                    Toast.makeText(
                        requireContext(),
                        "Fetched ${it.size} folders",
                        Toast.LENGTH_SHORT
                    ).show()
                } ?: run {
                    Toast.makeText(
                        requireContext(),
                        "No folders found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("homePart", "Error loading folders", e)
                Toast.makeText(
                    requireContext(),
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                loadingDialog.dismiss()
            }
        }
        //loadingDialog.dismiss()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_part, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment homePart.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            homePart().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
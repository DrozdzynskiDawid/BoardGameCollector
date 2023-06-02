package edu.put.inf151867

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import edu.put.inf151867.databinding.FragmentFirstBinding
import java.io.File

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dbFile = File("/data/data/edu.put.inf151867/databases/bgcDB.db")
        val databaseExists = dbFile.exists()
        if (!databaseExists) {
            findNavController().navigate(R.id.action_FirstFragment_to_configFragment)
        }

        binding.syncButton.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.gameButton.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_gamesFragment)
        }

        binding.extraButton.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_extrasFragment)
        }

        binding.clearButton.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(getString(R.string.clearAlert))
                .setCancelable(false)
                .setPositiveButton("TAK") {
                        dialog, id ->
                            activity?.finish()
                            activity?.applicationContext?.deleteDatabase(MyDBHandler.DATABASE_NAME)
                            val folder = File("${context?.filesDir}/XML")
                            if (folder.exists()) folder.deleteRecursively()
                            val tempImagesDir = File("${context?.filesDir}/images/")
                            if (tempImagesDir.exists()) tempImagesDir.deleteRecursively()
                }
                .setNegativeButton("NIE") {
                        dialog, id -> dialog.cancel()
                }
            val alert = builder.create()
            alert.show()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        binding.syncText.text = getString(R.string.syncText) + " " + MainActivity.LAST_SYNC_DATE
        binding.userText.text = getString(R.string.userText) + " " + MainActivity.USERNAME
        // Liczba gier i dodatków
        binding.gamesNumber.text = getString(R.string.gameText) + " " + MainActivity.GAMES_NUMBER
        binding.extrasNumber.text = getString(R.string.extrasText) + " " + MainActivity.EXPANSIONS_NUMBER
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
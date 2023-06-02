package edu.put.inf151867

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import edu.put.inf151867.databinding.FragmentExtrasBinding

class ExpansionsFragment : Fragment() {
    private var _binding: FragmentExtrasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExtrasBinding.inflate(inflater, container, false)
        showGames(null)
        binding.sortByTitleButton.setOnClickListener {
            showGames("title")
        }
        binding.sortByYearButton.setOnClickListener {
            showGames("publishedYear")
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showGames(sort: String?) {
        val table = binding.expansionsTable
        table.removeAllViews()
        val dbHandler = activity?.let { MyDBHandler(it.applicationContext, null, null, 1) }
        val gamesList: MutableList<Game>? = dbHandler?.findGames("boardgameexpansion",sort)
        var id = 1
        val header = TableRow(activity)
        val header1 = TextView(activity)
        val header2 = TextView(activity)
        val header3 = TextView(activity)
        header.gravity = Gravity.CENTER
        header1.text = "Lp."
        header1.textSize = 20F
        header1.setTypeface(null, Typeface.BOLD)
        header1.setPadding(25,25,25,25)
        header1.setBackgroundResource(R.drawable.row_borders)
        header2.text = "Miniaturka"
        header2.textSize = 20F
        header2.isSingleLine = false
        header2.setTypeface(null, Typeface.BOLD)
        header2.setPadding(25,25,25,25)
        header2.setBackgroundResource(R.drawable.row_borders)
        header3.text = "Tytuł (Rok wydania)"
        header3.textSize = 20F
        header3.isSingleLine = false
        header3.setTypeface(null, Typeface.BOLD)
        header3.setPadding(25,25,25,25)
        header3.setBackgroundResource(R.drawable.row_borders)
        header.addView(header1)
        header.addView(header2)
        header.addView(header3)
        table.addView(header)
        if (gamesList != null) {
            for (game in gamesList) {
                val tableRow = TableRow(activity)
                val column1 = TextView(activity)
                val column2 = ImageView(activity)
                val column3 = TextView(activity)
                tableRow.gravity = Gravity.CENTER
                column1.textSize = 20F
                column1.text = "$id."
                column1.height = 250
                column1.gravity = Gravity.CENTER
                column1.setPadding(25, 25, 25, 25)
                column1.setBackgroundResource(R.drawable.row_borders)
                tableRow.addView(column1)
                column2.setPadding(25, 25, 25, 25)
                column2.setBackgroundResource(R.drawable.row_borders)
                val url = game.thumbnail.toString()
                column2.load(url) {
                    placeholder(R.drawable.ic_launcher_background)
                    error(R.drawable.ic_launcher_background)
                }
                column2.minimumHeight = 250
                tableRow.addView(column2)
                column3.setHorizontallyScrolling(false)
                column3.ellipsize = null
                column3.width = 0
                column3.textSize = 15F
                column3.height = 250
                column3.gravity = Gravity.CENTER
                column3.setPadding(25, 25, 25, 25)
                if (game.publishedYear == 0) {
                    column3.text = "${game.title} (brak danych)"
                }
                else {
                    column3.text = "${game.title} (${game.publishedYear})"
                }
                column3.setBackgroundResource(R.drawable.row_borders)
                tableRow.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("title",game.title)
                    bundle.putString("thumbnail",game.thumbnail)
                    bundle.putString("id",game.bggID.toString())
                    findNavController().navigate(R.id.action_ExtrasFragment_to_gameDetailsFragment,bundle)
                }
                tableRow.addView(column3)
                table.addView(tableRow)
                id += 1
            }
        }
    }
}
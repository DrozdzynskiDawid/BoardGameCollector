package edu.put.inf151867

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import edu.put.inf151867.databinding.FragmentConfigBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileWriter
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory


/**
 * A simple [Fragment] subclass.
 * Use the [ConfigFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class ConfigFragment : Fragment() {
    private var _binding: FragmentConfigBinding? = null
    private val binding get() = _binding!!
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentConfigBinding.inflate(inflater, container, false)
        dialog = ProgressDialog(context)
        binding.buttonConfig.setOnClickListener { configDB() }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun configDB() {
        val username = binding.username.text.toString()
        if (username != "") {
            dialog?.setMessage(getString(R.string.progressDialog))
            dialog?.setCancelable(false)
            dialog?.setInverseBackgroundForced(false)
            dialog?.show()
            downloadFile(username, "boardgame")
            downloadFile(username, "boardgameexpansion")
        }
    }

    private fun downloadFile(username: String, type: String) {
        val urlString = if (type == "boardgameexpansion") {
            "https://boardgamegeek.com/xmlapi2/collection?username=${username}&subtype=${type}"
        } else {
            "https://boardgamegeek.com/xmlapi2/collection?username=${username}"
        }
        val xmlDirectory = File("${context?.filesDir}/XML/")
        if(!xmlDirectory.exists()) xmlDirectory.mkdir()
        val filename = "$xmlDirectory/${type}.xml"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(urlString)
                val reader = url.openStream().bufferedReader()
                val downloadFile = File(filename).also { it.createNewFile() }
                val writer = FileWriter(downloadFile).buffered()
                var line: String
                while (reader.readLine().also { line = it?.toString() ?: "" } != null)
                    writer.write(line)
                reader.close()
                writer.close()

                withContext(Dispatchers.Main) {
                    val dbHandler = activity?.let { MyDBHandler(it.applicationContext, null, null, 1) }
                    val gameList = loadData(type)
                    for (game in gameList) {
                        dbHandler?.addGame(game)
                    }
                    MainActivity.GAMES_NUMBER = dbHandler?.countGames("boardgame").toString()
                    MainActivity.EXPANSIONS_NUMBER = dbHandler?.countGames("boardgameexpansion").toString()
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                    val currentDate = sdf.format(Date())
                    MainActivity.LAST_SYNC_DATE = currentDate
                    MainActivity.USERNAME = username
                    dialog?.dismiss()
                    findNavController().navigate(R.id.action_configFragment_to_FirstFragment)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    when (e) {
                        is MalformedURLException ->
                            print("Malformed URL")
                        else ->
                            print("Error")
                    }
                    val incompleteFile = File(filename)
                    if (incompleteFile.exists()) incompleteFile.delete()
                }
            }
        }
    }

    private fun loadData(type: String): MutableList<Game> {
        val games: MutableList<Game> = mutableListOf()

        val filename = "${type}.xml"
        val path = context?.filesDir
        val inDir = File(path,"XML")

        if (inDir.exists()) {
            val file = File(inDir,filename)
            if (file.exists()) {
                val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)

                xmlDoc.documentElement.normalize()

                val items: NodeList = xmlDoc.getElementsByTagName("item")

                for (i in 0..items.length-1) {
                    val itemNode: Node = items.item(i)
                    if (itemNode.nodeType == Node.ELEMENT_NODE) {
                        val elem = itemNode as Element
                        val children = elem.childNodes
                        val attributes = elem.attributes
                        var currentID:String? = null

                        for (k in 0..attributes.length-1) {
                            val node = attributes.item(k)
                            if (node.nodeName=="objectid") {
                                currentID = node.textContent
                            }
                        }

                        var currentTitle:String? = null
                        var currentYear:String? = null
                        var currentThumbnail:String? = null

                        for (j in 0..children.length-1) {
                            val node = children.item(j)
                            if (node is Element) {
                                when (node.nodeName) {
                                    "name" -> {
                                        currentTitle = node.textContent
                                    }
                                    "thumbnail" -> {
                                        currentThumbnail = node.textContent
                                    }
                                    "yearpublished" -> {
                                        currentYear = node.textContent
                                    }
                                }
                            }
                        }

                        if (currentTitle!=null) {
                            val g = Game(type,currentTitle,currentYear?.toInt(),currentID?.toInt(),currentThumbnail)
                            games.add(g)
                        }
                    }
                }
            }
        }
        return games
    }

}
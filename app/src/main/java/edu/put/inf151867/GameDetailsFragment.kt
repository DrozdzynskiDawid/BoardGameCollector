package edu.put.inf151867

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import coil.load
import edu.put.inf151867.databinding.FragmentGameDetailsBinding
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
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory


/**
 * A simple [Fragment] subclass.
 * Use the [GameDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameDetailsFragment : Fragment() {

    private var _binding: FragmentGameDetailsBinding? = null
    private val binding get() = _binding!!
    private var imageId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGameDetailsBinding.inflate(inflater, container, false)

        binding.gameTitle.text = arguments?.getString("title")
        val imageUrl = arguments?.getString("thumbnail")
        binding.image.load(imageUrl) {
            placeholder(R.drawable.ic_launcher_background)
            error(R.drawable.ic_launcher_background)
        }
        val bggID = arguments?.getString("id")
        downloadFile(bggID)
        val tempImagesDir = File("${context?.filesDir}/images/")
        val currentImagesDir = File(tempImagesDir,"${bggID}/")
        if (currentImagesDir.listFiles() != null) {
            var i = 0
            for (file in currentImagesDir.listFiles()) {
                if (i == 0) {
                    binding.img1.setImageURI(file.toUri())
                }
                if (i == 1) {
                    binding.img2.setImageURI(file.toUri())
                }
                if (i == 2) {
                    binding.img3.setImageURI(file.toUri())
                }
                i += 1
            }
        }
        binding.img1.setLayoutParams(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
        binding.img1.setAdjustViewBounds(true)
        binding.img2.setLayoutParams(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
        binding.img2.setAdjustViewBounds(true)
        binding.img3.setLayoutParams(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
        binding.img3.setAdjustViewBounds(true)
        var imgid = 0
        var tempImageUri = initTempUri(bggID)
        val resultLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                when (imgid) {
                    1 -> binding.img1.setImageURI(tempImageUri)
                    2 -> binding.img2.setImageURI(tempImageUri)
                    3 -> binding.img3.setImageURI(tempImageUri)
                }
            }
        }

        val mGetContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
            result ->
            if (result != null) {
                when (imgid) {
                    1 -> binding.img1.setImageURI(result)
                    2 -> binding.img2.setImageURI(result)
                    3 -> binding.img3.setImageURI(result)
                }
            }
        }
        binding.takePhoto.setOnClickListener {
            imgid += 1
            tempImageUri = initTempUri(bggID)
            resultLauncher.launch(tempImageUri)
        }

        binding.getPhoto.setOnClickListener {
            imgid += 1
            mGetContent.launch("image/*")
        }

        binding.deletePhoto.setOnClickListener {
            imgid = 0
            binding.img1.setImageURI(null)
            binding.img2.setImageURI(null)
            binding.img3.setImageURI(null)
            val tempImagesDir = File("${context?.filesDir}/images/")
            val currentImagesDir = File(tempImagesDir,"${bggID}/")
            currentImagesDir.deleteRecursively()
        }

        var zoomOut1 = false
        var zoomOut2 = false
        var zoomOut3 = false
        binding.img1.setOnClickListener() {
            if (zoomOut1) {
                binding.img1.setLayoutParams(
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                )
                binding.img1.setAdjustViewBounds(true)
                zoomOut1 = false
            } else {
                binding.img1.setLayoutParams(
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                )
                binding.img1.setScaleType(ImageView.ScaleType.FIT_XY)
                zoomOut1 = true
            }
        }

        binding.img2.setOnClickListener() {
            if (zoomOut2) {
                binding.img2.setLayoutParams(
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                )
                binding.img2.setAdjustViewBounds(true)
                zoomOut2 = false
            } else {
                binding.img2.setLayoutParams(
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                )
                binding.img2.setScaleType(ImageView.ScaleType.FIT_XY)
                zoomOut2 = true
            }
        }

        binding.img3.setOnClickListener() {
            if (zoomOut3) {
                binding.img3.setLayoutParams(
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                )
                binding.img3.setAdjustViewBounds(true)
                zoomOut3 = false
            } else {
                binding.img3.setLayoutParams(
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                )
                binding.img3.setScaleType(ImageView.ScaleType.FIT_XY)
                zoomOut3 = true
            }
        }

        return binding.root
    }

    private fun initTempUri(id: String?): Uri {
        val tempImagesDir = File("${context?.filesDir}/images/")
        tempImagesDir.mkdir()
        val currentImagesDir = File(tempImagesDir,"${id}/")
        currentImagesDir.mkdir()

        val imgId = imageId
        imageId += 1

        val tempImage = File(
            currentImagesDir,
            "$imgId.jpg"
        )

        return FileProvider.getUriForFile(requireContext(),context?.packageName+".fileprovider",tempImage)
    }

    private fun downloadFile(bggID: String?) {
        val urlString = "https://boardgamegeek.com/xmlapi2/thing?id=${bggID}&stats=1"
        val xmlDirectory = File("${context?.filesDir}/XML/")
        if(!xmlDirectory.exists()) xmlDirectory.mkdir()
        val filename = "$xmlDirectory/${bggID}.xml"

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
                    val description = loadDescription(bggID)
                    binding.gameDescription.text = description
                    val rank = loadRank(bggID)
                    binding.rankText.text = getString(R.string.rankText) + " " + rank.toString()
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

    private fun loadDescription(bggID: String?): String? {
        val filename = "${bggID}.xml"
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

                        var currentDescription: String? = null

                        for (j in 0..children.length-1) {
                            val node = children.item(j)
                            if (node is Element) {
                                when (node.nodeName) {
                                    "description" -> {
                                        currentDescription = node.textContent
                                        return currentDescription
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return ""
    }

    private fun loadRank(bggID: String?): String? {
        val filename = "${bggID}.xml"
        val path = context?.filesDir
        val inDir = File(path,"XML")

        if (inDir.exists()) {
            val file = File(inDir,filename)
            if (file.exists()) {
                val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)

                xmlDoc.documentElement.normalize()

                val ranks: NodeList = xmlDoc.getElementsByTagName("ranks")

                for (i in 0..ranks.length-1) {
                    val itemNode: Node = ranks.item(i)
                    if (itemNode.nodeType == Node.ELEMENT_NODE) {
                        val elem = itemNode as Element
                        val children = elem.childNodes

                        var currentRank: String? = null

                        for (j in 0..children.length-1) {
                            val node = children.item(j)
                            if (node is Element) {
                                when (node.nodeName) {
                                    "rank" -> {
                                        val attributes = node.attributes
                                        for (k in 0..attributes.length-1) {
                                            val node = attributes.item(k)
                                            if (node.nodeName=="type" && node.textContent != "subtype") {
                                                continue
                                            }
                                            if (node.nodeName=="value") {
                                                currentRank = node.textContent
                                                return currentRank
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
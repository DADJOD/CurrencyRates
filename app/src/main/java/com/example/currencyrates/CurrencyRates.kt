@file:Suppress("DEPRECATION")

package com.example.currencyrates

import android.app.ListActivity
import android.os.Bundle
import android.widget.SimpleAdapter
import android.widget.Toast
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import org.xml.sax.SAXException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

    // this app does not work cause I pull request in main thread
@Suppress("SENSELESS_COMPARISON")
class CurrencyRates : ListActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        populate()
    }

    private fun populate() {
        val data: ArrayList<Map<String, String>> = getData()
        val from = arrayOf( KEY_CHAR_CODE, KEY_VALUE, KEY_NOMINAL, KEY_NAME )
        val to   = intArrayOf( R.id.charCodeView, R.id.valueView, R.id.nominalView, R.id.nameView )
        val sa   = SimpleAdapter(this, data, R.layout.item_view, from, to )
        listAdapter = sa
    }

    private fun getData(): ArrayList<Map<String, String>> {
        val list = ArrayList<Map<String, String>>()
        var m: MutableMap<String, String>

        try {
            // create URL object
            val url = URL(getString(R.string.rates_url))

            // connecting
            val httpConnection = url.openConnection() as HttpURLConnection

            // get response code from server
            val responseCode: Int = httpConnection.responseCode

            // if the response code is good, parse the thread(server response)
            // setting the date in application header
            // and inflate List with necessary Map's
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream: InputStream = httpConnection.inputStream
                val dbf: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
                val db: DocumentBuilder = dbf.newDocumentBuilder()

                val dom: Document = db.parse(inputStream)
                val docElement: Element = dom.documentElement
                val date = docElement.getAttribute("Date")
                title = "$title на $date"

                val nodeList: NodeList = docElement.getElementsByTagName("Valute")

                if (nodeList != null && nodeList.length > 0) {
                    for (i in 0 until nodeList.length) {
                        val entry: Element = nodeList.item(i) as Element
                        m = HashMap()

                        val charCode = entry
                            .getElementsByTagName(KEY_CHAR_CODE)
                            .item(0).firstChild
                            .nodeValue

                        val value = entry
                            .getElementsByTagName(KEY_VALUE)
                            .item(0).firstChild
                            .nodeValue

                        val nominal = "for " + entry
                            .getElementsByTagName(KEY_NOMINAL)
                            .item(0).firstChild
                            .nodeValue

                        val name = entry
                            .getElementsByTagName(KEY_NAME)
                            .item(0).firstChild
                            .nodeValue

                        m.put(KEY_CHAR_CODE, charCode)
                        m.put(KEY_VALUE, value)
                        m.put(KEY_NOMINAL, nominal)
                        m.put(KEY_NAME, name)

                        list.add(m)
                    }
                } else {
                    // Make error notification if the response code is not OK
                    Toast.makeText(this, "OOPS! Something went wrong :(", Toast.LENGTH_LONG).show()
                }
            }

        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        } catch (e: SAXException) {
            e.printStackTrace()
        }

        return list
    }

    companion object {
        private const val KEY_CHAR_CODE = "CharCode"
        private const val KEY_VALUE = "Value"
        private const val KEY_NOMINAL = "Nominal"
        private const val KEY_NAME = "Name"
    }
}
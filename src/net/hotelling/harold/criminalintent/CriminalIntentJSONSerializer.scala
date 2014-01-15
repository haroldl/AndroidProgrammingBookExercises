package net.hotelling.harold.criminalintent

import java.util.{List => JList}
import scala.collection.JavaConversions._
import android.content.Context
import org.json.JSONArray
import java.io.Writer
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.BufferedReader
import scala.io.Source
import org.json.JSONTokener

class CriminalIntentJSONSerializer(val context: Context, val filename: String) {

  def saveCrimes(crimes: JList[Crime]) {
    val array = crimes.foldLeft(new JSONArray) { (a: JSONArray, c: Crime) => a put c.toJSON }
 
    var writer: Writer = null
    try {
      val out = context.openFileOutput(filename, Context.MODE_PRIVATE)
      writer = new OutputStreamWriter(out)
      writer.write(array.toString)
    }
    finally {
      if (writer != null) {
        writer.close()
      }
    }
  }

  def loadCrimes(): JList[Crime] = {
    try {      
      val in = context.openFileInput(filename)
      val json = Source.fromInputStream(in).getLines.foldLeft(new StringBuilder) { (sb, line) => sb append line }
      val array = new JSONTokener(json.toString).nextValue().asInstanceOf[JSONArray]
      (0 until array.length) map { i => new Crime(array getJSONObject i) }
    } catch {
      case e: FileNotFoundException => List.empty[Crime]
    }
  }
}
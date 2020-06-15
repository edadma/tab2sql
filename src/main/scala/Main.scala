package xyz.hyperreal.tab2sql

import xyz.hyperreal.importer_sn.{Table, Column, Importer}


object Main extends App {

  args match {
    case Array(path) if path endsWith ".tab" =>
      val s = io.Source.fromFile(path)
      val content = s.mkString

      s.close

      val tables = Importer.importFromString(content, true, false)

      for (t <- tables) {

      }
    case _ =>
      println("missing file argument")
      sys.exit(1)
  }

}

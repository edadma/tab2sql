package xyz.hyperreal.tab2sql

import xyz.hyperreal.importer_sn.{Column, Importer, Table}

object Main extends App {

  args match {
    case Array(path, db) if path endsWith ".tab" =>
      val s       = io.Source.fromFile(path)
      val content = s.mkString

      s.close

      val tables = Importer.importFromString(content, true, false)

      println(s"CREATE DATABASE $db;\n")

      for (Table(name, header, _) <- tables) {
        println(s"CREATE TABLE $name (")

        for ((Column(name, typ, args), idx) <- header zipWithIndex) {
          print(s"  $name ")

          args match {
            case List("pk")         => print("SERIAL PRIMARY KEY")
            case List("fk", ft, fc) => print(s"${typ toUpperCase} REFERENCES $ft ($fc)")
            case _                  => print(typ toUpperCase)
          }

          println(if (idx < header.length - 1) "," else "")
        }

        println(");\n")
      }

      for (Table(name, header, data) <- tables) {
        println(s"INSERT INTO $name (${header map (_.name) mkString ", "}) VALUES")

        for ((r, idx) <- data zipWithIndex) {
          print(s"  (${r map {
            case "null"                                      => "null"
            case s: String if !s.matches("\\d+(?:\\.\\d+)?") => s"'$s'"
            case v                                           => v
          } mkString ", "})")

          println(if (idx < data.length - 1) "," else ";\n")
        }
      }

    case _ =>
      println("missing file argument")
      sys.exit(1)
  }

}

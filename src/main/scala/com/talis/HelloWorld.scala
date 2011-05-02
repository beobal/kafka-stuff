package com.talis

import java.io.{BufferedOutputStream, File, FileOutputStream}
import scala.io.Source

object HelloWorld{  

	def foo(path: String) = {
		for (file <- new File(path).listFiles) {
			if (!file.isDirectory) {
				val src = Source.fromFile(file)
				val count = src.getLines.foldLeft(0) { (i, line) => i + 1 }
				println(count);
			}
		}
	}
	
	def main(args: Array[String]) = {
		foo(args(0))
	}
}
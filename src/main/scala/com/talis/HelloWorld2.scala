package com.talis

import java.io.{BufferedOutputStream, File, FileOutputStream}
import scala.io.Source

object HelloWorld2{  

	def foo(path: String) = {
		println("Hello world2")
	}
	
	def main(args: Array[String]) = {
		foo(args(0))
	}
}

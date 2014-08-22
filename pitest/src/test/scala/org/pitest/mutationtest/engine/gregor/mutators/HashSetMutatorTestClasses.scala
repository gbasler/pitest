package org.pitest.mutationtest.engine.gregor.mutators

import java.util.concurrent.Callable

import scala.collection.JavaConverters._
import scala.collection.immutable.TreeSet

object HashSetMutatorTestClasses {

  class HasHeadOption(values: java.util.List[Int]) extends Callable[String] {
    val set: Set[Int] = Set(values.asScala.toSeq: _*)

    def call: String = {
      set.headOption.map(_.toString).getOrElse("")
    }
  }

  class HasToSeq(values: java.util.List[Int]) extends Callable[String] {
    val set: Set[Int] = Set(values.asScala.toSeq: _*)

    def call: String = {
      set.toSeq.mkString(", ")
    }
  }

}

object TreeSetMutatorTestClasses {

  class HasHeadOption(values: java.util.List[Int]) extends Callable[String] {
    val set: Set[Int] = TreeSet(values.asScala.toSeq: _*)

    def call: String = {
      set.headOption.map(_.toString).getOrElse("")
    }
  }

  class HasToSeq(values: java.util.List[Int]) extends Callable[String] {
    val set: Set[Int] = TreeSet(values.asScala.toSeq: _*)

    def call: String = {
      set.toSeq.mkString(", ")
    }
  }

}

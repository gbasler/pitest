package org.pitest.mutationtest.engine.gregor.mutators

import java.util.concurrent.Callable

import scala.collection.JavaConverters._

object SetMutatorTestClasses {

  class HasHeadOption(values: java.util.List[Int]) extends Callable[String] {
    val set: Set[Int] = Set(values.asScala.toSeq: _*)


    def call: String = {
      set.headOption.map(_.toString).getOrElse("")
    }
  }

}

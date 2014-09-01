package org.pitest.mutationtest.engine.gregor.mutators

import java.util.concurrent.Callable

import scala.collection.JavaConverters._
import scala.collection.immutable.{HashSet, TreeSet}

object HashSetMutatorTestClasses {

  class HasHeadOption(values: java.util.List[Int]) extends Callable[String] {
    val set: HashSet[Int] = HashSet(values.asScala.toSeq: _*)

    def call: String = {
      set.headOption.map(_.toString).getOrElse("")
    }
  }

  class HasToSeq(values: java.util.List[Int]) extends Callable[String] {
    val set: HashSet[Int] = HashSet(values.asScala.toSeq: _*)

    def call: String = {
      set.toSeq.mkString(", ")
    }
  }

}

object SetMutatorTestClasses {

  class HasHeadOption(values: java.util.List[Int]) extends Callable[String] {
    val set: Set[Int] = Set(values.asScala.toSeq: _*)

    def call: String = {
      set.headOption.map(_.toString).getOrElse("")
    }
  }

  class HasTake(values: java.util.List[Int]) extends Callable[String] {
    val set: Set[Int] = Set(values.asScala.toSeq: _*)

    def call: String = {
      set.take(1).mkString(", ")
    }
  }

  class HasTakeRight(values: java.util.List[Int]) extends Callable[String] {
    val set: Set[Int] = Set(values.asScala.toSeq: _*)

    def call: String = {
      set.takeRight(1).mkString(", ")
    }
  }

  class HasToList(values: java.util.List[Int]) extends Callable[String] {
    val set: Set[Int] = Set(values.asScala.toSeq: _*)

    def call: String = {
      set.toList.mkString(", ")
    }
  }

  class HasToSeq(values: java.util.List[Int]) extends Callable[String] {
    val set: Set[Int] = Set(values.asScala.toSeq: _*)

    def call: String = {
      set.toSeq.mkString(", ")
    }
  }

  class HasToIndexedSeq(values: java.util.List[Int]) extends Callable[String] {
    val set: Set[Int] = Set(values.asScala.toSeq: _*)

    def call: String = {
      set.toIndexedSeq.mkString(", ")
    }
  }

  class HasToIntArray(values: java.util.List[Int]) extends Callable[String] {
    val set: Set[Int] = Set(values.asScala.toSeq: _*)

    def call: String = {
      set.toArray.mkString(", ")
    }
  }

  class HasToDoubleArray(values: java.util.List[Double]) extends Callable[String] {
    val set: Set[Double] = Set(values.asScala.toSeq: _*)

    def call: String = {
      set.toArray.mkString(", ")
    }
  }

  class HasToStringArray(values: java.util.List[String]) extends Callable[String] {
    val set: Set[String] = Set(values.asScala.toSeq: _*)

    def call: String = {
      set.toArray.mkString(", ")
    }
  }

  class HasForeach(values: java.util.List[String]) extends Callable[String] {
    val set: Set[String] = Set(values.asScala.toSeq: _*)

    def call: String = {
      val sb = new StringBuilder()
      set.foreach(sb.append)
      sb.toString()
    }
  }

  class HasMap(values: java.util.List[String]) extends Callable[String] {
    val set: Set[String] = Set(values.asScala.toSeq: _*)

    def call: String = {
      set.map(" " + _).mkString(", ")
    }
  }

  class HasMapWithBreakout(values: java.util.List[String]) extends Callable[String] {
    val set: Set[String] = Set(values.asScala.toSeq: _*)

    def call: String = {
      val seq: Seq[String] = set.map(" " + _)(collection.breakOut)
      seq.mkString(", ")
    }
  }

  class HasFlatMap(values: java.util.List[String]) extends Callable[String] {
    val set: Set[String] = Set(values.asScala.toSeq: _*)

    def call: String = {
      set.flatMap(s => Seq(" " + s)).mkString(", ")
    }
  }

  class HasFoldLeft(values: java.util.List[Int]) extends Callable[String] {
    val set: Set[Int] = Set(values.asScala.toSeq: _*)

    def call: String = {
      set.foldLeft("")((s, i) => s + i)
    }
  }

  class HasReduceLeft(values: java.util.List[String]) extends Callable[String] {
    val set: Set[String] = Set(values.asScala.toSeq: _*)

    def call: String = {
      set.reduceLeft(_ + _)
    }
  }

  class HasScanLeft(values: java.util.List[String]) extends Callable[String] {
    val set: Set[String] = Set(values.asScala.toSeq: _*)

    def call: String = {
      set.scanLeft("")((s, i) => s + i).mkString(", ")
    }
  }

  class HasScanLeftWithBreakout(values: java.util.List[String]) extends Callable[String] {
    val set: Set[String] = Set(values.asScala.toSeq: _*)

    def call: String = {
      val seq: Seq[String] = set.scanLeft("")((s, i) => s + i)(collection.breakOut)
      seq.mkString(", ")
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

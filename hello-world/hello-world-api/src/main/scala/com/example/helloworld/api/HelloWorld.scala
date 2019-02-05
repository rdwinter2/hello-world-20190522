package com.example.helloworld.api

//import com.example.common.regex.Matchers
import com.example.common.response.ErrorResponse
import com.example.common.utils.JsonFormats._
import com.example.common.validation.ValidationViolationKeys._

import ai.x.play.json.Jsonx
import com.wix.accord.Validator
import com.wix.accord.dsl._
import com.wix.accord.Descriptions._
import java.time.{Duration, Instant}
import java.util.UUID
import julienrf.json.derived
import play.api.{Environment, Mode}
import play.api.libs.json._

// Hello World regex matchers

object Matchers {
      val Email = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"""
      val Id = """^[a-zA-Z0-9\-\.\_\~]{1,64}$"""
      val Name = """^[a-zA-Z0-9\-\.\_\~]{1,128}$"""
      val Description = """^.{1,2048}$"""
      val Motivation = """^.{1,2048}$"""
      val Op = """^add|remove|replace|move|copy|test$"""
  }

// Hello World algebraic data type {
//
// An algebraic data type is a kind of composite type.
// They are built up from Product types and Sum types.
//
// Product types - a tuple or record (this and that)
//   final case class ScalaPerson(val name: String, val age: Int)
//
// Sum types - a disjoint union or variant type (this or that)
//   sealed trait Pet
//   final case class Cat(name: String) extends Pet
//   final case class Fish(name: String, color: String) extends Pet
//   final case class Squid(name: String, age: Int) extends Pet
//
// Sum types as a better alternative to enumeration
//  sealed abstract class Status extends Product with Serializable
//  object Status {
//    final case object Ok extends Status
//    final case object Nok extends Status
//  }

sealed trait ShapeOfWater
final case class CircleOfLife(radiusDude: Double) extends ShapeOfWater
final case class RectangleCanBeSquare(width: Double, height: Double) extends ShapeOfWater

final case class HelloWorld(
    name: String
    description: Option[String]
    name: String,
  description: Option[String])

object HelloWorld {
  implicit val format: Format[HelloWorld] = Jsonx.formatCaseClass

  val helloWorldValidator: Validator[HelloWorld] =
    validator[HelloWorld] { helloWorld =>
      helloWorld.name is notEmpty
      helloWorld.name should matchRegexFully(Matchers.Name)
      helloWorld.description.each should matchRegexFully(Matchers.Description)
    }
}
final case class CircleOfLife(
    radiusDude: Double
    name: String,
  description: Option[String])

object HelloWorld {
  implicit val format: Format[HelloWorld] = Jsonx.formatCaseClass

  val helloWorldValidator: Validator[HelloWorld] =
    validator[HelloWorld] { helloWorld =>
      helloWorld.name is notEmpty
      helloWorld.name should matchRegexFully(Matchers.Name)
      helloWorld.description.each should matchRegexFully(Matchers.Description)
    }
}
final case class RectangleCanBeSquare(
    width: Double
    height: Double
    name: String,
  description: Option[String])

object HelloWorld {
  implicit val format: Format[HelloWorld] = Jsonx.formatCaseClass

  val helloWorldValidator: Validator[HelloWorld] =
    validator[HelloWorld] { helloWorld =>
      helloWorld.name is notEmpty
      helloWorld.name should matchRegexFully(Matchers.Name)
      helloWorld.description.each should matchRegexFully(Matchers.Description)
    }
}

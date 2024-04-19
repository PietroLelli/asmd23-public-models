package scala.u07.task2

import u07.modelling.SPN
import u07.utils.MSet

import java.util.Random
import org.knowm.xchart.{QuickChart, SwingWrapper, XYChart}


object BrusselatorPetriNet:
  enum Place:
    case A, B, D, E, X, Y

  export Place.*
  export u07.modelling.CTMCSimulation.*
  export u07.modelling.SPN.*

  val brusselatorPetriNet = SPN[Place](
    Trn(MSet(A), m => 1, MSet(X, A), MSet()),
    Trn(MSet(X, X, Y), m => m(Y), MSet(X, X, X), MSet()),
    Trn(MSet(B, X), m => m(X)*0.5, MSet(Y, D, B), MSet()),
    Trn(MSet(X), m => m(X)*0.5, MSet(E), MSet()))

  @main def main =
    val simulation = toCTMC(brusselatorPetriNet).newSimulationTrace(MSet(A,B,B,B,X,Y), new Random)
      .take(500)

    val times = simulation.map(_._1).toArray
    val xCounts = simulation.map(_._2.countOccurrences(X)).map(_.toDouble).toArray
    val yCounts = simulation.map(_._2.countOccurrences(Y)).map(_.toDouble).toArray

    val chart = QuickChart.getChart("Brusselator Simulation", "Time", "Count", "X", times, xCounts)
    chart.addSeries("Y", times, yCounts)

    chart.getStyler.setLegendVisible(true)
    chart.getStyler.setMarkerSize(0)

    new SwingWrapper[XYChart](chart).displayChart().setTitle("Brusselator Simulation")

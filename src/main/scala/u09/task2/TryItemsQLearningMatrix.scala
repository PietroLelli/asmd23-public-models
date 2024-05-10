package scala.u09.task2

import scala.u09.task2.ExtendedQMatrix.Facade
import scala.u09.task2.ExtendedQMatrix.Move.*

object TryItemsQLearningMatrix extends App:
  var totalItems = Set((1,1), (3,3), (7,2))
  var items = Set((1,1), (3,3), (7,2))
  val rlItems: ExtendedQMatrix.Facade = Facade(
    width = 9,
    height = 6,
    initial = (0, 1),
    terminal = {case (7,2) => true; case _ => false},
    jumps = { PartialFunction.empty },
    obstacles = Set.empty,
    itemsToCollect = items,
    gamma = 0.9, //Future reward importance
    alpha = 0.2, //Past knowledge importance
    epsilon = 0.8, //Exploration factor
    v0 = 1
  )

  rlItems.reward = {
    case (s, a) if totalItems.contains(s) && !items.contains(s) => (totalItems.size - items.size + 1) * -4
    case (s, a) if items.contains(s) =>
      items = items - s
      (totalItems.size - items.size + 1) * 20
    case ((x,y), a)  if (x == 0 && a == LEFT) || (x == rlItems.width-1 && a == RIGHT) || (y == 0 && a == UP) || (y == rlItems.height-1 && a == DOWN) => -50
    case _ => 0
  }

  rlItems.resetMap = () => {
    items = items ++ totalItems;
    rlItems.enemyMoves = List.empty
  }

  val q0 = rlItems.qFunction
  println(rlItems.show(q0.vFunction, "%2.2f"))
  val q1 = rlItems.makeLearningInstance().learn(10000, 100, q0)
  println(rlItems.show(q1.vFunction, "%2.2f"))
  println(rlItems.show(s => if rlItems.itemsToCollect.contains(s) then "$" else q1.bestPolicy(s).toString, "%7s"))

  val agentPath = rlItems.qSystem.run(q1.bestPolicy).take(30)
  agentPath.toList.zipWithIndex.map {
    case((e1, e2), index) => (e1, if(index == 0) e2 else agentPath(index-1)._2)
  }

  println(rlItems.show(s => {
    if rlItems.itemsToCollect.contains(s) then "$" else if s == rlItems.initial then agentPath.head._1 else agentPath.find((ac, st) => st == s).map((ac, st) => ac).getOrElse(".")
  },"%7s"))
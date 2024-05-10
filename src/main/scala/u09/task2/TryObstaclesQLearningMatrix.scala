package scala.u09.task2

object TryObstaclesQLearningMatrix extends App:

  import scala.u09.task2.ExtendedQMatrix.Move.*
  import scala.u09.task2.ExtendedQMatrix.*
  val mapObstacles = Set((3,1), (5,0), (7,2))

  val rlObstacles: ExtendedQMatrix.Facade = Facade(
    width = 10,
    height = 4,
    initial = (0,1),
    terminal = {case _=>false},
    jumps = { PartialFunction.empty },
    obstacles = mapObstacles,
    itemsToCollect = Set.empty,
    gamma = 0.9, //Future reward importance
    alpha = 0.5, //Past knowledge importance
    epsilon = 0.3, //Exploration factor
    v0 = 1
  )

  rlObstacles.reward = {
    case((9, 1), _) => 1; case (s, _) if mapObstacles.contains(s) => -10;
    case ((x,y), a)  if (x == 0 && a == LEFT) || (x == rlObstacles.width-1 && a == RIGHT) || (y == 0 && a == UP) || (y == rlObstacles.height-1 && a == DOWN) => -10
    case _ => 0}

  rlObstacles.resetMap = () => rlObstacles.enemyMoves = List.empty

  val q0 = rlObstacles.qFunction
  val q1 = rlObstacles.makeLearningInstance().learn(10000,100,q0)
  println(rlObstacles.show(q1.vFunction,"%2.2f"))
  println(rlObstacles.show(s => if rlObstacles.obstacles.contains(s) then "X" else q1.bestPolicy(s).toString,"%7s"))

  val agentPath = rlObstacles.qSystem.run(q1.bestPolicy).take(30)
  agentPath.toList.zipWithIndex.map {
    case ((e1, e2), index) => (e1, if (index == 0) e2 else agentPath(index - 1)._2)
  }
  println(rlObstacles.show(s => {
    if rlObstacles.obstacles.contains(s) then "X" else if s == rlObstacles.initial then agentPath.head._1 else agentPath.find((ac, st) => st == s).map((ac, st) => ac).getOrElse(".")
  }, "%7s"))
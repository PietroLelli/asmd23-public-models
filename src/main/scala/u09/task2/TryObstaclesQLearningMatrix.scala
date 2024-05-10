package scala.u09.task2

object TryObstaclesQLearningMatrix extends App:

  import scala.u09.task2.ExtendedQMatrix.Move.*
  import scala.u09.task2.ExtendedQMatrix.*
  val mapObstacles = Set((3,1), (5,1), (7,1))

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
  println(rlObstacles.show(q0.vFunction,"%2.2f"))
  val q1 = rlObstacles.makeLearningInstance().learn(10000,100,q0)
  println(rlObstacles.show(q1.vFunction,"%2.2f"))
  println(rlObstacles.show(s => if rlObstacles.obstacles.contains(s) then "*" else q1.bestPolicy(s).toString,"%7s"))

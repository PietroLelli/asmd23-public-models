package scala.u09.task2

object TryExtendedQLearningMatrix extends App :

  import scala.u09.task2.ExtendedQMatrix.*

  val obstacles = Set((1,1), (3,1), (5,1), (7,1), (2,3), (4,3), (6,3), (8,3))
  val rl: ExtendedQMatrix.Facade = Facade(
    width = 10,
    height = 5,
    initial = (0,0),
    terminal = {case _=> false},
    obstacles = { obstacles },
    reward = { case ((9,1), _) => 1; case ((x,y), _) => if obstacles.contains((x,y)) then -5 else 0},
    jumps = { PartialFunction.empty },
    gamma = 0.9, //i reward futuri contano meno di quelli presenti
    alpha = 0.5, //conto di più quello che so gia rispetto alle cose nuove (con 0.5 fa la media
    epsilon = 0.3, //fattore di esplorazione
    v0 = 1
  )

  val q0 = rl.qFunction
  println(rl.show(q0.vFunction,"%2.2f"))
  val q1 = rl.makeLearningInstance().learn(10000,100,q0)
  println(rl.show(q1.vFunction,"%2.2f"))
  println(rl.show(s => if rl.obstacles.contains(s) then "*" else q1.bestPolicy(s).toString,"%7s"))
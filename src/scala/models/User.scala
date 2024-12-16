package scala.models

case class User
(
   user_id: Int,
   name: String,
   email: String,
   saving_goal: Double,
   amount_of_money: Double,
   debt: Double,
   income: Double
)

object User {
  def apply(c: User): User = {
    User(
      c.user_id,
      c.name,
      c.email,
      c.saving_goal.toDouble,
      c.amount_of_money.toDouble,
      c.debt.toDouble,
      c.income.toDouble
    )
  }
}
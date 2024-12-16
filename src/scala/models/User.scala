package scala.models

case class User
(
   id: Int,
   name: String,
   email: String,
   saving_goal: BigDecimal,
   amount_of_money: BigDecimal,
   debt: BigDecimal,
   income: BigDecimal
)

object User {
  def apply(c: User): User = {
    User(
      c.id,
      c.name,
      c.email,
      c.saving_goal,
      c.amount_of_money,
      c.debt,
      c.income
    )
  }
}
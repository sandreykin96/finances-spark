package scala.models

case class UserMonthResult
(
   user_id: Int,
   period: String,
   total_wasted_sum: Double,
   name: String,
   email: String,
   saving_goal: Double,
   amount_of_money: Double,
   debt: Double,
   income: Double,
)

object UserMonthResult {
  def apply(c: UserMonthResult): UserMonthResult = {
    UserMonthResult(
      c.user_id,
      c.period,
      c.total_wasted_sum,
      c.name,
      c.email,
      c.saving_goal,
      c.amount_of_money,
      c.debt,
      c.income
    )
  }
}

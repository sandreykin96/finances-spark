package scala.models
import java.sql.Timestamp
import java.util
import java.sql.Date
import java.text.SimpleDateFormat
import java.util

case class Receipt
(
  user: Int,
  created_at: Timestamp,
  name: String,
  price: Double,
  quantity: Int,
  sum: Double,
  nds: Int,
  nds_sum: Double,
  product_type: Int,
  period: String
)

object Receipt {

  def apply(c: Receipt): Receipt = {
    Receipt(
      c.user,
      c.created_at,
      c.name,
      c.price,
      c.quantity,
      c.sum,
      c.nds,
      c.nds_sum,
      c.product_type,
      c.period
    )
  }
}
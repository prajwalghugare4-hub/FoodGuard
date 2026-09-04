package com.example

import com.example.data.model.RescueRequestEntity
import com.example.data.model.RescueStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.SecureRandom

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun `verify secure 4-digit PIN generation constraints`() {
    val random = SecureRandom()
    for (i in 1..50) {
      val pin = (1000 + random.nextInt(9000)).toString()
      assertEquals(4, pin.length)
      assertTrue(pin.toInt() in 1000..9999)
    }
  }

  @Test
  fun `verify structured alert message contains required rescue fields`() {
    val rescue = RescueRequestEntity(
      rescueId = "res_12345",
      providerId = "usr_1",
      providerName = "Mom's Mess",
      providerPhone = "+91 98765 43210",
      foodItems = "Rice, Dal, Paneer",
      mealType = "Lunch",
      quantity = "18 Plates",
      pickupDeadline = "04:30 PM, Today",
      pickupDeadlineTimestamp = 1756640000000L,
      pickupAddress = "4th Cross, Downtown",
      verificationPin = "4892",
      status = RescueStatus.OPEN
    )

    val msg = """
🍲 FOOD RESCUE ALERT from ${rescue.providerName}

🍛 Items:
${rescue.quantity} of ${rescue.mealType}
${rescue.foodItems}

📍 Pickup Address:
${rescue.pickupAddress}

⏰ Pickup Deadline:
${rescue.pickupDeadline}

🔐 Verification PIN:
${rescue.verificationPin}

📞 Mess Phone:
${rescue.providerPhone}
    """.trimIndent()

    assertTrue(msg.contains("18 Plates of Lunch"))
    assertTrue(msg.contains("Rice, Dal, Paneer"))
    assertTrue(msg.contains("4th Cross, Downtown"))
    assertTrue(msg.contains("4892"))
    assertTrue(msg.contains("+91 98765 43210"))
  }
}


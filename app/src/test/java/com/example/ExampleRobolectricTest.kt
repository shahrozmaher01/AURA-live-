package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.BismaRepository
import com.example.model.GiftCategory
import com.example.model.RoomCategory
import com.example.model.VipTier
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context verifies Bisma Live app name`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Bisma Live", appName)
  }

  @Test
  fun `test room creation and seat configurations`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = BismaRepository(context)

    val user = repository.currentUser.value
    assertNotNull(user)

    // Test creating 8, 10, 15, and 20 seats
    val room8 = repository.createVoiceRoom("8 Seats Party", RoomCategory.PARTY, 8, "Welcome")
    assertEquals(8, room8.seatCount)
    assertEquals(8, room8.seats.size)
    assertEquals(user!!.id, room8.seats[0].userId)

    val room15 = repository.createVoiceRoom("15 Seats Acoustic", RoomCategory.MUSIC, 15, "Singing")
    assertEquals(15, room15.seatCount)
    assertEquals(15, room15.seats.size)

    val room20 = repository.createVoiceRoom("20 Seats Mega", RoomCategory.CHAT, 20, "Big Chat")
    assertEquals(20, room20.seatCount)
    assertEquals(20, room20.seats.size)
  }

  @Test
  fun `test sitting on seat, leaving seat, and gifting economy`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = BismaRepository(context)

    val room = repository.createVoiceRoom("Test Room", RoomCategory.CHAT, 10, "Notice")
    repository.enterRoom(room)

    // Join seat 2
    val sat = repository.joinSeat(1)
    assertTrue(sat)
    assertTrue(repository.isUserOnSeat(repository.currentUser.value!!.id))

    // Leave seat
    val left = repository.leaveSeat()
    assertTrue(left)
    assertFalse(repository.isUserOnSeat(repository.currentUser.value!!.id))

    // Gift economy test: coins deduction, transactions, rich XP
    val initialCoins = repository.currentUser.value!!.coins
    val gift = repository.gifts.value.first()
    val success = repository.sendGift(gift, 2)
    assertTrue(success)
    val expectedRemaining = initialCoins - (gift.priceCoins * 2)
    assertEquals(expectedRemaining, repository.currentUser.value!!.coins)
    assertTrue(repository.transactions.value.isNotEmpty())
  }

  @Test
  fun `test social follow and friend requests`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = BismaRepository(context)

    val targetId = 100101L
    val followed = repository.toggleFollow(targetId)
    assertTrue(followed)
    assertTrue(repository.isFollowing(targetId))

    // Unfollow
    val unfollowed = repository.toggleFollow(targetId)
    assertFalse(unfollowed)
    assertFalse(repository.isFollowing(targetId))
  }

  @Test
  fun `test store purchase and backpack equipment`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = BismaRepository(context)

    val storeItem = repository.storeItems.value.first()
    val initialCoins = repository.currentUser.value!!.coins
    val bought = repository.purchaseStoreItem(storeItem)
    assertTrue(bought)
    assertEquals(initialCoins - storeItem.priceCoins, repository.currentUser.value!!.coins)

    val bpItem = repository.backpack.value.find { it.storeItemId == storeItem.id }
    assertNotNull(bpItem)
    repository.equipBackpackItem(bpItem!!)
    assertEquals(storeItem.id, repository.currentUser.value!!.equippedFrameId)
  }
}

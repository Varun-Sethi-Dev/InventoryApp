package com.example.inventory

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.inventory.data.InventoryDatabase
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.Throws

@RunWith(AndroidJUnit4::class)
class ItemDaoTest {
    private lateinit var itemDao: ItemDao
    private lateinit var inventoryDatabase: InventoryDatabase

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        inventoryDatabase = Room.inMemoryDatabaseBuilder(context, InventoryDatabase::class.java)
            .allowMainThreadQueries().build()
        // Allowing main thread queries, just for testing.
        itemDao = inventoryDatabase.itemDao()
    }

    @After
    fun closeDb() {
        inventoryDatabase.close()
    }

    private var item1 = Item(1, "Apples", 12.0, 30)
    private var item2 = Item(id = 2, "oranges", 21.0, 10)
    private suspend fun addOneItemToDb() {
        itemDao.insert(item1)
    }

    private suspend fun addTwoItemsToDb() {
        itemDao.insert(item1)
        itemDao.insert(item2)
    }

    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsItemIntoDB() = runBlocking {
        addOneItemToDb()
        val allItems: List<Item> = itemDao.getAllItems().first()
        assertEquals(allItems[0], item1)
    }

    @Test
    @Throws(Exception::class)
    fun daoGetAllItems_returnsAllItemsFromDB() = runBlocking {
        addTwoItemsToDb()
        val allItem = itemDao.getAllItems().first()
        assertEquals(item1, allItem[0])
        assertEquals(item2, allItem[1])
    }
    @Test
    @Throws
    fun daoGetItem_returnItemFromDB() = runBlocking {
        addOneItemToDb()
        val item = itemDao.getItem(1)
        assertEquals(item1,item.first())
    }
    @Test
    @Throws(Exception::class)
    fun daoUpdateItems_updateItemsInDB() = runBlocking {
        addTwoItemsToDb()
        itemDao.update(Item(1,"Apples_2.0",14.0,15))
        itemDao.update(Item(2,"oranges_2.0",23.0,5))
        val allItems = itemDao.getAllItems().first()
        assertEquals(Item(1,"Apples_2.0",14.0,15),allItems[0])
        assertEquals(Item(2,"oranges_2.0",23.0,5),allItems[1])
    }
    @Test
    @Throws(Exception::class)
    fun daoDeleteAllItems_deleteAllItemsFromDB(){
        runBlocking {
            addTwoItemsToDb()
            itemDao.delete(item1)
            itemDao.delete(item2)
            val allItems = itemDao.getAllItems().first()
            assertTrue(allItems.isEmpty())
        }
    }
}
package com.fire.mangareader.data.local.dao

import androidx.room.*
import com.fire.mangareader.data.local.entity.CustomListEntity
import com.fire.mangareader.data.local.entity.CustomListMangaCrossRef

@Dao
interface CustomListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCustomList(customList: CustomListEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMangaToList(crossRef: CustomListMangaCrossRef)

    @Delete
    fun removeMangaFromList(crossRef: CustomListMangaCrossRef)

    @Query("SELECT * FROM custom_lists")
    fun getAllCustomLists(): List<CustomListEntity>

    @Query("SELECT mangaUrl FROM custom_list_manga_cross_ref WHERE listId = :listId ORDER BY addedAt DESC")
    fun getMangaUrlsForList(listId: String): List<String>
}

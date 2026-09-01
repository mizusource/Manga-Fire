package com.fire.mangareader.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE type = 'comment' AND mangaId = :mangaId ORDER BY timestamp DESC")
    fun getCommentsByNewest(mangaId: Int): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE type = 'comment' AND mangaId = :mangaId ORDER BY timestamp ASC")
    fun getCommentsByOldest(mangaId: Int): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE type = 'comment' AND mangaId = :mangaId ORDER BY likes DESC, timestamp DESC")
    fun getCommentsByMostLiked(mangaId: Int): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE type = 'reply' AND replyToId = :commentId ORDER BY timestamp ASC")
    fun getReplies(commentId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(comments: List<CommentEntity>)

    @Query("UPDATE comments SET likes = :likes, dislikes = :dislikes, isLikedByUser = :isLiked, isDislikedByUser = :isDisliked WHERE id = :commentId")
    suspend fun updateReaction(commentId: String, likes: Int, dislikes: Int, isLiked: Boolean, isDisliked: Boolean)

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: String)
}

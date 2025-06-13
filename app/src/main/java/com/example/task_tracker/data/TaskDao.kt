package com.example.task_tracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addTask(taskEntity: Task)

    @Query("Select * from `task-table`")
    abstract fun getTasks(): Flow<List<Task>>

    @Update()
    abstract suspend fun updateTask(taskEntity: Task)

    @Delete
    abstract suspend fun deleteTask(taskEntity: Task)
}

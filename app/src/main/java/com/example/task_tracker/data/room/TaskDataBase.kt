package com.example.task_tracker.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.task_tracker.data.room.convertor.TaskTypeConverter
import com.example.task_tracker.data.room.task.Task

@Database(
    entities = [Task::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(TaskTypeConverter::class)
abstract class TaskDataBase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDataBase? = null

        fun getTaskDatabase(context: Context): TaskDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = TaskDataBase::class.java,
                    name = "tasklist.db"
                ).fallbackToDestructiveMigration(false).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
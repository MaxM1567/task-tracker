package com.example.task_tracker.di

import android.content.Context
import com.example.task_tracker.data.TaskDataBase
import com.example.task_tracker.data.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideTasksDatabase(@ApplicationContext context: Context): TaskDataBase {
        return TaskDataBase.getTaskDatabase(context)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(database: TaskDataBase): TaskRepository {
        return TaskRepository(database.taskDao())
    }
}
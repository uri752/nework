package ru.netology.nework.dao.job

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nework.entity.JobEntity

@Dao
interface JobDao {
    @Query("SELECT * FROM JobEntity ORDER BY id DESC")
    fun get(): LiveData<List<JobEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(job: JobEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(jobs: List<JobEntity>)

    @Query("DELETE FROM JobEntity")
    suspend fun removeAll()

    @Query("DELETE FROM JobEntity WHERE id = :id")
    suspend fun removeById(id: Long)

}
package com.example.tradeempire.model

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase


@Entity(tableName = "chartSymbol")
data class ChartSymbolEntity(
    @PrimaryKey
    val symbol: String
)
@Entity(tableName = "symbols")
data class SymbolEntity(
    @PrimaryKey
    val symbol: String,
    val currency: String,
    val description: String,
    val displaySymbol: String
)


@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey
    val symbol: String,
    val currentPrice: Double,
    val highPrice: Double,
    val lowPrice: Double,
    val openPrice: Double,
    val dp: Double,
    val timestamp: Int,
    val cachedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "subscribedSymbols" ,
    indices = [Index(value = ["symbol"], unique = true)]
)
data class SubSymbolEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val symbol: String
)

@Dao
interface QuoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChartSymbol(symbol: ChartSymbolEntity)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: QuoteEntity)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllQuotes(quotes: List<QuoteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscribedSymbol(symbol: SubSymbolEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSymbols(quote: SymbolEntity)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSymbols(quotes: List<SymbolEntity>)

    @Query("SELECT * FROM chartSymbol LIMIT 1")
    suspend fun getChartSymbol(): ChartSymbolEntity?

    @Query("DELETE FROM chartSymbol")
    suspend fun clearChartSymbols()


    @Query("SELECT * FROM quotes WHERE symbol = :symbol")
    suspend fun getQuoteBySymbol(symbol: String): QuoteEntity?


    @Query("SELECT * FROM quotes WHERE symbol IN (:symbols)")
    suspend fun getQuotesBySymbols(symbols: List<String>): List<QuoteEntity>

    @Query("SELECT * FROM symbols")
    suspend fun getAllSymbols(): List<SymbolEntity>

    @Query("SELECT * FROM subscribedSymbols")
    suspend fun getSubscribedSymbols(): List<SubSymbolEntity>

    @Query("DELETE FROM quotes WHERE cachedAt < :timestampThreshold")
    suspend fun deleteStaleQuotes(timestampThreshold: Long): Int
}

@Database(entities = [ChartSymbolEntity::class, SymbolEntity::class, QuoteEntity::class, SubSymbolEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun quoteDao(): QuoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finnhub_db"
                ).fallbackToDestructiveMigration(false)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos")
    fun getAllTodos(): Flow<List<Todo>>

    @Insert
    suspend fun insertTodo(todo: Todo)
}
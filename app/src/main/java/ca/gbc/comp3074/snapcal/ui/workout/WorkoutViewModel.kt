package ca.gbc.comp3074.snapcal.ui.workout
import androidx.lifecycle.*
import ca.gbc.comp3074.snapcal.data.model.WorkoutEntry
import ca.gbc.comp3074.snapcal.data.repo.WorkoutRepository
import kotlinx.coroutines.launch
class WorkoutViewModel(private val repo: WorkoutRepository) : ViewModel() {
    val allEntries = repo.observeAll(); val todayBurned = repo.observeTodayBurned()
    fun observeUpcoming(nowMillis:Long) = repo.observeUpcoming(nowMillis)
    fun addActivity(name:String,caloriesBurned:Int,dateMillis:Long) { viewModelScope.launch{repo.add(WorkoutEntry(name=name,caloriesBurned=caloriesBurned,dateMillis=dateMillis,isScheduled=false))} }
    fun scheduleWorkout(name:String,dateMillis:Long) { viewModelScope.launch{repo.add(WorkoutEntry(name=name,caloriesBurned=0,dateMillis=dateMillis,isScheduled=true))} }
    fun delete(entry:WorkoutEntry) { viewModelScope.launch{repo.delete(entry)} }
}
class WorkoutViewModelFactory(private val repo:WorkoutRepository) : ViewModelProvider.Factory {
    override fun <T:ViewModel> create(modelClass:Class<T>): T { @Suppress("UNCHECKED_CAST") return WorkoutViewModel(repo) as T }
}

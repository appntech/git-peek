package explore.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import explore.data.local.GitPeekDatabase
import explore.data.local.GitHubUserDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): GitPeekDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            GitPeekDatabase::class.java,
            "GitHub_Users.db"
        ).build()
    }

    @Provides
    fun provideGitHubUserDao(database: GitPeekDatabase): GitHubUserDao = database.gitHubUserDao()
}
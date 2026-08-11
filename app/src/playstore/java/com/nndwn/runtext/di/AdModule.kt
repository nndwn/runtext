package com.nndwn.runtext.di

import com.nndwn.runtext.ads.AdHelper
import com.nndwn.runtext.ads.RewardedAdHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AdModule {
    @Provides
    @Singleton
    fun provideAdHelper(): AdHelper {
        return RewardedAdHelper()
    }
}

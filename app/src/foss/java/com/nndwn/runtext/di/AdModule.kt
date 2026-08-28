package com.nndwn.runtext.di

import com.nndwn.runtext.helper.AdHelper
import com.nndwn.runtext.helper.BillingHelper
import com.nndwn.runtext.helper.StubAdHelper
import com.nndwn.runtext.helper.StubBillingHelper
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
    return StubAdHelper()
  }

  @Provides
  @Singleton
  fun provideBillingHelper(): BillingHelper {
    return StubBillingHelper()
  }
}

package com.nndwn.runtext.di

import com.nndwn.runtext.helper.BillingHelper
import com.nndwn.runtext.helper.BillingManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayStoreModule {

  @Provides
  @Singleton
  fun provideBillingHelper(billingManager: BillingManager): BillingHelper {
    return billingManager
  }
}

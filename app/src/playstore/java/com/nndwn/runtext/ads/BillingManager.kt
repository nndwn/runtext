package com.nndwn.runtext.ads

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingManager @Inject constructor(
    @param:ApplicationContext private val context : Context
) : PurchasesUpdatedListener, BillingHelper {
    private companion object {
        const val REMOVE_ADS_PRODUCT_ID = "remove_ads_permanent"
    }
    
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _purchaseSuccessEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val purchaseSuccessEvent: SharedFlow<Unit> = _purchaseSuccessEvent.asSharedFlow()

    private val billingClient : BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .build()

    private val _productDetails = MutableStateFlow<ProductDetails?>(null)
    
    override val removeAdsPrice: StateFlow<String?> = _productDetails.map { details ->
        details?.oneTimePurchaseOfferDetails?.formattedPrice
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), null)

    private var onPurchasedListener: ((Boolean) -> Unit)? = null

    override fun launchBillingFlow(activity : Activity) {
        val details = _productDetails.value ?: return
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }


    override fun startConnection(setPurchased: (Boolean) -> Unit, billingDisconnected : () -> Unit) {
        this.onPurchasedListener = setPurchased
        if (billingClient.isReady) {
            queryPurchases(setPurchased)
            return
        }
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK){
                    queryPurchases(setPurchased)
                    queryProductDetails()
                }
            }

            override fun onBillingServiceDisconnected() {
                billingDisconnected()
            }

        })
    }

    private fun queryProductDetails(){
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(REMOVE_ADS_PRODUCT_ID)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        billingClient.queryProductDetailsAsync(params) { billingResult, queryProductDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK){
                val list = queryProductDetailsResult.productDetailsList
                _productDetails.value = list.firstOrNull()
            }
        }
    }

    fun queryPurchases(setPurchased : (Boolean) -> Unit){
        if (!billingClient.isReady) return
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        billingClient.queryPurchasesAsync(params){ billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK){
                val hasRemoveAds = purchases.any { item ->
                    item.products.contains(REMOVE_ADS_PRODUCT_ID) &&
                            item.purchaseState == Purchase.PurchaseState.PURCHASED
                }
                setPurchased(hasRemoveAds)
            }
            purchases.filter {it.purchaseState == Purchase.PurchaseState.PURCHASED && !it.isAcknowledged }
                .forEach {acknowledgePurchase(it)}
        }
    }

    private fun acknowledgePurchase(purchase : Purchase){
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params){ billingResult ->
             if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                 onPurchasedListener?.invoke(true)
                 _purchaseSuccessEvent.tryEmit(Unit)
             }
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: List<Purchase?>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null){
            for (purchase in purchases) {
                if (purchase?.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    if (!purchase.isAcknowledged) {
                        acknowledgePurchase(purchase)
                    } else {
                        onPurchasedListener?.invoke(true)
                    }
                }
            }
        }
    }

}